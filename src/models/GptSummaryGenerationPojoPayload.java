package models;

import java.util.List;

public class GptSummaryGenerationPojoPayload {
    private final String model = "gpt-3.5-turbo";
    private final List<Message> messages;

    public GptSummaryGenerationPojoPayload(List<Message> messages) {
        this.messages = messages;
    }
}
