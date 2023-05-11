package simbot.yzg.bot.botframe.serviceImpl;

import DownloadTools.DownLoad;
import DownloadTools.util.QRCodeUtil;
import catcode.CatCodeUtil;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.*;
import love.forte.simbot.resources.Resource;
import simbot.yzg.bot.botframe.component.InfoFactory;
import simbot.yzg.bot.commonapi.entity.authority;
import simbot.yzg.bot.botframe.component.picFolderInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import static java.lang.Math.max;

@Component
public class MyProduce {
	public static HashMap<Integer, Integer> evaluation = new HashMap<>();
	public static Deque<Integer> idleId = new LinkedList<>();
	public static String netPicPath = "D:\\botPic\\randomPicSave\\";

	public static HashMap<Integer, String> localPicID = new HashMap<>();

	public static int localSize = 0;
	public static HashMap<String, String> VisQR = new HashMap<>();
	public static Pattern number = Pattern.compile("\\b\\d+\\b");//1324364 etc.
	private static authority groupAuth, userAuth;
	private static final HashMap<String, picFolderInfo> folder = new HashMap<>();
	private static final HashMap<String, String> nameToPath = new HashMap<>();
	private static final String DefaultMsg = "无权限";

	private static CatCodeUtil util = CatCodeUtil.INSTANCE;





	public static final String ToMoreMsg = "[CAT:image,file=D:\\botPic\\z.jpg]不可以压榨bot";
	public HashMap<String, Integer> sendedTimes = new HashMap<>();
	public HashMap<String, Boolean> warned = new HashMap<>();
	public HashMap<String, Integer> localPicSended = new HashMap<>();
	public static long min = System.currentTimeMillis() / 60000;

	@Bean
	public QRCodeUtil QRCodeUtil(){
		return new QRCodeUtil().setToken();
	}



	public MyProduce(InfoFactory infoFactory) {
		HashSet<Integer> usedIndex = new HashSet<>();
		try {
			//得到已有网络图片的评价信息
			Scanner in = new Scanner(new File(netPicPath + "evaluation.txt"));
			while (in.hasNext()) {
				int index = in.nextInt(), value = in.nextInt();
				evaluation.put(index, value);
				usedIndex.add(index);
			}
			//得到剩余可用的网络图片id
			for (int i = 0; i < 2000; i++) {
				if (!usedIndex.contains(i)) {
					idleId.offerFirst(i);
				}
			}
			in.close();
			//创建几个文件夹的图片名信息
			//  folder.put("ff14h",infoFactory.makeInfo("ff14h", "D:\\新建文件夹\\ff14\\h"));
			//  folder.put("ff14", infoFactory.makeInfo("ff14", "D:\\新建文件夹\\ff14\\nonh"));
			//  folder.put("localPic",infoFactory.makeInfo("localPic", "D:\\botPic\\pixivDownLoad"));
			//  folder.put("localPich",infoFactory.makeInfo("localPich", "D:\\botPic\\pixivh"));
			//  folder.put("netPic", infoFactory.makeInfo("netPic", "D:\\botPic\\randomPicSave"));
			//  folder.put("伪娘",infoFactory.makeInfo("伪娘", "D:\\新建文件夹\\伪娘"));
			//  folder.put("福利姬",infoFactory.makeInfo("福利姬", "D:\\新建文件夹\\福利姬"));
			//  folder.put("miku", infoFactory.makeInfo("miku", "D:\\新建文件夹\\Miku"));
			//  folder.put("贴贴", infoFactory.makeInfo("贴贴", "D:\\botPic\\贴贴"));
			//  System.err.println("图片文件夹信息读取完毕");
			groupAuth = new authority(true);
			userAuth = new authority(false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean getAuth(GroupMessageEvent groupMsg, String level) {
		String code=groupMsg.getGroup().getId().toString(),user=groupMsg.getAuthor().getId().toString();
		return groupAuth.haveAuth(code, level)||userAuth.haveAuth(user,"admin");
	}

	public boolean getAuth(String id, String level, boolean isUser) {
		if(isUser) return userAuth.haveAuth(id, level);
		else return groupAuth.haveAuth(id,level);
	}


	public picFolderInfo getFolderPath(String s) {
		return folder.get(s);
	}

	public void setSendPicPath(String name, String path) {
		nameToPath.put(name, path);
	}

	public boolean deleteByName(String name) {
		return deleteByPath(nameToPath.get(name));
	}

	private boolean deleteByPath(String s) {
		File file = new File(s);
		return file.renameTo(new File("D:\\botPic\\deletePic\\" + file.getName()));
	}

	public void finish() {
		File file = new File(netPicPath + "evaluation.txt");
		try {
			FileWriter fw = new FileWriter(file);
			fw.write("");
			fw.flush();
			evaluation.forEach((key, value) -> {
				try {
					if (key >= 0) {
						fw.write(key + "   " + value + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			System.out.println("评分信息保存完毕");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		folder.forEach((k, v) -> folder.put(k, v.refresh()));
		System.out.println("文件夹信息刷新完毕");
	}

	public void down(String url, String path) {
		File file = new File(path);
		try {

			BufferedInputStream in = new BufferedInputStream(DownLoad.getUrlInputStream(url));
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
			byte[] temp = new byte[2048];
			int len = in.read(temp);
			while (len != -1) {
				out.write(temp, 0, len);
				len = in.read(temp);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public MessageReceipt sendMsg(GroupMessageEvent groupMsg, Message.Element<?> element, boolean haveAuth) {
		System.out.println("element.toString() = " + element.toString());
		System.out.println("element.getKey() = " + element.getKey());
		//if(element.toString())
		return this.sendMsg(groupMsg,Messages.toMessages(element), haveAuth);
	}

	public MessageReceipt sendMsg(GroupMessageEvent groupMsg, Messages msg, boolean haveAuth) {
		MessageReceipt messageReceipt=null;

		String code = groupMsg.getGroup().getId().toString(), userID =groupMsg.getAuthor().getId().toString();
		if (msg == null||msg.getSize()==0) {
			return messageReceipt;
		}

		boolean sended = warned.getOrDefault(code, false);
		int RTimes = sendedTimes.getOrDefault(code, 0);
		//check times
		if ("1154459434".equals(userID) || msg.getFirstOrNull(Image.Key)!=null) {
			//可以发送
		} else if (!sended && RTimes > 3) {//未警告且已达3次
			warned.put(code, true);
			msg=Messages.toMessages(
					Image.of(Resource.of(new File("D:\\botPic\\z.jpg")))
			,Text.of( "不可以压榨bot"));
			groupMsg.getGroup().sendBlocking(msg);
			return messageReceipt;
		} else if (sended) {//达五次已警告，不管
			return messageReceipt;
		}
		System.out.println("msg = " + msg);
		/*
		for (Message.Element<?> element : msg) {
			if(element.toString().contains("file")){
				RTimes--;
			}else {
				RTimes++;
			}
		}*/
		RTimes++;
		RTimes=max(0,RTimes);
		sendedTimes.put(code, RTimes);

		System.out.println("群" + code + "当前一分钟内发言次数" + RTimes);
		if ((/*"1154459434".equals(userID) ||*/ haveAuth)&&msg.getSize()>0) {
			try {
				messageReceipt = groupMsg.getGroup().sendBlocking(msg);

				System.out.println("messageReceipt.isSuccess() = " + messageReceipt.isSuccess());
			}catch (Exception e){
				e.printStackTrace();
			}
			//sender.sendGroupMsg(code, msg);
		} else if (!haveAuth) {
			groupMsg.getGroup().sendBlocking(Messages.toMessages(Text.of(DefaultMsg)));
		}
		return messageReceipt;
	}

	public static void sendMsg(PrivateMsg privateMsg, Sender sender, String msg, boolean haveAuth) {
		String userID = privateMsg.getAccountInfo().getAccountCode();
		if (!haveAuth) {
			sender.sendPrivateMsg(userID, DefaultMsg);
			return;
		}
		sender.sendPrivateMsg(userID, msg);
	}
}