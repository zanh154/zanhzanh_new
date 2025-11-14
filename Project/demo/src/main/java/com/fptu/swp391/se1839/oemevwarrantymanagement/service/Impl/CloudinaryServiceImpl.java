package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.CloudinaryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudinaryServiceImpl implements CloudinaryService {

    final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        if (file == null || file.isEmpty())
            return null;

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folderName,
                        "resource_type", "auto"));

        return (String) uploadResult.get("secure_url"); // Trả về link ảnh
    }

    public List<String> uploadMultiple(MultipartFile[] files, String folderName) throws IOException {
        List<String> urls = new ArrayList<>();
        if (files == null || files.length == 0)
            return urls;

        for (MultipartFile f : files) {
            String url = uploadFile(f, folderName);
            if (url != null)
                urls.add(url);
        }
        return urls;
    }
}
