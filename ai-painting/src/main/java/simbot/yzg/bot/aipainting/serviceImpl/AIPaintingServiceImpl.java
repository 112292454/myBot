package simbot.yzg.bot.aipainting.serviceImpl;

import DownloadTools.util.Base64Util;
import DownloadTools.util.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import simbot.yzg.bot.aipainting.entity.PaintResponses;
import simbot.yzg.bot.aipainting.entity.paintInfo;
import simbot.yzg.bot.aipainting.entity.t2iArgs;
import simbot.yzg.bot.commonapi.service.AIPaintingService;
import simbot.yzg.bot.commonapi.service.PictureService;
import simbot.yzg.bot.commonapi.vo.Result;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@DubboService(group = "simbot", version = "1.0.0",interfaceClass = AIPaintingService.class)
@Service
@ConfigurationProperties(prefix = "ai")
public class AIPaintingServiceImpl implements AIPaintingService {

	@Value("${ai.host}")
	public static String host="http://10.28.166.91:7860/";

	@Value("${ai.second_host}")
	public static String secondHost="http://localhost:7860/";

	@DubboReference(group = "simbot",interfaceClass = PictureService.class ,check = false)
	PictureService pictureService;

	private static final String t2iURLPath="sdapi/v1/txt2img";

	private Map<Long, t2iArgs> initedTasks=new HashMap<>();

	Logger log= LoggerFactory.getLogger(AIPaintingServiceImpl.class);

	@PostConstruct
	public void init(){
		new Thread(()->{
			while (true){
				try {
					Thread.sleep(1000*60*5);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				initedTasks.clear();
			}
		}).start();
	}


//	@Override
	private PaintResponses getSameSizePaints(t2iArgs args) {
		String url = host + t2iURLPath;
		String payload = null;
		long t = System.currentTimeMillis();
		PaintResponses paintResponse = new PaintResponses();

		try {
			payload = JSONObject.toJSONString(args);
			String response = HttpUtil.post(url, payload);
			paintResponse = JSONObject.parseObject(response, PaintResponses.class);
			paintResponse.setInfo(JSONObject.parseObject(paintResponse.getTemp(), paintInfo.class));

		} catch (Exception e) {
			log.warn("ai画图首选主机{}失效，选择备用机：{}", host, secondHost);
			try {
				if (paintResponse.getImages() == null || paintResponse.getImages().isEmpty()) {
					url = secondHost + t2iURLPath;
					String response = HttpUtil.post(url, payload);
					paintResponse = JSONObject.parseObject(response, PaintResponses.class);
					paintResponse.setInfo(JSONObject.parseObject(paintResponse.getTemp(), paintInfo.class));
				}
			} catch (Exception ee) {
				ee.printStackTrace();
				return paintResponse;
			}
		}


//			List<byte[]> Images =new ArrayList<>();
//			for (String image : paintResponse.getImages()) {
//				Images.add(Base64Util.decodeBase64ToBytes(image));
//			}

//			if (Images.isEmpty()) {
//				throw new RuntimeException("未获取到返回结果");
//			} else {
		paintResponse.getInfo().setUsedTime(System.currentTimeMillis() - t);

		log.info("画图成功，参数：{}", paintResponse.getInfo().myString());
		return paintResponse;
//			}
	}

//	@Override
	private List<PaintResponses> getDiffSizePaints(t2iArgs args) {
		int batch = args.getNIter();
		List<PaintResponses> res=new ArrayList<>();

		for (int i = 0; i < batch; i++) {
			t2iArgs tempArg=new t2iArgs();
			tempArg.initArgs(args);
			tempArg.setNIter(1);
			if(tempArg.isNeedShape()) tempArg.autoShape();
			res.add(getSameSizePaints(tempArg));
		}
		return res;
	}

	@Override
	public  Result<List<byte[]>> doPaint() {
		return doPaint("");
	}

	@Override
	public Result<List<byte[]>> doPaint(long id) {
		Result<List<byte[]>> result = doPaint(initedTasks.get(id));
		initedTasks.remove(id);
		return result;
	}

	@Override
	public Result<List<byte[]>> doPaint(String input) {
		return doPaint(parseArgs(input));
	}

	public  Result<List<byte[]>> doPaint(t2iArgs args) {
		log.info("收到画图请求,预计用时：{}s", args.getCostSeconds());
		if(!args.costCanPass()){
			return Result.error("请降低参数大小，使得耗时1分钟内");
		}
		if(getQueueTime(Long.MAX_VALUE)>10*60){
			return Result.error("任务队列已满十分钟，请稍候");
		}
		List<PaintResponses> sizePaints = getDiffSizePaints(args);

		List<byte[]> Images = new ArrayList<>();
		StringBuilder msg=new StringBuilder();
		for (PaintResponses image : sizePaints) {
			if(image.getImages()==null) continue;
			msg.append(image.getInfo().myString()).append("\n");
			for (String s : image.getImages()) {
				Images.add(Base64Util.decodeBase64ToBytes(s));
			}
		}

		Result<List<byte[]>> result ;
		if(Images.isEmpty()){
			Images.add(fastGetStoreImage());
			result=Result.<List<byte[]>>error("画图服务失效").data(Images);
		}else {
			result=Result.<List<byte[]>>success(msg.toString()).data(Images);
		}
		return result;
	}

	private static Pattern nums = Pattern.compile("^\\d-");

	private static Pattern size = Pattern.compile("\\d{2,4}\\*\\d{2,4}");

	/**
	 * 以前写复杂一些的命令行其实也没人用，这次重写的简单一点
	 * /ap3-512*768 miku表示画三张512*768的，提词为miku的图片。
	 * 之前写batch size、style之类的统统去掉，反正娱乐
	 */
	public t2iArgs parseArgs(String input) {

		t2iArgs args = new t2iArgs().init();

		//setCnt
		Matcher m1 = nums.matcher(input);
		if(m1.find()){
			String cnt = m1.group();
			args.setNIter(Integer.parseInt(cnt.substring(0,cnt.length() -1)));
			input=input.substring(2);
		}

		//setSize
		Matcher m2 = size.matcher(input);
		if(m2.find()){
			String size = m2.group();
			args.setSize(Integer.parseInt(size.substring(0,size.indexOf("*"))),
					Integer.parseInt(size.substring(size.indexOf("*")+1)));
			input=input.replace(size,"");
			args.setNeedShape(false);
		}

		//highRes
		if(input.contains("-h")) {
			args.setEnableHr(true);
			input=input.replace("-h ","");

		}

		if(StringUtils.isNotBlank(input)) {
			args.addPrompt(input);
		}

		return args;
	}

	@Override
	public byte[] fastGetStoreImage() {
		return pictureService.geiAiPicture().getContent();
	}

	@Override
	public String helpMsg() {
		//TODO: finally, add a help msg
		return "/ap：画图  /ah：highres放大，约4倍耗时\n" +
				"更多参数：/ap3-512*768 miku表示画三张512*768的，提词为miku的图片。" +
				"/ap last表示使用上一次的提示词";
	}

//	@Override
	public double getQueueTime(long id) {
		AtomicReference<Double> time = new AtomicReference<>((double) 0);
		initedTasks.forEach((k,v)-> {
			if(k<=id) time.updateAndGet(a -> a + v.getCostSeconds());
		});
		return time.get();
	}

	@Override
	public double getPredictTime(long id) {
		return getQueueTime(id);
	}

	@Override
	public long prepare(String args) {
		long t=System.currentTimeMillis()*10+new Random().nextInt(10);
		initedTasks.put(t,parseArgs(args));
		return t;
	}
}
