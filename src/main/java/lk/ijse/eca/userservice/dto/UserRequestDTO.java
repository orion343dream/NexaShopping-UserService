package lk.ijse.eca.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lk.ijse.eca.userservice.validation.ValidImage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserRequestDTO {

    public interface OnCreate {}

    // NIC/ID field — accepts any non-blank alphanumeric identifier (username, NIC, etc.)
    @NotBlank(groups = OnCreate.class, message = "ID is required")
    private String nic;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Mobile is required")
    private String mobile;

    @Email(message = "Invalid email format")
    private String email;

    @NotNull(groups = OnCreate.class, message = "Picture is required")
    @ValidImage
    private MultipartFile picture;
}
