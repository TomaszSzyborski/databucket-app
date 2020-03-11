package pl.databucket.exception;

@SuppressWarnings("serial")
//TODO generify for other exceptions
public class AlreadyExistsException extends Exception {
    public AlreadyExistsException(AlreadyExistingItem item, String itemName) {
        super(String.format("%s %s already exists!", item.toString(), itemName));
    }

}
