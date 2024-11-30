package faang.school.postservice.service.image;

import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Slf4j
@Service
public class ImageResizeService {
    public byte[] resizeAndConvert(BufferedImage image, int maxWidth, int maxHeight) {
        image = resize(image, maxWidth, maxHeight);

        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "JPEG", os);
            return os.toByteArray();
        } catch (Exception e) {
            log.error("Image read error", e);
            throw new IllegalStateException("Image read error", e);
        }
    }

    public BufferedImage resize(BufferedImage image, int maxWidth, int maxHeight) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width == height) { // square
            int newSize = Math.min(maxHeight, maxWidth);
            image = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, newSize, newSize);
        } else {
            int newWidth = Math.min(maxWidth, width);
            int newHeight = Math.min(maxHeight, height);
            image = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, newWidth, newHeight);
        }
        return image;
    }
}
