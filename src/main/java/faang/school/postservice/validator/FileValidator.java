package faang.school.postservice.validator;

import faang.school.postservice.exeption.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class FileValidator {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final int MAX_WIDTH = 1080;
    private static final int MAX_HEIGHT = 1080;

    public void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            log.warn("File size exceeds the maximum limit of 5 MB");
            throw new DataValidationException("File size exceeds the maximum limit of 5 MB");
        }

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image != null) {
                if (image.getWidth() > MAX_WIDTH || image.getHeight() > MAX_HEIGHT) {
                    log.warn("Image resolution exceeds the maximum limit");
                    throw new DataValidationException("Image resolution exceeds the maximum limit");
                }
            }
        } catch (IOException e) {
            log.error("Error reading image file", e);
            throw new RuntimeException("Error reading image file", e);
        }
    }

    public void validateNumberOfFiles(List<MultipartFile> files, long numberOfFiles) {
        if (files.size() > numberOfFiles) {
            log.warn("Cannot upload more than {} files", numberOfFiles);
            throw new DataValidationException(
                    String.format("Cannot upload more than %d files", numberOfFiles));
        }
    }
}
