package simbot.yzg.bot.imagesource.serviceImpl;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import love.forte.simbot.message.Image;
import love.forte.simbot.message.Message;
import love.forte.simbot.message.Messages;
import love.forte.simbot.message.Text;
import love.forte.simbot.resources.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import simbot.yzg.bot.commonapi.service.PicSourcesService;
import simbot.yzg.bot.commonapi.utils.UrlUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@DubboService(group = "simbot", version = "1.0.0",interfaceClass = PicSourcesService.class)
public class PicSourcesServiceImpl implements PicSourcesService {

	/*  url特殊字符的转码：

	空格  %20
	"    %22
	#    %23
	%    %25
	&    %26
	(    %28
	)    %29
	+    %2B
	,    %2C
	/    %2F
	:    %3A
	;    %3B
	<    %3C
	=    %3D
	>    %3E
	?    %3F
	@    %40
	\    %5C
	|    %7C

	*/

	private  final HashMap<String,String> name2Prop;
	public PicSourcesServiceImpl(){
		name2Prop=new HashMap<>();
		name2Prop.put("thumbnail", "缩略图");
		name2Prop.put("similarity", "相似度");
		name2Prop.put("index_name", "总id");
		name2Prop.put("title", "作品标题");
		name2Prop.put("pixiv_id", "p站作品Id");
		name2Prop.put("member_name", "p站作者名");
		name2Prop.put("member_id", "p站作者Id");
		name2Prop.put("ext_urls", "链接");


	}

	@Override
	public ArrayList<Messages> getPicSource(Image image) throws MalformedURLException {
		Messages sendMessages=Messages.emptyMessages();
		String response="",request=image.getResource().getName();
		request= UrlUtil.getURLEncoderString(request);

		String url="https://saucenao.com/search.php?db=999&output_type=2&testmode=1&numres=3&hide=0&" +
				"api_key=1690ab3427f002a07e4b2e04144925acfe375a7e&url=" + request;
		StringBuilder sb = new StringBuilder();

		try {
			URLConnection uc = new URL(url).openConnection();
			//BufferedReader in= new BufferedReader(new InputStreamReader(DownLoad.getUrlInputStream(url),"utf-8"));
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(),"utf-8"));
			String inputLine = null;
			while ( (inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}
			response=sb.toString();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}


		Gson gson=new Gson();
		Map<String,String> temp=gson.fromJson(response,Map.class);
		ArrayList<LinkedTreeMap> result;
		if(temp==null)result=new ArrayList<>();
		else result= gson.fromJson(gson.toJson(temp.get("results")),ArrayList.class);
		ArrayList<Map<String, Message.Element<?>>> sources=new ArrayList<>();

		for (LinkedTreeMap<String,Map> source : result) {
			Map<String, String> header = source.get("header"), body = source.get("data");
			double sim=Double.parseDouble(header.getOrDefault("similarity", "0"));
			if(sim<80) continue;

			body.forEach((Object k,Object v)->{
				try {
					body.put(k.toString(),(long)Double.parseDouble(v.toString())+"");
				} catch (Exception e) {

				}
			});

			Map<String,Message.Element<?>> res=new HashMap<>();
			res.put("thumbnail", Image.of(Resource.of(new URL(header.get("thumbnail")))));
			res.put("similarity", Text.of(header.get("similarity")));
			res.put("index_name", Text.of(header.get("index_name")));
			for (Map.Entry entry : body.entrySet()) {
				res.put(entry.getKey().toString(), Text.of(entry.getValue().toString()));
			}
			sources.add(res);
			if ( sim> 85 && body.containsKey("pixiv_id")) {
				sources.clear();
				res.clear();
				res.put("thumbnail",Image.of(Resource.of(new URL(header.get("thumbnail")))));
				res.put("similarity", Text.of(header.get("similarity")));
				res.put("index_name", Text.of(header.get("index_name")));
				for (Map.Entry entry : body.entrySet()) {
					res.put(entry.getKey().toString(), Text.of(entry.getValue().toString()));
				}
				sources.add(res);
				break;
			}
		}
		ArrayList<Messages> messagesArrayList = new ArrayList<>();
		for (Map<String, Message.Element<?>> source : sources) {
			messagesArrayList.add(this.build(source));
		}
		if(messagesArrayList.size()==0){
			messagesArrayList.add(Messages.toMessages(Text.of("无相似度足够的结果")));
		}
		System.out.println("res = " + sources);
		//System.out.println("response = " + response);
		return messagesArrayList;
	}

	@Override
	public Messages build(Map<String, Message.Element<?>> map){
		AtomicReference<Messages> res= new AtomicReference<>(Messages.emptyMessages());
		map.forEach((k,v)-> {
			if(name2Prop.containsKey(k)) {
				k=name2Prop.get(k);
			}
			res.set(res.get().plus(Text.of(k + ":")).plus(v).plus(Text.of("\n")));
		});
		return res.get();
	}

/*
	public ForwardMessage buileForwardMsgByList(ArrayList<Messages> messagesList, Bot bot){


		messagesList.get(0).
		// 比如你要发给一个群
		final Group group = miraiBot.getGroup(745769821);
		// 寻找一个发送者，比如某个群员
		final NormalMember senderOnGroup = group.getBotAsMember();
		// 构建转发消息
		final ForwardMessageBuilder forwardMessageBuilder = new ForwardMessageBuilder(group);

		// TODO 通过 builder 构建一个 mirai的 ForwardMessage
		// forwardMessageBuilder.add()
		// 比如说你要发送图片，则构建mirai原生的图片对象:

		// 通过file
		//final ExternalResource resource1 = ExternalResource.create(new File("..."));

		// 或其他方式
		// ExternalResource.create(...)

		// 通过某个群上传
		//final Image image = group.uploadImage(resource1);


		// 添加一个图片
		//forwardMessageBuilder.add(senderOnGroup, image);
		for (Messages m : messagesList) {
			forwardMessageBuilder.add(m);
		}

		// 得到最终的转发消息
		final ForwardMessage forwardMessage = forwardMessageBuilder.build();
		return forwardMessage;

		// 得到 mirai 组件的消息正文
		//final MiraiMessageContent miraiMessageContent = builder.message(forwardMessage).build();

		// 通过 simbot 发送消息
		//sender.sendGroupMsg(123123, miraiMessageContent);

		// 当然，你也可以选择直接通过mirai本身发送:
		// group.sendMessage(forwardMessage);
	}*/
}

//
///**
// * Auto-generated: 2022-07-28 3:36:25
// *
// * @author www.jsons.cn
// * @website http://www.jsons.cn/json2java/
// */
//class pixivClass {
//
//	private List<String> extUrls;
//	private String title;
//	private String pixivId;
//	private String memberName;
//	private String memberId;
//
//	public void setExtUrls(List<String> extUrls) {
//		this.extUrls = extUrls;
//	}
//	public String getExtUrls() {
//		return extUrls.toString();
//	}
//
//	public void setTitle(String title) {
//		this.title = title;
//	}
//	public String getTitle() {
//		return title;
//	}
//
//	public void setPixivId(String pixivId) {
//		this.pixivId = pixivId;
//	}
//	public String getPixivId() {
//		return pixivId;
//	}
//
//	public void setMemberName(String memberName) {
//		this.memberName = memberName;
//	}
//	public String getMemberName() {
//		return memberName;
//	}
//
//	public void setMemberId(String memberId) {
//		this.memberId = memberId;
//	}
//	public String getMemberId() {
//		return memberId;
//	}
//
//}
