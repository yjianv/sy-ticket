-- 添加用户默认工作空间字段
ALTER TABLE users 
ADD COLUMN default_workspace_id BIGINT COMMENT '默认工作空间ID',
ADD CONSTRAINT fk_user_default_workspace 
    FOREIGN KEY (default_workspace_id) REFERENCES workspaces(id);

-- 为现有用户设置默认工作空间（选择第一个可用的工作空间）
UPDATE users u 
SET default_workspace_id = (
    SELECT id FROM workspaces WHERE enabled = true ORDER BY id LIMIT 1
) 
WHERE u.default_workspace_id IS NULL;
