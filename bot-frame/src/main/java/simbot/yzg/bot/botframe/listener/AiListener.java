package simbot.yzg.bot.botframe.listener;

import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.Listener;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.*;
import love.forte.simbot.resources.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simbot.yzg.bot.botframe.serviceImpl.MyProduce;
import simbot.yzg.bot.commonapi.service.AIPaintingService;
import simbot.yzg.bot.commonapi.vo.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class AiListener {

	@DubboReference(group = "simbot", version = "1.0.0",interfaceClass=AIPaintingService.class,check = false)
	AIPaintingService aiPaintingService;

	@Autowired
	MyProduce myProduce;

	private Map<String,String> userLastArgs=new HashMap<>();

	Logger log= LoggerFactory.getLogger(AiListener.class);

/*	@Listener
	@Filter(value = "/dubbo",matchType= MatchType.TEXT_CONTAINS)
	public void dubboTest(GroupMessageEvent groupMessageEvent){
		myProduce.sendMsg(groupMessageEvent, love.forte.simbot.message.Text.of(aiPaintingService.helpMsg()), true);
	}*/
	/*@Listener
	@Filter(value = "/atest",matchType= MatchType.TEXT_CONTAINS)
	public void test(GroupMessageEvent groupMessageEvent) throws IOException {
		boolean auth=myProduce.getAuth(groupMessageEvent, "pic");
		Path path = Paths.get("D://pic.jpg");
		byte[] bytes = Files.readAllBytes(path);
		StandardResource resource = Resource.of(new ByteArrayInputStream(bytes));
		ResourceImage image = Image.of(resource);

		myProduce.sendMsg(groupMessageEvent, image, auth);
	}*/

	@Listener
	@Filter(value = "/ap",matchType= MatchType.TEXT_STARTS_WITH)
	public void aiPaintFull(GroupMessageEvent groupMessageEvent){
		boolean auth=myProduce.getAuth(groupMessageEvent, "pic");
		if(auth) {
			String args=groupMessageEvent.getMessageContent().getPlainText();
			args=args.substring(3);
			if(args.trim().equals("help")) {
				myProduce.sendMsg(groupMessageEvent,Text.of(aiPaintingService.helpMsg()), auth);
				return;
			}
			if(args.trim().equals("last")) args=userLastArgs.get(groupMessageEvent.getAuthor().getId().toString());
			else userLastArgs.put(groupMessageEvent.getAuthor().getId().toString(),args);

			long taskID=aiPaintingService.prepare(args);

			asyncPaintAndSend(groupMessageEvent, auth, taskID);
		}else{
			myProduce.sendMsg(groupMessageEvent, Text.of("default"), auth);
		}


	}




	@Listener
	@Filter(value = "/ah",matchType= MatchType.TEXT_STARTS_WITH)
	public void aiPaintHighRes(GroupMessageEvent groupMessageEvent){
		boolean auth=myProduce.getAuth(groupMessageEvent, "r18");
		if(auth) {
			String args=groupMessageEvent.getMessageContent().getPlainText();
			args=args.substring(3);
			if(args.trim().equals("help")) {
				myProduce.sendMsg(groupMessageEvent,Text.of(aiPaintingService.helpMsg()), auth);
				return;
			}
			if(args.trim().equals("last")) args=userLastArgs.get(groupMessageEvent.getAuthor().getId().toString());
			else userLastArgs.put(groupMessageEvent.getAuthor().getId().toString(),args);
			if (!args.contains("-h")) args=args+" -h ";//设置highRes

			long taskID=aiPaintingService.prepare(args);

			asyncPaintAndSend(groupMessageEvent,auth, taskID);
		}else{
		myProduce.sendMsg(groupMessageEvent, Text.of("default"), auth);
		}
	}
	private void asyncPaintAndSend(GroupMessageEvent groupMessageEvent, boolean auth, long taskID) {
		//draw
		CompletableFuture.supplyAsync(()->{
			return  aiPaintingService.doPaint(taskID);
		}).thenApply(result->CompletableFuture.supplyAsync(()-> {
			Messages images = getImages(result);
			myProduce.sendMsg(groupMessageEvent, images, auth);
			MessageReceipt receipt = myProduce.sendMsg(groupMessageEvent, getInfos(result).plus(Text.of("\n本消息30s后撤回")), auth);
			log.info("挂起撤回任务");
			try {
				Thread.sleep(1000 * 30);
				receipt.deleteAsync();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			log.info("完成撤回任务");
			return null;
		})).thenAccept(receipt->{

		});


		//pre notify
		CompletableFuture.supplyAsync(()->
				myProduce.sendMsg(groupMessageEvent, Text.of("预计共用时：" +
						(int) (aiPaintingService.getPredictTime(taskID)) + "s"), auth)
		).thenAccept(receipt->CompletableFuture.supplyAsync(()->{
			try {
				Thread.sleep(1000 * 30);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			receipt.deleteAsync();
			return null;
		}));
	}
	@NotNull
	private Messages getImages(Result<List<byte[]>> doPaint) {
		List<byte[]> paints = doPaint.getData();
		Messages images = Messages.emptyMessages();
		if(doPaint.isSuccess()){
//			images=Messages.toMessages(Text.of(doPaint.getMsg()));
			for (byte[] paint : paints) {
				images=images.plus(Image.of(Resource.of(paint,"")));
			}
		}else{
			//images=Messages.toMessages(Text.of("服务出错，获取随机已有图片"));
			//TODO：获取随机已有图片
		}

		return images;
	}

	@NotNull
	private Messages getInfos(Result<List<byte[]>> doPaint) {
		Messages images = Messages.emptyMessages();
		if(doPaint.isSuccess()){
			images=Messages.toMessages(Text.of(doPaint.getMsg()));
		}else{
			images=Messages.toMessages(Text.of(doPaint.getMsg()+"\n服务出错，获取随机已有图片"));
		}

		return images;
	}

}
