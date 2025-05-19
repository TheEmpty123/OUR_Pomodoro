package com.mobile.pomodoro.services.impl;

import com.mobile.pomodoro.dto.request.BaodailyTaskRequestDTO;
import com.mobile.pomodoro.dto.request.PlanRequestDTO;
import com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO.DailyTaskResponeseDTO;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;
import com.mobile.pomodoro.dto.response.PlanToEditResponseDTO.PlanToEditResponseDTO;
import com.mobile.pomodoro.dto.response.ToDoResponeseDTO.ToDoResponseDTO;
import com.mobile.pomodoro.entities.DailyTask;
import com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO.DailyTaskResponeseDTO.SingleDailyTaskDTO;
import com.mobile.pomodoro.entities.Plan;
import com.mobile.pomodoro.entities.PlanTask;
import com.mobile.pomodoro.entities.User;
import com.mobile.pomodoro.repositories.DailyTaskRepository;
import com.mobile.pomodoro.repositories.PlanRepository;
import com.mobile.pomodoro.repositories.PlanTaskRepository;
import com.mobile.pomodoro.services.IDailyTaskService;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DailyTaskServiceImpl extends AService implements IDailyTaskService {
    @Autowired
    private DailyTaskRepository toDoRepository;
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private PlanTaskRepository planTaskRepository;
    @Autowired
    private PlanServiceImpl planService;


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
    public PlanToEditResponseDTO getDailyTaskPlanDetails(Long dailyTaskId, User user) throws Exception {
        return null;
    }

    @Override
    @Transactional
    public MessageResponseDTO updateDailyTask(Long dailyTaskId, PlanToEditResponseDTO request, User user) throws Exception {
        // Kiểm tra DailyTask tồn tại
        Optional<DailyTask> dailyTaskOptional = toDoRepository.findById(dailyTaskId);
        if (dailyTaskOptional.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy daily task");
        }
        DailyTask dailyTask = dailyTaskOptional.get();

        // Kiểm tra quyền sở hữu
        if (!dailyTask.getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("Người dùng không sở hữu daily task này");
        }

        // Lấy Plan liên kết
        Long planId = dailyTask.getPlanId();
        if (planId == null) {
            throw new IllegalArgumentException("Daily task không liên kết với kế hoạch");
        }
        Optional<Plan> planOptional = planRepository.findById(planId);
        if (planOptional.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy kế hoạch");
        }
        Plan plan = planOptional.get();

        // Cập nhật title
        plan.setTitle(request.getTitle());
        dailyTask.setTitle(request.getTitle());
        planRepository.save(plan);
        toDoRepository.save(dailyTask);

        toDoRepository.deleteByPlanId(planId);

        List<PlanTask> tasks = new ArrayList<>();
        int order = 1;
        for (PlanToEditResponseDTO.Step step : request.getSteps()) {
            int duration = step.getPlan_duration();
            int half = duration / 2;

            tasks.add(new PlanTask(plan, step.getPlan_title(), half, order++));
            tasks.add(new PlanTask(plan, "short break", request.getS_break_duration(), order++));
            tasks.add(new PlanTask(plan, step.getPlan_title(), duration - half, order++));

            if (step.getOrder() < request.getSteps().size()) {
                tasks.add(new PlanTask(plan, "long break", request.getL_break_duration(), order++));
            }
        }
        planTaskRepository.saveAll(tasks);

        return MessageResponseDTO.builder()
                .message("Cập nhật kế hoạch thành công")
                .build();
    }

    @Override
    @Transactional
    public MessageResponseDTO completeDailyTask(Long dailyTaskId, User user) throws Exception {
        // Kiểm tra DailyTask tồn tại
        Optional<DailyTask> dailyTaskOptional = toDoRepository.findById(dailyTaskId);
        if (dailyTaskOptional.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy daily task");
        }
        DailyTask dailyTask = dailyTaskOptional.get();

        if (!dailyTask.getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("Người dùng không sở hữu daily task này");
        }

        // Đánh dấu hoàn thành
        dailyTask.setIsDone(1);
        toDoRepository.save(dailyTask);

        return MessageResponseDTO.builder()
                .message("Đánh dấu hoàn thành daily task thành công")
                .build();
    }

    @Override
    @Transactional
    public MessageResponseDTO deleteDailyTask(Long dailyTaskId, User user) throws Exception {
        // Kiểm tra DailyTask tồn tại
        Optional<DailyTask> dailyTaskOptional = toDoRepository.findById(dailyTaskId);
        if (dailyTaskOptional.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy daily task");
        }
        DailyTask dailyTask = dailyTaskOptional.get();

        if (!dailyTask.getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("Người dùng không sở hữu daily task này");
        }

        toDoRepository.delete(dailyTask);

        return MessageResponseDTO.builder()
                .message("Xóa daily task thành công")
                .build();
    }

    @Override
    public MessageResponseDTO createDailyTask(BaodailyTaskRequestDTO request, User user) {
        log.info("Tạo daily task rồi lưu lại cho user id: " + user.getUserId() + " bởi user: " + user.getUsername());
        planService.createPlan(PlanRequestDTO.builder()
                .title(request.getDaily_task_description())
                .s_break_duration(request.getS_break_duration())
                .l_break_duration(request.getL_break_duration())
                .steps(request.getSteps())
                .build(), user);

        long id = PlanServiceImpl.save;

        log.info("Daily task đã được tạo và lưu lại id: " + id);
        DailyTask dailyTask = DailyTask.builder()
                .title(request.getTitle())
                .isDone(0)
                .planId(id)
                .userId(user.getUserId())
                .build();

        log.info("Daily task đã được tạo và lưu lại id: " + dailyTask.getPlanId());
        try {
            log.info("planid: " + dailyTask.getPlanId());
            toDoRepository.save(dailyTask);
            log.info("Tạo daily task thành công cho user id: " + user.getUserId());

            return
                    MessageResponseDTO.builder()
                            .message("Tạo daily thành công")
                            .build();
        } catch (Exception e) {
            log.error("Lỗi khi tạo daily task: " + e.getMessage(), e);
            return MessageResponseDTO.builder()
                    .message(" không thành công")
                    .build();
        }


    }
}
