package br.com.alura.screenmatch.service;

import okhttp3.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConsultaGemini {

    private static final String MODEL = "models/gemini-1.5-flash-latest";
    private static final String URL = "https://generativelanguage.googleapis.com/v1beta/" + MODEL + ":generateContent";

    private static OkHttpClient client = new OkHttpClient();
    private static ObjectMapper mapper = new ObjectMapper();
    private static String apiKey;

    public ConsultaGemini() {
        this.apiKey = System.getenv("GEMINI_APIKEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("A variável de ambiente GEMINI_APIKEY não foi definida.");
        }
    }

    public static String obterTraducao(String texto) {
        try {
            // Monta o body seguindo o padrão da API v1beta
            String json = "{\n" +
                    "  \"contents\": [\n" +
                    "    {\n" +
                    "      \"parts\": [\n" +
                    "        { \"text\": \"Traduza para o português: " + texto + "\" }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            RequestBody body = RequestBody.create(
                    json,
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(URL + "?key=" + apiKey)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                return "Erro: " + response.code() + " - " + response.message();
            }

            String responseJson = response.body().string();
            JsonNode root = mapper.readTree(responseJson);

            // Extrai o texto traduzido do local real da resposta
            return root
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao processar tradução.";
        }
    }
}
