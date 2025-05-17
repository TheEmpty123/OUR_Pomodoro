package com.mobile.pomodoro.services.impl;

import com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO.DailyTaskResponeseDTO;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;
import com.mobile.pomodoro.dto.response.ToDoResponeseDTO.ToDoResponseDTO;
import com.mobile.pomodoro.entities.DailyTask;
import com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO.DailyTaskResponeseDTO.SingleDailyTaskDTO;
import com.mobile.pomodoro.repositories.DailyTaskRepository;
import com.mobile.pomodoro.services.IDailyTaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DailyTaskServiceImpl extends AService implements IDailyTaskService {
    @Autowired
    private DailyTaskRepository toDoRepository;

    DailyTaskServiceImpl() {
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
}
