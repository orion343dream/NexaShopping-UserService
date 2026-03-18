package lk.ijse.eca.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private String nic;
    private String name;
    private String address;
    private String mobile;
    private String email;
    private String picture;
}
