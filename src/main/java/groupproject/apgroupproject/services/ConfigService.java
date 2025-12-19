package groupproject.apgroupproject.services;

import groupproject.apgroupproject.models.AiConfig;

public class ConfigService {
    private static final String DEFAULT_CHAT_MODEL = "gpt-4o-mini";
    private static final String DEFAULT_EMBEDDING_MODEL = "text-embedding-3-small";
    private static final double DEFAULT_TEMPERATURE = 0.2;

    public AiConfig loadAiConfig() {
        String apiKey = "OPEN_API_KEY";

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "OPENAI_API_KEY environment variable is not set."
            );
        }

        return new AiConfig(
                apiKey,
                DEFAULT_CHAT_MODEL,
                DEFAULT_EMBEDDING_MODEL,
                DEFAULT_TEMPERATURE
        );
    }
}
