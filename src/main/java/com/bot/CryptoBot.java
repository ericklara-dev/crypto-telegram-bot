package com.bot;

import com.bot.api.CryptoAPI;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CryptoBot extends TelegramLongPollingBot {

    private final CryptoAPI cryptoAPI = new CryptoAPI();

    @Override
    public void onUpdateReceived(Update update) {
        // Solo procesamos mensajes de texto
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.startsWith("/start")) {
                sendWelcomeMessage(chatId);
            } else if (messageText.startsWith("/crypto")) {
                processCryptoCommand(messageText, chatId);
            }
        }
    }

    private void sendWelcomeMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("¡Bienvenido al Bot de Criptomonedas! 🚀\n\n" +
                "Utiliza el comando /crypto seguido del nombre de una criptomoneda para consultar su precio.\n\n" +
                "Por ejemplo: /crypto bitcoin");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void processCryptoCommand(String command, long chatId) {
        String[] parts = command.split(" ", 2);

        if (parts.length < 2) {
            sendMessage(chatId, "Por favor, especifica una criptomoneda. Ejemplo: /crypto bitcoin");
            return;
        }

        String coin = parts[1].trim().toLowerCase();
        String price = cryptoAPI.getPrice(coin);

        sendMessage(chatId, price);
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        // Reemplaza esto con el nombre de usuario de tu bot
        return "MiCryptoBotUsername";
    }

    @Override
    public String getBotToken() {
        // Reemplaza esto con el token de tu bot
        return "TU_TOKEN_AQUI";
    }
}
