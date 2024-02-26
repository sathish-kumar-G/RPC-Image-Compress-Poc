package net.breezeware.rpccompressimagepoc.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import net.breezeware.dynamo.aws.s3.exception.DynamoS3Exception;
import net.breezeware.dynamo.aws.s3.service.api.S3Service;
import net.breezeware.dynamo.utils.exception.DynamoException;
import net.breezeware.rpccompressimagepoc.service.api.ThumbNailatorImageCompressService;
import net.coobird.thumbnailator.Thumbnails;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ThumbNailatorImageCompressServiceImpl implements ThumbNailatorImageCompressService {

    @Autowired
    S3Service s3Service;

    @Value("${aws.s3-bucket}")
    private String awsS3Bucket;

    @Override
    public byte[] generateCompressImageUsingThumbNailator(String filePath, float compressionQuality) {
        // Download image byte
        byte[] bytes = downloadImageFromAwsS3(filePath);

        // Resize and compress the image
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(new ByteArrayInputStream(bytes)).scale(1.0).outputQuality(compressionQuality)
                    .toOutputStream(outputStream);

            log.info("After - Image downloaded successfully. Size: " + outputStream.toByteArray().length + " bytes");

            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error resizing image: " + e.getMessage());
            throw new DynamoException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public byte[] compressImageToTargetSizeUsingThumbNailator(String imagePath, long targetSizeKB) {

        byte[] inputImageBytes = downloadImageFromAwsS3(imagePath);

        try {
            // Set initial quality and scale
            float initialQuality = 0.5f;
            // float scaleFactor = 1.0f;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            boolean sizeExceeded;
            do {
                sizeExceeded = false;

                // log.info("Image Size = {}, Quality = {}, Scale Factor =
                // {}",outputStream.size()/1024,initialQuality,scaleFactor);
                log.info("Image Size = {}, Quality = {}", outputStream.size() / 1024, initialQuality);

                outputStream.reset();

                // Use Thumbnail to compress the image
                Thumbnails.of(new ByteArrayInputStream(inputImageBytes)).outputQuality(initialQuality).scale(1.0)
                        // .size(2500,1000)
                        // .scale(scaleFactor)

                        .toOutputStream(outputStream);

                // Check the output size
                if (outputStream.size() / 1024 > targetSizeKB) {
                    // If size exceeds the target, adjust quality and scale for the next iteration
                    sizeExceeded = true;
                    initialQuality -= 0.10f;
                    // scaleFactor -= 0.10f;

                }

            } while (sizeExceeded && initialQuality > 0.1f);

            if (outputStream.size() == 0) {
                Thumbnails.of(new ByteArrayInputStream(inputImageBytes)).outputQuality(0.4).scale(1.0)
                        .toOutputStream(outputStream);
            }

            log.info("After - Image compressed successfully. Size: " + outputStream.toByteArray().length + " bytes");

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new DynamoException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public byte[] compressImageToTargetSizeAndWidthUsingThumbNailator(String imagePath, long targetSizeKB,
            int targetWidth) {

        byte[] inputImageBytes = downloadImageFromAwsS3(imagePath);

        try {
            // Set initial quality
            float initialQuality = 0.5f;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            boolean sizeExceeded;
            do {
                sizeExceeded = false;

                log.info("Image Size = {}, Quality = {}", outputStream.size() / 1024, initialQuality);

                outputStream.reset();

                // Use Thumbnail to compress the image
                Thumbnails.of(new ByteArrayInputStream(inputImageBytes)).outputQuality(initialQuality)
                        .width(targetWidth).toOutputStream(outputStream);

                // Check the output size
                if (outputStream.size() / 1024 > targetSizeKB) {
                    // If size exceeds the target, adjust quality and scale for the next iteration
                    sizeExceeded = true;
                    initialQuality -= 0.10f;
                }

            } while (sizeExceeded && initialQuality > 0.1f);

            if (outputStream.size() == 0) {
                Thumbnails.of(new ByteArrayInputStream(inputImageBytes)).outputQuality(0.4).width(targetWidth)
                        .toOutputStream(outputStream);
            }

            log.info("After - Image compressed successfully. Size: " + outputStream.toByteArray().length + " bytes");

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new DynamoException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public byte[] compressImageToTargetSizeAndHeightUsingThumbNailator(String imagePath, long targetSizeKB,
            int targetHeight) {

        byte[] inputImageBytes = downloadImageFromAwsS3(imagePath);

        try {
            // Set initial quality
            float initialQuality = 0.5f;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            boolean sizeExceeded;
            do {
                sizeExceeded = false;

                log.info("Image Size = {}, Quality = {}", outputStream.size() / 1024, initialQuality);

                outputStream.reset();

                // Use Thumbnail to compress the image
                Thumbnails.of(new ByteArrayInputStream(inputImageBytes)).outputQuality(initialQuality)
                        .height(targetHeight).toOutputStream(outputStream);

                // Check the output size
                if (outputStream.size() / 1024 > targetSizeKB) {
                    // If size exceeds the target, adjust quality and scale for the next iteration
                    sizeExceeded = true;
                    initialQuality -= 0.10f;
                }

            } while (sizeExceeded && initialQuality > 0.1f);

            if (outputStream.size() == 0) {
                Thumbnails.of(new ByteArrayInputStream(inputImageBytes)).outputQuality(0.4).height(targetHeight)
                        .toOutputStream(outputStream);
            }

            log.info("After - Image compressed successfully. Size: " + outputStream.toByteArray().length + " bytes");

            return outputStream.toByteArray();
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

}
