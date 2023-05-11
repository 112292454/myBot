/*
package love.simbot.example.batch;

import love.simbot.example.pic.component.PicPath;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


@Configuration
//@EnableBatchProcessing
public class BatchConfig {

	//数据的读取
	@Bean
	@StepScope
	FlatFileItemReader<PicPath> itemReader(@Value("#{jobParameters['path']}") String path) {
		FlatFileItemReader<PicPath> reader = new FlatFileItemReader<>();
		//配置文件位置
		reader.setResource(new FileSystemResource(path));
		// 通过setLineMapper方法设置每一行的数据信息
		reader.setLineMapper(new DefaultLineMapper<PicPath>() {{
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setNames("id", "path");
				// 配置列与列之间的间隔符（这里是空格）
				setDelimiter(" ");
			}});
			setFieldSetMapper(new BeanWrapperFieldSetMapper() {{
				setTargetType(PicPath.class);
			}});
		}});
		return reader;
	}

	//加入自定义的itemprocessor，对传入的对象处理：将姓名的字母变成大写
	@Bean
	@StepScope
	ItemProcessor<PicPath, PicPath> itemProcessor(@Value("#{jobParameters['kind']}") String kind){
		return (pic -> {
			pic.setKind(kind);
			return pic;
		});
	}


	//输出数据
	@Bean
	ItemWriter<PicPath> jdbcBatchItemWriter(DataSource dataSource) {
		JdbcBatchItemWriter writer = new JdbcBatchItemWriter();
		// 配置使用的数据源
		writer.setDataSource(dataSource);
		writer.setSql("insert into picpath(path,kind) " +
				"values(:path,:kind)");
		writer.setItemSqlParameterSourceProvider(
				new BeanPropertyItemSqlParameterSourceProvider<>());
		return writer;
	}

	@Bean
	public JobRepository JobRepository(DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception{
		JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
		jobRepositoryFactoryBean.setDataSource(dataSource);
		jobRepositoryFactoryBean.setTransactionManager(transactionManager);
		jobRepositoryFactoryBean.setDatabaseType("mysql");
		return jobRepositoryFactoryBean.getObject();
	}

	@Bean(name = "myLauncher")
	public SimpleJobLauncher jobLauncher(DataSource dataSource, PlatformTransactionManager transactionManager)throws Exception{
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(JobRepository(dataSource, transactionManager));
		return jobLauncher;
	}

	@Bean
	public Job importJob(JobBuilderFactory jobs,Step s1) {
		return jobs.get("importJob")
				.incrementer(new RunIdIncrementer())
				.flow(s1)
				.end()
				.build();
	}

	//job如何进行
	@Bean
	Step myStep(StepBuilderFactory stepBuilderFactory
			, ItemReader<PicPath> reader
			, ItemWriter<PicPath> writer
			, ItemProcessor<PicPath, PicPath>processor) {
		// Step通过stepBuilderFactory进行配置
		return stepBuilderFactory.get("myStep") //Step的name
				.<PicPath, PicPath>chunk(51200)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}

}
*/
