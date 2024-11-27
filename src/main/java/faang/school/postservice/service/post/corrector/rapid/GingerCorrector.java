package faang.school.postservice.service.post.corrector.rapid;

import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Component
public class GingerCorrector {

    public List<Post> correct(List<Post> posts) throws IOException, InterruptedException {
        //Ginger - проверка грамматики на базе искусственного интеллекта

        for (Post post : posts) {
            String textToCorrect = post.getContent();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://ginger4.p.rapidapi.com/correction?lang=US&generateRecommendations=false&flagInfomralLanguage=true"))
                    .header("x-rapidapi-key", "f08e509406msh0f69a660309f6bfp1a2c3bjsndccae6eb8803")
                    .header("x-rapidapi-host", "ginger4.p.rapidapi.com")
                    .header("Content-Type", "text/plain")
                    .header("Accept-Encoding", "identity")
                    .method("POST", HttpRequest.BodyPublishers.ofString(textToCorrect))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            post.setContent(response.body());
        }

        return posts;
    }

}
