package net.breezeware.rpccompressimagepoc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.breezeware.rpccompressimagepoc.service.api.AwtGraphics2DImageCompressService;
import net.breezeware.rpccompressimagepoc.service.api.ImageIOAPIImageCompressService;
import net.breezeware.rpccompressimagepoc.service.api.ImgscalrCompressService;
import net.breezeware.rpccompressimagepoc.service.api.ThumbNailatorImageCompressService;

@RestController
@RequestMapping("/api/image-compress")
public class ImageCompressController {

    @Autowired
    ImageIOAPIImageCompressService imageIOAPIImageCompressService;

    @Autowired
    ThumbNailatorImageCompressService thumbNailatorImageCompressService;

    @Autowired
    AwtGraphics2DImageCompressService awtGraphics2DImageCompressService;

    @Autowired
    ImgscalrCompressService imgscalrCompressService;

    @GetMapping("/image-io-api")
    public ResponseEntity<byte[]> generateCompressImageUsingImageIOAAPI(@RequestParam("file-path") String filePath,
            @RequestParam("quality") float compressionQuality) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentDispositionFormData("inline", "image.jpeg");

        byte[] bytes =
                imageIOAPIImageCompressService.generateCompressImageUsingImageIOAAPI(filePath, compressionQuality);

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    @GetMapping("/thumb-nailator-api")
    public ResponseEntity<byte[]> generateCompressImageUsingThumbNailator(@RequestParam("file-path") String filePath,
            @RequestParam("quality") float compressionQuality) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentDispositionFormData("inline", "image.jpeg");

        byte[] bytes =
                thumbNailatorImageCompressService.generateCompressImageUsingThumbNailator(filePath, compressionQuality);

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    @GetMapping("/thumb-nailator-api/target-size")
    public ResponseEntity<byte[]> compressImageToTargetSizeUsingThumbNailator(
            @RequestParam("file-path") String filePath, @RequestParam("size") int compressionQuality) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentDispositionFormData("inline", "image.jpeg");

        byte[] bytes = thumbNailatorImageCompressService.compressImageToTargetSizeUsingThumbNailator(filePath,
                compressionQuality);

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    @GetMapping("/thumb-nailator-api/target-size-and-width")
    public ResponseEntity<byte[]> compressImageToTargetSizeAndWidthUsingThumbNailator(
            @RequestParam("file-path") String filePath, @RequestParam("size") int compressionQuality,
            @RequestParam("width") int width) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentDispositionFormData("inline", "image.jpeg");

        byte[] bytes = thumbNailatorImageCompressService.compressImageToTargetSizeAndWidthUsingThumbNailator(filePath,
                compressionQuality, width);

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    @GetMapping("/thumb-nailator-api/target-size-and-height")
    public ResponseEntity<byte[]> compressImageToTargetSizeAndHeightUsingThumbNailator(
            @RequestParam("file-path") String filePath, @RequestParam("size") int compressionQuality,
            @RequestParam("height") int height) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentDispositionFormData("inline", "image.jpeg");

        byte[] bytes = thumbNailatorImageCompressService.compressImageToTargetSizeAndHeightUsingThumbNailator(filePath,
                compressionQuality, height);

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    @GetMapping("/img-scalr-api")
    public ResponseEntity<byte[]> generateCompressImageUsingImgscalr(@RequestParam("file-path") String filePath,
            @RequestParam("quality") float compressionQuality) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentDispositionFormData("inline", "image.jpeg");

        byte[] bytes = imgscalrCompressService.generateCompressImageUsingImgscalr(filePath, compressionQuality);

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    @GetMapping("/img-scalr-api/target-size")
    public ResponseEntity<byte[]> generateCompressImageUsingImgscalrWithTargetSize(
            @RequestParam("file-path") String filePath, @RequestParam("size") int compressionSize) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentDispositionFormData("inline", "image.jpeg");

        byte[] bytes =
                imgscalrCompressService.generateCompressImageUsingImgscalrWithTargetSize(filePath, compressionSize);

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    @GetMapping("/img-scalr-api/target-size-and-width")
    public ResponseEntity<byte[]> generateCompressImageUsingImgscalrWithTargetSizeAndWidth(
            @RequestParam("file-path") String filePath, @RequestParam("size") int compressionSize,
            @RequestParam("width") int width) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentDispositionFormData("inline", "image.jpeg");

        byte[] bytes = imgscalrCompressService.generateCompressImageUsingImgscalrWithTargetSizeAndWidth(filePath,
                compressionSize, width);

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    @GetMapping("/img-scalr-api/target-size-and-height")
    public ResponseEntity<byte[]> generateCompressImageUsingImgscalrWithTargetSizeAndHeight(
            @RequestParam("file-path") String filePath, @RequestParam("size") int compressionSize,
            @RequestParam("height") int height) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentDispositionFormData("inline", "image.jpeg");

        byte[] bytes = imgscalrCompressService.generateCompressImageUsingImgscalrWithTargetSizeAndHeight(filePath,
                compressionSize, height);

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    @GetMapping("/awt-graphics-api")
    public ResponseEntity<byte[]> generateCompressImageUsingAwtGraphics2D(@RequestParam("file-path") String filePath,
            @RequestParam("quality") float compressionQuality) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentDispositionFormData("inline", "image.jpeg");

        byte[] bytes =
                awtGraphics2DImageCompressService.generateCompressImageUsingAwtGraphics2D(filePath, compressionQuality);

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

}
