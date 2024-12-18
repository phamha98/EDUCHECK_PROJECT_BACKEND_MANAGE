package do_an.backend_educheck.configs;

import do_an.backend_educheck.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(ResourceNotFoundException ex) {
        // handle exception
        return new ResponseEntity<>(ex.getMessage() + " không tồn tại hoặc mật khẩu sai!",
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ResourceFoundException.class)
    public ResponseEntity<String> handleFoundException(ResourceFoundException ex) {
        // handle exception
        return new ResponseEntity<>("Tài khoản đã tồn tại: " + ex.getMessage(), HttpStatus.FOUND);
    }

    @ExceptionHandler(value = ArgumentException.class)
    public ResponseEntity<String> handleArgumentException(ArgumentException ex) {
        return new ResponseEntity<>("Có lỗi xảy ra: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = EditProfileException.class)
    public ResponseEntity<String> handleEditProfileException(EditProfileException ex) {
        return new ResponseEntity<>("Thông tin của bạn không chính xác vui lòng thử lại!", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = ValidateException.class)
    public ResponseEntity<String> handleEditProfileException(ValidateException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
