
package ru.job4j.bmb.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class TgRemoteService extends TelegramLongPollingBot {

    private final String botName;
    private final String botToken;

    public TgRemoteService(@Value("${telegram.bot.name}") String botName,
                           @Value("${telegram.bot.token}") String botToken) {
        this.botName = botName;
        this.botToken = botToken;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Вы написали: " + messageText);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}