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
import pl.com.karwowsm.musiqueue.persistence.model.RoomMember;

class RoomControllerTests extends BaseApiTests {

    private static final String BASE_PATH = "/rooms";

    private Room room1;

    private Room room2;

    @BeforeEach
    void setup() {
        room1 = roomRepository.saveAndFlush(Room.builder()
            .name("test1")
            .host(userAccount1)
            .build());

        room2 = roomRepository.saveAndFlush(Room.builder()
            .name("test2")
            .host(userAccount1)
            .build());

        roomMemberRepository.saveAndFlush(RoomMember.builder()
            .userAccountId(userAccount1.getId())
            .userAccount(userAccount1)
            .roomId(room2.getId())
            .build());
    }

    @Test
    void testFind() {
        ResponseEntity<DeserializablePage<Room>> response = send(HttpMethod.GET, BASE_PATH, null, new ParameterizedTypeReference<DeserializablePage<Room>>() {
        });
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(2, response.getBody().getNumberOfElements());
        Assert.assertEquals(0, response.getBody().getContent().stream().filter(it -> it.getId().equals(room1.getId())).findFirst().get().getMembersCount().intValue());
        Assert.assertEquals(1, response.getBody().getContent().stream().filter(it -> it.getId().equals(room2.getId())).findFirst().get().getMembersCount().intValue());
    }
}
