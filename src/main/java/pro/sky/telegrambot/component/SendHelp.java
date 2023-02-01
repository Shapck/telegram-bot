package pro.sky.telegrambot.component;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class SendHelp {

    private final TelegramBot telegramBot;
    private Logger LOG = LoggerFactory.getLogger(SendHelp.class);

    public SendHelp(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void sendMessage(Long chatId, String text, @Nullable ParseMode parseMode) {
        SendMessage sendMessage = new SendMessage(chatId,text);
        if(parseMode != null) {
            sendMessage.parseMode(parseMode);
        }
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()) {
            LOG.error(sendResponse.toString());
        }
    }

    public void sendMessage(Long chatId, String text) {
        sendMessage(chatId,text, null);
    }
}
