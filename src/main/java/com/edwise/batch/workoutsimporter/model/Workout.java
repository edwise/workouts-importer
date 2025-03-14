package com.edwise.batch.workoutsimporter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.batch.item.ResourceAware;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "workouts")
public class Workout implements ResourceAware {

    public static final String[] CSV_FIELDS = {"title", "startTime", "endTime", "description", "exerciseTitle",
            "superSetId", "exerciseNotes", "setIndex", "setType", "weightKg", "reps", "distanceKm", "durationSeconds", "rpe"};

    private String        title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String        description;
    private String        exerciseTitle;
    private Integer       superSetId;
    private String        exerciseNotes;
    private Integer       setIndex;
    private String        setType;
    private Double        weightKg;
    private Integer       reps;
    private Double        distanceKm;
    private Integer       durationSeconds;
    private Double        rpe;

    private String inputSrcFileName;

    @Override
    public void setResource(Resource resource) {
        this.inputSrcFileName = resource.getFilename();
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Workout workout)) {
            return false;
        }

        return startTime.equals(workout.startTime) &&
               endTime.equals(workout.endTime) &&
               exerciseTitle.equals(workout.exerciseTitle) &&
               setIndex.equals(workout.setIndex);
    }

    @Override
    public int hashCode() {
        int result = startTime.hashCode();
        result = 31 * result + endTime.hashCode();
        result = 31 * result + exerciseTitle.hashCode();
        result = 31 * result + setIndex.hashCode();
        return result;
    }
}
