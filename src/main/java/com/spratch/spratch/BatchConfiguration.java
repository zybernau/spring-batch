package com.spratch.spratch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

@Configurable
@EnableBatchProcessing
public class BatchConfiguration {

  @Autowired
  public JobBuilderFactory jobBuilderFactory;

  @Autowired
  public StepBuilderFactory stepBuilderFactory;


  @Bean
  public FlatFileItemReader<Stocks> reader() {
    return new FlatFileItemReaderBuilder<Stocks>()
              .name("stockItemReader")
              .resource(new ClassPathResource("import_data.csv"))
              .delimited()
              .names(new String[]{"stockName",	"ftHigh",	"ftLow",	"buyPrice",	"sellPrice",	"margin",	"profitPercent",})
              .fieldSetMapper(new BeanWrapperFieldSetMapper<Stocks>() {{
                setTargetType(Stocks.class);
              }})
              .build();
  }


  @Bean
  public StockProcessor processor() {
    return new StockProcessor();
  }

  @Bean
  public JdbcBatchItemWriter<Stocks> writer(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Stocks>()
          .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
          .sql("insert into stocks (stockName,ftHigh,ftLow,buyPrice,sellPrice,margin,profitPercent) values (:stockName,:ftHigh,:ftLow,:buyPrice,:sellPrice,:margin,:profitPercent)")
          .dataSource(dataSource)
          .build();
  }

  @Bean
  public Job ImportStockJob(JobCompletionNotificationListener  listener, Step step1) {
    return jobBuilderFactory.get("importUserJob")
			.incrementer(new RunIdIncrementer())
			.listener(listener)
			.flow(step1)
			.end()
			.build();
  }

  @Bean
	public Step step1(JdbcBatchItemWriter<Stocks> writer) {
		return stepBuilderFactory.get("step1")
			.<Stocks, Stocks> chunk(10)
			.reader(reader())
			.processor(processor())
			.writer(writer)
			.build();
	}
}

