package com.mobile.pomodoro.services.impl;

import com.mobile.pomodoro.CustomException.UserNotFoundException;
import com.mobile.pomodoro.dto.request.PlanRequestDTO;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;
import com.mobile.pomodoro.dto.response.PlanResponseDTO.PlanResponseDTO;
import com.mobile.pomodoro.entities.Plan;
import com.mobile.pomodoro.entities.PlanTask;
import com.mobile.pomodoro.entities.User;
import com.mobile.pomodoro.mapper.response.PlanResponseDTOMapper;
import com.mobile.pomodoro.repositories.PlanRepository;
import com.mobile.pomodoro.repositories.PlanTaskRepository;
import com.mobile.pomodoro.repositories.UserRepository;
import com.mobile.pomodoro.services.IPlanService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
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
        }
        catch (Exception e) {
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

                tasks.add(new PlanTask(plan, step.getPlan_title() , half, order++));
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





}
