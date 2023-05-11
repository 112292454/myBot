package simbot.yzg.bot.wordcloud.serviceImpl;


import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.image.AngleGenerator;
import com.kennycason.kumo.palette.LinearGradientColorPalette;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import simbot.yzg.bot.commonapi.entity.MessageDivision;
import simbot.yzg.bot.commonapi.entity.MyWordFrequency;
import simbot.yzg.bot.commonapi.service.WordCloudService;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@DubboService(group = "simbot", version = "1.0.0",interfaceClass = WordCloudService.class)
public class WordCloudServiceImpl implements WordCloudService {
	@Resource
	RedisTemplate<String, MessageDivision> redisTemplate;
	@Override
	public String createWordCountPic(String code){
		//FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
		//frequencyAnalyzer.setWordFrequenciesToReturn(600);
		//frequencyAnalyzer.setMinWordLength(2);
		//frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());
		// 可以直接从文件中读取
		//List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(getInputStream("D:\\citydo-one\\技术\\Java_Note-master\\python\\tp\\Trump.txt"));
		List<WordFrequency> wordFrequencies = new ArrayList<>();


		MessageDivision division = redisTemplate.opsForValue().
				get(code+"|" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		List<MyWordFrequency> frequencies= division.getDivision();

		for (MyWordFrequency frequency : frequencies) {
			wordFrequencies.add(new WordFrequency(frequency.getWord(),frequency.getTimes()));
		}
		wordFrequencies=wordFrequencies.stream().sorted((x, y) -> y.getFrequency() - x.getFrequency()).limit(50).collect(Collectors.toList());


		//加入分词并随机生成权重，每次生成得图片都不一样
		//test.stream().forEach(e-> wordFrequencies.add(new WordFrequency(e,new Random().nextInt(test.size()))));
		//此处不设置会出现中文乱码
		java.awt.Font font = new java.awt.Font("楷体", 0, 20);
		//设置图片分辨率
		Dimension dimension = new Dimension(450, 450);
		//此处的设置采用内置常量即可，生成词云对象
		WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
		//设置边界及字体
		wordCloud.setPadding(3);
		//因为我这边是生成一个圆形,这边设置圆的半径
		wordCloud.setBackground(new RectangleBackground(new Dimension(450,450)));
		wordCloud.setFontScalar(new SqrtFontScalar(12, 60));
		//设置词云显示的三种颜色，越靠前设置表示词频越高的词语的颜色
		wordCloud.setColorPalette(new LinearGradientColorPalette(Color.RED, Color.YELLOW, Color.CYAN, 5, 5));
		wordCloud.setKumoFont(new KumoFont(font));
		wordCloud.setBackgroundColor(new Color(0, 0, 0));
		AngleGenerator angleGenerator=new AngleGenerator(new double[]{1.5707963267948966,0.7853981633974483,0,0,0,0,0});
		//AngleGenerator angleGenerator=new AngleGenerator(0,90,9);
		wordCloud.setAngleGenerator(angleGenerator);
		//因为我这边是生成一个圆形,这边设置圆的半径
		//wordCloud.setBackground(new CircleBackground(255));
		wordCloud.build(wordFrequencies);
		//生成词云图路径
		wordCloud.writeToFile("D:\\botPic\\词云.png");
		return "D:\\botPic\\词云.png";
	}

}
