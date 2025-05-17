package com.mobile.pomodoro.services.impl;

import com.mobile.pomodoro.dto.request.DailyTaskRequestDTO;
import com.mobile.pomodoro.dto.request.PlanRequestDTO;
import com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO.DailyTaskResponeseDTO;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;
import com.mobile.pomodoro.dto.response.PlanResponseDTO.PlanResponseDTO;
import com.mobile.pomodoro.entities.DailyTask;
import com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO.DailyTaskResponeseDTO.SingleDailyTaskDTO;
import com.mobile.pomodoro.entities.Plan;
import com.mobile.pomodoro.entities.PlanTask;
import com.mobile.pomodoro.entities.User;
import com.mobile.pomodoro.repositories.DailyTaskRepository;
import com.mobile.pomodoro.repositories.PlanRepository;
import com.mobile.pomodoro.repositories.PlanTaskRepository;
import com.mobile.pomodoro.services.IDailyTaskService;

import com.mobile.pomodoro.services.IPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DailyTaskServiceImpl extends AService implements IDailyTaskService {
    @Autowired
    private DailyTaskRepository dailyTaskRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private PlanTaskRepository planTaskRepository;

    DailyTaskServiceImpl() {
        initData();
    }

    @Override
    public void initData() {
        log.setName(this.getClass().getSimpleName());
        log.info("Initializing data");
    }

    @Override
    public DailyTaskResponeseDTO getAllDailyTaskByUser(Long userId) {
        log.info("Đang lấy danh sách công việc hàng ngày cho userId: {}", userId);
        try {
            List<DailyTask> dailytasks = dailyTaskRepository.findByUserId(userId);
            if (dailytasks.isEmpty()) {
                log.warn("Không tìm thấy công việc nào cho userId: {}", userId);
            }
            List<SingleDailyTaskDTO> dailytaskDTOs = dailytasks.stream()
                    .map(dailytask -> SingleDailyTaskDTO.builder()
                            .plan_id(dailytask.getPlanId())
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
    public MessageResponseDTO createDailyTask(DailyTaskRequestDTO request, Long userId) {
        log.info("Bắt đầu tạo DailyTask cho userId: ", userId);
        try {
            User user = new User();
            user.setUserId(userId);
            Plan plan = Plan.builder()
                    .title(request.getDaily_task_description())
                    .createdAt(LocalDateTime.now())
                    .user(user)
                    .build();
            planRepository.save(plan);
            List<PlanTask> planTasks = new ArrayList<>();
            int order = 1;

            for (int i = 0; i < request.getSteps().size(); i++) {
                DailyTaskRequestDTO.StepRequest step = request.getSteps().get(i);

                int fullDuration = step.getPlan_duration();
                int halfDuration = fullDuration / 2;
                planTasks.add(new PlanTask(plan, step.getPlan_title(), halfDuration, order++));

                planTasks.add(new PlanTask(plan, "short break", request.getS_break_duration(), order++));
                planTasks.add(new PlanTask(plan, step.getPlan_title(), fullDuration - halfDuration, order++));
                if (i < request.getSteps().size() - 1) {
                    planTasks.add(new PlanTask(plan, "long break", request.getL_break_duration(), order++));
                }
            }

            planTaskRepository.saveAll(planTasks);
            DailyTask dailyTask = DailyTask.builder()
                    .title(request.getTitle())
                    .planId(plan.getPlanId())
                    .userId(userId)
                    .created_At(LocalDateTime.now())
                    .isDone(0)
                    .build();

            dailyTaskRepository.save(dailyTask);
            log.info(" Đã lưu DailyTask với id =", dailyTask.getId());

            return new MessageResponseDTO("Tạo daily task thành công");
        } catch (Exception e) {
            log.error("Lỗi khi tạo DailyTask: " + e.getMessage(), e);
            return new MessageResponseDTO("Tạo daily task thất bại: " + e.getMessage());
        }
    }


}
