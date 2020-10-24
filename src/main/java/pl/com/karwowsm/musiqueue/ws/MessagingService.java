package pl.com.karwowsm.musiqueue.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import pl.com.karwowsm.musiqueue.persistence.model.Room;
import pl.com.karwowsm.musiqueue.persistence.model.RoomMember;
import pl.com.karwowsm.musiqueue.persistence.model.RoomTrack;
import pl.com.karwowsm.musiqueue.ws.dto.RoomEvent;
import pl.com.karwowsm.musiqueue.ws.dto.RoomMemberEvent;
import pl.com.karwowsm.musiqueue.ws.dto.RoomTrackEvent;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingService {

    private final SimpMessageSendingOperations messagingTemplate;

    public void publishRoomEvent(Room room, RoomEvent.Type eventType) {
        RoomEvent roomEvent = new RoomEvent(eventType, room);
        log.info("Publishing room event: {}", roomEvent);
        messagingTemplate.convertAndSend("/topic/rooms/" + room.getId(), roomEvent);
    }

    public void publishRoomMemberEvent(RoomMember roomMember, RoomMemberEvent.Type eventType) {
        RoomMemberEvent roomMemberEvent = new RoomMemberEvent(eventType, roomMember.getRoomId(), roomMember.getUserAccount());
        log.info("Publishing room members event: {}", roomMemberEvent);
        messagingTemplate.convertAndSend("/topic/rooms/" + roomMember.getRoomId() + "/members", roomMemberEvent);
    }

    public void publishRoomTrackEvent(RoomTrack track, RoomTrackEvent.Type eventType) {
        publishRoomTrackEvent(track.getRoomId(), new RoomTrackEvent(eventType, track));
    }

    public void publishRoomTrackEvent(RoomTrack track, RoomTrackEvent.Type eventType, Instant timestamp) {
        publishRoomTrackEvent(track.getRoomId(), new RoomTrackEvent(eventType, track, timestamp));
    }

    private void publishRoomTrackEvent(UUID roomId, RoomTrackEvent roomTrackEvent) {
        log.info("Publishing room tracklist event: {}", roomTrackEvent);
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/tracklist", roomTrackEvent);
    }
}
