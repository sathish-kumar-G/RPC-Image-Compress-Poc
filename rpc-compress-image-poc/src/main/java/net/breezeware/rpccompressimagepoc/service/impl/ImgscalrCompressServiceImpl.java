package net.breezeware.rpccompressimagepoc.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import net.breezeware.dynamo.aws.s3.exception.DynamoS3Exception;
import net.breezeware.dynamo.aws.s3.service.api.S3Service;
import net.breezeware.dynamo.utils.exception.DynamoException;
import net.breezeware.rpccompressimagepoc.service.api.ImgscalrCompressService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ImgscalrCompressServiceImpl implements ImgscalrCompressService {

    @Autowired
    S3Service s3Service;

    @Value("${aws.s3-bucket}")
    private String awsS3Bucket;

    @Override
    public byte[] generateCompressImageUsingImgscalr(String filePath, float quality) {
        try {
            // Download the image from AWS S3
            byte[] originalImageData = downloadImageFromAwsS3(filePath);

            // Resize the image using imgscalr-lib
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(originalImageData));
            BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC,
                    originalImage.getWidth(), originalImage.getHeight(), Scalr.OP_ANTIALIAS);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            compressImageWithQuality(resizedImage, quality, outputStream);

            log.info("After - Image compressed successfully. Size: " + outputStream.size() + " bytes");

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new DynamoException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public byte[] generateCompressImageUsingImgscalrWithTargetSize(String filePath, int targetSizeKB) {
        try {
            // Download the image from AWS S3
            byte[] originalImageData = downloadImageFromAwsS3(filePath);

            // Resize the image using imgscalr-lib
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(originalImageData));
            BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC,
                    originalImage.getWidth(), originalImage.getHeight(), Scalr.OP_ANTIALIAS);

            // Compress the resized image to the target size
            byte[] compressedImageData = compressImageToTargetSize(resizedImage, targetSizeKB);

            log.info("After - Image compressed successfully. Size: " + compressedImageData.length + " bytes");

            return compressedImageData;
        } catch (IOException e) {
            throw new DynamoException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public byte[] generateCompressImageUsingImgscalrWithTargetSizeAndWidth(String filePath, int targetSizeKB,
            int targetWidth) {
        try {
            // Download the image from AWS S3
            byte[] originalImageData = downloadImageFromAwsS3(filePath);

            // Resize the image using imgscalr-lib
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(originalImageData));

            BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH,
                    targetWidth, Scalr.OP_ANTIALIAS);

            // Compress the resized image to the target size
            byte[] compressedImageData = compressImageToTargetSize(resizedImage, targetSizeKB);

            log.info("After - Image compressed successfully. Size: " + compressedImageData.length + " bytes");

            return compressedImageData;
        } catch (IOException e) {
            throw new DynamoException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public byte[] generateCompressImageUsingImgscalrWithTargetSizeAndHeight(String filePath, int targetSizeKB,
            int targetHeight) {
        try {
            // Download the image from AWS S3
            byte[] originalImageData = downloadImageFromAwsS3(filePath);

            // Resize the image using imgscalr-lib
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(originalImageData));

            BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_HEIGHT,
                    targetHeight, Scalr.OP_ANTIALIAS);

            // Compress the resized image to the target size
            byte[] compressedImageData = compressImageToTargetSize(resizedImage, targetSizeKB);

            log.info("After - Image compressed successfully. Size: " + compressedImageData.length + " bytes");

            return compressedImageData;
        } catch (IOException e) {
            throw new DynamoException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

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

    private byte[] compressImageToTargetSize(BufferedImage image, int targetSizeKB) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            float compressionQuality = 0.5f;
            float step = 0.1f;

            // Iterate until the compressed image size is within the target size
            while (compressionQuality > 0.1f) {

                log.info("Compression Quality = {}, Image Size = {}", compressionQuality, outputStream.size() / 1024);

                outputStream.reset();

                // Compress the image with the current quality setting
                compressImageWithQuality(image, compressionQuality, outputStream);

                // Adjust the compression quality for the next iteration
                compressionQuality -= step;

                // Check if the compressed image size is within the target size
                if (outputStream.size() / 1024 <= targetSizeKB) {
                    return outputStream.toByteArray();
                }

            }

            // If the loop did not succeed, try compressing once with the minimum quality
            if (outputStream.size() == 0) {
                outputStream.reset();
                compressImageWithQuality(image, 0.4f, outputStream);
            }

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new DynamoException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    private void compressImageWithQuality(BufferedImage image, float compressionQuality, OutputStream outputStream)
            throws IOException {
        // Get the ImageWriter for JPEG format
        ImageWriter imageWriter = ImageIO.getImageWritersByFormatName("jpeg").next();

        // Set the compression quality
        ImageWriteParam writeParam = imageWriter.getDefaultWriteParam();
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        writeParam.setCompressionQuality(compressionQuality);

        // Write the compressed image to the output stream
        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);
        imageWriter.setOutput(imageOutputStream);
        imageWriter.write(null, new javax.imageio.IIOImage(image, null, null), writeParam);

        // Clean up resources
        imageWriter.dispose();
        imageOutputStream.close();
    }
}
