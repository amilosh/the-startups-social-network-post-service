package faang.school.postservice.validator.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.dto.resource.ResourceType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;


public class ResourceFileSizeValidator implements ConstraintValidator<ValidResourceFileSize, ResourceDto> {

    private long defaultMaxSizeInBytes;
    private ResourceType[] applicableTypes;

    @Override
    public void initialize(ValidResourceFileSize constraintAnnotation) {
        this.defaultMaxSizeInBytes = constraintAnnotation.defaultMaxSizeInBytes();
        this.applicableTypes = constraintAnnotation.applicableTypes();
    }

    @Override
    public boolean isValid(ResourceDto resourceDto, ConstraintValidatorContext context) {
        if (resourceDto == null || resourceDto.getFile() == null || resourceDto.getType() == null) {
            return true;
        }
        if (!isApplicableType(resourceDto.getType())) {
            return true;
        }

        MultipartFile file = resourceDto.getFile();
        ResourceType type = resourceDto.getType();

        long maxSizeInBytes = getMaxSizeForResourceType(type);
        return file.getSize() < maxSizeInBytes;
    }

    private boolean isApplicableType(ResourceType type) {
        for (ResourceType applicableType : applicableTypes) {
            if (applicableType == type) {
                return true;
            }
        }
        return false;
    }

    private long getMaxSizeForResourceType(ResourceType type) {
        switch (type) {
            case IMAGE:
                return 5 * 1024 * 1024;
            default:
                return defaultMaxSizeInBytes;
        }
    }
}
