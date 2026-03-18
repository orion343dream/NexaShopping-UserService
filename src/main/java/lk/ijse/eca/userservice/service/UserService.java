package lk.ijse.eca.userservice.service;

import lk.ijse.eca.userservice.dto.UserRequestDTO;
import lk.ijse.eca.userservice.dto.UserResponseDTO;

import java.util.List;

public interface UserService {

    UserResponseDTO createUser(UserRequestDTO dto);

    UserResponseDTO updateUser(String nic, UserRequestDTO dto);

    void deleteUser(String nic);

    UserResponseDTO getUser(String nic);

    List<UserResponseDTO> getAllUsers();

    byte[] getUserPicture(String nic);
}
