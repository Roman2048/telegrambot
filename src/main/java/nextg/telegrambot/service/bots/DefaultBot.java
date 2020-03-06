package nextg.telegrambot.service.bots;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nextg.telegrambot.domain.Update;
import nextg.telegrambot.exception.ConnectionTimeOut;
import nextg.telegrambot.exception.TokenNotFoundException;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class DefaultBot {

    @Value("${token}")
    String token;
    String botUrl = "https://api.telegram.org/bot";
    String methodName = "getUpdates";
    String proxyHostName = "51.38.71.101";
    int proxyPort = 8080;

    @Autowired
    UpdateRepository updateRepository;

    public Integer update() throws ConnectionTimeOut, JsonProcessingException, TokenNotFoundException {
        HttpClient client = getHttpClient();
        HttpRequest request = getHttpRequest();
        String response = doRequest(getHttpClient(), getHttpRequest());
        if (response.equals("")) { throw new ConnectionTimeOut(); }
        List<JsonNode> updates = getJsonNodes(response);
        return saveToDb(updates);
    }

    private int saveToDb(List<JsonNode> updates) {
        int counterOfUpdates = 0;
        for (JsonNode jn : updates) {
            Long id = jn.get("update_id").asLong();
            if (!updateRepository.existsById(id)) {
                String content = jn.toString();
                String userId = jn.get("message").get("from").get("id").toString();
                updateRepository.save(new Update(id, content, userId));
                counterOfUpdates++;
            }
        }
        return counterOfUpdates;
    }

    private List<JsonNode> getJsonNodes(String response) throws JsonProcessingException {
        JsonNode jsonNode = new ObjectMapper().readTree(response);
        return jsonNode.findParents("update_id");
    }

    private String doRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .completeOnTimeout("", 4, TimeUnit.SECONDS)
                    .join();
    }

    private HttpRequest getHttpRequest() throws TokenNotFoundException {
        if (token == null) { throw new TokenNotFoundException(); }
        return HttpRequest.newBuilder()
                    .header("Accept", "application/json")
                    .uri(URI.create(botUrl + token + "/" + methodName))
                    .build();
    }

    private HttpClient getHttpClient() {
        return HttpClient.newBuilder()
                    .proxy(ProxySelector.of(new InetSocketAddress(proxyHostName, proxyPort)))
                    .build();
    }
}
