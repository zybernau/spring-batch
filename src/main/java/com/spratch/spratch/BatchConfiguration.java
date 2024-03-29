package com.spratch.spratch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

// tag::setup[]
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	private static final Logger log = LoggerFactory.getLogger(WebController.class);

	@Autowired
	private MadWait madWhat;

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	// end::setup[]

	// tag::readerwriterprocessor[]
	@Bean
	public FlatFileItemReader<Person> reader() {
		return new FlatFileItemReaderBuilder<Person>()
			.name("personItemReader")
			.resource(new ClassPathResource("sample-data.csv"))
			.delimited()
			.names(new String[]{"firstName", "lastName"})
			.fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
				setTargetType(Person.class);
			}})
			.build();
	}

	@Bean
	public ItemReader<Person> normalReader() {
//		return new ItemReader<Person>() {
//			@Override
//			public Person read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
//				Person person = new Person();
//				person.setFirstName("Rajamohan");
//				person.setLastName("Manivannan");
//				log.info("Current Thread @ normal reader: " + Thread.currentThread().getName());

//				return person;
//			}
//		};
		return new ItemReader<Person>() {
			@Override
			public Person read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
				log.info("Starts waiting...");
				madWhat.waiter();
				log.info("Stops waiting.");
				return null;
			}
		};
	}

	@Bean
	public PersonItemProcessor processor() {
		return new PersonItemProcessor();
	}


	@Bean
	public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Person>()
			.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
			.sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
			.dataSource(dataSource)
			.build();
	}
	// end::readerwriterprocessor[]

	// tag::jobstep[]
	@Bean(name = "batchJobBean")
	public Job batchJobBean(JobCompletionNotificationListener listener, Step step1, Step step2) {
		return jobBuilderFactory.get("batchJobBean")
			.incrementer(new RunIdIncrementer())
			.listener(listener)
			.flow(step1)
				.next(step2)
			.end()
			.build();
	}

	@Bean
	public Step step1(JdbcBatchItemWriter<Person> writer) throws InterruptedException {
		return stepBuilderFactory.get("step1")
			.<Person, Person> chunk(10)

			.reader(reader())
			.processor(processor())
			.writer(writer)

			.build();
	}

	@Bean
	public Step step2(JdbcBatchItemWriter<Person> writer) {
		return stepBuilderFactory.get("step2")
				.<Person, Person> chunk(10)
				.reader(normalReader())
				.processor(processor())
				.writer(writer)
				.build();
	}
	// end::jobstep[]
}
