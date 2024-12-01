package faang.school.postservice.service.counter;

import faang.school.postservice.annotations.SendUserActionToCounter;
import faang.school.postservice.service.counter.enumeration.UserAction;

public interface UserActionCounter {
    UserAction getUserAction();

    void executeCounting(Object returnValue, SendUserActionToCounter sendUserActionToCounter);
}
