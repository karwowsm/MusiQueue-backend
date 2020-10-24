package pl.com.karwowsm.musiqueue.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Room {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Column(unique = true)
    private String name;

    @Positive
    private Integer userQueuedTracksLimit;

    @NotNull
    @ManyToOne
    private UserAccount host;

    @OneToOne
    private RoomTrack currentTrack;

    private Instant startedPlayingAt;

    @Formula("(SELECT COUNT(rm.user_account_id) FROM room_members rm WHERE rm.room_id = id)")
    private Integer membersCount;

    public boolean isPlaying() {
        return currentTrack != null
            && Instant.now().isBefore(startedPlayingAt.plusMillis(currentTrack.getTrack().getDuration()));
    }
}
