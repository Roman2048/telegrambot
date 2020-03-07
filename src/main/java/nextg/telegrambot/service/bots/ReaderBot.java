package nextg.telegrambot.service.bots;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nextg.telegrambot.domain.Update;
import nextg.telegrambot.exception.ConnectionTimeOutException;
import nextg.telegrambot.exception.TokenNotFoundException;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ReaderBot extends AbstractBot {

    private String methodName = "getUpdates";

    public Set<JsonNode> update() throws ConnectionTimeOutException, JsonProcessingException, TokenNotFoundException {
        HttpClient httpClient = getHttpClient();
        HttpRequest httpRequest = getHttpRequest();
        String response = doRequest(getHttpClient(), getHttpRequest());
        if (response.equals("")) { throw new ConnectionTimeOutException(); }
        List<JsonNode> updates = getJsonNodes(response);
        return saveToDb(updates);
    }

    private Set<JsonNode> saveToDb(List<JsonNode> updates) {
        Set<JsonNode> newUpdates = new HashSet<>();
        for (JsonNode jn : updates) {
            Long id = jn.get("update_id").asLong();
            if (!updateRepository.existsById(id)) {
                String content = jn.toString();
                String userId = jn.get("message").get("from").get("id").toString();
                updateRepository.save(new Update(id, content, userId));
                newUpdates.add(jn);
            }
        }
        return newUpdates;
    }

    private List<JsonNode> getJsonNodes(String response) throws JsonProcessingException {
        JsonNode jsonNode = new ObjectMapper().readTree(response);
        return jsonNode.findParents("update_id");
    }

    private HttpRequest getHttpRequest() throws TokenNotFoundException {
        if (token == null) { throw new TokenNotFoundException(); }
        return HttpRequest.newBuilder()
                    .header("Accept", "application/json")
                    .uri(URI.create(botUrl + token + "/" + methodName))
                    .build();
    }
}
