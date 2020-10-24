package pl.com.karwowsm.musiqueue.api.request;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Objects;

@Getter
@ToString
public class UserAccountCreateRequest {

    @NotNull
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = "must be alphanumeric")
    private String username;

    @NotNull
    @Size(min = 8, max = 20)
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = "must be alphanumeric")
    @ToString.Exclude
    private String password;

    @NotNull
    @Size(min = 8, max = 20)
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = "must be alphanumeric")
    @ToString.Exclude
    private String passwordConfirmation;

    @Email
    @NotBlank
    private String email;

    @AssertTrue(message = "Passwords don't match")
    public boolean isPasswordConfirmed() {
        return Objects.equals(password, passwordConfirmation);
    }
}
