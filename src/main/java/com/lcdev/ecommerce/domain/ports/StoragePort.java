package com.lcdev.ecommerce.domain.ports;

import org.springframework.web.multipart.MultipartFile;

public interface StoragePort {
    String upload(String path, MultipartFile file);
    void delete(String path);
    byte[] download(String path);
}