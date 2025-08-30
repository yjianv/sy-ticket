package com.syticket.service;

import com.syticket.config.MinioConfig;
import com.syticket.entity.FileEntity;
import com.syticket.mapper.FileMapper;
import com.syticket.util.SecurityUtils;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 文件服务类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Service
public class FileService {
    
    @Autowired
    private MinioClient minioClient;
    
    @Autowired
    private MinioConfig minioConfig;
    
    @Autowired
    private FileMapper fileMapper;
    
    /**
     * 上传文件
     * 
     * @param file 上传的文件
     * @param relatedType 关联类型
     * @param relatedId 关联ID
     * @return 文件信息
     */
    @Transactional
    public FileEntity uploadFile(MultipartFile file, FileEntity.RelatedType relatedType, Long relatedId) {
        try {
            // 生成文件hash
            String fileHash = calculateFileHash(file.getInputStream());
            
            // 检查是否已存在相同文件
            FileEntity existingFile = fileMapper.findByHash(fileHash);
            if (existingFile != null) {
                // 复用现有文件，创建新的关联记录
                FileEntity newFileRecord = new FileEntity(
                    file.getOriginalFilename(),
                    existingFile.getStoredName(),
                    existingFile.getFilePath(),
                    file.getSize(),
                    file.getContentType(),
                    SecurityUtils.getCurrentUserId(),
                    relatedType,
                    relatedId
                );
                newFileRecord.setFileHash(fileHash);
                fileMapper.insert(newFileRecord);
                return newFileRecord;
            }
            
            // 生成存储文件名
            String storedName = generateStoredName(file.getOriginalFilename());
            String filePath = generateFilePath(storedName);
            
            // 上传到MinIO
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(filePath)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            
            // 保存文件记录
            FileEntity fileEntity = new FileEntity(
                file.getOriginalFilename(),
                storedName,
                filePath,
                file.getSize(),
                file.getContentType(),
                SecurityUtils.getCurrentUserId(),
                relatedType,
                relatedId
            );
            fileEntity.setFileHash(fileHash);
            fileMapper.insert(fileEntity);
            
            return fileEntity;
            
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 下载文件
     * 
     * @param fileId 文件ID
     * @return 文件输入流
     */
    public InputStream downloadFile(Long fileId) {
        try {
            FileEntity fileEntity = fileMapper.findById(fileId);
            if (fileEntity == null) {
                throw new RuntimeException("文件不存在");
            }
            
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileEntity.getFilePath())
                    .build()
            );
            
        } catch (Exception e) {
            throw new RuntimeException("文件下载失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 删除文件
     * 
     * @param fileId 文件ID
     */
    @Transactional
    public void deleteFile(Long fileId) {
        try {
            FileEntity fileEntity = fileMapper.findById(fileId);
            if (fileEntity == null) {
                return;
            }
            
            // 检查是否有其他记录使用相同文件
            FileEntity sameHashFile = fileMapper.findByHash(fileEntity.getFileHash());
            if (sameHashFile != null && !sameHashFile.getId().equals(fileId)) {
                // 有其他记录使用相同文件，只删除数据库记录
                fileMapper.deleteById(fileId);
            } else {
                // 没有其他记录使用，删除MinIO中的文件和数据库记录
                minioClient.removeObject(
                    RemoveObjectArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .object(fileEntity.getFilePath())
                        .build()
                );
                fileMapper.deleteById(fileId);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("文件删除失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据关联查找文件列表
     * 
     * @param relatedType 关联类型
     * @param relatedId 关联ID
     * @return 文件列表
     */
    public List<FileEntity> getFilesByRelated(FileEntity.RelatedType relatedType, Long relatedId) {
        return fileMapper.findByRelated(relatedType.name(), relatedId);
    }
    
    /**
     * 根据ID获取文件信息
     * 
     * @param fileId 文件ID
     * @return 文件信息
     */
    public FileEntity getFileById(Long fileId) {
        return fileMapper.findById(fileId);
    }
    
    /**
     * 检查文件是否存在
     * 
     * @param fileId 文件ID
     * @return 是否存在
     */
    public boolean isFileExists(Long fileId) {
        try {
            FileEntity fileEntity = fileMapper.findById(fileId);
            if (fileEntity == null) {
                return false;
            }
            
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileEntity.getFilePath())
                    .build()
            );
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 生成存储文件名
     * 
     * @param originalName 原始文件名
     * @return 存储文件名
     */
    private String generateStoredName(String originalName) {
        String extension = "";
        if (originalName != null && originalName.lastIndexOf('.') > 0) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }
        return UUID.randomUUID().toString() + extension;
    }
    
    /**
     * 生成文件存储路径
     * 
     * @param storedName 存储文件名
     * @return 文件路径
     */
    private String generateFilePath(String storedName) {
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return datePath + "/" + storedName;
    }
    
    /**
     * 计算文件hash
     * 
     * @param inputStream 文件输入流
     * @return 文件hash
     */
    private String calculateFileHash(InputStream inputStream) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int bytesRead;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            
            byte[] hashBytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("计算文件hash失败", e);
        }
    }
}
