package pl.com.karwowsm.musiqueue.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "room_members")
public class RoomMember {

    @Id
    private UUID userAccountId;

    @MapsId
    @OneToOne
    private UserAccount userAccount;

    @NotNull
    private UUID roomId;
}
