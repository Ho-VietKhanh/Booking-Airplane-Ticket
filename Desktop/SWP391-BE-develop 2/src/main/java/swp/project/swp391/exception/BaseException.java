package swp.project.swp391.exception;

import lombok.Getter;
import swp.project.swp391.constant.ErrorHandler;

@Getter
public class BaseException extends RuntimeException {

    private final ErrorHandler errorHandler;

    public BaseException(ErrorHandler errorHandler) {
        super(errorHandler.getMessage());
        this.errorHandler = errorHandler;
    }
}