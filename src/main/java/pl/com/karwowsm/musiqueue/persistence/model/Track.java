package pl.com.karwowsm.musiqueue.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Track {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    private String title;

    @NotBlank
    private String artist;

    @NotNull
    private Integer duration;

    private String imageUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Source source;

    @NotBlank
    private String trackId;

    @Formula("(SELECT COUNT(rt.track_id) FROM room_tracks rt WHERE rt.track_id = id)")
    private Integer queuedNumber;

    public enum Source {
        UPLOADED,
        YOUTUBE,
        SPOTIFY,
        SOUNDCLOUD
    }
}
