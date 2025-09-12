package com.bot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class App {
    public static void main(String[] args) {
        try {
            // Inicializar API de Telegram
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            // Registrar nuestro bot
            botsApi.registerBot(new CryptoBot());

            System.out.println("Bot iniciado correctamente!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
