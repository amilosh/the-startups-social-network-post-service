package faang.school.postservice.validator.post.post_controller;

import faang.school.postservice.exception.FileProcessException;
import faang.school.postservice.properties.FileUploadProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class FileUploadValidator implements ConstraintValidator<ValidUploadFiles, MultipartFile[]> {

    private final Tika tika;
    private final FileUploadProperties fileProperties;

    @Override
    public boolean isValid(MultipartFile[] files, ConstraintValidatorContext context) {
        for (MultipartFile file: files) {
            try {
                String fileType = tika.detect(file.getInputStream());
                if (!isValidFile(fileType, file.getSize())) {
                    setResponseContext(context, file.getOriginalFilename(), "Invalid file type or exceeded size");
                    return false;
                }
            } catch (IOException e) {
                throw new FileProcessException("Exception occurred while processing file '%s': ".formatted(file.getOriginalFilename()));
            }
        }
        return true;
    }

    private boolean isValidFile(String fileType, long fileSize) {
        if (fileType != null) {
            String fileTypeGroup = fileType.split("/")[0];
            long typeMaxSize = fileProperties.getTypes().getOrDefault(fileTypeGroup, 0L);
            return typeMaxSize >= fileSize;
        }
        return false;
    }

    private void setResponseContext(ConstraintValidatorContext context, String fileName, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                "%s for file '%s'".formatted(message, fileName))
                .addConstraintViolation();
    }
}
