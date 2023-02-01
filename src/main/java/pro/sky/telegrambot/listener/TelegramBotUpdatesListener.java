package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.component.SendHelp;
import pro.sky.telegrambot.service.NotificationService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private Logger LOG = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private static final Pattern PATTERN = Pattern.compile("([0-9.:\\s]{16})\\s([\\W+]+)");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final TelegramBot telegramBot;
    private final NotificationService notificationService;
    private SendHelp sendHelp;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationService notificationService,SendHelp sendHelp) {
        this.telegramBot = telegramBot;
        this.notificationService = notificationService;
        this.sendHelp = sendHelp;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(update -> {
                logger.info("Processing update: {}", update);
                String text = update.message().text();
                Long chatId = update.message().chat().id();
                if ("/start".equals(text)) {
                    sendHelp.sendMessage(chatId, "Для палнирования задачи отправьте её в формате: \n*01.01.2023 20:00 Сделать домашнюю работу*", ParseMode.Markdown);
                } else {
                    Matcher matcher = PATTERN.matcher(text);
                    LocalDateTime dateTime;
                    if (matcher.find() && (dateTime = parse(matcher.group(1))) != null) {
                        String message = matcher.group(2);
                        notificationService.create(chatId,message,dateTime);
                        sendHelp.sendMessage(chatId,"Задача запланирована!");
                    } else {
                        sendHelp.sendMessage(chatId, "Некорректный формат сообщения");
                    }
                }
            });
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }


    @Nullable
    private LocalDateTime parse(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

}
