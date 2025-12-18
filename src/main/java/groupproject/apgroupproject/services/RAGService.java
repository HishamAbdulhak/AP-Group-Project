package groupproject.apgroupproject.services;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

public class RAGService {

    public static class RagService {


        private final EmbeddingStore<TextSegment> embeddingStore;
        private final EmbeddingModel embeddingModel;


        private final Assistant assistant;


        public interface Assistant {
            String chat(String userMessage);
        }

        public RagService() {

            this.embeddingStore = new InMemoryEmbeddingStore<>();


            this.embeddingModel = OpenAiEmbeddingModel.builder()
                    .apiKey("demo")
                    .modelName("text-embedding-3-small")
                    .build();

            ChatLanguageModel chatModel = OpenAiChatModel.builder()
                    .apiKey("demo")
                    .modelName("gpt-3.5-turbo")
                    .build();

            ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(embeddingModel)
                    .maxResults(3) // Number of chunks to retrieve
                    .minScore(0.7) // Similarity threshold
                    .build();

            this.assistant = AiServices.builder(Assistant.class)
                    .chatLanguageModel(chatModel)
                    .contentRetriever(contentRetriever)
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                    .build();
        }


        public String askQuestion(String question) {
            try {
                return assistant.chat(question);
            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }
        }


        public EmbeddingStore<TextSegment> getEmbeddingStore() {
            return embeddingStore;
        }

        public EmbeddingModel getEmbeddingModel() {
            return embeddingModel;
        }
    }
}



