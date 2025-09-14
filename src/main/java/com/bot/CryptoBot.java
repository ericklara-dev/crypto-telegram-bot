package com.bot;

import com.bot.api.CryptoAPI;
import com.bot.config.BotConfig;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class CryptoBot extends TelegramLongPollingBot {

    // Set para almacenar usuarios que ya han iniciado el bot
    private Set<Long> activeUsers = new HashSet<>();

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
            long chatId = update.getMessage().getChatId();

            // Manejo del primer contacto con el bot - mostrar mensaje de bienvenida
            if (!activeUsers.contains(chatId)) {
                sendWelcomeMessage(chatId);
                return;
            }

            // Comandos habilitados después de activar el bot
            if (msg.startsWith("/start")) {
                sendText(chatId, "✅ ¡BitBeeperBot ya está activo! Usa /crypto <moneda>, /change <moneda>, /top, o /help para más info.");
            } else if (msg.startsWith("/crypto")) {
                String[] parts = msg.split(" ");
                if (parts.length < 2) {
                    sendText(chatId, "❌ Uso correcto: /crypto <moneda>\n\nEjemplo: /crypto bitcoin");
                } else {
                    String coin = parts[1].toLowerCase();
                    String result = CryptoAPI.getPrice(coin);
                    sendText(chatId, result);
                }
            } else if (msg.startsWith("/change")) {
                String[] parts = msg.split(" ");
                if (parts.length < 2) {
                    sendText(chatId, "❌ Uso correcto: /change <moneda>\n\nEjemplo: /change bitcoin");
                } else {
                    String coin = parts[1].toLowerCase();
                    String result = CryptoAPI.getPriceWithChange(coin);
                    sendText(chatId, result);
                }
            } else if (msg.startsWith("/top")) {
                String result = CryptoAPI.getTopCryptos();
                sendText(chatId, result);
            } else if (msg.startsWith("/help")) {
                String helpMessage = "📖 **AYUDA - BITBEEPERBOT**\n\n" +
                        "🔹 `/crypto <moneda>` - Precio actual\n" +
                        "   Ejemplo: `/crypto bitcoin`\n\n" +
                        "🔹 `/change <moneda>` - Precio con variación 24h\n" +
                        "   Ejemplo: `/change bitcoin`\n" +
                        "   Salida: 📊 BTC → $65,000 USD | Cambio 24h: 📈+2.4%\n\n" +
                        "🔹 `/top` - Top 5 criptomonedas\n" +
                        "   Muestra las 5 principales con precios y cambios\n\n" +
                        "🔹 `/help` - Mostrar esta ayuda\n\n" +
                        "💡 **Monedas populares:**\n" +
                        "`bitcoin`, `ethereum`, `cardano`, `solana`, `dogecoin`";
                sendTextWithMarkdown(chatId, helpMessage);
            } else {
                sendText(chatId, "❓ Comando no reconocido. Usa /help para ver todos los comandos disponibles.");
            }
        }

        // Manejo de botones inline (callbacks)
        else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals("start_bot")) {
                activeUsers.add(chatId);
                sendStartMessage(chatId);

                // Responder al callback para quitar el loading
                try {
                    execute(new org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery(
                        update.getCallbackQuery().getId()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendWelcomeMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setParseMode("Markdown");

        String welcomeText = "🤖 **¡Bienvenido a BitBeeperBot!** 🚀\n\n" +
                "Tu asistente personal para el mundo de las **criptomonedas** 📈\n\n" +
                "🔹 **¿Qué puedo hacer por ti?**\n" +
                "• 💰 Consultar precios en tiempo real\n" +
                "• 📊 Ver variaciones de 24 horas\n" +
                "• 🏆 Mostrar el TOP 5 de criptomonedas\n" +
                "• 📖 Ayudarte con comandos y ejemplos\n\n" +
                "🌟 **Datos actualizados desde CoinGecko**\n" +
                "⚡ **Respuestas instantáneas**\n" +
                "🔒 **100% gratuito y seguro**\n\n" +
                "👇 **Haz clic en 'Iniciar' para comenzar a explorar el mundo crypto!**";

        message.setText(welcomeText);

        // Crear botón inline "Iniciar"
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton startButton = new InlineKeyboardButton();
        startButton.setText("🚀 Iniciar BitBeeperBot");
        startButton.setCallbackData("start_bot");
        row.add(startButton);
        rows.add(row);

        keyboard.setKeyboard(rows);
        message.setReplyMarkup(keyboard);

        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendStartMessage(long chatId) {
        String startText = "✅ **¡BitBeeperBot activado!** 🎉\n\n" +
                "🚀 **Ahora puedes usar todos los comandos:**\n\n" +
                "💰 `/crypto bitcoin` - Precio actual\n" +
                "📊 `/change ethereum` - Precio con cambio 24h\n" +
                "🏆 `/top` - Top 5 criptomonedas\n" +
                "📖 `/help` - Lista completa de comandos\n\n" +
                "💡 **¡Comienza escribiendo cualquier comando!**\n" +
                "🌟 Ejemplo: `/crypto bitcoin`";

        sendTextWithMarkdown(chatId, startText);
    }

    private void sendText(long chatId, String text) {
        try {
            execute(new SendMessage(String.valueOf(chatId), text));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendTextWithMarkdown(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setParseMode("Markdown");

        try {
            execute(message);
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
