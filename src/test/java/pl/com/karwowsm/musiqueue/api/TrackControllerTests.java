package pl.com.karwowsm.musiqueue.api;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.com.karwowsm.musiqueue.helper.DeserializablePage;
import pl.com.karwowsm.musiqueue.persistence.model.Room;
import pl.com.karwowsm.musiqueue.persistence.model.RoomTrack;
import pl.com.karwowsm.musiqueue.persistence.model.Track;

import java.time.Instant;

class TrackControllerTests extends BaseApiTests {

    private static final String BASE_PATH = "/tracks";

    private Track track1;

    private Track track2;

    @BeforeEach
    void setup() {
        track1 = trackRepository.saveAndFlush(Track.builder()
            .title("test1")
            .artist("test1")
            .duration(Integer.MAX_VALUE)
            .source(Track.Source.UPLOADED)
            .trackId("test1")
            .build());

        track2 = trackRepository.saveAndFlush(Track.builder()
            .title("test2")
            .artist("test2")
            .duration(Integer.MAX_VALUE)
            .source(Track.Source.UPLOADED)
            .trackId("test2")
            .build());

        Room room = roomRepository.saveAndFlush(Room.builder()
            .name("test")
            .host(userAccount1)
            .build());

        roomTrackRepository.saveAndFlush(RoomTrack.builder()
            .track(track2)
            .roomId(room.getId())
            .owner(userAccount1)
            .userIndex(0)
            .addedAt(Instant.now())
            .index(0)
            .build());
    }

    @Test
    void testFind() {
        ResponseEntity<DeserializablePage<Track>> response = send(HttpMethod.GET, BASE_PATH, null, new ParameterizedTypeReference<DeserializablePage<Track>>() {
        });
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(2, response.getBody().getNumberOfElements());
        Assert.assertEquals(0, response.getBody().getContent().stream().filter(it -> it.getId().equals(track1.getId())).findFirst().get().getQueuedNumber().intValue());
        Assert.assertEquals(1, response.getBody().getContent().stream().filter(it -> it.getId().equals(track2.getId())).findFirst().get().getQueuedNumber().intValue());
    }
}
