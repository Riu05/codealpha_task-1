package com.riya.chatbot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.Properties;

public class GeminiClient {
    private static final String API_KEY;

    static {
        String key = null;
        try (InputStream input = GeminiClient.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("⚠️ config.properties not found!");
            } else {
                prop.load(input);
                key = prop.getProperty("GEMINI_API_KEY");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        API_KEY = key;
    }

    public static String getGeminiReply(String userMessage) {
        if (API_KEY == null || API_KEY.isEmpty()) {
            return "❌ API Key is missing! Please check config.properties";
        }

        try {
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String payload = "{ \"contents\": [ { \"parts\": [ { \"text\": \"" + userMessage + "\" } ] } ] }";

            OutputStream os = connection.getOutputStream();
            os.write(payload.getBytes());
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            BufferedReader br;

            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                System.err.println("Gemini API Error - Code: " + responseCode);
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            if (responseCode != 200) {
                return "⚠️ Gemini API Error:\n" + response.toString();
            }

            // ✅ Parse the JSON using Gson
            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray candidates = jsonObject.getAsJsonArray("candidates");
            if (candidates != null && candidates.size() > 0) {
                JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                JsonObject content = firstCandidate.getAsJsonObject("content");
                JsonArray parts = content.getAsJsonArray("parts");
                if (parts != null && parts.size() > 0) {
                    return parts.get(0).getAsJsonObject().get("text").getAsString();
                }
            }

            return "Gemini replied but I couldn’t read the response properly.";

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error talking to Gemini: " + e.getMessage();
        }
    }
}
