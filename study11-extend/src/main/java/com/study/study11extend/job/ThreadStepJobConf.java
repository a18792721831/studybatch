package com.study.study11extend.job;

import com.study.study11extend.tasklet.MySyncTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiayq
 * @Date 2020-12-19
 */
//@Component
public class ThreadStepJobConf {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobLauncher jobLauncher;

    @PostConstruct
    public void runJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job(), new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
    }

    private Job job() {
        return jobBuilderFactory.get("thread-step-job")
                .start(getStep("thread-step"))
                .build();
    }

    private Step getStep(String name) {
        AtomicInteger atomicInteger = new AtomicInteger();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(3);
        executor.setCorePoolSize(3);
        executor.setQueueCapacity(20);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return stepBuilderFactory.get(name)
//                .tasklet((sc, cc) -> {
//                    int number = atomicInteger.getAndIncrement();
//                    System.out.println(sc.getStepExecution().getStepName() + number + " , thread : " +
//                            Thread.currentThread().getName() + " , time : " + LocalDateTime.now().format(
//                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " >>> sleep 10s .");
//                    TimeUnit.SECONDS.sleep(10);
//                    if (number > 20) {
//                        return RepeatStatus.FINISHED;
//                    }
//                    return RepeatStatus.CONTINUABLE;
//                })
                .tasklet(new MySyncTasklet())
                .taskExecutor(executor)
//                .throttleLimit(2)
                .build();
    }

}
