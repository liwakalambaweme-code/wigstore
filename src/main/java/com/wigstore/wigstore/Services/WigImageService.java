package com.wigstore.wigstore.service;

import com.wigstore.wigstore.model.Wig;
import com.wigstore.wigstore.model.WigImage;
import com.wigstore.wigstore.repository.WigImageRepository;
import com.wigstore.wigstore.repository.WigRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class WigImageService {

    // we choose the extension from the content type, never from the user's file name
    private static final Map<String, String> ALLOWED = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp");

    private final WigImageRepository imageRepository;
    private final WigRepository wigRepository;
    private final Path uploadPath;
    private final String baseUrl;

    public WigImageService(WigImageRepository imageRepository,
                           WigRepository wigRepository,
                           @Value("${app.upload-dir:uploads}") String uploadDir,
                           @Value("${app.base-url:http://localhost:8090}") String baseUrl)
            throws IOException {
        this.imageRepository = imageRepository;
        this.wigRepository = wigRepository;
        this.baseUrl = baseUrl;
        this.uploadPath = Path.of(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
    }

    @Transactional
    public WigImage addImage(Long wigId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please choose an image");
        }
        String extension = ALLOWED.get(file.getContentType());
        if (extension == null) {
            throw new IllegalArgumentException("Only JPG, PNG or WEBP images are allowed");
        }
        Wig wig = wigRepository.findById(wigId)
                .orElseThrow(() -> new IllegalArgumentException("Wig not found"));

        String fileName = UUID.randomUUID() + extension;
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, uploadPath.resolve(fileName));
        }

        int existing = imageRepository.countByWigId(wigId);
        WigImage image = new WigImage();
        image.setWig(wig);
        image.setFileName(fileName);
        image.setDisplayOrder(existing);
        image.setPrimaryImage(existing == 0);   // the first photo is the cover by default
        return imageRepository.save(image);
    }

    public List<WigImage> getImages(Long wigId) {
        return imageRepository.findByWigIdOrderByDisplayOrderAsc(wigId);
    }

    @Transactional
    public void setPrimary(Long wigId, Long imageId) {
        List<WigImage> images = imageRepository.findByWigIdOrderByDisplayOrderAsc(wigId);
        if (images.stream().noneMatch(i -> i.getId().equals(imageId))) {
            throw new IllegalArgumentException("Image not found for this wig");
        }
        for (WigImage i : images) {
            i.setPrimaryImage(i.getId().equals(imageId));   // exactly one cover
        }
        imageRepository.saveAll(images);
    }

    @Transactional
    public void deleteImage(Long imageId) throws IOException {
        WigImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found"));
        Long wigId = image.getWig().getId();
        boolean wasPrimary = image.isPrimaryImage();

        Files.deleteIfExists(uploadPath.resolve(image.getFileName()));
        imageRepository.delete(image);

        if (wasPrimary) {   // promote the next photo to cover
            imageRepository.findByWigIdOrderByDisplayOrderAsc(wigId).stream()
                    .findFirst()
                    .ifPresent(next -> {
                        next.setPrimaryImage(true);
                        imageRepository.save(next);
                    });
        }
    }

    // the full URL is built here, so only the file name is stored in the database
    public String urlFor(WigImage image) {
        return baseUrl + "/uploads/" + image.getFileName();
    }
}