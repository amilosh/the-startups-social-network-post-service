package faang.school.postservice.dto.sightengine.textAnalysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TextAnalysisResponse {
    private String status;
    private Request request;
    @JsonProperty("moderation_classes")
    private ModerationClasses moderation_classes;
}
