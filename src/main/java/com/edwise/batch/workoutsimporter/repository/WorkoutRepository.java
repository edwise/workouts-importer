package com.edwise.batch.workoutsimporter.repository;

import com.edwise.batch.workoutsimporter.model.Workout;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutRepository extends MongoRepository<Workout, String> {

    boolean existsByGenId(String genId);
}
