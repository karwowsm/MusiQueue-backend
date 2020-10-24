package pl.com.karwowsm.musiqueue.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class FileEntity {

    @Id
    @NotBlank
    private String path;

    private String originalName;

    @NotEmpty
    private byte[] content;
}
