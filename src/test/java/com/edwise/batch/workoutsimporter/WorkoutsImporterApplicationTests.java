package com.edwise.batch.workoutsimporter;

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
    private MongoTemplate mongoTemplate;

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
    void givenDBHasExistingWorkouts_whenLaunchJob_ShouldImportOnlyNewWorkouts() throws Exception {
        LocalDateTime lastEndTime = LocalDateTime.of(2025, 2, 23, 9, 0);
        Workout existingWorkout = createWorkout(lastEndTime);
        mongoTemplate.save(existingWorkout);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThatWorkoutsAreInTheDB(5);
    }

    @Test
    void givenDBHasExistingWorkoutsAndAllCsvWorkoutsAreOlder_whenLaunchJob_ShouldNotImportAnyWorkout()
            throws Exception {
        LocalDateTime intermediateEndTime = LocalDateTime.of(2025, 3, 15, 8, 59);
        Workout existingWorkout = createWorkout(intermediateEndTime);
        mongoTemplate.save(existingWorkout);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        List<Workout> workouts = mongoTemplate.findAll(Workout.class);
        assertThat(workouts)
                .hasSize(1)
                .first()
                .extracting(Workout::getTitle)
                .isEqualTo("Existing workout");
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

    private Workout createWorkout(LocalDateTime lastEndTime) {
        Workout existingWorkout = new Workout();
        existingWorkout.setTitle("Existing workout");
        existingWorkout.setStartTime(lastEndTime);
        existingWorkout.setEndTime(lastEndTime);
        return existingWorkout;
    }
}
