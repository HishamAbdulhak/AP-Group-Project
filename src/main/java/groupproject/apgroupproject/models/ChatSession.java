package groupproject.apgroupproject.models;

import java.util.ArrayList;
import java.util.List;

public class ChatSession {

    // A simple structure to hold message data
    public static class ChatMessage {
        public String text;
        public boolean isUser;

        public ChatMessage(String text, boolean isUser) {
            this.text = text;
            this.isUser = isUser;
        }
    }

    // Static list = Global Memory
    private static final List<ChatMessage> history = new ArrayList<>();

    public static void addMessage(String text, boolean isUser) {
        history.add(new ChatMessage(text, isUser));
    }

    public static List<ChatMessage> getHistory() {
        return history;
    }

    public static void clear() {
        history.clear();
    }
}