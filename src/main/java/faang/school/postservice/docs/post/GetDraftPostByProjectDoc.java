package faang.school.postservice.docs.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Get drafts by project id", description = "Returns deleted post")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful"),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                        value = "Project not found"
                )
        )),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                        value = "Project not found"
                )
        ))
})
public @interface GetDraftPostByProjectDoc {}
