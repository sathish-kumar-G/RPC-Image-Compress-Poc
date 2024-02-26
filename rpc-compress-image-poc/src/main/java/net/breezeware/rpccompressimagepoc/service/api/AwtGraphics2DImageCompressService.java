package net.breezeware.rpccompressimagepoc.service.api;

public interface AwtGraphics2DImageCompressService {
    byte[] generateCompressImageUsingAwtGraphics2D(String filePath, float compressionQuality);
}
