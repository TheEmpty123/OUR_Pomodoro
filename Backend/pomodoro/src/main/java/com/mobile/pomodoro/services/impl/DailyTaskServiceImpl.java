package com.mobile.pomodoro.services.impl;

import com.mobile.pomodoro.dto.request.*;
import com.mobile.pomodoro.dto.request.PlanToEditRequestDTO;
import com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO;
import com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO.*;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;
import com.mobile.pomodoro.dto.response.PlanToEditResponseDTO;
import com.mobile.pomodoro.entities.DailyTask;
import com.mobile.pomodoro.entities.Plan;
import com.mobile.pomodoro.entities.PlanTask;
import com.mobile.pomodoro.entities.User;
import com.mobile.pomodoro.repositories.DailyTaskRepository;
import com.mobile.pomodoro.repositories.PlanRepository;
import com.mobile.pomodoro.repositories.PlanTaskRepository;
import com.mobile.pomodoro.services.IDailyTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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
            throw new RuntimeException("Failed");
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

            return new MessageResponseDTO("Succeed");
        } catch (Exception e) {
            log.error("Lỗi khi tạo DailyTask: " + e.getMessage(), e);
            return new MessageResponseDTO("Failed");
        }
    }

    @Override
    public PlanToEditResponseDTO getDailyTaskPlanDetails(Long dailyTaskId, User user) {
        log.info("Bắt đầu lấy chi tiết DailyTask ID: {}", dailyTaskId);
        try {
            DailyTask dailyTask = dailyTaskRepository.findById(dailyTaskId).orElse(null);
            if (dailyTask == null) {
                throw new IllegalArgumentException("Không tìm thấy daily task");
            }
            if (!dailyTask.getUserId().equals(user.getUserId())) {
                throw new IllegalArgumentException("Người dùng không sở hữu daily task này");
            }
            Long planId = dailyTask.getPlanId();
            if (planId == null) {
                throw new IllegalArgumentException("Daily task không liên kết với kế hoạch nào");
            }
            Plan plan = planRepository.findById(planId).orElse(null);
            if (plan == null) {
                throw new IllegalArgumentException("Không tìm thấy kế hoạch");
            }
            List<PlanTask> tasks = planTaskRepository.findTaskByPlanId(planId);
            List<PlanToEditResponseDTO.Step> stepList = new ArrayList<>();
            int order = 1;
            for (int i = 0; i < tasks.size(); ) {
                PlanTask currentTask = tasks.get(i);
                String title = currentTask.getTask_name();

                if (title.equalsIgnoreCase("short break") || title.equalsIgnoreCase("long break")) {
                    i++;
                    continue;
                }
                int duration = (int) currentTask.getDuration();
                if (i + 2 < tasks.size()) {
                    PlanTask nextTask = tasks.get(i + 2);
                    if (nextTask.getTask_name().equals(title)) {
                        duration += nextTask.getDuration();
                    }
                }
                stepList.add(PlanToEditResponseDTO.Step.builder()
                        .order(order++)
                        .plan_title(title)
                        .plan_duration(duration)
                        .build());
                i += 3;
            }
            int sBreakDuration = 0;
            for (PlanTask task : tasks) {
                if (task.getTask_name().equalsIgnoreCase("short break")) {
                    sBreakDuration = (int) task.getDuration();
                    break;
                }
            }
            int lBreakDuration = 0;
            for (PlanTask task : tasks) {
                if (task.getTask_name().equalsIgnoreCase("long break")) {
                    lBreakDuration = (int) task.getDuration();
                    break;
                }
            }
            log.info("Hoàn tất lấy chi tiết kế hoạch cho DailyTask ID: {}", dailyTaskId);
            return PlanToEditResponseDTO.builder()
                    .title(plan.getTitle())
                    .s_break_duration(sBreakDuration)
                    .l_break_duration(lBreakDuration)
                    .steps(stepList)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi khi lấy chi tiết DailyTask: " + e.getMessage(), e);
            throw new RuntimeException("Không thể lấy chi tiết DailyTask. " + e.getMessage());
        }
    }

    @Override
    public PlanToEditResponseDTO planToEdit(PlanToEditRequestDTO request, User user) {
        log.info(String.format("Bắt đầu xử lý plan-to-edit cho planId: %d và userId: %d", request.getPlan_id(), user.getUserId()));
        try {
            List<PlanTask> tasks = planTaskRepository.findTaskByPlanId(request.getPlan_id());
            if (tasks.isEmpty()) {
                log.warn(String.format("Không tìm thấy kế hoạch hoặc không được phép truy cập cho planId: %d", request.getPlan_id()));
                throw new IllegalArgumentException("Kế hoạch không tồn tại hoặc không thuộc về người dùng");
            }

            int sBreakDuration = 0;
            int lBreakDuration = 0;
            for (PlanTask task : tasks) {
                if ("short break".equalsIgnoreCase(task.getTask_name())) {
                    sBreakDuration = (int) task.getDuration();
                    break;
                }
            }
            for (PlanTask task : tasks) {
                if ("long break".equalsIgnoreCase(task.getTask_name())) {
                    lBreakDuration = (int) task.getDuration();
                    break;
                }
            }
            Map<String, Integer> taskDurations = new HashMap<>();
            for (PlanTask task : tasks) {
                String title = task.getTask_name();
                if (!"short break".equalsIgnoreCase(title) && !"long break".equalsIgnoreCase(title)) {
                    if (taskDurations.containsKey(title)) {
                        taskDurations.put(title, taskDurations.get(title) + (int) task.getDuration());
                    } else {
                        taskDurations.put(title, (int) task.getDuration());
                    }
                }
            }
            List<PlanToEditResponseDTO.Step> stepList = new ArrayList<>();
            int order = 1;
            for (Map.Entry<String, Integer> entry : taskDurations.entrySet()) {
                PlanToEditResponseDTO.Step step = PlanToEditResponseDTO.Step.builder()
                        .plan_title(entry.getKey())
                        .plan_duration(entry.getValue())
                        .order(order++)
                        .build();
                stepList.add(step);
            }
            PlanToEditResponseDTO response = PlanToEditResponseDTO.builder()
                    .title(request.getTitle())
                    .s_break_duration(sBreakDuration)
                    .l_break_duration(lBreakDuration)
                    .steps(stepList)
                    .build();
            log.info(String.format("Hoàn tất xử lý plan-to-edit cho planId: %d", request.getPlan_id()));
            return response;
        } catch (Exception e) {
            log.error(String.format("Lỗi khi xử lý plan-to-edit: %s", e.getMessage()), e);
            throw new RuntimeException("Không thể xử lý kế hoạch để chỉnh sửa: " + e.getMessage());
        }
    }

    @Override
    public MessageResponseDTO completeDailyTask(Long id, User user) {
        log.info("Bắt đầu đánh dấu hoàn thành DailyTask ID: " + id + " cho userId: " + (user != null ? user.getUserId() : "null"));
        try {
            if (id == null) {
                log.error("ID tác vụ hàng ngày không hợp lệ");
                throw new IllegalArgumentException("ID tác vụ hàng ngày không hợp lệ");
            }
            if (user == null) {
                log.error("Không tìm thấy thông tin người dùng cho yêu cầu hoàn thành task ID: " + id);
                throw new IllegalArgumentException("Không tìm thấy thông tin người dùng");
            }

            Optional<DailyTask> dailyTaskOptional = dailyTaskRepository.findById(id);
            if (!dailyTaskOptional.isPresent()) {
                log.error("Không tìm thấy DailyTask với ID: " + id);
                throw new IllegalArgumentException("Không thành công");
            }

            DailyTask dailyTask = dailyTaskOptional.get();
            if (!dailyTask.getUserId().equals(user.getUserId())) {
                log.error("Người dùng " + user.getUserId() + " không sở hữu DailyTask " + id);
                throw new IllegalArgumentException("Truy cập không được phép vào tác vụ hàng ngày");
            }

            if (dailyTask.getIsDone() == 1) {
                log.info("DailyTask ID: " + id + " đã được đánh dấu hoàn thành trước đó");
                return new MessageResponseDTO("Tác vụ hàng ngày đã được hoàn thành trước đó");
            }

            dailyTask.setIsDone(1);
            dailyTaskRepository.save(dailyTask);
            log.info("Đã đánh dấu hoàn thành DailyTask ID: " + id);
            return new MessageResponseDTO("Tác vụ hàng ngày đã được đánh dấu hoàn thành");
        } catch (IllegalArgumentException e) {
            log.error("Lỗi xác thực khi đánh dấu hoàn thành DailyTask ID: " + id + ": " + e.getMessage());
            return new MessageResponseDTO(e.getMessage());
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi đánh dấu hoàn thành DailyTask ID: " + id + ": " + e.getMessage(), e);
            return new MessageResponseDTO("Tác vụ hàng ngày không thành công");
        }
    }

    @Override
    public MessageResponseDTO deleteDailyTask(Long id, User user) {
        log.info("Bắt đầu xóa DailyTask ID: " + id + " cho userId: " + (user != null ? user.getUserId() : "null"));
        try {
            if (id == null) {
                log.error("ID tác vụ hàng ngày không hợp lệ");
                throw new IllegalArgumentException("ID tác vụ hàng ngày không hợp lệ");
            }
            if (user == null) {
                log.error("Không tìm thấy thông tin người dùng cho yêu cầu xóa task ID: " + id);
                throw new IllegalArgumentException("Không tìm thấy thông tin người dùng");
            }

            Optional<DailyTask> dailyTaskOptional = dailyTaskRepository.findById(id);
            if (!dailyTaskOptional.isPresent()) {
                log.error("Không tìm thấy DailyTask với ID: " + id);
                throw new IllegalArgumentException("Không thành công");
            }

            DailyTask dailyTask = dailyTaskOptional.get();
            if (!dailyTask.getUserId().equals(user.getUserId())) {
                log.error("Người dùng " + user.getUserId() + " không sở hữu DailyTask " + id);
                throw new IllegalArgumentException("Truy cập không được phép vào tác vụ hàng ngày");
            }

            Long planId = dailyTask.getPlanId();
            if (planId != null) {
                planTaskRepository.deleteByPlanId(planId);
                log.info("Đã xóa các PlanTask liên quan đến planId: " + planId);
            }

            dailyTaskRepository.deleteById(id);
            log.info("Đã xóa DailyTask ID: " + id);
            return new MessageResponseDTO("Xóa tác vụ hàng ngày thành công");
        } catch (IllegalArgumentException e) {
            log.error("Lỗi xác thực khi xóa DailyTask ID: " + id + ": " + e.getMessage());
            return new MessageResponseDTO(e.getMessage());
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi xóa DailyTask ID: " + id + ": " + e.getMessage(), e);
            return new MessageResponseDTO("Không thể xóa tác vụ hàng ngày: Lỗi hệ thống");
        }
    }
}
