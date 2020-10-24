package pl.com.karwowsm.musiqueue.api.request;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@ToString
public class RoomUpdateRequest {

    @NotBlank
    private String name;

    @Positive
    private Integer userQueuedTracksLimit;
}
