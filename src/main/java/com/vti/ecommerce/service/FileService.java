package com.vti.ecommerce.service;

import com.vti.ecommerce.exception.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {
    private final Path root = Paths.get("uploads");

    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public String save(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String path = "/uploads/" + fileName;
            Files.copy(file.getInputStream(), this.root.resolve(fileName));
            return path;
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
//                String fileName = file.getOriginalFilename();
//                return "/uploads/" + fileName;
                throw new ConflictException("This file name is already exist");
            }

            throw new RuntimeException(e.getMessage());
        }
    }
}

