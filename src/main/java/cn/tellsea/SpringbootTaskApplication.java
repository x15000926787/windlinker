package cn.tellsea;



import cn.tellsea.Model.DataList;
import cn.tellsea.Model.DevList;
import cn.tellsea.Model.TimeTask;
import cn.tellsea.component.FirstClass;

import cn.tellsea.quartz.*;


import cn.tellsea.service.HelloService;
import cn.tellsea.service.RedisService;
import cn.tellsea.task.UpdateDataJob;
import cn.tellsea.utils.JedisUtil;
import cn.tellsea.utils.anaUtil;
import cn.tellsea.utils.jdbcUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
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
    /*@Autowired
    private RedisService redisService;*/
    /*@Autowired
    private FirstClass firstClass;*/
    @Autowired
    private  anaUtil anautil;
   /* @Autowired
    private UpdateDataJob updateDataJob;*/
    public DataList dat;
    public DevList dev;
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
    private void init() throws SQLException, ParseException, InterruptedException {


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

            anautil.loadAna_v();
            anautil.loadDevList();
            anautil.loadParaList();
           /* try {

                anautil.objana_v = JSONObject.parseObject(helloService.selectAllData().toString().replace("[","{").replace("]","}"));

                log.warn(anautil.objana_v.toString());
            }catch (Exception e)
            {
                log.error("objana_v 初始化异常   "+e.toString()+"   "+helloService.selectAllDev().toString().replace("[","{").replace("]","}"));
            }*/
            /*try {
                anautil.dev_list = JSONObject.parseObject(helloService.selectAllDev().toString().replace("[","{").replace("]","}"));

                log.warn(anautil.dev_list.toString());
            }catch (Exception e)
            {
                log.error("dev_list 初始化异常   "+e.toString()+"   "+helloService.selectAllDev().toString().replace("[","{").replace("]","}"));
            }*/

        }else
            log.error("helloService error");

        //log.info(redisService.get("ai_23"));


        /***********************刷新实时数据******************/
        //firstClass.redis_executor.execute(updateDataJob);


        String luaStr = null,tkey = null;
        Jedis jedis = null;
        Map<String, Response<String>> responses = null;
        Pipeline p = null;
        log.info("refresh data...");

        responses = new HashMap<String,Response<String>>(anautil.objana_v.keySet().size());



        try {
            jedis= JedisUtil.getInstance().getJedis();

            p = jedis.pipelined();
            for(String key1 : anautil.objana_v.keySet()) {
                //log.info(((JSONObject)anautil.objana_v.get(key1)).get("kkey").toString());
                responses.put(key1, p.get(((JSONObject)anautil.objana_v.get(key1)).get("kkey").toString()+"_.value"));

            }

            try {
                if (p!=null)  p.sync();
            }
            catch (JedisConnectionException e) {
                log.error("lua err: " +  e.toString() );
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            for(String k : responses.keySet()) {


                    tkey = ((JSONObject)anautil.objana_v.get(k)).get("kkey").toString();
                if (jedis.exists(tkey+"_.value")){
                    try {
                        luaStr = responses.get(k).get().toString().trim().replace(".000","");

                       // log.info(k);


                        log.warn("read redis: "+(tkey) +"  " + luaStr );
                        dat = JSONObject.toJavaObject((JSONObject)anautil.objana_v.getJSONObject(k),DataList.class);
                        dev = JSONObject.toJavaObject((JSONObject)anautil.dev_list.getJSONObject(String.valueOf(dat.getPid())),DevList.class);

                        switch(dat.getType()){
                            case 0 :
                                if (Integer.parseInt(luaStr)==dev.getReton())
                                {
                                    dev.setRun(1);
                                    anautil.objana_v.getJSONObject(k).put("run","1");
                                } else{
                                    dev.setRun(0);
                                    anautil.objana_v.getJSONObject(k).put("run","0");

                                }


                                helloService.updateDevRun(dev);
                                break; //可选
                            case 1 :
                                if (Integer.parseInt(luaStr)==dev.getErron()){
                                    dev.setError(1);
                                    anautil.objana_v.getJSONObject(k).put("error","1");
                                }

                                else{
                                    dev.setError(0);
                                    anautil.objana_v.getJSONObject(k).put("error","0");
                                }

                                helloService.updateDevErr(dev);
                                break; //可选
                            case 2 :
                                if (Integer.parseInt(luaStr)==dev.getStatuson()){
                                    dev.setStatus(0);
                                    anautil.objana_v.getJSONObject(k).put("status","0");
                                }

                                else{
                                    dev.setStatus(1);
                                    anautil.objana_v.getJSONObject(k).put("status","1");
                                }
                                log.info(dev.toString());
                                helloService.updateDevStatus(dev);
                                break; //可选
                            case 8 :
                                dev.setRuntime(Float.parseFloat(luaStr));
                                anautil.objana_v.getJSONObject(k).put("runtime",luaStr);
                                helloService.updateDevTime(dev);
                                break; //可选

                            default : //可选
                                break;
                                
                        }

                        if ((anautil.coldoutwd.matches(tkey))){
                            anaUtil.data[0] = Float.parseFloat(luaStr);
                        }
                        if ((anautil.coldinwd.matches(tkey))){
                            anaUtil.data[1] = Float.parseFloat(luaStr);
                        }
                        if ((anautil.coldinyl.matches(tkey))){
                            anaUtil.data[2] = Float.parseFloat(luaStr);
                        }
                        if ((anautil.coldoutyl.matches(tkey))){
                            anaUtil.data[3] = Float.parseFloat(luaStr);
                        }
                        if ((anautil.coolinwd.matches(tkey))){
                            anaUtil.data[4] = Float.parseFloat(luaStr);
                        }
                        if ((anautil.cooloutwd.matches(tkey))){
                            anaUtil.data[5] = Float.parseFloat(luaStr);
                        }
                        if ((anautil.coolinyl.matches(tkey))){
                            anaUtil.data[6] = Float.parseFloat(luaStr);
                        }
                        if ((anautil.coldoutyl.matches(tkey))){
                            anaUtil.data[7] = Float.parseFloat(luaStr);
                        }

                        //anautil.handleMessage(tkey+"_.value");

                    } catch (Exception e) {
                        log.error(k+","+tkey+","+luaStr+":"+e.toString());
                    }
                }

            }
            for (int i=0;i<4;i++)
            {
                anaUtil.data_now[i] = anaUtil.data[i*2+1]-anaUtil.data[i*2];
            }
            anautil.calcpl();
            try {
                p.close();
            }catch (IOException ee){}



            JedisUtil.getInstance().returnJedis(jedis);

            responses.clear();


            luaStr = null;

            responses = null;
            p = null;//jedis.pipelined();


        }

        catch (JedisConnectionException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }










        QuartzManager.addJob("keepRedisAlive" , keepaliveJob.class, "*/30 * * * * ? *");

        /**********************开启定时任务*******************/

        List<TimeTask> tasktype1 =null; //helloService.selectAllTimeTask();

        try {
            tasktype1 = helloService.selectAllTimeTask();

            for (TimeTask tmap : tasktype1) {


                Map<String, Integer> maps=new HashMap<>();
                switch (((Integer)tmap.getType()).intValue()) {
                    case 1:
                        maps.put("vv",(tmap.getId()));
                        QuartzManager.addJob("qjob" + tmap.getId(), QuartzJob.class, tmap.getCronstr(),maps);
                        log.warn("定时开关机任务 : {} {}", tmap.getCronstr() , tmap.getName());
                        break;
                    case 2:
                        maps.put("vv",(tmap.getId()));
                        QuartzManager.addJob("djob" + tmap.getId(), QuartzJob.class, tmap.getCronstr(),maps);
                        log.warn("定时开关机任务 : {} {}", tmap.getCronstr() , tmap.getName());
                        break;
                    case 3:
                       HashMap fmap = new HashMap<>();

                        fmap.put("luaname", tmap.getLuaname());
                        log.warn("定时脚本任务:" + tmap.getCronstr() + fmap.toString());
                        QuartzManager.addJob("ljob" + tmap.getId(), LuaJob.class, tmap.getCronstr(), fmap);
                        break;
                    case 4:
                        QuartzManager.addJob("ljob" + tmap.getId(), calcJob.class, tmap.getCronstr(), tmap);
                        log.warn("定时调频任务:" + tmap.getCronstr() + tmap.toString());
                        break;



                }
            }
        } catch (Exception e) {
            log.error("生成定时任务出错了" + e.toString());
            e.printStackTrace();
        }
        log.info("应用初始化完成");
        //JedisUtil.getInstance().getJedis().set("dd","1","NX","EX",10);
        anautil.init();

    }
}
