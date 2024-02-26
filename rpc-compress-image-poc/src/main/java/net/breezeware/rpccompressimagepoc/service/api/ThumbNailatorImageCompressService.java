package net.breezeware.rpccompressimagepoc.service.api;

public interface ThumbNailatorImageCompressService {
    byte[] generateCompressImageUsingThumbNailator(String filePath, float compressionQuality);

    byte[] compressImageToTargetSizeUsingThumbNailator(String imagePath, long targetSizeKB);

    byte[] compressImageToTargetSizeAndWidthUsingThumbNailator(String imagePath, long targetSizeKB, int targetWidth);

    byte[] compressImageToTargetSizeAndHeightUsingThumbNailator(String imagePath, long targetSizeKB, int targetHeight);
}
