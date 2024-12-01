package faang.school.postservice.service.counter;

import faang.school.postservice.annotations.SendUserActionToCounter;
import faang.school.postservice.service.counter.enumeration.UserAction;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

@Aspect
@Component
public class UserActionCounterReceiver {
    private final Map<UserAction, UserActionCounter> counters;
    private final Executor executor;

    public UserActionCounterReceiver(List<UserActionCounter> counters, Executor kafkaPublisherExecutor) {
        this.counters = counters.stream()
                .collect(Collectors.toMap(UserActionCounter::getUserAction, Function.identity()));
        this.executor = kafkaPublisherExecutor;
    }

    @Async("kafkaPublisherExecutor")
    @AfterReturning(
            pointcut = "@annotation(sendUserActionToCounter)",
            returning = "returnValue")
    public void publishEvent(Object returnValue, SendUserActionToCounter sendUserActionToCounter) {
        executor.execute(() -> executeAppropriateCounter(returnValue, sendUserActionToCounter));
    }

    private void executeAppropriateCounter(Object returnValue, SendUserActionToCounter sendUserActionToCounter) {
        UserActionCounter counter = counters.get(sendUserActionToCounter.userAction());
        counter.executeCounting(returnValue, sendUserActionToCounter);
    }
}
