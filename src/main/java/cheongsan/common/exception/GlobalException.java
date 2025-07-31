package cheongsan.common.exception;

import cheongsan.common.constant.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseDTO> handleIllegalStateException(IllegalStateException e) {
        ResponseDTO errorResponse = new ResponseDTO(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDTO> handleIllegalArgumentException(IllegalArgumentException e) {
        ResponseDTO errorResponse = new ResponseDTO(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDTO> handleRuntimeException(RuntimeException e) {
        ResponseDTO errorResponse = new ResponseDTO(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleException(Exception e) {
        ResponseDTO errorResponse = new ResponseDTO(ResponseMessage.INTERNAL_SERVER_ERROR.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
