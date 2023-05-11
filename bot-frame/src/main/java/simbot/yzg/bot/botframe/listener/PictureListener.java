package simbot.yzg.bot.botframe.listener;

import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.Listener;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.At;
import love.forte.simbot.message.Image;
import love.forte.simbot.message.Messages;
import love.forte.simbot.message.Text;
import love.forte.simbot.resources.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simbot.yzg.bot.botframe.serviceImpl.MyProduce;
import simbot.yzg.bot.commonapi.entity.PictureResponse;
import simbot.yzg.bot.commonapi.service.PictureService;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;

import static simbot.yzg.bot.botframe.serviceImpl.MyProduce.number;

@Component
public class PictureListener {
    public static int e97 = 1000000007;

    Logger log = LoggerFactory.getLogger(PictureListener.class);


    @Autowired
    MyProduce myProduce;

    @DubboReference(group = "simbot",interfaceClass = PictureService.class,check = false)
    PictureService pictureService;


    /**
     * @param groupMessageEvent
     * @param
     * @Author Guo
     * @CreateTime 2022-05-20 00:10
     * @Return void
     * @Discription 随机网络涩图
     */
    @Listener
    @Filter(value = "/ir", matchType = MatchType.TEXT_STARTS_WITH)
    public void getRandomNetPic(GroupMessageEvent groupMessageEvent) {
        // 获取消息正文。
        boolean auth = myProduce.getAuth(groupMessageEvent, "pic");
        String text = groupMessageEvent.getMessageContent().getPlainText();
        int num = 1;
        Matcher m = number.matcher(text);
        if (m.find()) {
            num = Integer.parseInt(m.group().trim());
        }
        final Messages[] sendMessages = {Messages.emptyMessages()};
        log.info("{}发送了{},要求{}张网络图片",groupMessageEvent.getAuthor().getId(),groupMessageEvent.getMessageContent().getMessages(),num);

        List<PictureResponse> netPics = pictureService.getNetPics(num);
        netPics.forEach(a-> sendMessages[0] = sendMessages[0].plus(Image.of(Resource.of(a.getContent(),"temp"))));

        myProduce.sendMsg(groupMessageEvent, sendMessages[0], auth);
    }

    /**
     * @param groupMessageEvent
     * @param
     * @Author Guo
     * @CreateTime 2022-05-20 00:10
     * @Return void
     * @Discription 本地涩图
     */
    @Listener
    @Filter(value = "/lr", matchType = MatchType.TEXT_STARTS_WITH)
    public void getRandomLocalPic(GroupMessageEvent groupMessageEvent) {
        // 获取消息正文。
        String text = groupMessageEvent.getMessageContent().getPlainText().trim();
        boolean auth = myProduce.getAuth(groupMessageEvent, "pic");
        int num = 1;
        Matcher m = number.matcher(text);
        if (m.find()) {
            num = Integer.parseInt(m.group().trim());
        }
        if (!groupMessageEvent.getAuthor().getId().toString().equals("1154459434")) {
            num = Math.min(num, 3);
        }

        doSendPicture(groupMessageEvent, text, auth, num);
    }


    @Listener
    @Filter(value = "来点", matchType = MatchType.TEXT_STARTS_WITH)
    public void RandomLocalPicReuse(GroupMessageEvent groupMessageEvent) {
        // 获取消息正文。
        String text = groupMessageEvent.getMessageContent().getPlainText().trim();
        if (!text.endsWith("涩图") && !text.endsWith("色图")) {
            return;
        }
        text = text.replaceAll("来点|涩图|色图", " ");
        boolean auth = myProduce.getAuth(groupMessageEvent, "pic");
        int num = 3;

        doSendPicture(groupMessageEvent, text, auth, num);
    }


    private void doSendPicture(GroupMessageEvent groupMessageEvent, String text, boolean auth, int num) {
        String flag = "localPic", code = groupMessageEvent.getGroup().getId().toString();

        int times = myProduce.localPicSended.getOrDefault(code, 0);
        if (times > 30) {
            if (times != e97) {
                myProduce.sendMsg(groupMessageEvent, Text.of("过多的图片要求"), auth);
                myProduce.localPicSended.put(code, e97);
            }
            return;
        }
        if(text.contains("miku")){
            flag="miku";
            auth = myProduce.getAuth(groupMessageEvent, "basic");
        }else if (text.contains("ff")) {
            flag = "ff14";
        } else if (text.contains("伪娘")) {
            flag = "伪娘";
        }

        if (text.contains("福利姬")) {
            flag = "福利姬";
            auth = myProduce.getAuth(groupMessageEvent, "r18");
        }else if (text.contains("h")) {
            flag += "h";
            auth = myProduce.getAuth(groupMessageEvent, "r18");
        }

        Messages sendMessage = Messages.emptyMessages();
        System.out.println(code + "发送了" + groupMessageEvent.getMessageContent().getMessages() + "要求" + num + "张" + flag + "图片");

        StringBuilder sb = new StringBuilder();
        List<PictureResponse> paths=pictureService.getLocalPics(flag,num);
        for (PictureResponse pic : paths) {
            String name = pic.getTitle();
            System.out.println("图片:" + name);
            if (flag.contains("local")) {
                sb.append("name:").append(name);
            }
            sendMessage = sendMessage.
                    plus(Text.of(sb.toString())).
                    plus(Image.of(Resource.of(pic.getContent(),"temp")));
            myProduce.setSendPicPath(name, pic.getId()+"");
            sb = new StringBuilder();

        }

        myProduce.sendMsg(groupMessageEvent, sendMessage, auth);
        times = Math.max(times, myProduce.localPicSended.getOrDefault(code, 0)) + (auth?num:0);
        myProduce.localPicSended.put(code, times);
        System.out.println(code + "已发送" + times + "本地图片\n\n");
    }

    /**
     * @param groupMessageEvent
     * @param
     * @Author Guo
     * @CreateTime 2022-07-20 00:49
     * @Return void
     * @Discription 贴贴
     */
    @Listener
    @Filter(value = "贴贴", matchType = MatchType.TEXT_ENDS_WITH)
    public void tietie(GroupMessageEvent groupMessageEvent) {
        Messages res = Messages.emptyMessages();
        if (groupMessageEvent.getMessageContent().getMessages().get(At.Key).toString().contains("3425460643")) {
            res = res.plus(Image.of(Resource.of(pictureService.getLocalPic("贴贴").getContent(),"temp")));
            if (new Random().nextInt(30) ==1) {
                res = Messages.toMessages(Text.of(
                        "    　      ∧,,　\n" +
                                "        　 ヾ  ｀. ､`フ\n" +
                                "　  　( ,｀'´ヽ､､ﾂﾞ\n" +
                                "　 (ヽｖ'   　` ' ' ﾞつ\n" +
                                "　　,ゝ　 ⌒`ｙ'''´\n" +
                                "　 （ (´＾ヽこつ\n" +
                                "　　 ) )\n" +
                                "　　(ノ"));
            }
        }
        myProduce.sendMsg(groupMessageEvent, res, true);
    }

}
