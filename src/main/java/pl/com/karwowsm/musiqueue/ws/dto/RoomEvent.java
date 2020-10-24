package pl.com.karwowsm.musiqueue.ws.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import pl.com.karwowsm.musiqueue.persistence.model.Room;

@RequiredArgsConstructor
@Getter
@ToString
public class RoomEvent extends Event<RoomEvent.Type> {

    private final Type type;

    private final Room room;

    public enum Type {
        UPDATED,
        DELETED
    }
}
