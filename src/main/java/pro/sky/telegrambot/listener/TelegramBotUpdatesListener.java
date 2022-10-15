package pro.sky.telegrambot.listener;

import NotificationTask.NotificationTask;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            // Process your updates here
            if (Objects.equals(update.message().text(), "/start")) {
                SendMessage message = new SendMessage(update.message().chat().id(), "привет тебе от бота!");
                SendResponse response = telegramBot.execute(message);
            }

            Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");

            Matcher matcher = pattern.matcher(update.message().text());
            if (!matcher.matches() && !Objects.equals(update.message().text(), "/start"))  {
                // обрабатываем ситуацию, когда строка соответствует паттерну
                SendMessage message = new SendMessage(update.message().chat().id(), "запрос не соответвует формату");
                SendResponse response = telegramBot.execute(message);
            } else {
                NotificationTask notificationTask = new NotificationTask();
                String date = matcher.group(1);
                String item = matcher.group(3);

                notificationTask.setText(item);
                notificationTask.setDateTime(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                notificationTask.setChatId(update.message().chat().id());
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }





}
