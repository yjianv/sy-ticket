package com.syticket.controller;

import com.syticket.entity.User;
import com.syticket.entity.Workspace;
import com.syticket.service.TicketService;
import com.syticket.service.UserService;
import com.syticket.service.WorkspaceService;
import com.syticket.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计报表控制器
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Controller
@RequestMapping("/reports")
public class ReportsController {
    
    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private WorkspaceService workspaceService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 统计报表页面
     */
    @GetMapping
    public String reports(@RequestParam(value = "workspaceId", required = false) Long workspaceId, Model model) {
        // 获取当前工作空间（由WorkspaceControllerAdvice统一处理）
        Workspace currentWorkspace = (Workspace) model.getAttribute("currentWorkspace");
        workspaceId = currentWorkspace != null ? currentWorkspace.getId() : null;
        model.addAttribute("workspaceId", workspaceId);
        
        return "reports/index";
    }
    
    /**
     * 获取统计概览数据
     */
    @GetMapping("/overview")
    @ResponseBody
    public Map<String, Object> getOverview(@RequestParam(value = "workspaceId", required = false) Long workspaceId) {
        Map<String, Object> overview = new HashMap<>();
        
        // 总工单数
        overview.put("totalTickets", ticketService.getTotalCount(workspaceId));
        
        // 各状态工单数
        overview.put("openTickets", ticketService.getCountByStatus(workspaceId, "OPEN"));
        overview.put("inProgressTickets", ticketService.getCountByStatus(workspaceId, "IN_PROGRESS"));
        overview.put("resolvedTickets", ticketService.getCountByStatus(workspaceId, "RESOLVED"));
        overview.put("closedTickets", ticketService.getCountByStatus(workspaceId, "CLOSED"));
        
        // 今日新增工单
        overview.put("todayNewTickets", ticketService.getTodayNewCount(workspaceId));
        
        // 逾期工单数
        overview.put("overDueTickets", ticketService.getOverDueCount(workspaceId));
        
        return overview;
    }
    
    /**
     * 获取状态分布数据
     */
    @GetMapping("/status-distribution")
    @ResponseBody
    public List<Map<String, Object>> getStatusDistribution(@RequestParam(value = "workspaceId", required = false) Long workspaceId) {
        return ticketService.getStatusDistribution(workspaceId);
    }
    
    /**
     * 获取优先级分布数据
     */
    @GetMapping("/priority-distribution")
    @ResponseBody
    public List<Map<String, Object>> getPriorityDistribution(@RequestParam(value = "workspaceId", required = false) Long workspaceId) {
        return ticketService.getPriorityDistribution(workspaceId);
    }
    
    /**
     * 获取类型分布数据
     */
    @GetMapping("/type-distribution")
    @ResponseBody
    public List<Map<String, Object>> getTypeDistribution(@RequestParam(value = "workspaceId", required = false) Long workspaceId) {
        return ticketService.getTypeDistribution(workspaceId);
    }
    
    /**
     * 获取创建趋势数据（最近30天）
     */
    @GetMapping("/creation-trend")
    @ResponseBody
    public Map<String, Object> getCreationTrend(@RequestParam(value = "workspaceId", required = false) Long workspaceId,
                                                @RequestParam(value = "days", defaultValue = "30") int days) {
        return ticketService.getCreationTrend(workspaceId, days);
    }
    
    /**
     * 获取用户工作量统计
     */
    @GetMapping("/user-workload")
    @ResponseBody
    public List<Map<String, Object>> getUserWorkload(@RequestParam(value = "workspaceId", required = false) Long workspaceId) {
        return ticketService.getUserWorkload(workspaceId);
    }
    
    /**
     * 获取时效性分析
     */
    @GetMapping("/time-analysis")
    @ResponseBody
    public Map<String, Object> getTimeAnalysis(@RequestParam(value = "workspaceId", required = false) Long workspaceId) {
        Map<String, Object> timeAnalysis = new HashMap<>();
        
        // 平均处理时间（小时）
        timeAnalysis.put("avgProcessTime", ticketService.getAverageProcessTime(workspaceId));
        
        // 平均解决时间（小时）
        timeAnalysis.put("avgResolveTime", ticketService.getAverageResolveTime(workspaceId));
        
        // 最快解决时间（小时）
        timeAnalysis.put("minResolveTime", ticketService.getMinResolveTime(workspaceId));
        
        // 最慢解决时间（小时）
        timeAnalysis.put("maxResolveTime", ticketService.getMaxResolveTime(workspaceId));
        
        return timeAnalysis;
    }
    
    /**
     * 获取工作空间分布统计
     */
    @GetMapping("/workspace-distribution")
    @ResponseBody
    public List<Map<String, Object>> getWorkspaceDistribution() {
        return ticketService.getWorkspaceDistribution();
    }
}
