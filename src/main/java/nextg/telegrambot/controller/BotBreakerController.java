package nextg.telegrambot.controller;

import nextg.telegrambot.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/breaker")
public class BotBreakerController {

    @Autowired
    BotService botService;

    @GetMapping("/0")
    public void off() {
        botService.setActive(false);
    }

    @GetMapping("/1")
    public void on() {
        botService.setActive(true);
    }
}
