package nextg.telegrambot.service.bots;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nextg.telegrambot.domain.Annotation;
import nextg.telegrambot.domain.Update;
import nextg.telegrambot.exception.TokenNotFoundException;
import nextg.telegrambot.repository.AnnotationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Optional;
import java.util.Set;

@Component
public class WriterBot extends AbstractBot {

    private String methodName = "sendMessage?chat_id=";

    @Autowired
    private AnnotationRepository annotationRepository;

    public void answer(Set<JsonNode> newUpdates) throws TokenNotFoundException {
        for (JsonNode currentNode : newUpdates) {
            Long update_id = currentNode.get("update_id").asLong();
            String userId = currentNode.get("message").get("from").get("id").toString();
            String userInput = currentNode.get("message").get("text").toString().replaceAll("\"", "");
            HttpClient httpClient = getHttpClient();
            String messageToUser = findAnnotationInDb(userInput);
            HttpRequest httpRequest = getHttpRequest(userId, messageToUser.replaceAll(" ", "+"));
            sendMessage(update_id, httpClient, httpRequest);
        }
    }

    private void sendMessage(Long update_id, HttpClient httpClient, HttpRequest httpRequest) {
        boolean responseStatus = false;
        while(!responseStatus) {
            String telegramResponse = doRequest(httpClient, httpRequest);
            if (telegramResponse == null) {
                telegramResponse = "";
            }
            try {
                responseStatus = new ObjectMapper().readTree(telegramResponse).get("ok").asBoolean();
            } catch (JsonProcessingException e) {
                continue;
            }
            if (responseStatus && !telegramResponse.equals("")) {
                Optional<Update> optionalUpdate = updateRepository.findById(update_id);
                Update update = optionalUpdate.orElseThrow();
                update.setAnswered(true);
                updateRepository.save(update);
            }
        }
    }

    private String findAnnotationInDb(String userInput) {
        Iterable<Annotation> annotations = annotationRepository.findAll();
        String messageToUser = "Not found";
        for (Annotation a : annotations) {
            if (a.getName().equalsIgnoreCase(userInput)) { messageToUser = a.getDescription(); }
        }
        return messageToUser;
    }

    private HttpRequest getHttpRequest(String userId, String messageToUser) throws TokenNotFoundException {
        if (token == null) { throw new TokenNotFoundException(); }
        return HttpRequest.newBuilder()
                .header("Accept", "application/json")
                .uri(URI.create(botUrl + token + "/" + methodName + userId + "&text=" + messageToUser))
                .build();
    }
}
