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
     * Priority for API Key:
     * 1) Local app_config.properties (Working directory) - Best for professor's portability
     * 2) Computer's Environment Variable (OPENAI_API_KEY) - Secondary fallback
     */
    public AiConfig loadConfig() {
        Properties p = loadProperties();

        // Check the property file first (Self-contained approach)
        String apiKey = p.getProperty(KEY_API);

        // Fallback to environment variable only if the property file is blank
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = System.getenv("OPENAI_API_KEY");
        }

        // Apply defaults if still blank
        apiKey = (apiKey == null) ? "" : apiKey.trim();

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
     * Saves user-selected configuration to local file.
     * Ensures changes made in the GUI persist across restarts.
     */
    public void saveConfig(String apiKey, String chatModel, double temperature) {
        Properties p = loadProperties();

        p.setProperty(KEY_API, (apiKey == null) ? "" : apiKey.trim());
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

        // 1. Load defaults from inside the JAR/Resource folder
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("groupproject.apgroupproject/" + CONFIG_FILE)) {
            if (in != null) p.load(in);
        } catch (IOException ignored) {}

        // 2. Overwrite with user-saved config from the project root [cite: 214]
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

    private static double parseDouble(String v, double def) {
        try {
            return v == null ? def : Double.parseDouble(v.trim());
        } catch (Exception e) {
            return def;
        }
    }

    public AiConfig loadAiConfig() {
        return loadConfig();
    }
}