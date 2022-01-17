package com.spratch.spratch;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport  {

  private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }


  public void afterJob(JobExecution jobExecution) {
    if(jobExecution.getBatchStatus() == BatchStatus.COMPLETED) {
      log.info("Job Completed !!! arrae bhai !!!");
      jdbcTemplate.query("SELECT stockName from Stocks",
        (rs, row) -> rs.getString(1)
        ).forEach(stock -> log.info("Found stock " + stock + " in the DB"));
    }
  }

}
