package cn.tellsea;


import cn.tellsea.Model.DevList;
import cn.tellsea.component.FirstClass;
import cn.tellsea.service.HelloService;
import cn.tellsea.service.RedisService;
import cn.tellsea.utils.anaUtil;
import cn.tellsea.utils.jdbcUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/*@SpringBootApplication
public class SpringbootTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootTaskApplication.class, args);
    }

}*/




@Slf4j
@SpringBootApplication

public class SpringbootTaskApplication implements CommandLineRunner {
   // protected static final Logger LOGGER = LoggerFactory.getLogger(SpringbootTaskApplication.class);

    /*@Autowired
    NettyConfig nettyConfig;*/
    /*@Autowired
    private jdbcUtil jdbcutil;*/
    @Autowired
    private HelloService helloService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private FirstClass firstClass;
    @Autowired
    private  anaUtil anautil;
    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringbootTaskApplication.class).run(args);
              //  .web(WebApplicationType.NONE)

        //SpringApplication.run(Aws310Application.class, args);
    }

    /**
     * spring boot启动后，会进入该方法
     */
    @Override
    public void run(String... args) throws Exception {

        init();
    }

    /**
     * 初始化信息
     */
    @SuppressWarnings("unused")
    private void init() throws SQLException {

       // Consts.SERVER_PORT = nettyConfig.getPort();
       // Consts.MODULE_TYPE = nettyConfig.getModuleType();
        if (Objects.nonNull(helloService))
        {


            try {
                anautil.objana_v = JSONObject.parseObject(helloService.selectAllData().toString().replace("[","{").replace("]","}"));

                log.warn(anautil.objana_v.toString());
            }catch (Exception e)
            {
                log.error("objana_v 初始化异常   "+e.toString()+"   "+helloService.selectAllDev().toString().replace("[","{").replace("]","}"));
            }
            try {
                anautil.dev_list = JSONObject.parseObject(helloService.selectAllDev().toString().replace("[","{").replace("]","}"));

                log.warn(anautil.dev_list.toString());
            }catch (Exception e)
            {
                log.error("objana_v 初始化异常   "+e.toString()+"   "+helloService.selectAllDev().toString().replace("[","{").replace("]","}"));
            }

        }else
            log.error("helloService error");

        //log.info(redisService.get("ai_23"));
        try {
            jdbcUtil.connection=jdbcUtil.getConnection();
        }catch(Exception e)
        {
            log.info("mysql connect error!");
        }

        if (Objects.nonNull(jdbcUtil.connection))
        {
            log.info(jdbcUtil.findSimpleResult("select * from prtu where rtuno=1",null).toString());
        }

        log.info("应用初始化完成");
    }
}
