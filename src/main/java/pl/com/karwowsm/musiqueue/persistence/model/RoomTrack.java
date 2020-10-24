package pl.com.karwowsm.musiqueue.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "room_tracks")
public class RoomTrack {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @ManyToOne
    private Track track;

    private UUID roomId;

    @ManyToOne
    private UserAccount owner;

    @NotNull
    private Integer userIndex;

    @NotNull
    private Instant addedAt;

    @NotNull
    private Integer index;
}
