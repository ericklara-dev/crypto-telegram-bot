package com.bot.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

public class CryptoAPI {
    private static final OkHttpClient client = new OkHttpClient();

    public static String getPrice(String coin) {
        try {
            String url = "https://api.coingecko.com/api/v3/simple/price?ids=" + coin + "&vs_currencies=usd";
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String jsonData = response.body().string();
                JSONObject json = new JSONObject(jsonData);
                if (!json.has(coin)) return "No encontré esa moneda.";
                double price = json.getJSONObject(coin).getDouble("usd");
                return "💰 " + coin.toUpperCase() + " → $" + price + " USD";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error obteniendo el precio.";
    }
}
