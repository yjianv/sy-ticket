package com.syticket.mapper;

import com.syticket.entity.FileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文件数据访问层
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Mapper
public interface FileMapper {
    
    /**
     * 根据ID查找文件
     * 
     * @param id 文件ID
     * @return 文件信息
     */
    @Select("SELECT * FROM files WHERE id = #{id}")
    FileEntity findById(@Param("id") Long id);
    
    /**
     * 根据关联类型和ID查找文件列表
     * 
     * @param relatedType 关联类型
     * @param relatedId 关联ID
     * @return 文件列表
     */
    List<FileEntity> findByRelated(@Param("relatedType") String relatedType, @Param("relatedId") Long relatedId);
    
    /**
     * 根据文件hash查找文件
     * 
     * @param fileHash 文件hash
     * @return 文件信息
     */
    @Select("SELECT * FROM files WHERE file_hash = #{fileHash}")
    FileEntity findByHash(@Param("fileHash") String fileHash);
    
    /**
     * 插入文件记录
     * 
     * @param file 文件信息
     * @return 影响的行数
     */
    int insert(FileEntity file);
    
    /**
     * 更新文件记录
     * 
     * @param file 文件信息
     * @return 影响的行数
     */
    int update(FileEntity file);
    
    /**
     * 删除文件记录
     * 
     * @param id 文件ID
     * @return 影响的行数
     */
    @Select("DELETE FROM files WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据上传者查找文件列表
     * 
     * @param uploaderId 上传者ID
     * @return 文件列表
     */
    @Select("SELECT * FROM files WHERE uploader_id = #{uploaderId} ORDER BY created_at DESC")
    List<FileEntity> findByUploader(@Param("uploaderId") Long uploaderId);
}
