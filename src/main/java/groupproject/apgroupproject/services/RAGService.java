package groupproject.apgroupproject.services;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import groupproject.apgroupproject.models.AiConfig;

import java.util.List;

public class RAGService {

    // STATIC: Shared memory across all screens. Logic persists even if Controller is re-created.
    private static final EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
    private static final ConfigService configService = new ConfigService();

    private final Assistant assistant;

    public interface Assistant {
        @SystemMessage({
                "You are a helpful university student support assistant.",
                "The documents you read have a 'file_name' attached to them.",
                "Answer the user's question using ONLY the information provided.",
                "If you find the answer, end your response with exactly:",
                "'Reference: [insert file_name here]'",
                "Do not use brackets in the final output, just the filename.",
                "If the answer is not in the context, say: 'I'm sorry, I don't have information about that in my documents.'"
        })
        String chat(String userMessage);
    }

    public RAGService() {
        // Load latest config every time we init (in case settings changed)
        AiConfig config = configService.loadAiConfig();

        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(config.getApiKey())
                .modelName(config.getEmbeddingModel())
                .build();

        ChatLanguageModel chatModel = OpenAiChatModel.builder()
                .apiKey(config.getApiKey())
                .modelName(config.getChatModel())
                .temperature(config.getTemperature())
                .build();

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore) // Uses the STATIC store
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.6)
                .build();

        RetrievalAugmentor augmentor = DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .contentInjector(DefaultContentInjector.builder()
                        .metadataKeysToInclude(List.of("file_name"))
                        .build())
                .build();

        this.assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatModel)
                .retrievalAugmentor(augmentor)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    public String askQuestion(String question) {
        try {
            return assistant.chat(question);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // Static getters so IngestionService can access the SHARED memory
    public static EmbeddingStore<TextSegment> getEmbeddingStore() {
        return embeddingStore;
    }

    // Helper to get a model consistent with config
    public static EmbeddingModel createEmbeddingModel() {
        AiConfig config = configService.loadAiConfig();
        return OpenAiEmbeddingModel.builder()
                .apiKey(config.getApiKey())
                .modelName(config.getEmbeddingModel())
                .build();
    }
}