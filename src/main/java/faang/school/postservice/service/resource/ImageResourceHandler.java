package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceType;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageResourceHandler implements ResourceHandler {
    private final S3Service s3Service;
    private final static ResourceType type = ResourceType.IMAGE;

    private static final int MAX_HORIZONTAL_WIDTH = 1080;
    private static final int MAX_HORIZONTAL_HEIGHT = 566;
    private static final int MAX_SQUARE_SIZE = 1080;

    @Override
    public Resource addResource(MultipartFile file, String folder) {
        log.info("File with the name {} processed by Image handler", file.getOriginalFilename());
        MultipartFile processedFile = processImage(file);
        return s3Service.uploadFile(processedFile, folder);
    }

    @Override
    public void deleteResource(String key) {
        log.info("Deleting image with key {} with the image handler", key);
        s3Service.deleteFile(key);
    }

    @Override
    public InputStream getResource(String key) {
        log.info("Retrieving image with key {} with the image handler", key);
        return s3Service.downloadFile(key);
    }

    @Override
    public ResourceType getType() {
        return type;
    }

    private MultipartFile processImage(MultipartFile file) {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new IllegalArgumentException("Image is null");
            }

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            if (isResizingNeeded(width, height)) {
                int[] newDimensions = calculateNewDimensions(width, height);
                int newWidth = newDimensions[0];
                int newHeight = newDimensions[1];

                log.info("Resizing image {} to dimensions {}X{}", file.getOriginalFilename(), newWidth, newHeight);

                BufferedImage resizedImage = Thumbnails.of(originalImage)
                        .size(newWidth, newHeight)
                        .asBufferedImage();
                return convertBufferedImageToMultipartFile(resizedImage, file);
            }
            return file;

        } catch (IOException e) {
            log.error("Error processing image {}: {}", file.getOriginalFilename(), e.getMessage());
            throw new IllegalArgumentException("Error processing image " + file.getOriginalFilename(), e);
        }
    }

    private boolean isResizingNeeded(int width, int height) {
        boolean isSquare = width == height;
        if (isSquare) {
            return width > MAX_SQUARE_SIZE;
        } else {
            return width > MAX_HORIZONTAL_HEIGHT || height > MAX_HORIZONTAL_HEIGHT;
        }
    }

    private int[] calculateNewDimensions(int width, int height) {
        boolean isSquare = width == height;
        if (isSquare) {
            return new int[]{width, height};
        } else {
            float aspectRatio = (float) width / (float) height;
            if (aspectRatio > 1) {
                return new int[]{MAX_HORIZONTAL_WIDTH, (int) (MAX_HORIZONTAL_WIDTH / aspectRatio)};
            } else {
                return new int[]{(int) (MAX_HORIZONTAL_HEIGHT * aspectRatio), MAX_HORIZONTAL_HEIGHT};
            }
        }
    }

    private MultipartFile convertBufferedImageToMultipartFile(BufferedImage image, MultipartFile originalFile) throws IOException {
        String fileExtension = getFileExtension(Objects.requireNonNull(originalFile.getOriginalFilename()));
        String imageFormat = "jpeg";
        if ("png".equalsIgnoreCase(fileExtension)) {
            imageFormat = "png";
        } else if ("jpg".equalsIgnoreCase(fileExtension) || "jpeg".equalsIgnoreCase(fileExtension)) {
            imageFormat = "jpeg";
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, imageFormat, outputStream);
        return new MultipartFileAdapter(outputStream.toByteArray(), originalFile.getOriginalFilename(), "image/" + imageFormat);
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > 0) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }
}