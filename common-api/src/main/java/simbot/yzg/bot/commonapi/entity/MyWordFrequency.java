package simbot.yzg.bot.commonapi.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MyWordFrequency implements Serializable {
	private static final long serialVersionUID = 4467262065315909780L;

	public MyWordFrequency(){}
	public MyWordFrequency(String word, int times){
		this.word=word;
		this.times=times;
	}

	private int times;
	private String word;
}
