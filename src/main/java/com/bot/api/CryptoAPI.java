package com.bot.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class CryptoAPI {

    private static final String COINGECKO_API_URL = "https://api.coingecko.com/api/v3/simple/price";
    private final OkHttpClient client;

    public CryptoAPI() {
        // Configurar cliente HTTP con timeouts
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Obtiene el precio actual de una criptomoneda en USD
     * @param coinId El ID de la criptomoneda en CoinGecko (ej. bitcoin, ethereum, etc.)
     * @return Un mensaje formateado con el precio o un mensaje de error
     */
    public String getPrice(String coinId) {
        try {
            // Construir la URL con los parámetros
            String url = COINGECKO_API_URL + "?ids=" + coinId + "&vs_currencies=usd";

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return "Error al consultar el precio: " + response.code();
                }

                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);

                // Verificar si la moneda existe
                if (json.length() == 0) {
                    return "No se encontró información para la criptomoneda: " + coinId;
                }

                // Obtener el precio en USD
                double price = json.getJSONObject(coinId).getDouble("usd");
                DecimalFormat df = new DecimalFormat("#,##0.00");

                return "💰 " + capitalizeFirstLetter(coinId) + " (USD):\n" +
                       "💵 $" + df.format(price);

            } catch (IOException e) {
                e.printStackTrace();
                return "Error de conexión: " + e.getMessage();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return "Error al procesar la respuesta: " + e.getMessage();
        }
    }

    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
