package groupproject.apgroupproject.services;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import groupproject.apgroupproject.models.AiConfig;

public class OpenAiModelProvider implements AiModelProvider {

    private final ChatLanguageModel chatModel;
    private final EmbeddingModel embeddingModel;

    public OpenAiModelProvider(AiConfig config) {

        this.chatModel = OpenAiChatModel.builder()
                .apiKey(config.getApiKey())
                .modelName(config.getChatModel())
                .temperature(config.getTemperature())
                .build();

        this.embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(config.getApiKey())
                .modelName(config.getEmbeddingModel())
                .build();
    }

    @Override
    public ChatLanguageModel chatModel() {
        return chatModel;
    }

    @Override
    public EmbeddingModel embeddingModel() {
        return embeddingModel;
    }
}
