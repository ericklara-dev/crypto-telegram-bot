package com.bot.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.json.JSONArray;

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
                if (!json.has(coin)) return "❌ No encontré esa moneda.";
                double price = json.getJSONObject(coin).getDouble("usd");
                return "💰 " + coin.toUpperCase() + " → $" + String.format("%.2f", price) + " USD";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "❌ Error obteniendo el precio.";
    }

    public static String getPriceWithChange(String coin) {
        try {
            String url = "https://api.coingecko.com/api/v3/simple/price?ids=" + coin + "&vs_currencies=usd&include_24hr_change=true";
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String jsonData = response.body().string();
                JSONObject json = new JSONObject(jsonData);
                if (!json.has(coin)) return "❌ No encontré esa moneda.";

                JSONObject coinData = json.getJSONObject(coin);
                double price = coinData.getDouble("usd");
                double change24h = coinData.optDouble("usd_24h_change", 0.0);

                String changeEmoji = change24h >= 0 ? "📈" : "📉";
                String changeSign = change24h >= 0 ? "+" : "";

                return String.format("📊 %s → $%.2f USD | Cambio 24h: %s%s%.2f%%",
                    coin.toUpperCase(), price, changeEmoji, changeSign, change24h);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "❌ Error obteniendo el precio con cambio.";
    }

    public static String getTopCryptos() {
        try {
            String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=5&page=1&sparkline=false&price_change_percentage=24h";
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String jsonData = response.body().string();
                JSONArray coins = new JSONArray(jsonData);

                StringBuilder result = new StringBuilder("🏆 TOP 5 CRIPTOMONEDAS\n\n");

                for (int i = 0; i < coins.length(); i++) {
                    JSONObject coin = coins.getJSONObject(i);
                    String name = coin.getString("name");
                    String symbol = coin.getString("symbol").toUpperCase();
                    double price = coin.getDouble("current_price");
                    double change24h = coin.optDouble("price_change_percentage_24h", 0.0);

                    String emoji = getEmojiByRank(i + 1);
                    String changeEmoji = change24h >= 0 ? "📈" : "📉";
                    String changeSign = change24h >= 0 ? "+" : "";

                    result.append(String.format("%s %s (%s)\n💰 $%.2f USD | %s %s%.2f%%\n\n",
                        emoji, name, symbol, price, changeEmoji, changeSign, change24h));
                }

                return result.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "❌ Error obteniendo el top de criptomonedas.";
    }

    public static String getCryptoNews() {
        try {

            String url = "https://cryptonews-api.com/api/v1/category?section=general&items=5&page=1&token=demo";
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String jsonData = response.body().string();
                JSONObject json = new JSONObject(jsonData);

                if (!json.has("data") || json.getJSONArray("data").length() == 0) {
                    return "❌ No hay noticias disponibles en este momento.";
                }

                JSONArray newsArray = json.getJSONArray("data");
                StringBuilder result = new StringBuilder("📰 **ÚLTIMAS NOTICIAS CRYPTO**\n\n");

                for (int i = 0; i < Math.min(5, newsArray.length()); i++) {
                    JSONObject news = newsArray.getJSONObject(i);
                    String title = news.getString("title");
                    String source = news.getString("source_name");

                    String newsEmoji = getNewsEmoji(i);
                    result.append(String.format("%s **%s**\n💡 Fuente: %s\n\n",
                        newsEmoji, title, source));
                }

                result.append("🔗 *Datos proporcionados por Crypto News API*");
                return result.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "❌ Error obteniendo noticias crypto. Intenta más tarde.";
    }

    private static String getNewsEmoji(int index) {
        String[] emojis = {"🚀", "💎", "⚡", "🔥", "🌟"};
        return emojis[index % emojis.length];
    }

    private static String getEmojiByRank(int rank) {
        switch (rank) {
            case 1: return "🥇";
            case 2: return "🥈";
            case 3: return "🥉";
            case 4: return "4️⃣";
            case 5: return "5️⃣";
            default: return "🔸";
        }
    }
}
