package com.mobile.pomodoro.services.impl;

import com.mobile.pomodoro.CustomException.UserNotFoundException;
import com.mobile.pomodoro.dto.request.PlanRequestDTO;
import com.mobile.pomodoro.dto.request.PlanToEditRequestDTO;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;
import com.mobile.pomodoro.dto.response.PlanTaskResponeseDTO.PlanTaskResponeseDTO;
import com.mobile.pomodoro.dto.response.PlanResponseDTO.PlanResponseDTO;
import com.mobile.pomodoro.dto.response.PlanToEditResponseDTO.PlanToEditResponseDTO;
import com.mobile.pomodoro.dto.response.TaskToEditResponseDTO.TaskToEditResponseDTO;
import com.mobile.pomodoro.entities.Plan;
import com.mobile.pomodoro.entities.PlanTask;
import com.mobile.pomodoro.entities.User;
import com.mobile.pomodoro.mapper.response.PlanResponseDTOMapper;
import com.mobile.pomodoro.repositories.PlanRepository;
import com.mobile.pomodoro.repositories.PlanTaskRepository;
import com.mobile.pomodoro.repositories.UserRepository;
import com.mobile.pomodoro.services.IPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PlanServiceImpl extends AService implements IPlanService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private PlanResponseDTOMapper planResponseDTOMapper;
    @Autowired
    private PlanTaskRepository planTaskRepository;

    @Override
    public void initData() {
        log.setName(this.getClass().getSimpleName());
        log.info("Initializing data");
    }

    private boolean checkUserExists(String username) {
        return userRepository.findUsersByUsername(username).isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public PlanResponseDTO findPlan(String username) throws UserNotFoundException {
        if (checkUserExists(username))
            throw new UserNotFoundException(String.format("User with username: %s doesn't exist.", username));

        log.info("Finding plan");

        try {

            Optional<Plan> planOptional = planRepository.findRecentPlanByUsername(username);

            if (planOptional.isPresent()) {
                Plan plan = planOptional.get();
                var res = planResponseDTOMapper.mapToDTO(plan);
                log.info("Plan found: " + res);

                // Fetching tasks associated with the plan
                res.setSteps(planTaskRepository.findTaskByPlanId(plan.getPlanId())
                        .stream()
                        .map(planResponseDTOMapper::mapToDTO)
                        .toList());

                return res;
            }
        } catch (Exception e) {
            log.error("Error finding plan: " + e.getMessage());
        }
        return null;

    }

    @Override
    public MessageResponseDTO createPlan(PlanRequestDTO requestDTO, User user) {
        log.info("Tạo plan mới từ user: " + user.getUsername());
        try {
            Plan plan = Plan.builder()
                    .title(requestDTO.getTitle())
                    .createdAt(LocalDateTime.now())
                    .user(user)
                    .build();
            planRepository.save(plan);

            int order = 1;
            List<PlanTask> tasks = new ArrayList<>();

            for (int i = 0; i < requestDTO.getSteps().size(); i++) {
                PlanRequestDTO.StepRequest step = requestDTO.getSteps().get(i);
                int half = step.getPlan_duration() / 2;

                tasks.add(new PlanTask(plan, step.getPlan_title(), half, order++));
                tasks.add(new PlanTask(plan, "short break", requestDTO.getS_break_duration(), order++));
                tasks.add(new PlanTask(plan, step.getPlan_title(), step.getPlan_duration() - half, order++));

                if (i < requestDTO.getSteps().size() - 1) {
                    tasks.add(new PlanTask(plan, "long break", requestDTO.getL_break_duration(), order++));
                }
            }

            planTaskRepository.saveAll(tasks);
            log.info("Tạo plan thành công cho user id: " + user.getUserId());

            return MessageResponseDTO.builder()
                    .message("Tạo kế hoạch thành công")
                    .build();

        } catch (Exception e) {
            log.error("Lỗi khi tạo Plan: " + e.getMessage(), e);
            return MessageResponseDTO.builder()
                    .message("Lỗi khi tạo kế hoạch: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PlanResponseDTO findRecentPlan(String username) throws UserNotFoundException {
        return findPlan(username);
    }

    @Override
    public PlanTaskResponeseDTO processWithoutSaving(PlanRequestDTO requestDTO, User user) {
        log.info("Xử lý plan KHÔNG lưu từ user: " + user.getUsername());

        try {
            PlanTaskResponeseDTO responseDTO = new PlanTaskResponeseDTO();
            responseDTO.setTitle(requestDTO.getTitle());

            int order = 1;
            List<PlanTaskResponeseDTO.PlanTaskDTO> steps = new ArrayList<>();

            for (int i = 0; i < requestDTO.getSteps().size(); i++) {
                PlanRequestDTO.StepRequest step = requestDTO.getSteps().get(i);
                int duration = step.getPlan_duration();
                int half = duration / 2;

                steps.add(PlanTaskResponeseDTO.PlanTaskDTO.builder()
                        .task_name(step.getPlan_title())
                        .duration(half)
                        .task_order(order++)
                        .build());

                steps.add(PlanTaskResponeseDTO.PlanTaskDTO.builder()
                        .task_name("short break")
                        .duration(requestDTO.getS_break_duration())
                        .task_order(order++)
                        .build());

                steps.add(PlanTaskResponeseDTO.PlanTaskDTO.builder()
                        .task_name(step.getPlan_title())
                        .duration(duration - half)
                        .task_order(order++)
                        .build());

                if (i < requestDTO.getSteps().size() - 1) {
                    steps.add(PlanTaskResponeseDTO.PlanTaskDTO.builder()
                            .task_name("long break")
                            .duration(requestDTO.getL_break_duration())
                            .task_order(order++)
                            .build());
                }
            }

            responseDTO.setSteps(steps);
            log.info("Xử lý xong plan KHÔNG lưu: " + requestDTO.getTitle());
            return responseDTO;

        } catch (Exception e) {
            log.error("Lỗi khi xử lý plan không lưu: " + e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PlanToEditResponseDTO convertPlanToEdit(PlanToEditRequestDTO requestDTO, User user) {
        log.info("Chuyển đổi kế hoạch sang định dạng chỉnh sửa cho plan_id: " + requestDTO.getPlan_id() + " bởi người dùng: " + user.getUsername());

        try {
            if (checkUserExists(user.getUsername())) {
                throw new UserNotFoundException(String.format("Không tìm thấy người dùng với tên: %s.", user.getUsername()));
            }

            Optional<Plan> planOptional = planRepository.findById(requestDTO.getPlan_id());
            if (planOptional.isEmpty()) {
                log.error("Không tìm thấy kế hoạch với id: " + requestDTO.getPlan_id());
                throw new IllegalArgumentException("Không tìm thấy kế hoạch");
            }
            Plan plan = planOptional.get();
            if (!plan.getUser().getUserId().equals(user.getUserId())) {
                log.error("Người dùng: " + user.getUsername() + " không sở hữu plan_id: " + requestDTO.getPlan_id());
                throw new IllegalArgumentException("Người dùng không sở hữu kế hoạch này");
            }

            List<PlanTask> planTasks = planTaskRepository.findTaskByPlanId(requestDTO.getPlan_id());

            PlanToEditResponseDTO response = new PlanToEditResponseDTO();
            response.setTitle(requestDTO.getTitle());

            Integer sBreakDuration = null;
            Integer lBreakDuration = null;
            Map<String, Double> mergedTasks = new LinkedHashMap<>();

            for (PlanTask task : planTasks) {
                String taskName = task.getTask_name();
                Double duration = task.getDuration();
                if (duration == null) {
                    continue; // Bỏ qua nếu duration là null
                }
                if (taskName.equalsIgnoreCase("short break")) {
                    if (sBreakDuration == null) {
                        sBreakDuration = duration.intValue();
                    }
                    continue;
                } else if (taskName.equalsIgnoreCase("long break")) {
                    if (lBreakDuration == null) {
                        lBreakDuration = duration.intValue();
                    }
                    continue;
                }
                mergedTasks.merge(taskName, duration, Double::sum);
            }

            response.setS_break_duration(sBreakDuration);
            response.setL_break_duration(lBreakDuration);

            List<PlanTask> taskList = new ArrayList<>();
            int order = 1;
            for (Map.Entry<String, Double> entry : mergedTasks.entrySet()) {
                PlanTask task = new PlanTask();
                task.setId(requestDTO.getPlan_id());
                task.setTask_name(entry.getKey());
                task.setDuration(entry.getValue());
                task.setTask_order(order++);
                taskList.add(task);
            }
            List<PlanToEditResponseDTO.Step> responseSteps = new ArrayList<>();
            for (PlanTask task : taskList) {
                PlanToEditResponseDTO.Step step = new PlanToEditResponseDTO.Step();
                step.setPlan_title(task.getTask_name());
                step.setPlan_duration((int) task.getDuration());
                step.setOrder(task.getTask_order() - 1);
                responseSteps.add(step);
            }

            response.setSteps(responseSteps);
            log.info("Chuyển đổi kế hoạch sang định dạng chỉnh sửa thành công cho plan_id: " + requestDTO.getPlan_id());
            return response;

        } catch (Exception e) {
            log.error("Lỗi khi chuyển đổi kế hoạch sang định dạng chỉnh sửa: " + e.getMessage(), e);
            throw new RuntimeException("Không thể chuyển đổi kế hoạch sang định dạng chỉnh sửa: " + e.getMessage());
        }
    }


}
