package pl.databucket.exception;


@SuppressWarnings("serial")
public class ClassAlreadyExistsException extends AlreadyExistsException {

	public ClassAlreadyExistsException(String className) {
        super(AlreadyExistingItem.CLASS , className);
    }

}
