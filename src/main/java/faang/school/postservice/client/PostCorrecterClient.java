package faang.school.postservice.client;

import faang.school.postservice.dto.textGears.TextGearsRequest;
import faang.school.postservice.dto.textGears.TextGearsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "post-correcter", url = "${side-api.text-gears.base-url}")
public interface PostCorrecterClient {
    @PostMapping("${side-api.text-gears.auto-correct}")
    TextGearsResponse checkPost(@RequestBody TextGearsRequest request);
}
