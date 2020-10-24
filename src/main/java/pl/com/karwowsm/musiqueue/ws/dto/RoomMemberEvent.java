package pl.com.karwowsm.musiqueue.ws.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import pl.com.karwowsm.musiqueue.persistence.model.UserAccount;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@ToString
public class RoomMemberEvent extends Event<RoomMemberEvent.Type> {

    private final Type type;

    private final UUID roomId;

    private final UserAccount userAccount;

    public enum Type {
        JOINED,
        LEFT
    }
}
