package services;

import com.google.gson.*;
import models.GptImageGenerationPojoPayload;
import models.GptSummaryGenerationPojoPayload;
import models.Message;
import services.exceptions.HttpRequestException;
import services.exceptions.ResponseParsingException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ChatGptQuerier {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String apiKey = System.getenv("OPENAI_API_KEY");

    public String generateArticleSummary(String articleText) {
        String summaryGenerationRequestJsonPayload = createJsonPayloadForSummary(articleText);
        System.out.println("Summary request JSON payload: " + summaryGenerationRequestJsonPayload);

        String summaryGenerationRequestUrl = "https://api.openai.com/v1/chat/completions";
        HttpRequest summaryRequest = createHttpRequest(summaryGenerationRequestUrl, summaryGenerationRequestJsonPayload);
        HttpResponse<String> response = getResponse(summaryRequest);
        String generatedArticleSummary = parseSummaryResponse(response);

        return generatedArticleSummary;
    }

    public String generateDalleImage(String articleTitle) {
        String imageGenerationRequestJsonPayload = createJsonPayloadForImageGeneration(articleTitle);
        System.out.println("Image request JSON payload: " + imageGenerationRequestJsonPayload);

        String imageGenerationRequestUrl = "https://api.openai.com/v1/images/generations";
        HttpRequest imageGenerationRequest = createHttpRequest(imageGenerationRequestUrl, imageGenerationRequestJsonPayload);
        HttpResponse<String> response = getResponse(imageGenerationRequest);
        String generatedImageUrl = parseImageGenerationResponse(response);

        return generatedImageUrl;
    }


    private static String createJsonPayloadForSummary(String articleText) {
        Message messageForSystem = new Message(
                "system",
                "You are to summarize the following without any pretext or post-text. Return only the summary."
        );
        Message messageFromUser = new Message(
                "user",
                articleText
        );
        GptSummaryGenerationPojoPayload PojoPayload = new GptSummaryGenerationPojoPayload(
                List.of(messageForSystem, messageFromUser)
        );
        Gson gson = new Gson();
        String summaryRequestJsonPayload = gson.toJson(PojoPayload);
        return summaryRequestJsonPayload;
    }

    private static String createJsonPayloadForImageGeneration(String articleTitle) {
        GptImageGenerationPojoPayload imagePojoPayload = new GptImageGenerationPojoPayload(articleTitle);
        Gson gson = new Gson();
        String imageGenerationRequestJsonPayload = gson.toJson(imagePojoPayload);
        return imageGenerationRequestJsonPayload;
    }

    private HttpRequest createHttpRequest(String url, String jsonPayload) {
        HttpRequest request = HttpRequest.newBuilder()
                                      .uri(URI.create(url))
                                      .header("Content-Type", "application/json")
                                      .header("Authorization", "Bearer %s".formatted(apiKey))
                                      .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                                      .build();
        return request;
    }

    private HttpResponse<String> getResponse(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Request made:");
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            if (response.statusCode() >= 400)
                throw new HttpRequestException("HTTP Request failed with status code " + response.statusCode(), null);
            return response;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get response to HTTP Request", e);
        }
    }

    private String parseSummaryResponse(HttpResponse<String> response) {
        try {
            if (response == null || response.body() == null)
                throw new ResponseParsingException("Response or Response Body is null", null);
            JsonObject responseInJson = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray choicesArray = responseInJson.getAsJsonArray("choices");
            JsonObject message = choicesArray.get(0).getAsJsonObject().getAsJsonObject("message");
            String content = message.get("content").getAsString();
            return content;
        } catch (JsonParseException | NullPointerException | IndexOutOfBoundsException e) {
            throw new RuntimeException("Error parsing summary response", e);
        }
    }

    private String parseImageGenerationResponse(HttpResponse<String> response) {
        try {
            if (response == null || response.body() == null)
                throw new ResponseParsingException("Response or Response Body is null", null);
            JsonObject responseInJson = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray dataArray = responseInJson.getAsJsonArray("data");
            JsonObject firstElement = dataArray.get(0).getAsJsonObject();
            String imageUrl = firstElement.get("url").getAsString();
            return imageUrl;
        } catch (JsonParseException | NullPointerException | IndexOutOfBoundsException e) {
            throw new RuntimeException("Error parsing image generation response", e);
        }
    }
}
