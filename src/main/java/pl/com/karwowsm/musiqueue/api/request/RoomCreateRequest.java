package pl.com.karwowsm.musiqueue.api.request;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@ToString
public class RoomCreateRequest {

    @NotBlank
    private String name;

    @Min(1)
    private Integer userQueuedTracksLimit;
}
