package net.breezeware.rpccompressimagepoc.service.api;

public interface ImageIOAPIImageCompressService {


    byte[] generateCompressImageUsingImageIOAAPI(String filePath, float compressionQuality);
}
