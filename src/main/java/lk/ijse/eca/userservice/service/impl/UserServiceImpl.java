package lk.ijse.eca.userservice.service.impl;

import lk.ijse.eca.userservice.dto.UserRequestDTO;
import lk.ijse.eca.userservice.dto.UserResponseDTO;
import lk.ijse.eca.userservice.entity.User;
import lk.ijse.eca.userservice.mapper.UserMapper;
import lk.ijse.eca.userservice.exception.DuplicateUserException;
import lk.ijse.eca.userservice.exception.FileOperationException;
import lk.ijse.eca.userservice.exception.UserNotFoundException;
import lk.ijse.eca.userservice.repository.UserRepository;
import lk.ijse.eca.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository UserRepository;
    private final UserMapper UserMapper;

    @Value("${app.storage.path}")
    private String storagePathStr;

    private Path storagePath;

    /**
     * Creates a new user.
     *
     * Transaction strategy:
     *  1. Persist user record to DB (JPA defers the INSERT until flush/commit).
     *  2. Write picture file to disk (immediate).
     *  3. If the file write fails an exception is thrown, which causes
     *     @Transactional to roll back the DB INSERT — no orphaned record.
     *  4. If the file write succeeds the method returns normally and
     *     @Transactional commits both the record and the file atomically.
     */
    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto) {
        log.debug("Creating user with NIC: {}", dto.getNic());

        if (UserRepository.existsById(dto.getNic())) {
            log.warn("Duplicate NIC detected: {}", dto.getNic());
            throw new DuplicateUserException(dto.getNic());
        }

        String pictureId = UUID.randomUUID().toString();

        User user = UserMapper.toEntity(dto);
        user.setPicture(pictureId);

        // DB operation first (deferred) — rolls back if file save below throws
        UserRepository.save(user);
        log.debug("User persisted to DB: {}", dto.getNic());

        // Immediate file operation — failure triggers @Transactional rollback
        savePicture(pictureId, dto.getPicture());

        log.info("User created successfully: {}", dto.getNic());
        return UserMapper.toResponseDto(user);
    }

    /**
     * Updates an existing user.
     *
     * Transaction strategy:
     *  - If a new picture is supplied:
     *    1. Update DB record with new picture UUID (deferred).
     *    2. Write the new picture file (immediate).
     *    3. Failure at step 2 rolls back step 1 — old picture UUID stays in DB.
     *    4. On success, the old picture file is deleted (best-effort: a warning is
     *       logged on failure, but the transaction is NOT rolled back because DB and
     *       new file are already consistent).
     *  - If no new picture is supplied, only DB fields are updated.
     */
    @Override
    @Transactional
    public UserResponseDTO updateUser(String nic, UserRequestDTO dto) {
        log.debug("Updating user with NIC: {}", nic);

        User user = UserRepository.findById(nic)
                .orElseThrow(() -> {
                    log.warn("User not found for update: {}", nic);
                    return new UserNotFoundException(nic);
                });

        String oldPictureId = user.getPicture();
        boolean pictureChanged = dto.getPicture() != null && !dto.getPicture().isEmpty();
        String newPictureId = pictureChanged ? UUID.randomUUID().toString() : oldPictureId;

        UserMapper.updateEntity(dto, user);
        user.setPicture(newPictureId);

        // DB update (deferred) — rolls back if new file save below throws
        UserRepository.save(user);
        log.debug("User updated in DB: {}", nic);

        if (pictureChanged) {
            // Save new picture — failure triggers @Transactional rollback
            savePicture(newPictureId, dto.getPicture());
            // Remove old picture — best-effort; DB and new file are already consistent
            tryDeletePicture(oldPictureId);
        }

        log.info("User updated successfully: {}", nic);
        return UserMapper.toResponseDto(user);
    }

    /**
     * Deletes a user.
     *
     * Transaction strategy:
     *  1. Remove user record from DB (JPA defers the DELETE until flush/commit).
     *  2. Delete picture file from disk (immediate).
     *  3. If the file delete fails an exception is thrown, which causes
     *     @Transactional to roll back the DB DELETE — neither the record
     *     nor the file is removed.
     *  4. If the file delete succeeds the method returns normally and
     *     @Transactional commits, removing the record from the DB.
     */
    @Override
    @Transactional
    public void deleteUser(String nic) {
        log.debug("Deleting user with NIC: {}", nic);

        User user = UserRepository.findById(nic)
                .orElseThrow(() -> {
                    log.warn("User not found for deletion: {}", nic);
                    return new UserNotFoundException(nic);
                });

        String pictureId = user.getPicture();

        // DB deletion (deferred) — rolls back if file delete below throws
        UserRepository.delete(user);
        log.debug("User marked for deletion in DB: {}", nic);

        // Immediate file deletion — failure triggers @Transactional rollback
        deletePicture(pictureId);

        log.info("User deleted successfully: {}", nic);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUser(String nic) {
        log.debug("Fetching user with NIC: {}", nic);
        return UserRepository.findById(nic)
                .map(UserMapper::toResponseDto)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", nic);
                    return new UserNotFoundException(nic);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        log.debug("Fetching all users");
        List<UserResponseDTO> users = UserRepository.findAll()
                .stream()
                .map(UserMapper::toResponseDto)
                .collect(Collectors.toList());
        log.debug("Fetched {} users", users.size());
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getUserPicture(String nic) {
        log.debug("Fetching picture for user NIC: {}", nic);
        User user = UserRepository.findById(nic)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", nic);
                    return new UserNotFoundException(nic);
                });
        Path filePath = storagePath().resolve(user.getPicture());
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Failed to read picture for user: {}", nic, e);
            throw new FileOperationException("Failed to read picture for user: " + nic, e);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Path storagePath() {
        if (storagePath == null) {
            storagePath = Paths.get(storagePathStr);
        }
        try {
            Files.createDirectories(storagePath);
        } catch (IOException e) {
            throw new FileOperationException(
                    "Failed to create storage directory: " + storagePath.toAbsolutePath(), e);
        }
        return storagePath;
    }

    private void savePicture(String pictureId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileOperationException("Picture file must not be empty");
        }
        Path filePath = storagePath().resolve(pictureId);
        try {
            Files.write(filePath, file.getBytes());
            log.debug("Picture saved: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to save picture: {}", filePath, e);
            throw new FileOperationException("Failed to save picture file: " + pictureId, e);
        }
    }

    private void deletePicture(String pictureId) {
        Path filePath = storagePath().resolve(pictureId);
        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.debug("Picture deleted: {}", filePath);
            } else {
                log.warn("Picture file not found on disk (already removed?): {}", filePath);
            }
        } catch (IOException e) {
            log.error("Failed to delete picture: {}", filePath, e);
            throw new FileOperationException("Failed to delete picture file: " + pictureId, e);
        }
    }

    private void tryDeletePicture(String pictureId) {
        try {
            deletePicture(pictureId);
        } catch (FileOperationException e) {
            log.warn("Could not delete old picture file '{}'. Manual cleanup may be required.", pictureId);
        }
    }

}
