package simbot.yzg.bot.commonapi.entity;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MessageDivision implements Serializable {
	private static final long serialVersionUID = 4467262096505909780L;
	private List<MyWordFrequency> division;
	public transient HashMap<String, Integer> temp;

	public MessageDivision() {
		division = new ArrayList<>();
		temp = new HashMap<>();
	}

	public void templateChange() {
		temp.forEach((k, v) -> {
			if (StringUtils.hasText(k)&&k.length()>1) {
				division.add(new MyWordFrequency(k, v));
			}
		});
		try {
			division = division.stream()
					.sorted((x, y) -> y.getTimes() - x.getTimes())
					.limit(200)
					.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("division = " + division);
	}

	public void clear() {
		division.clear();
		temp.clear();
	}

	public void mergeDivision(List<MyWordFrequency> other) {
		if (other == null) return;
		for (MyWordFrequency f : other) {
			temp.put(f.getWord(), temp.getOrDefault(f.getWord(), 0) + f.getTimes());
		}
	}

}
