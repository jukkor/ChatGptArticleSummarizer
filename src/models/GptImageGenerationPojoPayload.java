package models;

public class GptImageGenerationPojoPayload {
    private final int n = 1;
    private final String size = "256x256";
    private final String model = "dall-e-2";
    private String prompt = "";

    public GptImageGenerationPojoPayload(String articleTitle) {
        this.prompt = prompt + articleTitle;
    }
}
