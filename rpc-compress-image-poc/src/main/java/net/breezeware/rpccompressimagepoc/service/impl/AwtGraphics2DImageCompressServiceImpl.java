package net.breezeware.rpccompressimagepoc.service.impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;

import javax.imageio.stream.ImageOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import net.breezeware.dynamo.aws.s3.exception.DynamoS3Exception;
import net.breezeware.dynamo.aws.s3.service.api.S3Service;
import net.breezeware.dynamo.utils.exception.DynamoException;
import net.breezeware.rpccompressimagepoc.service.api.AwtGraphics2DImageCompressService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AwtGraphics2DImageCompressServiceImpl implements AwtGraphics2DImageCompressService {

    @Autowired
    S3Service s3Service;

    @Value("${aws.s3-bucket}")
    private String awsS3Bucket;

    @Override
    public byte[] generateCompressImageUsingAwtGraphics2D(String filePath, float compressionQuality) {

        byte[] imageData = downloadImageFromAwsS3(filePath);
        String imageType = extractImageType(imageData);

        BufferedImage inputImage = compressImage(imageData, compressionQuality, imageType);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(Objects.requireNonNull(inputImage), imageType, outputStream);
        } catch (IOException e) {
          throw new DynamoException(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        log.info("After - Image downloaded successfully. Size: " + outputStream.toByteArray().length + " bytes");

        return outputStream.toByteArray();
    }

    public String extractImageType(byte[] imageData) {
        try {
            BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(imageData));

            return getImageFormat(inputImage, imageData);
        } catch (IOException e) {
       throw new DynamoException(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }

    private String getImageFormat(BufferedImage image, byte[] imageData) {
        try {

            BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = tempImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();

            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("png");
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                ImageReadParam param = reader.getDefaultReadParam();
                ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(imageData));
                reader.setInput(iis, false, false);
                return reader.getFormatName().toLowerCase();
            }

        } catch (IOException e) {
            throw new DynamoException(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        return null;
    }

    private BufferedImage compressImage(byte[] imageData, float compressionQuality, String imageType) {
        try {
            BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(imageData));

            // Create a new BufferedImage for compressed image
            BufferedImage compressedImage = new BufferedImage(
                    inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);

            // Get the Graphics2D object to draw on the compressed image
            Graphics2D g2d = compressedImage.createGraphics();

            // Draw the original image onto the compressed image
            g2d.drawImage(inputImage, 0, 0, null);

            // Clean up resources
            g2d.dispose();

            // Create a ByteArrayOutputStream for the compressed image
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Get an ImageWriter for the specified image type
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(imageType);
            if (writers.hasNext()) {
                ImageWriter writer = writers.next();
                ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);
                writer.setOutput(imageOutputStream);

                // Set compression quality
                ImageWriteParam writeParam = writer.getDefaultWriteParam();
                writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writeParam.setCompressionQuality(compressionQuality);

                // Write the compressed image
                writer.write(null, new IIOImage(compressedImage, null, null), writeParam);

                // Clean up resources
                writer.dispose();
                imageOutputStream.close();

                // Read the compressed image back from the ByteArrayOutputStream
                ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                BufferedImage finalCompressedImage = ImageIO.read(inputStream);

                // Clean up resources
                inputStream.close();
                outputStream.close();

                return finalCompressedImage;
            }
        } catch (IOException e) {
            throw new DynamoException(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        return null;
    }

    private byte[] downloadImageFromAwsS3(String imagePath) {
        log.info("Entering downloadImageFromAwsS3()");
        byte[] bytes;
        try {
            log.info("Downloading image from AWS S3...");
            bytes = s3Service.downloadObject(awsS3Bucket, imagePath);

            log.info("Before - Image downloaded successfully. Size: " + bytes.length + " bytes");

        } catch (DynamoS3Exception e) {
            log.error("Error downloading image from AWS S3: " + e.getMessage());
            throw new DynamoException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        log.info("Leaving downloadImageFromAwsS3()");
        return bytes;
    }
}
