package com.bot;

import com.bot.api.CryptoAPI;
import com.bot.config.BotConfig;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class CryptoBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return BotConfig.USERNAME;
    }

    @Override
    public String getBotToken() {
        return BotConfig.TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String msg = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            if (msg.startsWith("/start")) {
                sendText(chatId, "¡Hola! Soy CryptoBot. Usa /crypto <moneda>, ejemplo: /crypto bitcoin");
            } else if (msg.startsWith("/crypto")) {
                String[] parts = msg.split(" ");
                if (parts.length < 2) {
                    sendText(chatId, "Uso: /crypto <moneda>");
                } else {
                    String coin = parts[1].toLowerCase();
                    String result = CryptoAPI.getPrice(coin);
                    sendText(chatId, result);
                }
            }
        }
    }

    private void sendText(String chatId, String text) {
        try {
            execute(new SendMessage(chatId, text));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new CryptoBot());
        System.out.println("CryptoBot está online!");
    }
}
