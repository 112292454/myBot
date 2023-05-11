package simbot.yzg.bot.aipainting;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo(scanBasePackages = "simbot.yzg.aipainting")
public class AiPaintingApplication {


	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(AiPaintingApplication.class, args);


	}

}
