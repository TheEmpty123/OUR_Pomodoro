package com.mobile.pomodoro.services.impl;

import com.mobile.pomodoro.CustomException.UserNotFoundException;
import com.mobile.pomodoro.dto.response.PlanResponseDTO.PlanResponseDTO;
import com.mobile.pomodoro.entities.Plan;
import com.mobile.pomodoro.mapper.response.PlanResponseDTOMapper;
import com.mobile.pomodoro.repositories.PlanRepository;
import com.mobile.pomodoro.repositories.PlanTaskRepository;
import com.mobile.pomodoro.repositories.UserRepository;
import com.mobile.pomodoro.services.IPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    @Transactional(readOnly = true)
    public PlanResponseDTO findRecentPlan(String username) throws UserNotFoundException {
        return findPlan(username);
    }
}
