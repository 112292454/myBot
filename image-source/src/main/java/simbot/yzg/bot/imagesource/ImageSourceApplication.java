package simbot.yzg.bot.imagesource;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo(scanBasePackages = "simbot.yzg.bot.aipainting")
public class ImageSourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageSourceApplication.class, args);
	}

}
