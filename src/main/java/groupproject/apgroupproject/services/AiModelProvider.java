package groupproject.apgroupproject.services;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;

public interface AiModelProvider {

    ChatLanguageModel chatModel();

    EmbeddingModel embeddingModel();
}
