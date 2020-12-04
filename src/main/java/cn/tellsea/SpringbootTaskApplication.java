package cn.tellsea;



import cn.tellsea.component.FirstClass;

import cn.tellsea.quartz.LuaJob;
import cn.tellsea.quartz.QuartzJob;
import cn.tellsea.quartz.QuartzManager;


import cn.tellsea.service.HelloService;
import cn.tellsea.service.RedisService;
import cn.tellsea.task.UpdateDataJob;
import cn.tellsea.utils.anaUtil;
import cn.tellsea.utils.jdbcUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.sql.SQLException;
import java.util.*;


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
    @Autowired
    private jdbcUtil jdbcutil;
    @Autowired
    private HelloService helloService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private FirstClass firstClass;
    @Autowired
    private  anaUtil anautil;
    @Autowired
    private UpdateDataJob updateDataJob;

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


        /**********************同步mysql****************/
        try {
            jdbcutil.getConnection();
        }catch(Exception e)
        {
            log.info("mysql connect error!");
        }

        if (Objects.nonNull(jdbcutil.connection))
        {

            log.info(jdbcutil.findSimpleResult("select * from prtu where rtuno=1",null).toString());
            jdbcutil.releaseConn();
        }else
        {
            log.info("mysql 无法连接，使用本地配置信息。");
        }

        /**********************读取本地jsonobject************/
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
                log.error("dev_list 初始化异常   "+e.toString()+"   "+helloService.selectAllDev().toString().replace("[","{").replace("]","}"));
            }

        }else
            log.error("helloService error");

        //log.info(redisService.get("ai_23"));


        /***********************刷新实时数据******************/
        firstClass.redis_executor.execute(updateDataJob);


        /**********************开启定时任务*******************/
        String sql = "select * from timetask where type=1";
        List<Map<String, Object>> tasktype1 = new ArrayList<>();
        try {
            tasktype1 = jdbcutil.findModeResult(sql,null);
            for (Map<String, Object> tmap : tasktype1) {


                switch (((Integer)tmap.get("type")).intValue()) {
                    case 1:
                        QuartzManager.addJob("qjob" + tmap.get("id"), QuartzJob.class, tmap.get("cronstr").toString());
                        log.warn("定时ao/do任务:" + tmap.get("cronstr").toString() + tmap.toString());
                        break;
                    case 2:
                       HashMap fmap = new HashMap<>();
                        //logger.warn("存储5分钟历史数据任务:" + tmap.get("cronstr").toString());
                        fmap.put("luaname", tmap.get("luaname").toString());
                        log.warn("定时脚本任务:" + tmap.get("cronstr").toString() + fmap.toString());
                        QuartzManager.addJob("ljob" + tmap.get("id").toString(), LuaJob.class, tmap.get("cronstr").toString(), fmap);
                        break;



                }
            }
        } catch (Exception e) {
            log.error("生成定时任务出错了" + e.toString());
        }

        log.info("应用初始化完成");
    }
}
