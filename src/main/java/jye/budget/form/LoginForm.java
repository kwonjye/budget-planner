package jye.budget.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginForm {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
