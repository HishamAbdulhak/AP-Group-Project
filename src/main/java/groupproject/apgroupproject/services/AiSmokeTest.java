package groupproject.apgroupproject.services;

import dev.langchain4j.model.chat.ChatLanguageModel;
import groupproject.apgroupproject.models.AiConfig;

public class AiSmokeTest {

    public static void main(String[] args) {
        AiConfig config = new ConfigService().loadAiConfig();
        AiModelProvider provider = new OpenAiModelProvider(config);

        ChatLanguageModel chat = provider.chatModel();
        String reply = chat.generate("Reply with exactly: AI OK");

        System.out.println("AI reply: " + reply);
    }
}
