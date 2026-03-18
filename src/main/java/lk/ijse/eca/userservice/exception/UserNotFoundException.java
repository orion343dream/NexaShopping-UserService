package lk.ijse.eca.userservice.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String nic) {
        super("User with NIC '" + nic + "' not found");
    }
}
