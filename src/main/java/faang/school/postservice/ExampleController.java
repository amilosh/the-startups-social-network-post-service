package faang.school.postservice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/example")
public class ExampleController {
    @GetMapping("/{id}")
    @Operation(summary = "Получить пример по ID", description = "Возвращает данные по переданному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно выполнено"),
            @ApiResponse(responseCode = "404", description = "Пример не найден")
    })
    public ResponseEntity<String> getExampleById(
            @Parameter(description = "ID примера для поиска") @PathVariable Long id,
            @Parameter(description = "Дополнительный параметр") @RequestParam(required = false) String param) {
        return ResponseEntity.ok("Пример с ID: " + id);
    }

    @PostMapping
    @Operation(summary = "Создать сущность", description = "Возвращает данные после создания")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно выполнено"),
            @ApiResponse(responseCode = "400", description = "Ошибка пользователя")
    })
    public ResponseEntity<String> create(@RequestBody Map<String, String> data) {
        return ResponseEntity.status(HttpStatus.CREATED).body("Successful created");
    }
}
