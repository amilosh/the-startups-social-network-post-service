package faang.school.postservice.docs.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Get post by hashtag", description = "Returns posts")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful"),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(
                        value = "Hashtag is not valid"
                )
        )),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(
                        value = "Post not found"
                )
        ))
})
public @interface GetPostByHashtagDoc { }
