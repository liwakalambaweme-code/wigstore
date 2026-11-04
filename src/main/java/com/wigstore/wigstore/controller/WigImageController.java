package com.wigstore.wigstore.controller;

import com.wigstore.wigstore.dto.ApiDtos.ImageResponse;
import com.wigstore.wigstore.dto.ApiMapper;
import com.wigstore.wigstore.service.WigImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/wigs/{wigId}/images")
public class WigImageController {

    private final WigImageService imageService;
    private final ApiMapper mapper;

    public WigImageController(WigImageService imageService, ApiMapper mapper) {
        this.imageService = imageService;
        this.mapper = mapper;
    }

    // accepts one or several photos in a single request, under the field name "files"
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public List<ImageResponse> upload(@PathVariable Long wigId,
                                      @RequestParam("files") List<MultipartFile> files)
            throws IOException {
        List<ImageResponse> saved = new ArrayList<>();
        for (MultipartFile file : files) {
            saved.add(mapper.toImage(imageService.addImage(wigId, file)));
        }
        return saved;
    }

    @GetMapping
    public List<ImageResponse> list(@PathVariable Long wigId) {
        return imageService.getImages(wigId).stream().map(mapper::toImage).toList();
    }

    @PutMapping("/{imageId}/primary")
    public void makeCover(@PathVariable Long wigId, @PathVariable Long imageId) {
        imageService.setPrimary(wigId, imageId);
    }

    @DeleteMapping("/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long imageId) throws IOException {
        imageService.deleteImage(imageId);
    }
}