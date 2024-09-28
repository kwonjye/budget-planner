package jye.budget.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteUserForm {

    @NotBlank
    private String password;
}
