-- 创建模块表
CREATE TABLE modules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '模块ID',
    name VARCHAR(50) NOT NULL COMMENT '模块名称',
    description TEXT COMMENT '模块描述',
    owner_id BIGINT COMMENT '负责人用户ID',
    workspace_id BIGINT NOT NULL COMMENT '所属工作空间ID',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_workspace_id (workspace_id),
    INDEX idx_owner_id (owner_id),
    INDEX idx_name (name),
    INDEX idx_enabled (enabled),
    INDEX idx_sort_order (sort_order),
    
    CONSTRAINT fk_modules_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_modules_workspace FOREIGN KEY (workspace_id) REFERENCES workspaces(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统模块表';

-- 添加工单模块关联字段
ALTER TABLE tickets 
ADD COLUMN module_id BIGINT COMMENT '关联模块ID' AFTER workspace_id,
ADD INDEX idx_module_id (module_id),
ADD CONSTRAINT fk_tickets_module FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE SET NULL;

-- 插入一些默认模块数据（可选）
INSERT INTO modules (name, description, workspace_id, enabled, sort_order) 
SELECT 
    '系统管理' as name,
    '系统配置、用户管理等相关功能' as description,
    id as workspace_id,
    TRUE as enabled,
    1 as sort_order
FROM workspaces 
WHERE enabled = TRUE
LIMIT 1;

INSERT INTO modules (name, description, workspace_id, enabled, sort_order) 
SELECT 
    '工单系统' as name,
    '工单创建、处理、流转等功能' as description,
    id as workspace_id,
    TRUE as enabled,
    2 as sort_order
FROM workspaces 
WHERE enabled = TRUE
LIMIT 1;

INSERT INTO modules (name, description, workspace_id, enabled, sort_order) 
SELECT 
    '报表统计' as name,
    '各类数据报表和统计分析' as description,
    id as workspace_id,
    TRUE as enabled,
    3 as sort_order
FROM workspaces 
WHERE enabled = TRUE
LIMIT 1;
