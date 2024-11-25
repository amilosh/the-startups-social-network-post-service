package faang.school.postservice.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerExpiredAdRemover {

    @Scheduled(cron = "${schedule.cron.deleteExpiredAds}")
    public void deleteExpiredAds() {

    }
}
