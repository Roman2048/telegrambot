package nextg.telegrambot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import nextg.telegrambot.exception.ConnectionTimeOutException;
import nextg.telegrambot.exception.TokenNotFoundException;
import nextg.telegrambot.service.bots.DefaultBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BotService {

    @Autowired
    DefaultBot defaultBot;

    @Scheduled(fixedRate = 5000)
    public void runUpdateCycle() {
        try {
            Integer numberOfUpdated = defaultBot.update();
            System.out.print("+ ");
            if (numberOfUpdated != 0) {
                System.out.println("\n" + numberOfUpdated + " record(s) added");
            }
        } catch (TokenNotFoundException t) {
            System.out.print("- token error");
        } catch (ConnectionTimeOutException connectionTimeOutException) {
            System.out.print("- ");
        } catch (JsonProcessingException e) {
            System.out.print("- json error");
        }
    }
}
