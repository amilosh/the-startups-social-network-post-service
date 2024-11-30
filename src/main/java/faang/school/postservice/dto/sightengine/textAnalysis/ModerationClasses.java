package faang.school.postservice.dto.sightengine.textAnalysis;

import lombok.Getter;

import java.util.List;

@Getter
public class ModerationClasses {
    private List<String> available;
    private double sexual;
    private double discriminatory;
    private double insulting;
    private double violent;
    private double toxic;
}
