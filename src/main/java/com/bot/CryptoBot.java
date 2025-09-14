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
                sendText(chatId, "¡Hola! Soy CryptoBot. Usa /crypto <moneda>, /change <moneda>, /top, o /help para más info.");
            } else if (msg.startsWith("/crypto")) {
                String[] parts = msg.split(" ");
                if (parts.length < 2) {
                    sendText(chatId, "Uso: /crypto <moneda>");
                } else {
                    String coin = parts[1].toLowerCase();
                    String result = CryptoAPI.getPrice(coin);
                    sendText(chatId, result);
                }
            } else if (msg.startsWith("/change")) {
                String[] parts = msg.split(" ");
                if (parts.length < 2) {
                    sendText(chatId, "Uso: /change <moneda>\nEjemplo: /change bitcoin");
                } else {
                    String coin = parts[1].toLowerCase();
                    String result = CryptoAPI.getPriceWithChange(coin);
                    sendText(chatId, result);
                }
            } else if (msg.startsWith("/top")) {
                String result = CryptoAPI.getTopCryptos();
                sendText(chatId, result);
            } else if (msg.startsWith("/help")) {
                String helpMessage = "📖 AYUDA - CRYPTOBOT\n\n" +
                        "🔹 /crypto <moneda> - Precio actual\n" +
                        "   Ejemplo: /crypto bitcoin\n\n" +
                        "🔹 /change <moneda> - Precio con variación 24h\n" +
                        "   Ejemplo: /change bitcoin\n" +
                        "   Salida: 📊 BTC → $65,000 USD | Cambio 24h: 📈+2.4%\n\n" +
                        "🔹 /top - Top 5 criptomonedas\n" +
                        "   Muestra las 5 principales con precios y cambios\n\n" +
                        "🔹 /help - Mostrar esta ayuda\n\n" +
                        "💡 Monedas populares:\n" +
                        "bitcoin, ethereum, cardano, solana, dogecoin";
                sendText(chatId, helpMessage);
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
