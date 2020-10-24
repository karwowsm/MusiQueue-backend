package pl.com.karwowsm.musiqueue.api.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TrackUploadRequest {

    @NotNull
    private MultipartFile file;
}
