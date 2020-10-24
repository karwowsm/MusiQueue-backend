package pl.com.karwowsm.musiqueue.api.request;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@ToString
public class TokenCreateRequest {

    @NotBlank
    private String username;

    @NotBlank
    @ToString.Exclude
    private String password;
}
