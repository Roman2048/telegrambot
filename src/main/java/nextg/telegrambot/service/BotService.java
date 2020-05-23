package nextg.telegrambot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import nextg.telegrambot.exception.ConnectionTimeOutException;
import nextg.telegrambot.exception.TokenNotFoundException;
import nextg.telegrambot.service.bots.ReaderBot;
import nextg.telegrambot.service.bots.WriterBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class BotService {

    @Autowired
    ReaderBot readerBot;

    @Autowired
    WriterBot writerBot;

    @Scheduled(fixedRate = 1000)
    private void runUpdateCycle() {
        try {
            Set<JsonNode> newUpdates = readerBot.update();
            if (!newUpdates.isEmpty()) { writerBot.answer(newUpdates); }
        } catch (TokenNotFoundException | ConnectionTimeOutException | JsonProcessingException e) {
            e.getLocalizedMessage();
        }
    }
}
