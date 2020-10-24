package pl.com.karwowsm.musiqueue.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.com.karwowsm.musiqueue.api.request.RoomTrackCreateRequest;
import pl.com.karwowsm.musiqueue.api.request.TrackUploadRequest;
import pl.com.karwowsm.musiqueue.config.ResourcesConfig;
import pl.com.karwowsm.musiqueue.exception.InvalidFileException;
import pl.com.karwowsm.musiqueue.exception.ResourceNotFoundException;
import pl.com.karwowsm.musiqueue.exception.TrackUploadException;
import pl.com.karwowsm.musiqueue.persistence.model.FileEntity;
import pl.com.karwowsm.musiqueue.persistence.model.Track;
import pl.com.karwowsm.musiqueue.persistence.repository.FileEntityRepository;
import pl.com.karwowsm.musiqueue.persistence.repository.TrackRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService {

    private final TrackRepository trackRepository;

    private final FileEntityRepository fileEntityRepository;

    @Override
    public Track findOrCreate(RoomTrackCreateRequest request) {
        Track track = trackRepository.findBySourceAndTrackId(request.getSource(), request.getTrackId());
        if (track != null) {
            return track;
        }

        return trackRepository.saveAndFlush(Track.builder()
            .title(request.getTitle())
            .artist(request.getArtist())
            .duration(request.getDuration())
            .imageUrl(request.getImageUrl())
            .source(request.getSource())
            .trackId(request.getTrackId())
            .build());
    }

    @Override
    public Track upload(TrackUploadRequest request) {
        MultipartFile requestFile = request.getFile();

        String trackId;
        try {
            trackId = DigestUtils.md5DigestAsHex(requestFile.getBytes());
        } catch (IOException e) {
            throw new TrackUploadException(e);
        }
        String extension = FilenameUtils.getExtension(requestFile.getOriginalFilename());
        trackId += '.' + extension;

        Track track = trackRepository.findBySourceAndTrackId(Track.Source.UPLOADED, trackId);
        if (track != null) {
            return track;
        } else {
            log.trace("Uploading file: [originalFilename={}, trackId={}]", requestFile.getOriginalFilename(), trackId);

            try {
                Path path = ResourcesConfig.TRACKS_DIR_PATH.resolve(trackId);
                saveFile(path, requestFile.getOriginalFilename(), requestFile.getBytes());

                File file = new File(path.toUri());
                AudioFile audioFile = AudioFileIO.read(file);
                Tag tag = audioFile.getTag();

                String title = null;
                String artist = null;
                String imageUrl = null;
                if (tag != null) {
                    title = tag.getFirst(FieldKey.TITLE);
                    artist = tag.getFirst(FieldKey.ARTIST);
                    if (tag.getFirstArtwork() != null) {
                        String imageExtension = MimeTypeUtils.parseMimeType(tag.getFirstArtwork().getMimeType()).getSubtype();
                        String imageId = DigestUtils.md5DigestAsHex(tag.getFirstArtwork().getBinaryData());
                        Path imagePath = ResourcesConfig.IMAGES_DIR_PATH.resolve(imageId + '.' + imageExtension);
                        saveFile(imagePath, tag.getFirstArtwork().getBinaryData());
                        imageUrl = "/images/" + imageId + '.' + imageExtension;
                    }
                }
                Integer duration = (int) (audioFile.getAudioHeader().getPreciseTrackLength() * 1000);

                return trackRepository.saveAndFlush(Track.builder()
                    .title(!StringUtils.isEmpty(title) ? title : "Unknown")
                    .artist(!StringUtils.isEmpty(artist) ? artist : "Unknown")
                    .duration(duration)
                    .imageUrl(imageUrl)
                    .source(Track.Source.UPLOADED)
                    .trackId(trackId)
                    .build());

            } catch (CannotReadException e) {
                throw new InvalidFileException(e);
            } catch (IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
                throw new TrackUploadException(e);
            }
        }
    }

    @Override
    public Page<Track> find(Pageable pageable) {
        return trackRepository.findAll(pageable);
    }

    @Override
    public Track get(UUID id) {
        return trackRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Track.class, id));
    }

    private void saveFile(Path path, byte[] content) throws IOException {
        saveFile(path, null, content);
    }

    private void saveFile(Path path, String originalName, byte[] content) throws IOException {
        fileEntityRepository.saveAndFlush(FileEntity.builder()
            .path(ResourcesConfig.BASE_DIR_PATH.relativize(path).toString())
            .originalName(originalName)
            .content(content)
            .build());
        Files.write(path, content);
    }
}
