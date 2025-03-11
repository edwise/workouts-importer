package com.edwise.batch.workoutsimporter.batch;

import com.edwise.batch.workoutsimporter.model.Workout;
import com.edwise.batch.workoutsimporter.service.WorkoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@StepScope
public class WorkoutItemProcessor implements ItemProcessor<Workout, Workout> {

    private static final Logger log = LoggerFactory.getLogger(WorkoutItemProcessor.class);

    private final LocalDateTime lastEndTime;

    public WorkoutItemProcessor(WorkoutService workoutService) {
        lastEndTime = workoutService.getLastEndTime().orElse(null);
        log.info("Last endTime in DB: {}", lastEndTime != null ? lastEndTime : "NO WORKOUTS IN DB");
    }

    @Override
    public Workout process(Workout workout) {
        if (lastEndTime != null && (workout.getEndTime().isBefore(lastEndTime) || workout.getEndTime().isEqual(lastEndTime))) {
            return null;
        }
        log.info("Workout to insert: {}", workout);
        return workout;
    }
}
