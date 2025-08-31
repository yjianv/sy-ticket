-- 添加企业微信用户ID字段到用户表
ALTER TABLE users ADD COLUMN wechat_user_id VARCHAR(64) COMMENT '企业微信用户ID';

-- 添加索引以提高查询性能
CREATE INDEX idx_users_wechat_user_id ON users(wechat_user_id);
