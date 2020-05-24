package nextg.telegrambot.service.bots;

import nextg.telegrambot.repository.UpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

public abstract class AbstractBot {

    @Value("${token}")
    protected String token;

    protected String botUrl = "https://api.telegram.org/bot";
    protected String proxyHostName = "51.38.71.101";
    protected int proxyPort = 8080;

    @Autowired
    protected UpdateRepository updateRepository;

    protected HttpClient getHttpClient() {
        return HttpClient.newBuilder()
              //  .proxy(ProxySelector.of(InetSocketAddress.createUnresolved(proxyHostName, proxyPort)))
                .build();
    }

    protected String doRequest(HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .completeOnTimeout("", 4, TimeUnit.SECONDS)
                .join();
    }
}
