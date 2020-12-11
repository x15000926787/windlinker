package cn.tellsea.config;

/**
 * @author xuxu
 * @create 2020-11-11 23:09
 */
import cn.tellsea.component.KeyExpiredListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;



@Configuration
public class RedisConfiguration {

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) throws ClassNotFoundException {
        System.out.println("redisconfig");
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(new KeyExpiredListener(), new PatternTopic("__keyevent@0__:*"));
        return container;
    }
}


