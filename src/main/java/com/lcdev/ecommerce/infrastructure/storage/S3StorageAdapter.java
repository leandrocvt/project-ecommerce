package com.lcdev.ecommerce.infrastructure.storage;

import com.lcdev.ecommerce.application.service.exceptions.StorageException;
import com.lcdev.ecommerce.domain.ports.StoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class S3StorageAdapter implements StoragePort {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Override
    public String upload(String path, MultipartFile file) {
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(path)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return "https://" + bucketName + ".s3." + s3Client.serviceClientConfiguration().region().id() + ".amazonaws.com/" + path;
        } catch (Exception e) {
            throw new StorageException("Erro ao enviar para o S3", e);
        }
    }

    @Override
    public void delete(String path) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(path)
                .build());
    }

    @Override
    public byte[] download(String path) {
        try {
            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(path)
                            .build()
            );
            return response.asByteArray();
        } catch (Exception e) {
            throw new StorageException("Erro ao baixar do S3", e);
        }
    }
}





