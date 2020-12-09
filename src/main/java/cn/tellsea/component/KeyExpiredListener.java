package cn.tellsea.component;

/**
 * @author xuxu
 * @create 2020-11-11 23:08
 */
import cn.tellsea.utils.anaUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import org.springframework.data.redis.listener.KeyspaceEventMessageListener;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;

@Slf4j
public class KeyExpiredListener  implements MessageListener {

    /**
     * 客户端监听订阅的topic，当有消息的时候，会触发该方法;
     * 并不能得到value, 只能得到key。
     * 姑且理解为: redis服务在key失效时(或失效后)通知到java服务某个key失效了, 那么在java中不可能得到这个redis-key对应的redis-value。
     *      * 解决方案:
     *  创建copy/shadow key, 例如 set vkey "vergilyn"; 对应copykey: set copykey:vkey "" ex 10;
     *  真正的key是"vkey"(业务中使用), 失效触发key是"copykey:vkey"(其value为空字符为了减少内存空间消耗)。
     *  当"copykey:vkey"触发失效时, 从"vkey"得到失效时的值, 并在逻辑处理完后"del vkey"
     *
     * 缺陷:
     *  1: 存在多余的key; (copykey/shadowkey)
     *  2: 不严谨, 假设copykey在 12:00:00失效, 通知在12:10:00收到, 这间隔的10min内程序修改了key, 得到的并不是 失效时的value.
     *  (第1点影响不大; 第2点貌似redis本身的Pub/Sub就不是严谨的, 失效后还存在value的修改, 应该在设计/逻辑上杜绝)
     *  当"copykey:vkey"触发失效时, 从"vkey"得到失效时的值, 并在逻辑处理完后"del vkey"
     *
     */


    private FirstClass firstClass=null;

    private  anaUtil anautil=null;
    public KeyExpiredListener() throws ClassNotFoundException {

        anautil = (anaUtil) SpringUtil.getBean(Class.forName("cn.tellsea.utils.anaUtil"));
        firstClass = (FirstClass) SpringUtil.getBean(Class.forName("cn.tellsea.component.FirstClass"));
    }
    @Override
    public void onMessage(Message message, byte[] bytes) {
        byte[] body = message.getBody();// 建议使用: valueSerializer
        byte[] channel = message.getChannel();
        //log.info(String.format("channel: %s, body: %s, bytes: %s",new String(channel), new String(body), new String(bytes)));
        Runnable noArguments = () -> {
            try {



                // log.info(new String(body));

                 //log.info(anaUtil.objana_v.toJSONString());
                {
                    try {
                        if (new String(channel).contains("expired") ) {
                            //logger.warn(message);
                            anautil.handleExpired(new String(body));
                        }
                        else {


                            //log.warn(new String(body));
                            anautil.handleMessage(new String(body));

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.info(e.toString());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        };
        firstClass.redis_executor.execute( noArguments);
    }
}
