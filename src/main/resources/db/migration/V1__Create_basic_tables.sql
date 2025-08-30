-- 创建用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    avatar VARCHAR(255) COMMENT '头像URL',
    phone VARCHAR(20) COMMENT '电话',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT = '用户表';

-- 创建工作空间表
CREATE TABLE workspaces (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '空间名称',
    code VARCHAR(20) NOT NULL UNIQUE COMMENT '空间代码',
    description TEXT COMMENT '空间描述',
    color VARCHAR(7) DEFAULT '#3B82F6' COMMENT '空间颜色',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT = '工作空间表';

-- 创建工单表
CREATE TABLE tickets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '工单标题',
    content LONGTEXT COMMENT '工单内容(富文本)',
    ticket_no VARCHAR(50) NOT NULL UNIQUE COMMENT '工单编号',
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级',
    status ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED', 'CANCELLED') NOT NULL DEFAULT 'OPEN' COMMENT '状态',
    type ENUM('BUG', 'FEATURE', 'IMPROVEMENT', 'QUESTION', 'OTHER') NOT NULL DEFAULT 'OTHER' COMMENT '类型',
    workspace_id BIGINT NOT NULL COMMENT '工作空间ID',
    creator_id BIGINT NOT NULL COMMENT '创建人ID',
    assignee_id BIGINT COMMENT '指派人ID',
    resolver_id BIGINT COMMENT '解决人ID',
    estimated_hours DECIMAL(10,2) COMMENT '预估工时',
    actual_hours DECIMAL(10,2) COMMENT '实际工时',
    due_date DATETIME COMMENT '截止日期',
    resolved_at DATETIME COMMENT '解决时间',
    closed_at DATETIME COMMENT '关闭时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    FOREIGN KEY (workspace_id) REFERENCES workspaces(id),
    FOREIGN KEY (creator_id) REFERENCES users(id),
    FOREIGN KEY (assignee_id) REFERENCES users(id),
    FOREIGN KEY (resolver_id) REFERENCES users(id),
    
    INDEX idx_workspace_id (workspace_id),
    INDEX idx_creator_id (creator_id),
    INDEX idx_assignee_id (assignee_id),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_created_at (created_at)
) COMMENT = '工单表';

-- 创建工单评论表
CREATE TABLE ticket_comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_id BIGINT NOT NULL COMMENT '工单ID',
    user_id BIGINT NOT NULL COMMENT '评论人ID',
    content LONGTEXT NOT NULL COMMENT '评论内容(富文本)',
    type ENUM('COMMENT', 'SYSTEM') NOT NULL DEFAULT 'COMMENT' COMMENT '评论类型',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    
    INDEX idx_ticket_id (ticket_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) COMMENT = '工单评论表';

-- 创建工单流转记录表
CREATE TABLE ticket_flows (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_id BIGINT NOT NULL COMMENT '工单ID',
    from_user_id BIGINT COMMENT '流转发起人ID',
    to_user_id BIGINT COMMENT '流转接收人ID',
    from_status VARCHAR(20) COMMENT '原状态',
    to_status VARCHAR(20) COMMENT '新状态',
    action VARCHAR(20) NOT NULL COMMENT '操作类型',
    reason TEXT COMMENT '流转原因',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    FOREIGN KEY (from_user_id) REFERENCES users(id),
    FOREIGN KEY (to_user_id) REFERENCES users(id),
    
    INDEX idx_ticket_id (ticket_id),
    INDEX idx_from_user_id (from_user_id),
    INDEX idx_to_user_id (to_user_id),
    INDEX idx_created_at (created_at)
) COMMENT = '工单流转记录表';

-- 创建文件表
CREATE TABLE files (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    stored_name VARCHAR(255) NOT NULL COMMENT '存储文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_size BIGINT NOT NULL COMMENT '文件大小(字节)',
    mime_type VARCHAR(100) COMMENT 'MIME类型',
    file_hash VARCHAR(64) COMMENT '文件hash值',
    uploader_id BIGINT NOT NULL COMMENT '上传人ID',
    related_type ENUM('TICKET', 'COMMENT', 'AVATAR', 'OTHER') NOT NULL DEFAULT 'OTHER' COMMENT '关联类型',
    related_id BIGINT COMMENT '关联ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    FOREIGN KEY (uploader_id) REFERENCES users(id),
    
    INDEX idx_uploader_id (uploader_id),
    INDEX idx_related (related_type, related_id),
    INDEX idx_created_at (created_at)
) COMMENT = '文件表';

-- 插入默认工作空间
INSERT INTO workspaces (name, code, description, color) VALUES 
('测试环境', 'TEST', '测试环境工作空间', '#10B981'),
('生产环境', 'PROD', '生产环境工作空间', '#EF4444');

-- 插入默认管理员用户 (密码: admin123)
INSERT INTO users (username, password, email, real_name) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM0m.EQZY8Ev9V/8j3ZK', 'admin@syticket.com', '系统管理员');
