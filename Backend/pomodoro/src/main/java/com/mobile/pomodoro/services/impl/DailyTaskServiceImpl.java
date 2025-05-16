package com.mobile.pomodoro.services.impl;

import com.mobile.pomodoro.dto.request.PlanToEditRequestDTO;
import com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO.DailyTaskResponeseDTO;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;
import com.mobile.pomodoro.dto.response.PlanToEditResponseDTO.PlanToEditResponseDTO;
import com.mobile.pomodoro.dto.response.ToDoResponeseDTO.ToDoResponseDTO;
import com.mobile.pomodoro.entities.DailyTask;
import com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO.DailyTaskResponeseDTO.SingleDailyTaskDTO;
import com.mobile.pomodoro.entities.User;
import com.mobile.pomodoro.repositories.DailyTaskRepository;
import com.mobile.pomodoro.services.IDailyTaskService;

import com.mobile.pomodoro.services.IPlanService;
import com.mobile.pomodoro.utils.LogObj;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DailyTaskServiceImpl extends AService implements IDailyTaskService {
    @Autowired
    private DailyTaskRepository toDoRepository;
    private final IPlanService planService;
    private final LogObj log = new LogObj("DailyTaskServiceImpl");

    DailyTaskServiceImpl(IPlanService planService) {
        this.planService = planService;
        initData();
    }

    @Override
    public void initData() {
        log.setName(this.getClass().getSimpleName());
        log.info("Initializing data");
    }
    @Override
    public DailyTaskResponeseDTO getAllDailyTaskByUser (Long userId) {
        log.info("Đang lấy danh sách công việc hàng ngày cho userId: {}", userId);
        try {
        List<DailyTask> dailytasks = toDoRepository.findByUserId(userId);
        if (dailytasks.isEmpty()) {
            log.warn("Không tìm thấy công việc nào cho userId: {}", userId);
        }
        List<SingleDailyTaskDTO> dailytaskDTOs = dailytasks.stream()
                .map(dailytask -> SingleDailyTaskDTO.builder()
                        .title(dailytask.getTitle())
                        .is_done(dailytask.getIsDone())
                        .build())
                .collect(Collectors.toList());

        return DailyTaskResponeseDTO.builder()
                .list(dailytaskDTOs)
                .build();
        } catch (Exception e) {
            log.error("Lỗi khi lấy DailyTask: " + e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy công việc hàng ngày", e);
        }
    }
    @Override
    @Transactional
    public PlanToEditResponseDTO getDailyTaskPlanDetails(Long dailyTaskId, User user) throws Exception {
        log.info("Lấy chi tiết kế hoạch cho daily task id: " + dailyTaskId + " bởi người dùng: " + user.getUsername());

        // Truy vấn DailyTask theo id
        Optional<DailyTask> dailyTaskOptional = toDoRepository.findById(dailyTaskId);
        if (dailyTaskOptional.isEmpty()) {
            log.error("Không tìm thấy daily task với id: " + dailyTaskId);
            throw new IllegalArgumentException("Không tìm thấy daily task");
        }
        DailyTask dailyTask = dailyTaskOptional.get();

        // Kiểm tra quyền sở hữu
        if (!dailyTask.getUserId().equals(user.getUserId())) {
            log.error("Người dùng: " + user.getUsername() + " không sở hữu daily task id: " + dailyTaskId);
            throw new IllegalArgumentException("Người dùng không sở hữu daily task này");
        }

        // Lấy plan_id từ DailyTask
        Long planId = dailyTask.getPlanId();
        if (planId == null) {
            log.error("Daily task id: " + dailyTaskId + " không liên kết với kế hoạch nào");
            throw new IllegalArgumentException("Daily task không liên kết với kế hoạch");
        }

        // Tạo PlanToEditRequestDTO để gọi logic #FR2-2
        PlanToEditRequestDTO planRequest = new PlanToEditRequestDTO();
        planRequest.setPlan_id(planId);
        planRequest.setTitle(dailyTask.getTitle());
        // Không cần steps vì logic #FR2-2 lấy PlanTask từ cơ sở dữ liệu

        return planService.convertPlanToEdit(planRequest, user);
    }
}
