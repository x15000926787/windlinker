package cn.tellsea.component;

import cn.tellsea.service.HelloService;
import cn.tellsea.service.RedisService;
import cn.tellsea.utils.anaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Objects;
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
@WebListener
public final class FirstClass implements ServletContextListener {
    public static ThreadPoolExecutor redis_executor = null;

    @Autowired
    public static HelloService helloService;
    @Autowired
    public static RedisService tjedis;
    public FirstClass() {
        //log.info("firstclass");
        redis_executor = new ThreadPoolExecutor(20, 40, 60000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardOldestPolicy());
        redis_executor.allowCoreThreadTimeOut(true);

    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        redis_executor.shutdown();
       /* QuartzManager.shutdownJobs();
        try {
            Thread.sleep(30000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        redis_executor.shutdown();
        mqtt_executor.shutdown();
        bb_executor.shutdown();
        savehisdata_executor.shutdown();
        this.T4.stoplisten();
        this.T4.killThreadByName("lisner_data");
        logger.warn("应用程序关闭!");*/
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        log.warn("初始化系统服务...");
        //log.warn(tjedis.get("ai_23"));
        /*projectId = PropertyUtil.getProperty("project_id");
        this.springContext = WebApplicationContextUtils.getWebApplicationContext(arg0.getServletContext());
        if (this.springContext != null) {
            jdbcTemplate = (JdbcTemplate)this.springContext.getBean("jdbcTemplate");
        } else {
            logger.warn("获取应用程序上下文失败!");
            return;
        }
        String jsonstr = null;
        JSONObject job = new JSONObject();
        AnaUtil.loadAna_v();
        AnaUtil.loadDnArray();
        AnaUtil.loaddev();
        AnaUtil.load_condition();
        AnaUtil.load_msguser();
        AnaUtil.load_fullname();
        AnaUtil.load_onlinewarn();
        AnaUtil.saveno_kkey();
        int i = 0;
        logger.warn("实时数据监听开启");
        this.T4 = new ThreadSubscriber("lisner_data");
        this.T4.start();
        ApplicationContext context = (ApplicationContext) arg0.getServletContext()
                .getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        //logger.info(mqttService);
        String sql = "select * from timetask ";
        List<Map<String, Object>> tasktype1 = new ArrayList<>();
        try {
            tasktype1 = getJdbcTemplate().queryForList(sql);
            for (Map<String, Object> tmap : tasktype1) {
                Map<String, String> maps;
                Map<String, Object> fmap, xmap;
                i = ((Integer)tmap.get("id")).intValue();
                switch (((Integer)tmap.get("type")).intValue()) {
                    case 1:
                        mqttService = (MqttService) context.getBean("mqttCaseService");
                        maps = new HashMap<>();
                        maps.put("vv", "" + i);
                        QuartzManager.addJob("qjob" + i, QuartzJob.class, tmap.get("cronstr").toString(), maps,mqttService);
                        logger.warn("定时ao/do任务:" + tmap.get("cronstr").toString() + maps.toString());
                        break;
                    case 2:
                        fmap = new HashMap<>();
                        //logger.warn("存储5分钟历史数据任务:" + tmap.get("cronstr").toString());
                        fmap.put("luaname", tmap.get("luaname").toString());
                        logger.warn("定时脚本任务:" + tmap.get("cronstr").toString() + fmap.toString());
                        QuartzManager.addJob("ljob" + i, LuaJob.class, tmap.get("cronstr").toString(), fmap);
                        break;
                    case 3:
                        if (!PropertyUtil.getProperty("project_id").matches("12")) {
                            QuartzManager.addJob("saveHistyc", saveHistyc.class, tmap.get("cronstr").toString());
                            logger.warn("存储5分钟历史数据任务:" + tmap.get("cronstr").toString());
                        }

                        break;

                    case 4:
                        QuartzManager.addJob("UpdateDataJob", UpdateDataJob.class, tmap.get("cronstr").toString());
                        logger.warn("请求实时数据任务:" + tmap.get("cronstr").toString());
                        break;
                    case 5:
                        QuartzManager.addJob("checkonlinetime", checkOnlineTime.class, tmap.get("cronstr").toString());
                        logger.warn("check在线时长任务:" + tmap.get("cronstr").toString());
                        break;
                    case 6:
                        QuartzManager.addJob("reportjob", ReportJob.class, tmap.get("cronstr").toString());
                        logger.warn("报表任务:" + tmap.get("cronstr").toString());
                        break;
                    case 8:
                        QuartzManager.addJob("reportyyjob", ReportdnJob.class, tmap.get("cronstr").toString());
                        logger.warn("yinyong报表任务:" + tmap.get("cronstr").toString());
                        break;

                    case 10:
                        QuartzManager.addJob("reportdnjob", ReportDycJob.class, tmap.get("cronstr").toString());
                        logger.warn("大悦城报表任务:" + tmap.get("cronstr").toString());
                        break;
                    case 7:
                        QuartzManager.addJob("maxmindayjob", saveMaxMin.class, tmap.get("cronstr").toString());
                        logger.warn("daymaxmin任务:" + tmap.get("cronstr").toString());
                        break;
                    case 11:
                        // xmap = new HashMap<>();
                        // xmap.put("txtname", tmap.get("luaname").toString());
                        QuartzManager.addJob("zjdyjob", zjdyJob.class, tmap.get("cronstr").toString());
                        logger.warn("电量总加任务:" + tmap.get("cronstr").toString());
                        break;
                    case 12:
                        if (PropertyUtil.getProperty("project_id").matches("1")) {
                            QuartzManager.addJob("saveRedis", saveRedis.class, tmap.get("cronstr").toString());
                            logger.warn("存储5分钟redis数据任务:" + tmap.get("cronstr").toString());
                        }
                        break;
                    case 13:

                        QuartzManager.addJob("createDn", createDn.class, tmap.get("cronstr").toString());
                        logger.warn("电量合成任务:" + tmap.get("cronstr").toString());
                        break;
                    case 14:
                        mqttService = (MqttService) context.getBean("mqttCaseService");
                        QuartzManager.addJob("mqttzongzhao", ZongZhaoJob.class, tmap.get("cronstr").toString(),new HashMap<>(),mqttService);
                        logger.warn("mqtt总召任务:" + tmap.get("cronstr").toString());
                        break;
                    case 15:

                        QuartzManager.addJob("saveRedisAKu", saveredisAku.class, tmap.get("cronstr").toString());
                        logger.warn("aku存储5分钟redis数据任务:" + tmap.get("cronstr").toString());

                        break;
                    case 16:

                        QuartzManager.addJob("saveRedisAKuDoor", ScheduledTask.class, tmap.get("cronstr").toString());
                        logger.warn("aku存储Door数据任务:" + tmap.get("cronstr").toString());

                        break;


                }
                i++;
            }
        } catch (Exception e) {
            logger.error("生成定时任务出错了" + e.toString());
        }
        tasktype1 = null;*/
    }
}
