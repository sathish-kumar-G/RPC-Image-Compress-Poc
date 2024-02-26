package net.breezeware.rpccompressimagepoc.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import net.breezeware.dynamo.aws.s3.exception.DynamoS3Exception;
import net.breezeware.dynamo.aws.s3.service.api.S3Service;
import net.breezeware.dynamo.utils.exception.DynamoException;
import net.breezeware.rpccompressimagepoc.service.api.ImageIOAPIImageCompressService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ImageIOAPIImageCompressServiceImpl implements ImageIOAPIImageCompressService {

    @Autowired
    S3Service s3Service;

    @Value("${aws.s3-bucket}")
    private String awsS3Bucket;

    @Override
    public byte[] generateCompressImageUsingImageIOAAPI(String filePath, float compressionQuality) {

        // Download image byte
        byte[] bytes = downloadImageFromAwsS3(filePath);

        // Resize and compress the image
        bytes = resizeAndCompressImage(bytes, compressionQuality);

        return bytes;
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

    private byte[] resizeAndCompressImage(byte[] originalImage, float compressionQuality) {
        try {
            BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(originalImage));

            // Resize and compress the image
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            writeImageWithCompression(inputImage, outputStream, compressionQuality);

            log.info("After - Image downloaded successfully. Size: " + outputStream.toByteArray().length + " bytes");

            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error resizing and compressing image: " + e.getMessage());
            throw new DynamoException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    private void writeImageWithCompression(BufferedImage image, OutputStream outputStream, float compressionQuality)
            throws IOException {
        String formatName = getImageFormat(image);

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(formatName);
        ImageWriter writer = writers.next();

        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);
        writer.setOutput(imageOutputStream);

        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        if (writeParam instanceof JPEGImageWriteParam) {
            (writeParam).setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            (writeParam).setCompressionQuality(compressionQuality);
        }

        writer.write(null, new IIOImage(image, null, null), writeParam);

        imageOutputStream.close();
        writer.dispose();
    }

    private String getImageFormat(BufferedImage image) {
        String formatName = null;
        for (String format : ImageIO.getWriterFormatNames()) {
            if (ImageIO.getImageWritersByFormatName(format).hasNext()) {
                formatName = format;
                break;
            }

        }

        return formatName;
    }
}
