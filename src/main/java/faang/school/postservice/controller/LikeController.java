package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
@Tag(name = "Контроллер для управления лайками")
public class LikeController {
    private final LikeService likeService;

    @Operation(summary = "Получение лайка по id")
    @GetMapping("/{id}")
    public LikeDto getLikeById(@PathVariable long id) {
        return likeService.getLikeById(id);
    }

    @Operation(summary = "Получение всех лайков")
    @GetMapping
    public List<LikeDto> getAllLikes() {
        return likeService.getAllLikes();
    }

    @Operation(summary = "Добавление лайка")
    @PostMapping
    public LikeDto addLike(@RequestBody LikeDto likeDto) {
        return likeService.addLike(likeDto);
    }

    @Operation(summary = "Удаление лайка")
    @DeleteMapping
    public void deleteLike(@RequestBody @Valid LikeDto likeDto) {
        likeService.deleteLike(likeDto);
    }
}
