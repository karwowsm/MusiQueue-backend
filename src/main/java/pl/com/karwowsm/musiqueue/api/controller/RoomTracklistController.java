package pl.com.karwowsm.musiqueue.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.com.karwowsm.musiqueue.api.dto.RoomTracklist;
import pl.com.karwowsm.musiqueue.api.request.RoomTrackCreateRequest;
import pl.com.karwowsm.musiqueue.api.request.TrackUploadRequest;
import pl.com.karwowsm.musiqueue.persistence.model.RoomTrack;
import pl.com.karwowsm.musiqueue.persistence.model.Track;
import pl.com.karwowsm.musiqueue.persistence.model.UserAccount;
import pl.com.karwowsm.musiqueue.service.RoomService;
import pl.com.karwowsm.musiqueue.service.RoomTracklistService;
import pl.com.karwowsm.musiqueue.service.TrackService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/rooms/{roomId}/tracklist")
@RequiredArgsConstructor
public class RoomTracklistController {

    private final RoomService roomService;

    private final TrackService trackService;

    private final RoomTracklistService roomTracklistService;

    private final PlayRoomTrackRequestHandler playRoomTrackRequestHandler;

    @GetMapping
    public RoomTracklist getTracklist(@PathVariable UUID roomId) {
        log.trace("Getting room tracklist: roomId={}", roomId);
        RoomTracklist tracklist = roomTracklistService.get(roomId);
        log.debug("Got room tracklist: tracklist={}", tracklist);
        return tracklist;
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public RoomTrack addTrack(@AuthenticationPrincipal UserAccount userAccount, @PathVariable UUID roomId, @Valid TrackUploadRequest request) {
        log.trace("Adding track: [roomId={}, {}]", roomId, userAccount);
        roomService.assertExists(roomId);
        Track track = trackService.upload(request);
        RoomTrack roomTrack = roomTracklistService.addTrack(roomId, track, userAccount);
        log.debug("Added track: {}", roomTrack);
        return roomTrack;
    }

    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public RoomTrack addTrack(@AuthenticationPrincipal UserAccount userAccount, @PathVariable UUID roomId, @RequestBody @Valid RoomTrackCreateRequest request) {
        log.trace("Adding track: [roomId={}, {}, {}]", roomId, request, userAccount);
        roomService.assertExists(roomId);
        Track track = trackService.findOrCreate(request);
        RoomTrack roomTrack = roomTracklistService.addTrack(roomId, track, userAccount);
        log.debug("Added track: {}", roomTrack);
        return roomTrack;
    }

    @PatchMapping("/{trackId}")
    @ResponseStatus(HttpStatus.CREATED)
    public RoomTrack addTrack(@AuthenticationPrincipal UserAccount userAccount, @PathVariable UUID roomId, @PathVariable UUID trackId) {
        log.trace("Adding track: [roomId={}, trackId={}, {}]", roomId, trackId, userAccount);
        roomService.assertExists(roomId);
        Track track = trackService.get(trackId);
        RoomTrack roomTrack = roomTracklistService.addTrack(roomId, track, userAccount);
        log.debug("Added track: {}", roomTrack);
        return roomTrack;
    }

    @DeleteMapping("/{roomTrackId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrack(@AuthenticationPrincipal UserAccount userAccount, @PathVariable UUID roomId, @PathVariable UUID roomTrackId) {
        log.trace("Deleting roomTrack: [roomId={}, roomTrackId={}, {}]", roomId, roomTrackId, userAccount);
        RoomTrack roomTrack = roomTracklistService.deleteTrack(roomId, roomTrackId, userAccount);
        log.debug("Deleted roomTrack: {}", roomTrack);
    }

    @PatchMapping("/play/next")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void playNext(@PathVariable UUID roomId) {
        log.trace("Playing next track: roomId={}", roomId);
        RoomTrack played = roomTracklistService.playNext(roomId);
        log.debug("Played next track: {}", played);
    }

    @PatchMapping("/play/{roomTrackId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void playTrack(@AuthenticationPrincipal UserAccount userAccount, @PathVariable UUID roomId, @PathVariable UUID roomTrackId) {
        log.trace("Playing track: [roomId={}, roomTrackId={}, {}]", roomId, roomTrackId, userAccount);
        RoomTrack played = roomTracklistService.playTrack(roomId, roomTrackId, userAccount);
        log.debug("Played track: {}", played);
    }

    @GetMapping("/play/{roomTrackId}")
    public void play(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        playRoomTrackRequestHandler.handleRequest(request, response);
    }
}
