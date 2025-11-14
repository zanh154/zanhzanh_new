package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String uploadFile(MultipartFile file, String folderName) throws IOException;

    public List<String> uploadMultiple(MultipartFile[] files, String folderName) throws IOException;
}
