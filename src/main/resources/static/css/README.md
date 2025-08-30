# 样式架构说明

本目录包含研发工单系统的统一样式架构，采用模块化设计，提供一致的用户体验。

## 文件结构

```
css/
├── README.md           # 本说明文档
├── main.css           # 主样式文件，导入所有其他模块
├── design-system.css  # 设计系统变量定义
├── components.css     # 可复用组件样式
└── pages.css         # 页面特定样式
```

## 样式架构说明

### 1. 设计系统 (design-system.css)

定义了统一的设计变量，包括：

- **颜色系统**: 主色调、状态颜色、中性色等
- **字体系统**: 字体族、字体大小、行高、字重
- **间距系统**: 统一的间距规范
- **圆角系统**: 统一的圆角规范
- **阴影系统**: 统一的阴影效果
- **过渡动画**: 统一的动画效果
- **工单系统特定颜色**: 状态、优先级、类型的颜色定义

### 2. 组件样式 (components.css)

包含可复用的UI组件样式：

- **导航组件**: 侧边栏链接、活跃状态等
- **徽章组件**: 状态、优先级、类型徽章
- **按钮组件**: 主要、次要、成功、警告、危险按钮
- **表单组件**: 输入框、选择框、标签等
- **卡片组件**: 基础卡片、头部、内容、底部
- **表格组件**: 表格样式和hover效果
- **分页组件**: 分页导航样式
- **警告组件**: 成功、警告、错误提示
- **模态框组件**: 模态框、背景、头部、内容
- **头像组件**: 不同尺寸的用户头像
- **空状态组件**: 无数据时的提示样式
- **加载组件**: 加载动画效果

### 3. 页面样式 (pages.css)

包含特定页面的样式：

- **登录页面**: 登录表单、背景、布局
- **Editor.js**: 富文本编辑器样式
- **文件上传**: 拖拽上传组件样式
- **工单详情**: 评论、流转记录时间轴
- **统计卡片**: 仪表盘统计卡片
- **Toast通知**: 消息提示样式

### 4. 主样式文件 (main.css)

- 导入所有其他CSS模块
- 定义全局布局样式
- 应用头部、侧边栏、主内容区域样式
- 提供响应式设计支持
- 包含打印样式和暗色主题支持

## 使用方法

### 1. 引入样式

在HTML模板中引入主样式文件：

```html
<link rel="stylesheet" href="/css/main.css">
```

### 2. 使用设计变量

在CSS中使用定义的变量：

```css
.my-component {
  color: var(--color-text-primary);
  background-color: var(--color-surface);
  padding: var(--space-4);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-base);
}
```

### 3. 使用组件类

直接使用预定义的组件类：

```html
<!-- 按钮 -->
<button class="btn btn-primary">主要按钮</button>
<button class="btn btn-secondary">次要按钮</button>

<!-- 表单 -->
<div class="form-group">
  <label class="form-label required">标题</label>
  <input class="form-input" type="text" placeholder="请输入标题">
</div>

<!-- 卡片 -->
<div class="card">
  <div class="card-header">
    <h3 class="card-title">标题</h3>
  </div>
  <div class="card-body">
    内容
  </div>
</div>

<!-- 徽章 -->
<span class="badge status-open">待处理</span>
<span class="badge priority-high">高优先级</span>
```

## 设计原则

### 1. 一致性
- 统一的颜色、字体、间距规范
- 一致的交互反馈和动画效果
- 统一的组件样式和行为

### 2. 可维护性
- 模块化的CSS架构
- 语义化的类名命名
- 清晰的文件组织结构

### 3. 响应式设计
- 移动优先的设计方法
- 灵活的网格系统
- 适配不同屏幕尺寸

### 4. 可访问性
- 符合WCAG标准的颜色对比度
- 键盘导航支持
- 屏幕阅读器友好

### 5. 性能优化
- CSS变量减少重复代码
- 合理的选择器权重
- 优化的动画性能

## 维护指南

### 添加新组件
1. 在 `components.css` 中定义组件样式
2. 使用设计系统中的变量
3. 确保响应式设计
4. 添加必要的状态样式（hover、focus、disabled等）

### 添加新页面样式
1. 在 `pages.css` 中添加页面特定样式
2. 优先使用已有的组件类
3. 只在必要时添加页面特定样式

### 修改设计变量
1. 在 `design-system.css` 中修改变量值
2. 确保修改不会破坏现有组件
3. 测试所有页面的显示效果

## 浏览器支持

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

支持CSS自定义属性（CSS变量）的现代浏览器。

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本
- 建立设计系统
- 创建组件库
- 优化页面样式
- 统一样式架构
