package simbot.yzg.bot.commonapi.service;

import love.forte.simbot.message.Image;
import love.forte.simbot.message.Message;
import love.forte.simbot.message.Messages;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map;

public interface PicSourcesService {
	ArrayList<Messages> getPicSource(Image image) throws MalformedURLException;

	Messages build(Map<String, Message.Element<?>> map);
}
