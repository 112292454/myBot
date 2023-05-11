package simbot.yzg.bot.commonapi.service;

public interface MessagePersistenceService {
	void putMessage(String code, String s);

	void persistence();
}
