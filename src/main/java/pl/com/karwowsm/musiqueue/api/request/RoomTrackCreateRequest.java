package pl.com.karwowsm.musiqueue.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pl.com.karwowsm.musiqueue.persistence.model.Track;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Builder
@ToString
public class RoomTrackCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String artist;

    @NotNull
    @Positive
    private Integer duration;

    private String imageUrl;

    @NotNull
    private Track.Source source;

    @NotBlank
    private String trackId;
}
