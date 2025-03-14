package com.edwise.batch.workoutsimporter.batch;

import com.edwise.batch.workoutsimporter.helper.WorkoutIdGenerator;
import com.edwise.batch.workoutsimporter.model.Workout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class WorkoutItemProcessor implements ItemProcessor<Workout, Workout> {

    private static final Logger log = LoggerFactory.getLogger(WorkoutItemProcessor.class);

    @Override
    public Workout process(Workout workout) {
        fillWithGeneratedId(workout);
        return workout;
    }

    private void fillWithGeneratedId(Workout workout) {
        String generatedId = WorkoutIdGenerator.generateWorkoutId(workout);
        workout.setGenId(generatedId);

        log.info("Generated ID '{}' for workout: {}", generatedId, workout);
    }
}
