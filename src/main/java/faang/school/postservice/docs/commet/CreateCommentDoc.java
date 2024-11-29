package faang.school.postservice.docs.commet;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Create comment", description = "Returns created comment")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful"),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                        value = "Comment author id must be the same as user id"
                ))
        ),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                        value = "User or post not found"
                )
        ))
})
public @interface CreateCommentDoc {}
