package com.edwise.batch.workoutsimporter;

import com.edwise.batch.workoutsimporter.helper.WorkoutIdGenerator;
import com.edwise.batch.workoutsimporter.model.Workout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBatchTest
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class WorkoutsImporterApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0");

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private Job workoutsImporterJob;

    @Autowired
    private MongoTemplate     mongoTemplate;
    @Autowired
    private ExitCodeGenerator exitCodeGenerator;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeAll
    static void setupContainer() {
        mongoDBContainer.start();
    }

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(workoutsImporterJob);
        mongoTemplate.dropCollection(Workout.class);
    }

    @AfterEach
    void tearDown() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void givenDBIsEmpty_whenLaunchJob_ShouldImportAllWorkouts() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThatWorkoutsAreInTheDB(6);
    }

    @Test
    void givenDBHasRepeatedWorkout_whenLaunchJob_ShouldImportOnlyNewWorkouts() throws Exception {
        Workout repeatedWorkout = createRepeatedWorkout();
        mongoTemplate.save(repeatedWorkout);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThatWorkoutsAreInTheDB(6);
    }

    @Test
    void givenDBHasNotRepeatedWorkouts_whenLaunchJob_ShouldImportAllWorkout()
            throws Exception {
        Workout notRepeatedWorkout = createNotRepeatedWorkout();
        mongoTemplate.save(notRepeatedWorkout);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThatWorkoutsAreInTheDB(7);
    }


    private void assertThatWorkoutsAreInTheDB(int expectedSize) {
        List<Workout> workouts = mongoTemplate.findAll(Workout.class);

        assertThat(workouts)
                .isNotEmpty()
                .hasSize(expectedSize);
        assertThat(workouts)
                .anySatisfy(workout -> {
                    assertThat(workout.getTitle()).isEqualTo("Rutina Test2");
                    assertThat(workout.getExerciseTitle()).isEqualTo("Lateral Raise (Dumbbell)");
                    assertThat(workout.getWeightKg()).isEqualTo(7.5);
                    assertThat(workout.getReps()).isEqualTo(10);
                });
    }

    private Workout createRepeatedWorkout() {
        Workout workout = new Workout();
        workout.setTitle("Repeated workout");
        workout.setStartTime(LocalDateTime.of(2025, 2, 22, 8, 19));
        workout.setEndTime(LocalDateTime.of(2025, 2, 22, 9, 6));
        workout.setExerciseTitle("Shoulder Press (Dumbbell)");
        workout.setSetIndex(0);
        workout.setGenId(WorkoutIdGenerator.generateWorkoutId(workout));
        return workout;
    }

    private Workout createNotRepeatedWorkout() {
        Workout workout = new Workout();
        workout.setTitle("Not Repeated workout");
        workout.setStartTime(LocalDateTime.of(2025, 2, 23, 8, 12));
        workout.setEndTime(LocalDateTime.of(2025, 2, 23, 9, 2));
        workout.setExerciseTitle("Exercise 1234");
        workout.setSetIndex(3);
        workout.setGenId(WorkoutIdGenerator.generateWorkoutId(workout));
        return workout;
    }
}
