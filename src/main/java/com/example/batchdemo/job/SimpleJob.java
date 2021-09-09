package com.example.batchdemo.job;

import static org.springframework.data.domain.Sort.Direction.ASC;

import antlr.collections.impl.IntRange;
import com.example.batchdemo.job.persisted.User;
import com.example.batchdemo.job.persisted.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.parsing.AliasDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.DefaultsDefinition;
import org.springframework.beans.factory.parsing.ImportDefinition;
import org.springframework.beans.factory.parsing.ReaderEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
@EnableBatchProcessing
public class SimpleJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void initData() {
        userRepository.saveAll(
            IntStream.range(1, 5000)
            .mapToObj(i -> User.builder()
                .name(i + "")
                .email(i + "@email.com.tw")
                .build())
            .collect(Collectors.toList()));
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(8);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setQueueCapacity(30);
        return taskExecutor;
    }

    @Bean
    public RepositoryItemReader<User> itemReader() {
        log.info("reader");
        return new RepositoryItemReaderBuilder<User>()
            .name("user reader")
            .repository(userRepository)
            .methodName("findAll")
            .arguments()
            .sorts(Map.of("id", ASC))
            .build();
    }

    @Bean
    public CustomerProcessor itemProcessor() {
        log.info("processor");
        return new CustomerProcessor();
    }

    @Bean
    public RepositoryItemWriter<User> itemWriter() {
        log.info("writer");
        return new RepositoryItemWriterBuilder<User>()
            .repository(userRepository)
            .methodName("save")
            .build();
    }

    @Bean
    public Step userStep() {
        return stepBuilderFactory.get("step1")
            .<User, User>chunk(5)
            .reader(itemReader())
            .listener(new ItemListener())
            .processor(itemProcessor())
            .writer(itemWriter())
            .taskExecutor(taskExecutor())
//            .throttleLimit(1)
            .build();
    }

    @Bean
    public Job demoJob() {
        return jobBuilderFactory.get("demo-job")
            .start(userStep())
            .build();
    }
}
