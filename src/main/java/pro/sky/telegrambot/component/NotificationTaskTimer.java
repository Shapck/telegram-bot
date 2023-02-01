package pro.sky.telegrambot.component;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.repository.NotificationRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
@Component
public class NotificationTaskTimer {
    private final NotificationRepository notificationRepository;
    private final SendHelp sendHelp;

    public NotificationTaskTimer(NotificationRepository notificationRepository,SendHelp sendHelp) {
        this.notificationRepository = notificationRepository;
        this.sendHelp = sendHelp;
    }
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void task() {
        notificationRepository.findAllByNotificationDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
        ).forEach(notificationTask -> {
            sendHelp.sendMessage(notificationTask.getUserId(),notificationTask.getMessage());
            notificationRepository.delete(notificationTask);
        });

    }
}
