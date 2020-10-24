package pl.com.karwowsm.musiqueue.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import pl.com.karwowsm.musiqueue.config.ResourcesConfig;
import pl.com.karwowsm.musiqueue.exception.RoomTrackPlayerException;
import pl.com.karwowsm.musiqueue.persistence.model.Room;
import pl.com.karwowsm.musiqueue.persistence.model.Track;
import pl.com.karwowsm.musiqueue.service.RoomService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
class PlayRoomTrackRequestHandler extends ResourceHttpRequestHandler {

    private final RoomService roomService;

    @Override
    protected Resource getResource(HttpServletRequest request) throws IOException {
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        UUID roomId = UUID.fromString(pathVariables.get("roomId"));
        UUID roomTrackId = UUID.fromString(pathVariables.get("roomTrackId"));

        Room room = roomService.get(roomId);
        if (room.getCurrentTrack() == null) {
            throw new RoomTrackPlayerException(room);
        }
        if (!Objects.equals(room.getCurrentTrack().getId(), roomTrackId)) {
            throw new RoomTrackPlayerException(room, roomTrackId);
        }
        if (!room.getCurrentTrack().getTrack().getSource().equals(Track.Source.UPLOADED)) {
            throw new RoomTrackPlayerException(room.getCurrentTrack().getTrack());
        }
        Track track = room.getCurrentTrack().getTrack();

        File trackFile = ResourcesConfig.TRACKS_DIR_PATH.resolve(track.getTrackId()).toFile();

        if (trackFile.exists()) {
            return new FileSystemResource(trackFile);
        } else {
            throw new FileNotFoundException();
        }
    }
}
