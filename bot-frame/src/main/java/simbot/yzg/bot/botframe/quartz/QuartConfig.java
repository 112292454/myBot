package simbot.yzg.bot.botframe.quartz;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartConfig {
	public static class DemoJob01Configuration {

		@Bean
		public JobDetail demoJob01() {
			return JobBuilder.newJob(GroupSendTask.class)
					.withIdentity("GroupSendTask") // 名字为 demoJob01
					.storeDurably() // 没有 Trigger 关联的时候任务是否被保留。因为创建 JobDetail 时，还没 Trigger 指向它，所以需要设置为 true ，表示保留。
					.build();
		}

		@Bean
		public Trigger demoJob01Trigger() {
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 * * * * ?");
			// Trigger 构造器
			return TriggerBuilder.newTrigger()
					.forJob(demoJob01()) // 对应 Job 为 demoJob01
					.withIdentity("GroupSendTask") // 名字为 demoJob01Trigger
					.withSchedule(scheduleBuilder) // 对应 Schedule 为 scheduleBuilder
					.build();
		}

	}

	public static class DemoJob02Configuration {

		@Bean
		public JobDetail demoJob02() {
			return JobBuilder.newJob(GroupPicTask.class)
					.withIdentity("GroupPicTask") // 名字为 demoJob02
					.storeDurably() // 没有 Trigger 关联的时候任务是否被保留。因为创建 JobDetail 时，还没 Trigger 指向它，所以需要设置为 true ，表示保留。
					.build();
		}

		@Bean
		public Trigger demoJob02Trigger() {
			// 基于 Quartz Cron 表达式的调度计划的构造器
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 0/4 * * ?");
			// Trigger 构造器
			return TriggerBuilder.newTrigger()
					.forJob(demoJob02()) // 对应 Job 为 demoJob02
					.withIdentity("GroupPicTask") // 名字为 demoJob02Trigger
					.withSchedule(scheduleBuilder) // 对应 Schedule 为 scheduleBuilder
					.build();
		}

	}

}
