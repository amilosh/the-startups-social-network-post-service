package faang.school.postservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
public class MyAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("Exception in async context. Method: {}. Params: {} Message: {}. StackTrace: {}",
                method.getName(), params, ex.getMessage(), Arrays.toString(ex.getStackTrace()));
    }
}
