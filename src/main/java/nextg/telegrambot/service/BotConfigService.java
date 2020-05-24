package nextg.telegrambot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Загрузчик конфига.
 * Читает файл с конфигом и парсит его в мапу.
 */
@Component
public class BotConfigService {

    private String path = "src\\main\\resources\\bot_config.json";

    private String configAsString;

    private Map<String, String> configAsMap = new HashMap<>();

    public Map<String, String> getConfigAsMap() {
        return configAsMap;
    }

    public boolean loadConfig() {
        return readConfig() && parseConfig();
    }

    private boolean readConfig() {
        try {
            configAsString = Files.readString(Paths.get(path));
            System.out.println("Bot config is loaded");
            return true;
        } catch (IOException e) {
            System.out.println("Can't read config file");
            return false;
        }
    }

    private boolean parseConfig() {
        try {
            configAsMap = new ObjectMapper().readValue(configAsString, new TypeReference<Map<String,String>>(){});
            System.out.println("Bot config is parsed");
            return true;
        } catch (JsonProcessingException | IllegalArgumentException e) {
            System.out.println("Bot config parsing error: " + e.getLocalizedMessage());
            return false;
        }
    }
}
