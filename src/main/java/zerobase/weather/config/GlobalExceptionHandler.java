package zerobase.weather.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//ExceptionHandler는 Controller 혹은 RestController에서 예외 상황이 발생했을 때 예외를 받아서 처리한다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //서버에서 에러가 발생했다는 것을 클라이언트에 알림
    @ExceptionHandler(Exception.class)
    public Exception handlerAllException() {
        System.out.println("error from GlobalExceptionHandler");
        return new Exception();
    }
}
