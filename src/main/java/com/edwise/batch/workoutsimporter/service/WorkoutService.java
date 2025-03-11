package com.edwise.batch.workoutsimporter.service;

import com.edwise.batch.workoutsimporter.model.Workout;
import com.edwise.batch.workoutsimporter.repository.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;

    public WorkoutService(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public Optional<LocalDateTime> getLastEndTime() {
        return workoutRepository.findTopByOrderByEndTimeDesc()
                                .map(Workout::getEndTime);
    }
}
