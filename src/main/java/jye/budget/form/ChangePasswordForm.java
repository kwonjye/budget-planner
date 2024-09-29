package jye.budget.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordForm {

    @NotBlank
    private String password;

    @NotBlank
    private String changePassword;

    @NotBlank
    private String changePasswordConfirm;
}
