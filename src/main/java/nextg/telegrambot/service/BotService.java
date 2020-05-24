package nextg.telegrambot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import nextg.telegrambot.exception.ConnectionTimeOutException;
import nextg.telegrambot.exception.TokenNotFoundException;
import nextg.telegrambot.service.bots.ReaderBot;
import nextg.telegrambot.service.bots.WriterBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.lang.Thread.sleep;

@Component
public class BotService {

    private final ReaderBot readerBot;

    private final WriterBot writerBot;

    private final BotConfigService botConfigService;

    private boolean isActive;

    private int updateRate = 1000;

    public BotService(ReaderBot readerBot, WriterBot writerBot, BotConfigService botConfigService) {
        this.readerBot = readerBot;
        this.writerBot = writerBot;
        this.botConfigService = botConfigService;
    }

    @EventListener(ApplicationReadyEvent.class)
    private void runUpdateCycle() {
        if (botConfigService.loadConfig()) {
            setActive(true);
        } else {
            System.out.println("Can't load Bot config. Bot not started. Try to reload config manually");
        }
        setActive(true);
        while (isActive) {
            try { sleep(updateRate); } catch (InterruptedException e) { e.printStackTrace(); }
            if (isActive) {
                try {
                    Set<JsonNode> newUpdates = readerBot.update();
                    System.out.print("1");
                    if (!newUpdates.isEmpty()) { writerBot.answer(newUpdates); }
                } catch (TokenNotFoundException | ConnectionTimeOutException | JsonProcessingException e) {
                    System.out.print("0");
                }
            }
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        if (active) {
            System.out.println("Bot status changed to: ONLINE");
        } else {
            System.out.println("Bot status changed to: OFFLINE");
        }

    }
}
