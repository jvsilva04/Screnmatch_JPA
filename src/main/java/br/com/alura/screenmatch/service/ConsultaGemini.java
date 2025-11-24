package br.com.alura.screenmatch.service;
import com.google.cloud.ai.generativelanguage.v1beta.Content;
import com.google.cloud.ai.generativelanguage.v1beta.GenerateContentResponse;
import com.google.cloud.ai.generativelanguage.v1beta.GenerativeServiceSettings;
import com.google.cloud.ai.generativelanguage.v1beta.GenerativeServiceClient;
import com.google.cloud.ai.generativelanguage.v1beta.Part;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ApiClientHeaderProvider;

import java.io.IOException;

public class ConsultaGemini {
    public String obterTraducao(String texto) {
        // Pega a chave da API da variável de ambiente que você já configurou.
        String apiKey = System.getenv("GEMINI_APIKEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("Erro: A variável de ambiente GEMINI_APIKEY não foi definida.");
        }

        String modelName = "models/gemini-1.5-flash-latest"; // O nome do modelo tem um formato diferente

        try {
            // Configura o cabeçalho da API para incluir a chave
            ApiClientHeaderProvider headerProvider = ApiClientHeaderProvider.newBuilder()
                    .setResourceHeaderKey("x-goog-api-key")
                    .setResourceHeaderValue(apiKey)
                    .build();

            // Configura as definições do serviço para usar o cabeçalho de autenticação
            GenerativeServiceSettings settings = GenerativeServiceSettings.newBuilder()
                    .setTransportChannelProvider(
                            GenerativeServiceSettings.defaultHttpJsonTransportProviderBuilder().build())
                    .setHeaderProvider(headerProvider)
                    .build();

            // Usa um bloco try-with-resources para garantir que o cliente seja fechado
            try (GenerativeServiceClient client = GenerativeServiceClient.create(settings)) {

                // Monta o prompt para o modelo
                Content content = Content.newBuilder()
                        .addParts(Part.newBuilder().setText("traduza para o português o texto: " + texto))
                        .build();

                // Envia a requisição para a API
                GenerateContentResponse response = client.generateContent(modelName, content);

                // Extrai e retorna o texto da resposta
                return response.getCandidates(0).getContent().getParts(0).getText();
            }
        } catch (IOException e) {
            System.err.println("Ocorreu um erro de I/O ao se comunicar com a API do Gemini: " + e.getMessage());
            e.printStackTrace();
            return "Erro ao processar a tradução.";
        }
    }
}