package asiptsou.modsen.task.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalDefaultExceptionHandler extends ResponseEntityExceptionHandler {

  public static final String PATH = "path";
  public static final String STATUS = "status";

  @Override
  @NonNull
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatus status,
      WebRequest request) {
    Map<String, String> errors = new LinkedHashMap<>();

    String path = request.getDescription(false);

    errors.put(PATH, path);
    errors.put(STATUS, status.getReasonPhrase());

    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    return ResponseEntity.status(status).body(errors);
  }

  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<String> handleNotFoundException(NoSuchElementException exception) {
    return ResponseEntity.status(NOT_FOUND).body(exception.getMessage());
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
    return ResponseEntity.status(BAD_REQUEST).body(exception.getMessage());
  }
}
