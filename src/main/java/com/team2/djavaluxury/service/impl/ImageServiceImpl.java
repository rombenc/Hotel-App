package com.team2.djavaluxury.service.impl;

import com.team2.djavaluxury.entity.Image;
import com.team2.djavaluxury.utils.exception.FileStorageException;
import com.team2.djavaluxury.repository.ImageRepository;
import com.team2.djavaluxury.service.inter.ImageService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final Path directoryPath;
    private final Path thumbnailDirectoryPath;
    private final ImageRepository imageRepository;

    @Autowired
    public ImageServiceImpl(@Value("${app.djavaluxury.multipart.path-location}") String directoryPath,
                            @Value("${app.djavaluxury.multipart.thumbnail-path-location}") String thumbnailDirectoryPath,
                            ImageRepository imageRepository) {
        this.directoryPath = Paths.get(directoryPath);
        this.thumbnailDirectoryPath = Paths.get(thumbnailDirectoryPath);
        this.imageRepository = imageRepository;
    }

    @PostConstruct
    public void initDirectory() {
        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
            if (!Files.exists(thumbnailDirectoryPath)) {
                Files.createDirectories(thumbnailDirectoryPath);
            }
        } catch (IOException e) {
            throw new FileStorageException("Could not create the directory", e);
        }
    }

    @Override
    public String save(MultipartFile file) {
        try {
            String uniqueFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = directoryPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath);

            String thumbnailFileName = "thumbnail_" + uniqueFileName;
            Path thumbnailPath = thumbnailDirectoryPath.resolve(thumbnailFileName);
            Thumbnails.of(filePath.toFile())
                    .size(200, 200)
                    .toFile(thumbnailPath.toFile());

            return filePath.toString();
        } catch (IOException e) {
            log.error("Failed to store file", e);
            throw  new FileStorageException("Failed to store file", e);
        }
    }

    @Override
    public byte[] readFileFromLocation(String imageUrl) {
        try {
            Path filePath = new File(imageUrl).toPath();
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new FileStorageException("Failed to read file", e);
        }
    }

    @Override
    public Image create(MultipartFile multipartFile) {
        try {
            if (!List.of("image/jpeg", "image/png", "image/jpg", "image/svg+xml").contains(multipartFile.getContentType()))
                throw new ConstraintViolationException("Invalid image type", null);

            String uniqueFilename = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
            Path filePath = directoryPath.resolve(uniqueFilename);
            Files.copy(multipartFile.getInputStream(), filePath);

            String thumbnailFilename = "thumbnail_" + uniqueFilename;
            Path thumbnailPath = thumbnailDirectoryPath.resolve(thumbnailFilename);
            Thumbnails.of(filePath.toFile())
                    .size(200, 200)
                    .toFile(thumbnailPath.toFile());

            Image image = Image.builder()
                    .name(uniqueFilename)
                    .contentType(multipartFile.getContentType())
                    .size(multipartFile.getSize())
                    .path(filePath.toString())
                    .thumbnailPath(thumbnailPath.toString())
                    .build();
            imageRepository.saveAndFlush(image);

            return image;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file", e);
        }
    }

    @Override
    public Resource getById(String id) {
        try {
            Image image = imageRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "data not found"));
            Path filePath = Paths.get(image.getPath());
            if (!Files.exists(filePath))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "data not found");
            return new UrlResource(filePath.toUri());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            Image image = imageRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "data not found"));
            Path filePath = Paths.get(image.getPath());
            if (!Files.exists(filePath))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "data not found");
            Files.delete(filePath);

            Path thumbnailPath = Paths.get(image.getThumbnailPath());
            if (Files.exists(thumbnailPath)) {
                Files.delete(thumbnailPath);
            }

            imageRepository.delete(image);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
