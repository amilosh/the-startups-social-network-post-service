package faang.school.postservice.service.redis;

import org.springframework.stereotype.Component;

@Component
public class RedisReceiver {

    public void receiveMessage(String message) {
        System.out.println("Got message: " + message);
    }

}
