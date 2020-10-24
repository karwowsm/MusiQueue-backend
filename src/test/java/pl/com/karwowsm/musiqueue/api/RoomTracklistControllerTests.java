package pl.com.karwowsm.musiqueue.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.com.karwowsm.musiqueue.api.request.RoomTrackCreateRequest;
import pl.com.karwowsm.musiqueue.config.ResourcesConfig;
import pl.com.karwowsm.musiqueue.persistence.model.Room;
import pl.com.karwowsm.musiqueue.persistence.model.RoomTrack;
import pl.com.karwowsm.musiqueue.persistence.model.Track;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
class RoomTracklistControllerTests extends BaseApiTests {

    private static final String BASE_PATH = "/rooms/%s/tracklist";

    private Room room;

    @BeforeEach
    void setup() {
        room = roomRepository.saveAndFlush(Room.builder()
            .name("test")
            .host(userAccount1)
            .build());
    }

    @AfterAll
    static void tearDown() throws IOException {
        FileUtils.deleteDirectory(ResourcesConfig.TRACKS_DIR_PATH.toFile());
    }

    @Test
    void testAddTrackById() {
        String path = String.format(BASE_PATH, room.getId());
        int n = 150;
        CompletableFuture<List<ResponseEntity<RoomTrack>>> future = sendAsync(HttpMethod.PATCH, path, IntStream.range(0, n)
            .mapToObj(value -> RoomTrackCreateRequest.builder()
                .title("test")
                .artist("test")
                .duration(Integer.MAX_VALUE)
                .source(Track.Source.UPLOADED)
                .trackId(String.valueOf(value))
                .build()), RoomTrack.class);
        List<ResponseEntity<RoomTrack>> responses = future.join();
        Map<HttpStatus, List<ResponseEntity<RoomTrack>>> responsesMap = responses.stream().collect(Collectors.groupingBy(ResponseEntity::getStatusCode));
        responsesMap.keySet()
            .forEach(httpStatus -> log.debug("{}: {}", httpStatus, responsesMap.get(httpStatus).size()));
        log.debug("trackId -> index: {}", responsesMap.get(HttpStatus.CREATED).stream()
            .map(ResponseEntity::getBody)
            .map(roomTrack -> String.format("%s -> %d", roomTrack.getTrack().getTrackId(), roomTrack.getIndex()))
            .collect(Collectors.toList()));
        Assert.assertTrue(responses.stream().allMatch(response -> response.getStatusCode() == HttpStatus.CREATED));
        Assert.assertEquals(n, roomTrackRepository.findAll().size());
    }

    @Test
    void testAddTrackByUpload() {
        String path = String.format(BASE_PATH, room.getId());
        File resources = new File(getClass().getClassLoader().getResource("tracks").getFile());
        File[] files = resources.listFiles();
        for (File file : files) {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(file));
            HttpHeaders httpHeaders = buildHttpHeaders();
            httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(body, httpHeaders);

            ResponseEntity<RoomTrack> response = send(HttpMethod.PATCH, path, httpEntity, RoomTrack.class);
            Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }
        List<Track> tracks = trackRepository.findAll();
        List<Track> uploadedWithImages = tracks.stream().filter(it -> it.getImageUrl() != null).collect(Collectors.toList());
        Assert.assertEquals(files.length, tracks.size());
        Assert.assertEquals(files.length, getUploadedTracksFiles().length);
        Assert.assertEquals(uploadedWithImages.size(), getUploadedImagesFiles().length);
        Assert.assertEquals(2, uploadedWithImages.size());
        Assert.assertEquals(1, tracks.stream().filter(it -> it.getTitle().equals("Unknown")).count());
        Assert.assertEquals(2, tracks.stream().filter(it -> it.getArtist().equals("Unknown")).count());
    }

    private File[] getUploadedTracksFiles() {
        return ResourcesConfig.TRACKS_DIR_PATH.toFile()
            .listFiles((dir, name) -> !name.equals("images"));
    }

    private File[] getUploadedImagesFiles() {
        return ResourcesConfig.IMAGES_DIR_PATH.toFile()
            .listFiles();
    }
}
