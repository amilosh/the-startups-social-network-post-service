package faang.school.postservice.validator;

import faang.school.postservice.exception.InputValidationException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;

@Component
public class InputCommentControllerValidator {
    public void validate(BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            Map<String, String> map = new HashMap<>();
            bindingResult.getFieldErrors().forEach(
                    error -> {
                        map.put(error.getField(), error.getDefaultMessage());
                    }
            );
            throw new InputValidationException(map);
        }
    }
}
