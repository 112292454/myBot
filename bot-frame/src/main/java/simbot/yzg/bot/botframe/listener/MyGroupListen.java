package simbot.yzg.bot.botframe.listener;


import DownloadTools.DownLoad;
import DownloadTools.util.QRCodeUtil;
import catcode.CatCodeUtil;
import com.google.gson.Gson;
import love.forte.di.annotation.Beans;
import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.Listener;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.Identifies;
import love.forte.simbot.component.mirai.event.MiraiGroupMessageEvent;
import love.forte.simbot.component.mirai.message.MiraiMessageParserUtil;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.*;
import love.forte.simbot.resources.Resource;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.ForwardMessage;
import net.mamoe.mirai.message.data.ForwardMessageBuilder;
import net.mamoe.mirai.message.data.MessageChain;
import org.apache.commons.io.IOUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simbot.yzg.bot.botframe.dao.GroupDao;
import simbot.yzg.bot.botframe.serviceImpl.MyProduce;
import simbot.yzg.bot.commonapi.entity.GroupCode;
import simbot.yzg.bot.commonapi.service.MessagePersistenceService;
import simbot.yzg.bot.commonapi.service.PicSourcesService;
import simbot.yzg.bot.commonapi.utils.UrlUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 群消息监听的示例类。
 * 所有需要被管理的类都需要标注 {@link Beans} 注解。
 * @author ForteScarlet
 */

@Component
public class MyGroupListen {
    public static final String PIXIV_NOVEL_URL="https://www.pixiv.net/novel/show.php?id=";
    static Pattern p1 = Pattern.compile("text\":.\"[^\"]+");//text":4"xxxxxx
    static Pattern p3 = Pattern.compile("name:\\w+");//name:xxxxxx
    static Pattern Dice = Pattern.compile("\\d+(d|D)\\d+");//aDb;adb
    static Pattern validID = Pattern.compile("([一-龟|\\w| ]+([- ])+)?[\\d|.| ]{2,}-*([一-龟|\\w| ]+([- ])+)*[一-龟|\\w| ]+");//buptIDCheck
    static Pattern buptId = Pattern.compile("计科|计院|计算机|网安|电子|树莓|数媒|信通|通信|ai|AI|北师|国院|果园|电管|人文|工商管理|软工|物联|人工智能|经管");
    static HashSet<String> checkedID = new HashSet<>();


    private static Messages BUPTHELP;


    /**
     * log
     */
    private static Logger LOG = LoggerFactory.getLogger(MyGroupListen.class);

    @DubboReference(group = "simbot", version = "1.0.0",interfaceClass = MessagePersistenceService.class,check = false)
    MessagePersistenceService messagePersistenceService;

    @DubboReference(group = "simbot",version = "1.0.0",interfaceClass = PicSourcesService.class,check = false)
    PicSourcesService picSourcesService;




    @Autowired
    MyProduce myProduce;
    @Autowired
    QRCodeUtil q;

    private Messages getMsg(GroupMessageEvent groupMessageEvent) {
        return groupMessageEvent.getMessageContent().getMessages();
    }



    /**
     * @param groupMessageEvent
     * @param
     * @Author Guo
     * @CreateTime 2022-05-20 00:08
     * @Return void
     * @Discription 二维码识别
     */
    @Listener
    @Filter(value = "/qr", matchType = MatchType.TEXT_STARTS_WITH)
    public void QRCodeUtil(GroupMessageEvent groupMessageEvent) {
        String msg = groupMessageEvent.getMessageContent().getPlainText();
        boolean auth = myProduce.getAuth(groupMessageEvent, "basic");
        System.out.println(groupMessageEvent.getAuthor().getId() + "发送了" + msg + "要求二维码识别");
        Messages messages = getMsg(groupMessageEvent), sendMeaasges;


        ArrayList<String> urls = new ArrayList<>();
        //获取所有图片链接并得到其中的二维码识别结果
        List<Image> images = messages.get(Image.Key);
        System.out.println("img counts: " + images.size());
        for (Image image : images) {
            String s = image.getResource().getName().toString();
            System.out.println("s = " + s);
            String res = q.UrlQRCode(s);
            Matcher m1 = p1.matcher(res);
            while ((m1.find())) {
                res = m1.group().substring(8);
                urls.add(res);
                myProduce.VisQR.put(s, res);
            }
        }
        StringBuilder sb = new StringBuilder();
        //单次发送链接最大长约950字符，则手机端可以识别标蓝
        for (String url : urls) {
            if (DownLoad.isConnect(url)) {
                if (sb.length() + url.length() < 950) {
                    sb.append("有效：" + url + "\n");
                } else {
                    myProduce.sendMsg(groupMessageEvent, Text.of(sb.toString()), auth);
                    sb = new StringBuilder(url + "\n");
                }
            } else {
                if (sb.length() + url.length() < 950) {
                    sb.append(url + "\n");
                } else {
                    myProduce.sendMsg(groupMessageEvent, Text.of(sb.toString()), auth);
                    sb = new StringBuilder(url + "\n");
                }
            }
        }
        myProduce.sendMsg(groupMessageEvent, love.forte.simbot.message.Text.of(sb.toString()), auth);
    }

    @Listener
    @Filter(value = "/hnovel", matchType = MatchType.TEXT_STARTS_WITH)
    public void hNovelIndex(GroupMessageEvent groupMessageEvent){
        boolean auth=myProduce.getAuth(groupMessageEvent,"r18"),urlAuth=myProduce.getAuth(groupMessageEvent,"pic");
        ReceivedMessageContent messageContent = groupMessageEvent.getMessageContent();
        String text=messageContent.getPlainText();
        String tar=text.split(" ", 2)[1];
        String url="http://localhost:13120/";
        tar= UrlUtil.getURLEncoderString(tar);
        String request=url+tar;
        String res= null;
        try {
            res = IOUtils.toString(new URI(request));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        StringBuilder send=new StringBuilder();
        Map<String,String> temp=new Gson().fromJson(res,Map.class);

        StringBuilder finalSend = send;
        if(temp.size()==0) send=new StringBuilder("无结果(或搜索超时)");
        else{
            send.append("total:").append(temp.size()).append('\n');
            temp.entrySet().stream().skip(new Random().nextInt(Math.max(1,temp.size() - 5))).limit(5).forEach(e->{
                String p=e.getKey(),view=e.getValue();
                String index=p.substring(p.lastIndexOf('\\')+1);
                String title=index.substring(index.indexOf('-')+1, index.lastIndexOf('-'));
                index=index.substring(0,index.indexOf('-'));
                finalSend.append(PIXIV_NOVEL_URL+index);
                if(auth){
                    finalSend.append("    预览：").append(view);
                }else if(urlAuth){
                    finalSend.append("    标题：").append(title);
                }
                finalSend.append('\n');
            });
        }

        myProduce.sendMsg(groupMessageEvent,Text.of(send.toString()),urlAuth);
    }


    /**
     * @param groupMessageEvent
     * @param
     * @Author Guo
     * @CreateTime 2022-05-20 00:10
     * @Return void
     * @Discription 骰子
     */
    @Listener
    @Filter(value = "/r", matchType = MatchType.TEXT_STARTS_WITH)
    public void random(GroupMessageEvent groupMessageEvent) {
        CatCodeUtil util = CatCodeUtil.INSTANCE;

        String text = groupMessageEvent.getMessageContent().getPlainText().toLowerCase();
        boolean auth = myProduce.getAuth(groupMessageEvent, "basic");
        Matcher dice = Dice.matcher(text), num = MyProduce.number.matcher(text);
        int range = 100, res = 0, max = 50;
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        if (text.startsWith("/ra")) {
            if (num.find()) {
                max = Integer.parseInt(num.group());
            }
            if (max >= 100 || max <= 0) {
                builder.append("错误的成功率");
            } else {
                res = random.nextInt(range) + 1;
                text = text.substring(3).trim();
                String name = "default";
                for (String d : text.split(" ")) {
                    try {
                        Integer.parseInt(d.trim());
                    } catch (NumberFormatException n) {
                        name = d;
                        break;
                    }
                }
                builder.append(groupMessageEvent.getAuthor().getNickOrUsername());
                builder.append("进行").append(name).append("检定：D100=");
                builder.append(res).append("/").append(max).append("   ");

                if (res <= max) {
                    if (res <= 5) {
                        builder.append("大");
                    } else if (res <= max / 5) {
                        builder.append("极难");
                    } else if (res <= max / 2) {
                        builder.append("困难");
                    }
                    builder.append("成功");
                } else {
                    if (res > 95) {
                        builder.append("大");
                    }
                    builder.append("失败");
                }
            }
        } else if (dice.find()) {
            String[] n = dice.group().split("d");
            int times = Integer.parseInt(n[0]);
            range = Integer.parseInt(n[1]);
            for (int i = 0; i < times; i++) {
                res += random.nextInt(range) + 1;
            }
            builder.append(times).append("d").append(range).append("=").append(res);
        } else if (num.find()) {
            range = Integer.parseInt(num.group());
            builder.append("1d").append(range).append("=").append(random.nextInt(range) + 1);
        } else {
            builder.append("1d").append(range).append("=").append(random.nextInt(range) + 1);
        }
        myProduce.sendMsg(groupMessageEvent, Text.of(builder.toString()), auth);
        System.out.println(groupMessageEvent.getAuthor().getId() + "要求一个随机数");


    }


    /**
     * @param groupMessageEvent
     * @param
     * @Author Guo
     * @CreateTime 2022-05-20 00:10
     * @Return void
     * @Discription 识图——低配版
     */
    @Listener
    @Filter(value = "/sau", matchType = MatchType.TEXT_STARTS_WITH)
    public void imageSauce(GroupMessageEvent groupMessageEvent) {

        boolean auth = myProduce.getAuth(groupMessageEvent, "basic");
        Messages sendedMessages=Messages.emptyMessages();
        List<Image> images = groupMessageEvent.getMessageContent().getMessages().get(Image.Key);
        System.out.println("img counts: " + images.size());

        for (Image image : images) {
            ArrayList<Messages> res= null;
            try {
                res = picSourcesService.getPicSource(image);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            for (Messages re : res) {
                sendedMessages=sendedMessages.plus(re);
            }
        }

        myProduce.sendMsg(groupMessageEvent,sendedMessages, auth);
        System.out.println(groupMessageEvent.getAuthor().getId() + "要求图片识别——低配版");
    }
/*
    @Listener
    @Filter(value = "/zf", matchType = MatchType.TEXT_EQUALS_IGNORE_CASE)
    public void onGroupMessageEvent(MiraiGroupMessageEvent event) {
        // 得到此事件的所在群，并得到这个群原生的mirai contact对象。
        // 其他Mirai组件特供的对象也都有类似的功能，例如 MiraiMember.getOriginalContact
        // 可以通过此方式来得到mirai原生的对象类型。
        Group groupContact = event.getGroup().getOriginalContact();


        // 事件也是一样的。你可以在simbot所包装的Mirai事件中直接得到mirai原生事件。
        // 这里的 GroupMessageEvent是mirai的事件类型，而不是simbot的。
        net.mamoe.mirai.event.events.GroupMessageEvent originalEvent = event.getOriginalEvent();

        // 同理，消息也是。除了部分simbot标准消息类型以外（例如simbot中的At、Text），
        // 所有由mirai组件特殊实现的消息类型都会实现 MiraiSimbotMessage 接口。
        // 所有从接收的事件中得到的、由mirai组件特殊实现的消息类型都会实现 OriginalMiraiComputableSimbotMessage 接口。
        // 但是，如果你更希望直接操作原本的mirai消息链，则可能会更加方便。上文中我们已经得到了mirai的原生消息类型，
        // 因此你可以直接得到此事件的最原始的消息链。

        // mirai原生的消息链对象
        MessageChain messageChain = originalEvent.getMessage();
        messageChain.clear();
        messageChain.plus(new at);
        net.mamoe.mirai.message.data.Message test=MiraiMessageParserUtil.toOriginalMiraiMessage(Messages.toMessages(Text.of("aaa")), groupContact, new Continuation<net.mamoe.mirai.message.data.Message>() {
            @NotNull
            @Override
            public CoroutineContext getContext() {
                return new YieldContext();
            }

            @Override
            public void resumeWith(@NotNull Object o) {

            }
        });

        ForwardMessageBuilder builder = new ForwardMessageBuilder(groupContact);
        // 转发当前这条消息。
        // 使用一开始说到的方式，得到当前消息事件的发送者，然后把消息链放进去。
        builder.add(event.getAuthor().getOriginalContact(),test);
        // 构建转发消息
        ForwardMessage forwardMessage = builder.build();

        // 发送消息
        // 两种方式。第一种，通过simbot的 send/sendBlocking 发送。将ForwardMessage转化为simbot的消息对象即可。
        // 第二种，你上面得到了mirai原生的群对象，那么你可以直接通过mirai本身的api发送。
        // 这里以第一种为例。
        event.sendBlocking(MiraiMessageParserUtil.asSimbotMessage(forwardMessage));
    }*/

    @Listener
    @Filter(value = "/news", matchType = MatchType.TEXT_EQUALS_IGNORE_CASE)
    public void news(GroupMessageEvent groupMessageEvent) {
        Message.Element<?> element = null;
        try {
            element = Image.of(Resource.of(new URL("https://api.vvhan.com/api/60s")));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        myProduce.sendMsg(groupMessageEvent, element, true);
    }


    @Listener
    @Filter(value = "/makeqr", matchType = MatchType.TEXT_STARTS_WITH)
    public void makeQR(GroupMessageEvent groupMessageEvent) {
        String txt = groupMessageEvent.getMessageContent().getPlainText();
        txt = txt.substring(txt.indexOf(' ')).trim();
        Message.Element<?> element = null;
        try {
            element = Image.of(Resource.of(new URL("https://api.vvhan.com/api/qr?text=" + txt)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        myProduce.sendMsg(groupMessageEvent, element, true);
    }



    //@Listener
    @Filter(value = "hitokoto|一言", matchType = MatchType.REGEX_MATCHES)
    public void hitokoto(GroupMessageEvent groupMessageEvent) {
        String url = "https://api.vvhan.com/api/ian?type=json";
        if (new Random().nextInt(100) > 80) {
            url += "&cl=ac";
        } else {
            url += "&cl=wx";
        }
        URL u = null;
        try {
            u = new URL(url);
            Gson gson = new Gson();
            Map<String, String> map;
            map = gson.fromJson(new Scanner(u.openStream()).nextLine(), Map.class);
            Map<String, String> source = gson.fromJson(gson.toJson(map.get("data")), Map.class);
            String txt = source.get("vhan"), from = source.get("source");
            String res = txt + "\n         ——by" + from;
            myProduce.sendMsg(groupMessageEvent, Text.of(res), true);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * @param groupMessageEvent
     * @param
     * @Author Guo
     * @CreateTime 2022-05-20 00:11
     * @Return void
     * @Discription 帮助
     */
    @Listener
    @Filter(value = "/help", matchType = MatchType.TEXT_EQUALS_IGNORE_CASE)
    public void helpMsg(GroupMessageEvent groupMessageEvent) {
        boolean auth = myProduce.getAuth(groupMessageEvent, "basic");
        System.out.println(groupMessageEvent.getAuthor().getId() + "发送了" + groupMessageEvent.getMessageContent().getPlainText() + "希望获得帮助");
        StringBuilder builder = new StringBuilder();
        builder.append("当前功能:\n");
        //builder.append(" 1、“/ir [数量]“得到随机二刺猿图\n");
        builder.append(" 2、”/qr [图片]“（可一次多张）识别含有的二维码；“/makeqr [文本]”制作文本对应的二维码\n");
        if (myProduce.getAuth(groupMessageEvent.getGroup().getId().toString(),"pic",false)) {
            builder.append(" 3、“/lr [类型] [数量]“得到本地图片;”来点xx涩图“同前，固定三张\n");
            builder.append("    类型有：ff14、（空白）\n");
        } if (myProduce.getAuth(groupMessageEvent.getGroup().getId().toString(),"r18",false)) {
            builder.append("    ff14h、h、福利姬(图最多）\n");
        }
        builder.append(" 4、”/r [x]或[ydx]“,掷y个x面骰，默认x=100，y=1;“/ra [x(成功率)] [检定名称]”\n");
        builder.append(" 5、“/sau [图片]” 为识图（半成品，转发形式待制作）\n");
        if(myProduce.getAuth(groupMessageEvent.getGroup().getId().toString(),"r18",false)){
            builder.append(" 6、“hnovel [要搜索的内容]” 搜索随机5条h小说，支持正则表达式\n");
        }
        builder.append(" 7、“(我的)今日词云” 查看词云\n");
        builder.append("其他：“/news”今日新闻、“bupt”北邮相关");
        //builder.append(" 消息最后包含“private”则只发送私聊");
        myProduce.sendMsg(groupMessageEvent, Text.of(builder.toString()), auth);
    }

    /**
     * @param groupMessageEvent
     * @param
     * @Author Guo
     * @CreateTime 2022-07-20 00:11
     * @Return void
     * @Discription bupt相关资料
     */

    public boolean temp=true;
    @Listener
    public void OtherCheck(GroupMessageEvent groupMessageEvent) {
        String txt = groupMessageEvent.getMessageContent().getPlainText().toLowerCase(), groupCode = groupMessageEvent.getGroup().getId().toString(), userCode = groupMessageEvent.getAuthor().getId().toString();
        String res = "";
        Messages messages = Messages.emptyMessages();
        if ("681338941".equals(groupCode)) {
            if (txt.equals("bupt")) {
                if (BUPTHELP == null) {
                    messages = messages.plus(Text.of(
                            "看群公告群文件群相册群精华消息\n\n"
                                    + "学校官网(需有学号后使用)： webvpn.bupt.edu.cn  常用信息门户、本科教务管理系统\n"
                                    + "进群记得修改群名片 例如【20-河南-计算机-汪哈羊】"));
                    messages = messages.plus(Image.of(Resource.of(new File("D:\\botPic\\bupt\\tips.jpg"))));
                    messages = messages.plus(Text.of(
                            "功能：”学校地图“，”学校网址“，”宿舍规格“,”本部地图“，”快递地址“\n"
                                    + "“/put [名称] [群号]”放入群号，”北邮xx群“查询群号，“@我”@你自己"));
                    BUPTHELP = messages;
                } else {
                    messages = BUPTHELP;
                }
            } else if (txt.contains("宿舍规格")) {
                messages=messages.plus(Image.of(Resource.of(new File("D:\\botPic\\bupt\\宿舍.jpg"))));
            } else if (txt.equals("学校地图")) {
                messages=messages.plus(Image.of(Resource.of(new File("D:\\botPic\\bupt\\校园地形.jpg"))));
            } else if (txt.equals("本部地图")) {
                messages=messages.plus(Image.of(Resource.of(new File("D:\\botPic\\bupt\\本部地图.jpg"))));
            } else if (txt.contains("wifi") && txt.contains("怎么")) {
                messages=messages.plus(Image.of(Resource.of(new File("D:\\botPic\\bupt\\wifi.jpg"))));
            } else if (txt.equals("学校网址")) {
                res = "选课使用教务系统，如果无法直接打开可以通过VPN登陆。\n" +
                        "WebVpn系统（在校外访问内?必备）： https://webvpn.bupt.edu.cn/login以及https://libcon.bupt.edu.cn/  \n" +
                        "北邮人bt(只有ipv6才能访问)： http://byr.pt \n" +
                        "信息门户： http://my.bupt.edu.cn/ \n" +
                        "教务系统： https://jwgl.bupt.edu.cn/jsxsd/ \n" +
                        "北邮人论坛： https://bbs.byr.cn/\n" +
                        "图书馆预约： http://order.bupt.edu.cn/\n" +
                        "爱课堂： https://iclass.bupt.edu.cn/  \n" +
                        "22招生宣传： https://mp.weixin.qq.com/s/2hcgfdnDalK9MpHZzLnPCA";
            } else if (txt.equals("快递地址")) {
                res = "沙河地址：北京市昌平区沙河镇高教园北京邮电大学沙河校区\n" +
                        "本部地址：北京市海淀区西土城路10号北京邮电大学";
            } else if (txt.contains("生活费") && txt.contains("多少")) {
                res = "1500生活足够，有社交聚会/购物（衣物等）/谈恋爱等需求自行斟酌；第一个月建议至少3k+，" +
                        "如果家长没有准备被褥等大件额外再加";
            }

            /*Object[] temp=groupMessageEvent.getGroup().getMembers().toArray();
            Messages tempCheck=Messages.emptyMessages();
            for (Object o : temp) {
                Member member = (Member)o;
                String name=member.getNickname();
                if (name == null) {
                    name = "!";
                }
                {
                    name = name.replace((char) 8211, (char) 45)
                            .replace((char) 65293, (char) 45)
                            .replace((char) 8722, (char) 45)
                            .replace((char) 8212, (char) 45)
                            .replace('+', (char) 45)
                            .replace('/', (char) 45)
                            .replace('(', (char) 45)
                            .replace(')', (char) 45)
                            .replace('（', (char) 45)
                            .replace('）', (char) 45)
                            .replace('、', (char) 45);
                }
                boolean format=validID.matcher(name).find(),kind=buptId.matcher(name).find();
                if(format||kind){

                }else {
                    //if(tempCheck.size()<5){
                        tempCheck=tempCheck.plus(new At(member.getId()));
                    //}else {
                        //myProduce.sendMsg(groupMessageEvent, tempCheck, true);
                    //}
                }
                System.out.println(((Member) o).getNickname());
            }
            tempCheck=tempCheck.plus(Text.of("修改群名片 例如【20-所在省-计算机-汪哈羊】\n另：高中信息竞赛省一可以联系rf/杨老师"));
            myProduce.sendMsg(groupMessageEvent, tempCheck, true);
            System.err.println(tempCheck);
            temp=false
            */

        }

        if ("852209848".equals(groupCode)) {
            if ("939570061".equals(userCode)) {
                if (txt.contains("啵") || txt.contains("播")) res = "啵";
            }
        }
        if (res.length() != 0) {
            messages = messages.plus(Text.of(res));
        }
        myProduce.sendMsg(groupMessageEvent, messages, true);

    }

    @Listener
    @Filter(value = "海南学院", matchType = MatchType.TEXT_EQUALS)
    public void hainan(GroupMessageEvent groupMessageEvent) {
        Message.Element<?> element =
                Image.of(Resource.of(new File("D:\\botPic\\bupt\\海南学院.jpg")));
        myProduce.sendMsg(groupMessageEvent, element, true);
    }

    //@Listener
    public void IDCheck(GroupMessageEvent groupMessageEvent) {
        /*if(new Random().nextInt(5)!=1) {
            return;
        }*/
        if (!"681338941".equals(groupMessageEvent.getGroup().getId().toString())) {
            return;
        }
        checkedID.add("770816116");
        checkedID.add("3214447142");

        String id = groupMessageEvent.getAuthor().getNickname(), code = groupMessageEvent.getAuthor().getId().toString();
        Messages messages=Messages.emptyMessages();
        messages=messages.plus(new At(Identifies.ID(code)));
        if (id == null) {
            id = "!";
        }
        {
            id = id.replace((char) 8211, (char) 45)
                    .replace((char) 65293, (char) 45)
                    .replace((char) 8722, (char) 45)
                    .replace((char) 8212, (char) 45)
                    .replace('+', (char) 45)
                    .replace('/', (char) 45)
                    .replace('(', (char) 45)
                    .replace(')', (char) 45)
                    .replace('（', (char) 45)
                    .replace('）', (char) 45)
                    .replace('、', (char) 45);
        }
        System.out.println("检查buptid：" + id);
        messages=messages.plus(Text.of("记得修改群名片 例如【20-所在省-计算机-汪哈羊】"));
        Matcher m = validID.matcher(id);
        boolean match = validID.matcher(id).matches(), find = m.find() || id.length() > 11;
        if (match) {
            System.out.println("格式正确");
            return;
        } else if (find) {
            String s;
            try {
                s = m.group();
            } catch (IllegalStateException e) {
                System.out.println("存在为改昵称的id");
                s = "!";
            }
            if (id.startsWith(s) || id.endsWith(s)) {
                System.out.println("不符，但以合理id开头/结尾，跳过");
                return;
            }
            int t = new Random().nextInt(5);
            System.out.println("存在格式不符id,判断：" + t + (t == 3));
            if (t == 3 && !checkedID.contains(code)) {
                myProduce.sendMsg(groupMessageEvent,messages, true);
            }
            checkedID.add(code);
        } else if (!checkedID.contains(code)) {
            System.out.println("存在非法id");
            checkedID.add(code);
            /*if(new Random().nextInt(3)==1){
                checkedID.remove(code);
            }*/
            myProduce.sendMsg(groupMessageEvent, messages, true);
        }
    }
    @Autowired
    GroupDao groupDao;

    @Listener
    @Filter(value = "群", matchType = MatchType.TEXT_ENDS_WITH)
    public void groupCode(GroupMessageEvent groupMessageEvent) {
        String txt=groupMessageEvent.getMessageContent().getPlainText(),res="";
        if(txt.startsWith("北邮")){
            txt=txt.replace("北邮", "")
                    .replace("群", "");
            try {
                res=txt+"群："+groupDao.getGroupCodeByName(txt).getCode();
            } catch (Exception e) {
                res="未录入";
            }
        }
        myProduce.sendMsg(groupMessageEvent, Text.of(res), true);
    }

    @Listener
    @Filter(value = "/put", matchType = MatchType.TEXT_STARTS_WITH)
    public void putGroupCode(GroupMessageEvent groupMessageEvent) {
        String txt=groupMessageEvent.getMessageContent().getPlainText(),code=groupMessageEvent.getGroup().getId().toString();
        if(!"681338941".equals(code)){
            return;
        }
        String[] component=txt.split(" ");
        GroupCode groupCode = new GroupCode();
        for (int i = 1; i < 3; i++) {
            try {
                Long.parseLong(component[i]);

                groupCode.setCode(component[i]);
            } catch (NumberFormatException e) {
                groupCode.setName(component[i]);
            }
        }
        groupDao.addGroupCode(groupCode);
        myProduce.sendMsg(groupMessageEvent, Text.of("成功"), true);
    }

    @Listener
    @Filter(value = "@我", matchType = MatchType.TEXT_EQUALS)
    public void atSelf(GroupMessageEvent groupMessageEvent) {
        System.out.println("收到at自己的要求");
        myProduce.sendMsg(groupMessageEvent,new At(groupMessageEvent.getAuthor().getId()), true);
    }


    @Listener
    @Filter(value = "/finish", matchType = MatchType.TEXT_CONTAINS)
    public void closeRobot(GroupMessageEvent groupMessageEvent) {
        if (!"1154459434".equals(groupMessageEvent.getAuthor().getId().toString())) {
            System.out.println("invalid user finished");
            return;
        }
        myProduce.finish();
        System.out.println("save success");
    }


    /*@Listener
    @Filter(value = "down",matchType = MatchType.CONTAINS)
    public void VoteDown(groupMessageEvent groupMessageEvent){
        // 获取消息正文。
        System.out.println("进入downvote");
        String text=groupMessageEvent.getMessageContent().getPlainText().toLowerCase(Locale.ROOT).trim();
        String s=groupMessageEvent.getMsg();
        String name=p3.matcher(s).group();
        StringBuilder res=new StringBuilder();
        GroupAccountInfo info=groupMessageEvent.getAuthor();
        int idindex=s.indexOf("netPID")+6;
        if(idindex==7||!s.startsWith("[CAT:quote")) {
            return;
        }
        System.out.println(s);
        while (Character.isDigit(s.charAt(idindex))){
            res.append(s.charAt(idindex++));
        }
        idindex=Integer.parseInt(res.toString());
        if(text.contains("down")) {
            myProduce.evaluation.put(idindex,myProduce.evaluation.get(idindex)-1);
            System.out.println(info.getAccountNickname()+"("+info.getId()+")对id为"+idindex+"的图片做出了-1的评价");
        }
        System.out.println("图片"+idindex + "当前评价为" + myProduce.evaluation.get(idindex));
    }
    */


    @Listener
    public void infoCheck(GroupMessageEvent groupMessageEvent) {
        String groupName=groupMessageEvent.getGroup().getName(),groupCode=groupMessageEvent.getGroup().getId().toString();
        String userName=groupMessageEvent.getAuthor().getNickOrUsername(),userCode=groupMessageEvent.getAuthor().getId().toString();
        Messages messages = groupMessageEvent.getMessageContent().getMessages();
        String text= groupMessageEvent.getMessageContent().getPlainText();
        messagePersistenceService.putMessage(groupCode,text);
        messagePersistenceService.putMessage(userCode,text);
        messagePersistenceService.persistence();
        System.out.println("groupName= " + groupName);
        System.out.println("userName = " + userName);
        System.out.println("content=" + messages + "\n");
        List<Image<?>> imgs= messages.get(Image.Key);
        for (Image<?> image :imgs) {
            System.out.println("images= " + image.getResource());
            if(image.getResource().getName().contains("/0/0-0-")){
                StringBuilder name=new StringBuilder();
                name.append(groupMessageEvent.getGroup().getId().toString()).append("——")
                        .append(userName).append("____")
                        .append(groupName);
                System.out.println("有闪照：下载"+DownLoad.down(image.getResource().getName().toString(),"D:\\botPic\\backupPics\\",name.toString()+(System.currentTimeMillis()%1000000)));
            }
        }
        //System.out.println("quote：        " + groupMessageEvent.getMessageContent().getMessages().get(SimbotOriginalMiraiMessage.Key).get(0).toString()+ "\n");
        //System.out.println("groupMessageEvent.getComponent().toString() = " + groupMessageEvent.getComponent().toString());
        //System.out.println("groupMessageEvent.getKey(QuoteReply.Key).getParents() = " + groupMessageEvent.getKey().getParents());
    }



    //@Listener
    public void tooLongFold(MiraiGroupMessageEvent event) {
        if(event.getMessageContent().getPlainText().length()<200) return;
        // 得到此事件的所在群，并得到这个群原生的mirai contact对象。
        // 其他Mirai组件特供的对象也都有类似的功能，例如 MiraiMember.getOriginalContact
        // 可以通过此方式来得到mirai原生的对象类型。
        final Group groupContact = event.getGroup().getOriginalContact();
        // 事件也是一样的。你可以在simbot所包装的Mirai事件中直接得到mirai原生事件。
        // 这里的 GroupMessageEvent是mirai的事件类型，而不是simbot的。
        final net.mamoe.mirai.event.events.GroupMessageEvent originalEvent = event.getOriginalEvent();

        // 同理，消息也是。除了部分simbot标准消息类型以外（例如simbot中的At、Text），
        // 所有由mirai组件特殊实现的消息类型都会实现 MiraiSimbotMessage 接口。
        // 所有从接收的事件中得到的、由mirai组件特殊实现的消息类型都会实现 OriginalMiraiComputableSimbotMessage 接口。

        // 但是，如果你更希望直接操作原本的mirai消息链，则可能会更加方便。上文中我们已经得到了mirai的原生消息类型，
        // 因此你可以直接得到此事件的最原始的消息链。

        // mirai原生的消息链对象
        final MessageChain messageChain = originalEvent.getMessage();

        final ForwardMessageBuilder builder = new ForwardMessageBuilder(groupContact);
        // 转发当前这条消息。
        // 使用一开始说到的方式，得到当前消息事件的发送者，然后把消息链放进去。
        builder.add(event.getAuthor().getOriginalContact(), messageChain);

        // 构建转发消息
        final ForwardMessage forwardMessage = builder.build();

        // 发送消息
        // 两种方式。第一种，通过simbot的 send/sendBlocking 发送。将ForwardMessage转化为simbot的消息对象即可。
        // 第二种，你上面得到了mirai原生的群对象，那么你可以直接通过mirai本身的api发送。
        // 这里以第一种为例。
        event.sendBlocking(MiraiMessageParserUtil.asSimbotMessage(forwardMessage));
    }

    @Listener
    @Filter(value = "del", matchType = MatchType.TEXT_CONTAINS)
    public void delete(GroupMessageEvent groupMessageEvent) {
        // 获取消息正文。
        System.out.println("进入delete");
        String s = groupMessageEvent.getMessageContent().getPlainText(), name;
        Matcher m = p3.matcher(s);
        if (m.find()) {
            name = m.group().substring(5);
            //setter.setMsgRecall(groupMessageEvent.getFlag());
        } else {
            return;
        }
        System.out.println(myProduce.deleteByName(name));
    }
    /*@Listener
    public void ListenerMsg(GroupMessageEvent groupMessageEvent) {

        // 打印此次消息中的 纯文本消息内容。
        // 纯文本消息中，不会包含任何特殊消息（例如图片、表情等）。
        System.err.println("text:");
        System.out.println(groupMessageEvent.getMessageContent().getPlainText());

        // 打印此次消息中的 消息内容。
        // 消息内容会包含所有的消息内容，也包括特殊消息。特殊消息使用CAT码进行表示。
        // 需要注意的是，绝大多数情况下，getMsg() 的效率低于甚至远低于 getText()
        System.err.println("Msg:");
        String str=groupMessageEvent.getMsg();
        System.out.println(str);

        // 获取此次消息中的 消息主体。
        // messageContent代表消息主体，其中通过可以获得 msg, 以及特殊消息列表。
        // 特殊消息列表为 List<Neko>, 其中，Neko是CAT码的封装类型。

        MessageContent msgContent = groupMessageEvent.getMsgContent();
        // 打印消息主体
        System.err.println("messageContent:");
        System.out.println(msgContent);
        if(msgContent.toString().contains("flash")){
            String s=str.substring(str.indexOf("url="));
            System.err.println("有闪照：\nMsg:");
            System.out.println(s);
           .sendPrivateMsg("3425460643",s);
            List<Neko> imageCats = groupMessageEvent.getMsgContent().getCats("image");
            System.out.println("img counts: " + imageCats.size());
            AccountInfo accountInfo = groupMessageEvent.getAuthor();
            GroupInfo groupInfo = groupMessageEvent.getGroupInfo();
            StringBuilder name=new StringBuilder();
            name.append(accountInfo.getId()).append("——")
                    .append(accountInfo.getAccountNickname()).append("____")
                    .append(groupInfo.getGroupName());
            for (Neko image : imageCats) {
                DownLoad.down(image.getResource().toString(),"D:\\botPic\\backupPics\\",name.toString()+(System.currentTimeMillis()%1000000));
                System.out.println("Img url: " + image.getResource().toString());
            }
            System.out.println(name + "\n\n");
        }
        // 打印消息主体中的所有图片的链接（如果有的话）
        List<Neko> imageCats = msgContent.getCats("image");
        System.out.println("img counts: " + imageCats.size());
        for (Neko image : imageCats) {
            System.out.println("Img url: " + image.getResource().toString());
        }

        // 获取发消息的人。
        GroupAccountInfo accountInfo = groupMessageEvent.getAuthor();
        // 打印发消息者的账号与昵称。
        System.out.println(accountInfo.getId());
        System.out.println(accountInfo.getAccountNickname());


        // 获取群信息
        GroupInfo groupInfo = groupMessageEvent.getGroupInfo();
        // 打印群号与名称
        System.out.println(groupInfo.getGroupCode());
        System.out.println(groupInfo.getGroupName()+"\n\n");

    }*/

    /*@OngroupMessageEventRecall
    public void recallMsg(groupMessageEventRecall groupMessageEvent,){
        System.err.println("有撤回消息：\nMsg:");
        String str=groupMessageEvent.getMsg();
        System.out.println(str);

        List<Neko> imageCats = groupMessageEvent.getMsgContent().getCats("image");
        System.out.println("img counts: " + imageCats.size());
        AccountInfo accountInfo = groupMessageEvent.getAuthor();
        GroupInfo groupInfo = groupMessageEvent.getGroupInfo();
        StringBuilder name=new StringBuilder();
        name.append(accountInfo.getId()).append("——")
                .append(accountInfo.getAccountNickname()).append("____")
                .append(groupInfo.getGroupName());
        for (Neko image : imageCats) {
            DownLoad.down(image.getResource().toString(),"D:\\botPic\\backupPics\\",name.toString()+(System.currentTimeMillis()%1000000));
            System.out.println("Img url: " + image.getResource().toString());
        }
        System.out.println(name + "\n\n");
    }*/


    /*@Listener
    @Filter(value = "/special")
    public void special(GroupMessageEvent groupMessageEvent){
        try {
            BufferedInputStream in ;//= new BufferedInputStream(new URL("https://doubi.ren/zuanbot/api.php?level=max").openStream());

            System.out.println("special");
            /.sendPrivateMsg("1154459434",new String(temp));
            for (int i = 0; i < 500; i++) {
                byte[] temp=new byte[2048];
                for (int j = 0; j < 500000000; j++) {
                    if(j>999999) {j++;}
                }
                in=new BufferedInputStream(new URL("https://doubi.ren/zuanbot/api.php?level=max").openStream());
                in.read(temp);
               .sendPrivateMsg("2822795227","829090488",new String(temp));

            }
            System.out.println("special");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


}