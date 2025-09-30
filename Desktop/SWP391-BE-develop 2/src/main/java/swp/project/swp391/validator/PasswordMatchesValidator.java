package swp.project.swp391.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import swp.project.swp391.request.auth.RegisterRequest;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        RegisterRequest user = (RegisterRequest) obj;
        if (user.getPassword() == null || user.getConfirmPassword() == null) {
            return false;
        }
        return user.getPassword().equals(user.getConfirmPassword());
    }
}