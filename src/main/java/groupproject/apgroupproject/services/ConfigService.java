package groupproject.apgroupproject.services;

import groupproject.apgroupproject.models.AiConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ConfigService {

    private static final String CONFIG_FILE = "app_config.properties";

    private static final String KEY_API = "openai.apiKey";
    private static final String KEY_CHAT_MODEL = "openai.chatModel";
    private static final String KEY_EMBED_MODEL = "openai.embeddingModel";
    private static final String KEY_TEMP = "openai.temperature";

    private static final String DEFAULT_CHAT_MODEL = "gpt-4o-mini";
    private static final String DEFAULT_EMBED_MODEL = "text-embedding-3-small";
    private static final double DEFAULT_TEMP = 0.2;

    /**
     * Loads AI configuration.
     * Priority:
     * 1) Local saved config file (working directory)
     * 2) Resource defaults
     * 3) Environment variable (API key only)
     */
    public AiConfig loadConfig() {
        Properties p = loadProperties();

        String apiKey = firstNonEmpty(
                p.getProperty(KEY_API),
                System.getenv("OPENAI_API_KEY")
        );

        String chatModel = defaultIfBlank(
                p.getProperty(KEY_CHAT_MODEL),
                DEFAULT_CHAT_MODEL
        );

        String embedModel = defaultIfBlank(
                p.getProperty(KEY_EMBED_MODEL),
                DEFAULT_EMBED_MODEL
        );

        double temp = parseDouble(
                p.getProperty(KEY_TEMP),
                DEFAULT_TEMP
        );

        return new AiConfig(apiKey, chatModel, embedModel, temp);
    }

    /**
      Saves user-selected configuration to local file.
     */
    public void saveConfig(String apiKey, String chatModel, double temperature) {
        Properties p = loadProperties();

        p.setProperty(KEY_API, safe(apiKey));
        p.setProperty(KEY_CHAT_MODEL, defaultIfBlank(chatModel, DEFAULT_CHAT_MODEL));
        p.setProperty(KEY_TEMP, String.valueOf(temperature));

        if (!p.containsKey(KEY_EMBED_MODEL)) {
            p.setProperty(KEY_EMBED_MODEL, DEFAULT_EMBED_MODEL);
        }

        storeProperties(p);
    }

    /* -------------------- helpers -------------------- */

    private Properties loadProperties() {
        Properties p = new Properties();

        // Load defaults from resources
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("groupproject.apgroupproject/" + CONFIG_FILE)) {
            if (in != null) p.load(in);
        } catch (IOException ignored) {}

        // Load user-saved config from working directory
        Path local = Path.of(CONFIG_FILE);
        if (Files.exists(local)) {
            try (InputStream in = Files.newInputStream(local)) {
                p.load(in);
            } catch (IOException ignored) {}
        }

        return p;
    }

    private void storeProperties(Properties p) {
        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
            p.store(out, "Application Configuration");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config file", e);
        }
    }

    private static String defaultIfBlank(String v, String def) {
        return (v == null || v.isBlank()) ? def : v.trim();
    }

    private static String firstNonEmpty(String a, String b) {
        if (a != null && !a.isBlank()) return a.trim();
        if (b != null && !b.isBlank()) return b.trim();
        return "";
    }

    private static String safe(String v) {
        return v == null ? "" : v.trim();
    }

    private static double parseDouble(String v, double def) {
        try {
            return v == null ? def : Double.parseDouble(v.trim());
        } catch (Exception e) {
            return def;
        }
    }

    // Backward compatibility (in case other code still calls this)
    public AiConfig loadAiConfig() {
        return loadConfig();
    }
}
