package net.kravuar.recipes;

import net.kravuar.recipes.domain.exceptions.BusinessException;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Configuration
@ControllerAdvice
class WebConfig {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleDomainException(BusinessException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
