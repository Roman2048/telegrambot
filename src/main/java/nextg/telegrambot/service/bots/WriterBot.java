package nextg.telegrambot.service.bots;

import com.fasterxml.jackson.databind.JsonNode;
import nextg.telegrambot.domain.Annotation;
import nextg.telegrambot.domain.Update;
import nextg.telegrambot.exception.TokenNotFoundException;
import nextg.telegrambot.repository.AnnotationRepository;
import nextg.telegrambot.repository.UpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class WriterBot {

    @Value("${token}")
    String token;
    String botUrl = "https://api.telegram.org/bot";
    String methodName = "sendMessage?chat_id=";
    String proxyHostName = "51.38.71.101";
    int proxyPort = 8080;

    @Autowired
    AnnotationRepository annotationRepository;

    @Autowired
    UpdateRepository updateRepository;

    public void answer(Set<JsonNode> newUpdates) throws TokenNotFoundException {
        for (JsonNode jn : newUpdates) {
            Long update_id = jn.get("update_id").asLong();
            String userId = jn.get("message").get("from").get("id").toString();
            String userInput = jn.get("message").get("text").toString();
            HttpClient httpClient = HttpClient.newBuilder()
                    .proxy(ProxySelector.of(new InetSocketAddress(proxyHostName, proxyPort)))
                    .build();
            Iterable<Annotation> annotations = annotationRepository.findAll();
            String messageToUser = "Not+found";
            for (Annotation a : annotations) {
                if (a.getName().equalsIgnoreCase(userInput)) { messageToUser = a.getDescription(); }
            }
            if (token == null) { throw new TokenNotFoundException(); }
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Accept", "application/json")
                    .uri(URI.create(botUrl + token + "/" + methodName + userId + "&text=" + messageToUser))
                    .build();
            boolean responseDelivered = false;
            while(!responseDelivered) {
                String telegramResponse = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .completeOnTimeout("", 4, TimeUnit.SECONDS)
                        .join();
                if (!telegramResponse.equals("")) {
                    responseDelivered = true;
                    Optional<Update> optionalUpdate = updateRepository.findById(update_id);
                    Update update = optionalUpdate.orElseThrow();
                    update.setAnswered(true);
                    updateRepository.save(update);
                }
            }
        }
    }
}
