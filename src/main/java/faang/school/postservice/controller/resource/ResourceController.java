//package faang.school.postservice.controller.resource;
//
//import faang.school.postservice.config.context.UserContext;
//import faang.school.postservice.dto.resource.ResourceDto;
//import faang.school.postservice.exception.DataValidationException;
//import faang.school.postservice.service.resource.ResourceService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotNull;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.InputStream;
//
//@Validated
//@RestController
//@RequiredArgsConstructor
//@Slf4j
//@RequestMapping("api/v1/resources")
//@Tag(name = "Resource Controller", description = "Controller for managing resources")
//@ApiResponse(responseCode = "200", description = "Resource successfully updated")
//@ApiResponse(responseCode = "201", description = "Resource successfully added")
//@ApiResponse(responseCode = "204", description = "Resource successfully deleted")
//@ApiResponse(responseCode = "400", description = "Invalid input data")
//@ApiResponse(responseCode = "404", description = "Resource not found")
//@ApiResponse(responseCode = "500", description = "Server Error")
//public class ResourceController {
//
//    private final ResourceService resourceService;
//    private final UserContext userContext;
//
//    @Operation(
//            summary = "Download resource",
//            description = "Download resource by id"
//    )
//    @GetMapping(path = "/{resourceId}", produces = "application/octet-stream")
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseEntity<InputStream> get(
//            @PathVariable
//            @NotNull(message = "Resource id should not be null") Long resourceId) {
//        InputStream resource = resourceService.get(resourceId);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.IMAGE_JPEG);
//        return ResponseEntity.ok()
//                .headers(headers)
//                .body(resource);
//    }
//
//    @Operation(
//            summary = "Delete Resource",
//            description = "Delete resource for the authenticated user"
//    )
//    @DeleteMapping("/{resourceId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public ResponseEntity<Void> delete(
//            @PathVariable
//            @NotNull(message = "Resource id should not be null") Long resourceId) {
//        resourceService.delete(resourceId, userContext.getUserId());
//        return ResponseEntity.noContent().build();
//    }
//
//    @Operation(
//            summary = "Update Resource",
//            description = "Update resource for the authenticated user"
//    )
//    @PutMapping("/{resourceId}")
//    @ResponseStatus(HttpStatus.OK)
//    public ResourceDto update(
//            @PathVariable
//            @NotNull(message = "resource id should not be null") Long resourceId,
//            @RequestParam("file") @Valid MultipartFile file) {
//        validateFile(file);
//        return resourceService.update(resourceId, userContext.getUserId());
//    }
//
//    @Operation(
//            summary = "Add Resource",
//            description = "Add resource by authenticated user"
//    )
//    @PostMapping()
//    @ResponseStatus(HttpStatus.CREATED)
//    public ResourceDto add(@RequestParam("file") @Valid MultipartFile file) {
//        validateFile(file);
//        return resourceService.add(file, userContext.getUserId());
//    }
//
//    private void validateFile(MultipartFile file) {
//        if (file.isEmpty()) {
//            throw new DataValidationException("Uploaded file is empty.");
//        }
//    }
//}