package net.breezeware.rpccompressimagepoc.service.api;

public interface ImgscalrCompressService {
    byte[] generateCompressImageUsingImgscalr(String filePath, float compressionQuality);

    byte[] generateCompressImageUsingImgscalrWithTargetSize(String file, int targetSizeKB);

    byte[] generateCompressImageUsingImgscalrWithTargetSizeAndWidth(String filePath, int targetSizeKB,
                                                                    int targetWidth);

    byte[] generateCompressImageUsingImgscalrWithTargetSizeAndHeight(String filePath, int targetSizeKB,
                                                                     int targetHeight);
}
