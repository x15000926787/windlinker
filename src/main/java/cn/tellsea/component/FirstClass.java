package cn.tellsea.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxu
 * @create 2020-11-24 13:56
 */
@Slf4j
@Component
public class FirstClass {
    public static ThreadPoolExecutor redis_executor = null;


    public FirstClass() {
        //log.info("firstclass");
        redis_executor = new ThreadPoolExecutor(20, 40, 60000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardOldestPolicy());
        redis_executor.allowCoreThreadTimeOut(true);

    }
}
