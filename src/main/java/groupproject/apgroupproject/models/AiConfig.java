package groupproject.apgroupproject.models;

public class AiConfig {
    private final String apiKey;
    private final String chatModel;
    private final String embeddingModel;
    private final double temperature;

    public AiConfig(String apiKey, String chatModel, String embeddingModel, double temperature) {
        this.apiKey = apiKey;
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
        this.temperature = temperature;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getChatModel() {
        return chatModel;
    }

    public String getEmbeddingModel() {
        return embeddingModel;
    }

    public double getTemperature() {
        return temperature;
    }
}
