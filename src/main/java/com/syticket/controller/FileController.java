package com.syticket.controller;

import com.syticket.entity.FileEntity;
import com.syticket.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件管理控制器
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/files")
public class FileController {
    
    @Autowired
    private FileService fileService;
    
    /**
     * 上传文件
     * 
     * @param file 上传的文件
     * @param relatedType 关联类型
     * @param relatedId 关联ID
     * @return 上传结果
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "relatedType", defaultValue = "OTHER") String relatedType,
            @RequestParam(value = "relatedId", required = false) Long relatedId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                result.put("success", false);
                result.put("message", "文件不能为空");
                return ResponseEntity.badRequest().body(result);
            }
            
            // 检查文件大小（10MB限制）
            if (file.getSize() > 10 * 1024 * 1024) {
                result.put("success", false);
                result.put("message", "文件大小不能超过10MB");
                return ResponseEntity.badRequest().body(result);
            }
            
            FileEntity.RelatedType type = FileEntity.RelatedType.valueOf(relatedType.toUpperCase());
            FileEntity fileEntity = fileService.uploadFile(file, type, relatedId);
            
            result.put("success", true);
            result.put("message", "文件上传成功");
            result.put("data", Map.of(
                "id", fileEntity.getId(),
                "originalName", fileEntity.getOriginalName(),
                "fileSize", fileEntity.getFileSize(),
                "mimeType", fileEntity.getMimeType(),
                "downloadUrl", "/api/files/" + fileEntity.getId() + "/download"
            ));
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    /**
     * 下载文件
     * 
     * @param fileId 文件ID
     * @return 文件内容
     */
    @GetMapping("/{fileId}/download")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long fileId) {
        try {
            FileEntity fileEntity = fileService.getFileById(fileId);
            if (fileEntity == null) {
                return ResponseEntity.notFound().build();
            }
            
            InputStream inputStream = fileService.downloadFile(fileId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(fileEntity.getMimeType()));
            headers.setContentDispositionFormData("attachment", 
                URLEncoder.encode(fileEntity.getOriginalName(), StandardCharsets.UTF_8));
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取文件信息
     * 
     * @param fileId 文件ID
     * @return 文件信息
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<Map<String, Object>> getFileInfo(@PathVariable Long fileId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            FileEntity fileEntity = fileService.getFileById(fileId);
            if (fileEntity == null) {
                result.put("success", false);
                result.put("message", "文件不存在");
                return ResponseEntity.status(404).body(result);
            }
            
            result.put("success", true);
            result.put("data", Map.of(
                "id", fileEntity.getId(),
                "originalName", fileEntity.getOriginalName(),
                "fileSize", fileEntity.getFileSize(),
                "mimeType", fileEntity.getMimeType(),
                "createdAt", fileEntity.getCreatedAt(),
                "downloadUrl", "/api/files/" + fileEntity.getId() + "/download"
            ));
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取文件信息失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    /**
     * 获取关联文件列表
     * 
     * @param relatedType 关联类型
     * @param relatedId 关联ID
     * @return 文件列表
     */
    @GetMapping("/related")
    public ResponseEntity<Map<String, Object>> getRelatedFiles(
            @RequestParam String relatedType,
            @RequestParam Long relatedId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            FileEntity.RelatedType type = FileEntity.RelatedType.valueOf(relatedType.toUpperCase());
            List<FileEntity> files = fileService.getFilesByRelated(type, relatedId);
            
            result.put("success", true);
            result.put("data", files.stream().map(file -> Map.of(
                "id", file.getId(),
                "originalName", file.getOriginalName(),
                "fileSize", file.getFileSize(),
                "mimeType", file.getMimeType(),
                "createdAt", file.getCreatedAt(),
                "downloadUrl", "/api/files/" + file.getId() + "/download"
            )).toList());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取文件列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    /**
     * 预览文件（主要用于图片预览）
     * 
     * @param fileId 文件ID
     * @return 文件内容（用于在线预览）
     */
    @GetMapping("/{fileId}/preview")
    public ResponseEntity<InputStreamResource> previewFile(@PathVariable Long fileId) {
        try {
            FileEntity fileEntity = fileService.getFileById(fileId);
            if (fileEntity == null) {
                return ResponseEntity.notFound().build();
            }
            
            InputStream inputStream = fileService.downloadFile(fileId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(fileEntity.getMimeType()));
            // 设置为inline，浏览器会尝试直接显示而不是下载
            headers.setContentDispositionFormData("inline", 
                URLEncoder.encode(fileEntity.getOriginalName(), StandardCharsets.UTF_8));
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 删除文件
     * 
     * @param fileId 文件ID
     * @return 删除结果
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable Long fileId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            fileService.deleteFile(fileId);
            
            result.put("success", true);
            result.put("message", "文件删除成功");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "文件删除失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    /**
     * Editor.js 图片上传接口
     * 
     * @param image 上传的图片
     * @return Editor.js 格式的响应
     */
    @PostMapping("/upload/image")
    public ResponseEntity<Map<String, Object>> uploadImageForEditor(@RequestParam("image") MultipartFile image) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (image.isEmpty()) {
                result.put("success", 0);
                result.put("message", "图片不能为空");
                return ResponseEntity.badRequest().body(result);
            }
            
            // 检查是否为图片
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                result.put("success", 0);
                result.put("message", "只允许上传图片文件");
                return ResponseEntity.badRequest().body(result);
            }
            
            FileEntity fileEntity = fileService.uploadFile(image, FileEntity.RelatedType.OTHER, null);
            
            // Editor.js 格式的响应
            result.put("success", 1);
            result.put("file", Map.of(
                "url", "/api/files/" + fileEntity.getId() + "/download"
            ));
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", 0);
            result.put("message", "图片上传失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
}
