package lk.ijse.eca.userservice.exception;

public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException(String nic) {
        super("User with NIC '" + nic + "' already exists");
    }
}
