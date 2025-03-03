package com.edwise.batch.workoutsimporter.batch;

import com.edwise.batch.workoutsimporter.listener.JobCompletionNotificationListener;
import com.edwise.batch.workoutsimporter.model.Workout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Collections;

@Configuration
public class BatchConfiguration {

    private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);

    @Value("${workout-config.fileName}")
    private String workoutFileName;

    @Bean
    public FlatFileItemReader<Workout> reader() {
        return new FlatFileItemReaderBuilder<Workout>()
                .name("workoutReader")
                .resource(new ClassPathResource(workoutFileName))
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Workout.class);
                    setCustomEditors(Collections.singletonMap(
                            LocalDateTime.class,
                            new LocalDateTimeEditor()
                    ));
                }})
                .linesToSkip(1)
                .delimited()
                .names(Workout.FIELDS)
                .build();
    }

    @Bean
    public ItemProcessor<Workout, Workout> processor() {
        return workout -> {
            log.info("Processing workout: {}", workout);
            return workout;
        };
    }

    @Bean
    public ItemWriter<Workout> writer(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<Workout>()
                .template(mongoTemplate)
                .build();
    }

    @Bean
    public Job importJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
        return new JobBuilder("importWorkoutsJob", jobRepository)
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      FlatFileItemReader<Workout> reader,
                      ItemProcessor<Workout, Workout> processor,
                      ItemWriter<Workout> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Workout, Workout>chunk(5, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}