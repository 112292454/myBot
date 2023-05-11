package simbot.yzg.bot.botframe.quartz;

import simbot.yzg.bot.botframe.serviceImpl.MyProduce;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

public class GroupSendTask extends QuartzJobBean {
	@Autowired
	MyProduce myProduce;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		System.out.println("当前一分钟内发言过的群数量："+myProduce.sendedTimes.size());
		myProduce.sendedTimes.clear();
		myProduce.warned.clear();
		myProduce.min=System.currentTimeMillis()/60000;
		System.out.println("quartz task     " + new Date());
	}
}
