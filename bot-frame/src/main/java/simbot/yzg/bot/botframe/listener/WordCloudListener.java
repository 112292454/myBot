package simbot.yzg.bot.botframe.listener;

import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.Listener;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.Image;
import love.forte.simbot.message.Message;
import love.forte.simbot.resources.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simbot.yzg.bot.botframe.serviceImpl.MyProduce;
import simbot.yzg.bot.commonapi.service.WordCloudService;

import java.io.File;
import java.util.regex.Matcher;

@Component
public class WordCloudListener {
    @DubboReference(group = "simbot", version = "1.0.0",interfaceClass = WordCloudService.class,check = false)
    WordCloudService myWordCloud;
    @Autowired
    MyProduce myProduce;

    @Listener
    @Filter(value = "今日词云", matchType = MatchType.TEXT_ENDS_WITH)
    public void myWordCloud(GroupMessageEvent groupMessageEvent) {
        String code=groupMessageEvent.getGroup().getId().toString();
        String txt = groupMessageEvent.getMessageContent().getPlainText();
        if(txt.equals("我的今日词云")) {
            code=groupMessageEvent.getAuthor().getId().toString();
        }
        String path=myWordCloud.createWordCountPic(code);

        Message.Element<?> element = Image.of(Resource.of(new File(path)));
        myProduce.sendMsg(groupMessageEvent, element, true);
    }

    @Listener
    @Filter(value = "/cloudTest", matchType = MatchType.TEXT_STARTS_WITH)
    public void myWordCloudTest(GroupMessageEvent groupMessageEvent) {
        boolean auth=myProduce.getAuth(groupMessageEvent,"admin");
        String code=groupMessageEvent.getGroup().getId().toString();
        String txt = groupMessageEvent.getMessageContent().getPlainText();
        Matcher matcher = MyProduce.number.matcher(txt);
        if(matcher.find()){
            code=matcher.group();
        }
        String path=myWordCloud.createWordCountPic(code);


        Message.Element<?> element = Image.of(Resource.of(new File(path)));
        myProduce.sendMsg(groupMessageEvent, element, auth);
    }

}
