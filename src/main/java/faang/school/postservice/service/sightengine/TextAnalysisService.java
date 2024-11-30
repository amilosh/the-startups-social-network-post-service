package faang.school.postservice.service.sightengine;

import faang.school.postservice.dto.sightengine.textAnalysis.TextAnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class TextAnalysisService {
    private final WebClient webClient;

    @Value("${api.content-analysis-service.key}")
    private String apiKey;

    @Value("${api.content-analysis-service.secret}")
    private String apiSecret;

    @Retryable(
            retryFor = {ConnectException.class, TimeoutException.class, WebClientRequestException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 1.5)
    )
    @CircuitBreaker(maxAttempts = 5, openTimeout = 10000, resetTimeout = 20000)
    public Mono<TextAnalysisResponse> analyzeText(String text) {
        MultiValueMap<String, String> requestBody = buildRequestBody(text);
        return webClient.post()
                .uri("/1.0/text/check.json")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(TextAnalysisResponse.class);
    }

    private MultiValueMap<String, String> buildRequestBody(String text) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("text", text);
        body.add("lang", "ru,en");
        body.add("models", "general");
        body.add("mode", "ml");
        body.add("api-key", apiKey);
        body.add("api-secret", apiSecret);
        return body;
    }
}
