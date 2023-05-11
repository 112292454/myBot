package simbot.yzg.bot.botframe;

import love.forte.simboot.spring.autoconfigure.EnableSimbot;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import xyz.cssxsh.mirai.tool.FixProtocolVersion;
//import xyz.cssxsh.mirai.tool.FixProtocolVersion;


/**
 * simbot 启动类。
 * <p>
 * 此处的注解配置了两个配置文件：
 * <ul>
 *     <li>simbot.yml</li>
 *     <li>simbot-dev.yml</li>
 * </ul>
 * 其中，{@code simbot-dev.yml} 是一个测试环境的配置文件，只有当启动参数中存在 {@code --Sdev} 的时候才会被使用。
 * 如果你不需要一些特殊的配置文件，那么可以直接使用 {@code @SimbotApplication}.
 * <p>
 * 默认情况下，默认的配置文件名称为 {@code simbot.yml} 或 {@code simbot.properties}
 *
 * @author ForteScarlet
 */
//@SimbotApplication({
//        @SimbotResource(value = "simbot.yml", orIgnore = true),
//        @SimbotResource(value = "simbot-dev.yml", orIgnore = true, command = "dev"),
//})
//@SimbotApplication
@SpringBootApplication
//@MapperScan(basePackages = "love.simbot.example")
@EnableSimbot
@EnableScheduling
@EnableDiscoveryClient
@EnableDubbo
public class MyBot {

    public static void main(String[] args) {
        FixProtocolVersion.update();
        SpringApplication.run(MyBot.class,args);
//        SimbotApp.run(SimbotExampleApplication.class, args);

    }
}
