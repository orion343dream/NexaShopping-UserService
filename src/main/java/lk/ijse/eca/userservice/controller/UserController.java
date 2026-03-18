package lk.ijse.eca.userservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lk.ijse.eca.userservice.dto.UserRequestDTO;
import lk.ijse.eca.userservice.dto.UserResponseDTO;
import lk.ijse.eca.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
// Note: removed @Validated at class level to allow flexible IDs (not NIC-only)
public class UserController {

    private final UserService UserService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDTO> createUser(
            @Validated({Default.class, UserRequestDTO.OnCreate.class}) @ModelAttribute UserRequestDTO dto) {
        log.info("POST /api/v1/users - ID: {}", dto.getNic());
        UserResponseDTO response = UserService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{nic}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable String nic,
            @Valid @ModelAttribute UserRequestDTO dto) {
        log.info("PUT /api/v1/users/{}", nic);
        UserResponseDTO response = UserService.updateUser(nic, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{nic}")
    public ResponseEntity<Void> deleteUser(@PathVariable String nic) {
        log.info("DELETE /api/v1/users/{}", nic);
        UserService.deleteUser(nic);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{nic}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String nic) {
        log.info("GET /api/v1/users/{}", nic);
        UserResponseDTO response = UserService.getUser(nic);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("GET /api/v1/users");
        List<UserResponseDTO> users = UserService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{nic}/picture")
    public ResponseEntity<byte[]> getUserPicture(@PathVariable String nic) {
        log.info("GET /api/v1/users/{}/picture", nic);
        byte[] picture = UserService.getUserPicture(nic);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(picture);
    }
}
