package com.spratch.spratch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Set;

@RestController
@RequestMapping(path = "/batch")
public class WebController {

    private static final Logger log = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    @Qualifier(value = "batchJobBean")
    Job batchJob;

    @Bean
    public JobLauncher getJobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();


        simpleAsyncTaskExecutor.setConcurrencyLimit(5);

        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        jobLauncher.setTaskExecutor(simpleAsyncTaskExecutor);

        return jobLauncher;
    }

    @GetMapping("/start")
    public String initBatch() throws Exception {
        String status = "Start Batch \n";
        try {
            log.info("Current Thread @ init: " + Thread.currentThread().getName());
            status += "\n";
            status += stopPreviousBatch()?" Stopped previous job" : " no previous jobs running";
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("batchId", System.currentTimeMillis())
                    .toJobParameters();

            getJobLauncher().run(batchJob, jobParameters);
            status += "\n Batch Started";
        } catch(Exception ex) {
            log.error(ex.getMessage());
            status += "\n Batched Erred ";
        }
        return status;
    }


    @GetMapping("/check")
    public String checkBatch() {

        // check if job is running.
        Set<JobExecution> jobs = jobExplorer.findRunningJobExecutions("batchJobBean");
        log.info("into stop batch call.");
        if(jobs != null && !jobs.isEmpty()) {
            log.info("there are jobs running !!");
            return "jobs running !!!";
        }

        return "no jobs running !!!";
    }

    @GetMapping("/stop")
    public String checkAndStopBatch() throws NoSuchJobExecutionException, JobExecutionNotRunningException {
        log.info("Current Thread @ stop :" + Thread.currentThread().getName());

        return stopPreviousBatch()? "Stopped batch" : "no jobs running !!!";
    }

    private Boolean stopPreviousBatch() {
        try {
            Set<JobExecution> jobs = jobExplorer.findRunningJobExecutions("batchJobBean");
            log.info("into stop batch call.");
            if (jobs != null && !jobs.isEmpty()) {
                log.info("there are jobs running !!");
                JobExecution je = jobs.iterator().next();
//                Thread.currentThread().interrupt();
                je.setEndTime(new Date());
                je.setStatus(BatchStatus.ABANDONED);
                je.setExitStatus(ExitStatus.STOPPED);
                jobRepository.update(je);
                Thread.currentThread().interrupt();
                return true;
            }
        } catch(Exception ex) {
            log.error(ex.getMessage());
            return false;
        }
        return false;
    }

}
