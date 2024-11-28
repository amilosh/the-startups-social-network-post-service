package faang.school.postservice.client;

import faang.school.postservice.model.dto.UserDto;
import faang.school.postservice.redis.model.dto.AuthorRedisDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "user-service",
        url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/users/maxId")
    Long getMaxUserId();

    @GetMapping("/users/range")
    List<UserDto> getUsersByIdRange(@RequestParam("first") long first, @RequestParam("last") long last);

    @GetMapping("/api/v1/subscribe/allfollowing/{followerId}")
    List<AuthorRedisDto> getAllFollowing(@PathVariable String followerId);
}
