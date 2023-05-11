package simbot.yzg.bot.botframe.listener;

import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.Listener;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.event.FriendMessageEvent;
import love.forte.simbot.event.GroupMessageEvent;
import org.springframework.stereotype.Component;

@Component
public class MyListener {

	@Listener
	public void onEvent(FriendMessageEvent event) {

		System.out.println("11111");
	}

	@Listener
	@Filter(value = "/lr",matchType = MatchType.TEXT_STARTS_WITH)
	public void getRandomLocalPic(GroupMessageEvent groupMsg) {
		if(!"1154459434".equals(groupMsg.getAuthor().getId())){
			return;
		}
		// 获取消息正文。
		System.out.println(groupMsg.getMessageContent().getPlainText());
		System.out.println("groupMsg.getAuthor() = " + groupMsg.getAuthor());
		System.out.println("groupMsg.getAuthor().getId() = " + groupMsg.getAuthor().getId());
		System.out.println("groupMsg.getAuthor().getOrganization() = " + groupMsg.getAuthor().getOrganization());
		System.out.println("groupMsg.getAuthor().getNickOrUsername() = " + groupMsg.getAuthor().getNickOrUsername());
		System.out.println("groupMsg.getSource().getMembers() = " + groupMsg.getSource().getMembers());
		System.out.println("groupMsg.getId() = " + groupMsg.getId());
		System.out.println("groupMsg.getGroup().getDescription() = " + groupMsg.getGroup().getDescription());
		System.out.println("groupMsg.getMessageContent().getMessageId() = " + groupMsg.getMessageContent().getMessageId());
		System.out.println("groupMsg.getMessageContent().getPlainText() = " + groupMsg.getMessageContent().getPlainText());
		System.out.println("groupMsg.getMessageContent().getMessages() = " + groupMsg.getMessageContent().getMessages());
		/**
		lr 1111
		groupMsg.getAuthor() = love.forte.simbot.component.mirai.internal.MiraiMemberImpl@4779d778
		groupMsg.getAuthor().getId() = 1154459434
		groupMsg.getAuthor().getOrganization() = love.forte.simbot.component.mirai.internal.MiraiGroupImpl@1045afaf
		groupMsg.getAuthor().getNickOrUsername() = /*2
		groupMsg.getSource().getMembers() = java.util.stream.ReferencePipeline$3@5e1eb2af
		groupMsg.getId() = 8FRL5615RO8SCATTTECE8AFO136TESL1
		groupMsg.getGroup().getDescription() =
		groupMsg.getMessageContent().getMessageId() = 28341:-1706589965:1658853379:3425460643:0
		groupMsg.getMessageContent().getPlainText() = /lr 1111
		groupMsg.getMessageContent().getMessages() = Messages([Text(/lr 1111)])
		 */



        /*String text = groupMsg.getText().trim();
        boolean auth=myProduce.getAuth(groupMsg, "pic");
        int num = 1;
        Matcher m= number.matcher(text);
        if(m.find()) {
            num = Integer.parseInt(m.group().trim());
        }
        if(!groupMsg.getAccountInfo().getAccountCode().equals("1154459434")){
            num=Math.min(num,5);
        }

        LocalPicMethod1(groupMsg, sender, text, auth, num);*/
	}
}