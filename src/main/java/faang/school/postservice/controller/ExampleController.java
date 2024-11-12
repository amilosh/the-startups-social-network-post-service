package faang.school.postservice.controller;

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
    @Operation(summary = "Getting data by ID", description = "Returns data by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<String> getExampleById(
            @Parameter(description = "ID") @PathVariable Long id,
            @Parameter(description = "Param") @RequestParam(required = false) String param) {
        return ResponseEntity.ok("Example with ID: " + id);
    }

    @PostMapping
    @Operation(summary = "Creating data", description = "Returns data by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<String> create(@RequestBody Map<String, String> data) {
        return ResponseEntity.status(HttpStatus.CREATED).body("Successful created");
    }
}
