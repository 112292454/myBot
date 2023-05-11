package simbot.yzg.bot.botframe.quartz;

import simbot.yzg.bot.botframe.serviceImpl.MyProduce;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class GroupPicTask extends QuartzJobBean {
	@Autowired
	MyProduce myProduce;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		myProduce.localPicSended.clear();
	}
}
