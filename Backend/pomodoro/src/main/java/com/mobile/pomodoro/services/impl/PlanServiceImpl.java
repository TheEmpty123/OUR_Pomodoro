package com.mobile.pomodoro.services.impl;

import com.mobile.pomodoro.CustomException.UserNotFoundException;
import com.mobile.pomodoro.dto.response.PlanRequestDTO.PlanRequestDTO;
import com.mobile.pomodoro.dto.response.PlanResponseDTO.PlanResponseDTO;
import com.mobile.pomodoro.dto.response.TaskToEditResponseDTO.TaskToEditResponseDTO;
import com.mobile.pomodoro.entities.Plan;
import com.mobile.pomodoro.mapper.response.PlanResponseDTOMapper;
import com.mobile.pomodoro.repositories.PlanRepository;
import com.mobile.pomodoro.repositories.PlanTaskRepository;
import com.mobile.pomodoro.repositories.UserRepository;
import com.mobile.pomodoro.services.IPlanService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Service
public abstract class PlanServiceImpl extends AService implements IPlanService {
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
    @Transactional(readOnly = true)
    public PlanResponseDTO findRecentPlan(String username) throws UserNotFoundException {
        return findPlan(username);
    }

    @Override
    public TaskToEditResponseDTO convertPlanToEditFormat(Long planId) {
        return null;
    }


    public PlanResponseDTO processPlanWithoutSaving(PlanRequestDTO planRequest) {
        List<PlanResponseDTO.TaskDTO> steps = new ArrayList<>();
        int order = 1;
        for (PlanRequestDTO.StepDTO step : planRequest.getSteps()) {
            steps.add(createStep(order++, step.getPlan_title(), step.getPlan_duration()));
            steps.add(createStep(order++, "short break", planRequest.getS_break_duration()));
            if (order <= planRequest.getSteps().size() * 2) {
                steps.add(createStep(order++, "long break", planRequest.getL_break_duration()));
            }
        }

        PlanResponseDTO response = new PlanResponseDTO();
        response.setPlanTitle(planRequest.getTitle());
        response.setPlanId(null);  // Không lưu vào cơ sở dữ liệu, nên planId = null...
        response.setSteps(steps);

        return response;
    }

    private PlanResponseDTO.TaskDTO createStep(int order, String title, int duration) {
        PlanResponseDTO.TaskDTO step = new PlanResponseDTO().getSteps().get(0);
        step.setTask_order(order);
        step.setTask_name(title);
        step.setDuration(duration);
        return step;
    }

}
