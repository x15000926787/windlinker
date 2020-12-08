package cn.tellsea.utils;

import cn.tellsea.Model.*;
import cn.tellsea.service.HelloService;
import cn.tellsea.service.RedisService;
import cn.tellsea.service.impl.RedisServiceImpl;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.rmi.CORBA.Util;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

@Slf4j
@Component
public  class anaUtil {

    @Autowired
    public  HelloService helloService;

    //public  RedisService tjedis;//=new RedisServiceImpl();

    public  JSONObject objana_v = new JSONObject();

    public  JSONObject dev_list = new JSONObject();

    public  JSONObject key_id = new JSONObject();


    public  ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    public  ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");
    public  static Stack<Integer> st = new Stack<Integer>();
    public  static  int process= -1;
    public  static  int repeat= 1;
    public static ActionDetial taction;
    public static DevList devList;
    public  static  int zhjno= -1;
    public  static  int  step= -1;
    public  static  int lqbpqno= -1;
    public  static  String retkey = "empty";

    public  boolean isDoubleOrFloat(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        String regx = "[+-]*\\d+\\.?\\d*[Ee]*[+-]*\\d+";
        Pattern pattern = Pattern.compile(regx);
        boolean isNumber = pattern.matcher(str).matches();
        if (isNumber) {
            return isNumber;
        }
        regx = "^[-\\+]?[.\\d]*$";
        pattern = Pattern.compile(regx);
        return pattern.matcher(str).matches();
    }
    /*private List<prtuana> mapToObject(List<Map<String,Object>> logs){
        List<prtuana> Logs=new ArrayList<prtuana>();
        Map<String,Object> map=null;
        String userTypeName="";
        Byte userType=0;
        if(logs.size()>0) {
            for (int i = 0; i < logs.size(); i++) {
                prtuana ana = new prtuana();
                map = logs.get(i);
               *//* if (null != map.get("user_id")) {
                    ana.setUserId(map.get("user_id").toString());
                }


                if (null != map.get("user_name")) {
                    ana.setUserName(map.get("user_name").toString());
                }*//*

                Logs.add(ana);
            }

        }
        return Logs;
    }*/
/**
 * 执行Action
 */
    public  void  dostep() throws InterruptedException {

         step = taction.getStep();
        //ActionDetial actionDetial=null;
        //actionDetial = helloService.selectnextprocess(process,step);
        /*查找目标设备id*/
        int ttp = taction.getTargettype();
        String tkey = null,rkey = null;
       // log.info(""+helloService.selectdev(1).getId());
        try {
            if (ttp == 4 || ttp == 5)
                devList = helloService.selectDcfDev(helloService.selectdev(1).getId(),ttp);
            else {
                devList = helloService.selectdev(ttp);

            }
        }catch (Exception e)
        {
            devList = null;
        }

       if (Objects.nonNull(devList)) {
           //log.info(devList.toString());

        zhjno = devList.getId();

        log.info("step {} info : 目标设备 ID：{}  名称：{}",step,zhjno,devList.getDevname());
        /*查找目标设备控制键*/

         tkey = helloService.selectcontrolkey(zhjno,3).getKkey();
        log.info("step {} info : 目标控制键 {}",step,tkey);
        /*查找目标设备控制反馈键*/
        if (ttp == 4 || ttp == 5)
        {
            if (process==1) rkey = helloService.selectcontrolkey(zhjno, 4).getKkey();
            else rkey = helloService.selectcontrolkey(zhjno, 5).getKkey();
        }
        else {
            rkey = helloService.selectcontrolkey(zhjno, 0).getKkey();
        }
        log.info("step {} info : 目标控制反馈键 {}",step,rkey);

        if (devList.fresh!=0)
        {
            log.info("step {} info : 发送复位命令 {}",step,devList.getFresh());
            setRedisVal(tkey,String.valueOf(devList.getFresh()));
            sleep(3000);
        }else
        {
            log.info("step {} info : 此设备无复位命令 ",step);
        }


        retkey = rkey;
        setRedisVal(tkey,String.valueOf(process==1?devList.getPoweron():devList.getPoweroff()));
        setRedisProVal("timeout",String.valueOf(repeat),taction.getWait());
        log.info("step {} info : 发送控制命令 {} {}",step,tkey,process==1?devList.getPoweron():devList.getPoweroff());
       }else
       {
           //告警，无可以设备
           log.info("step {} info : 无此类型可用设备, 设备类型号 {} ,进程退出.",step,ttp);
           if (process==1 && !st.empty())
           {
               log.info("开机进程异常退出，进入撤销进程...");
               process = 0;
               step =0;
               goback();
           }
       }
    }


    /**
     * 撤销,只考虑开机进程的撤销
     */
    public  void  goback() throws InterruptedException {


        if (!st.empty()) {
            zhjno = st.pop();
            int ttp = 0;
            String tkey = null,rkey = null;
            devList = helloService.selectdevbyid(zhjno);
            taction = helloService.selectAction(0,devList.getType());



            if (Objects.nonNull(devList)) {
                log.info(devList.toString());

                zhjno = devList.getId();

                log.info("goback info : 目标设备 ID：{}  名称：{}", zhjno, devList.getDevname());
                /*查找目标设备控制键*/

                tkey = helloService.selectcontrolkey(zhjno, 3).getKkey();
                log.info("goback info : 目标控制键 {}", tkey);
                /*查找目标设备控制反馈键*/
                if (ttp == 4 || ttp == 5) {
                    if (process == 1) rkey = helloService.selectcontrolkey(zhjno, 4).getKkey();
                    else rkey = helloService.selectcontrolkey(zhjno, 5).getKkey();
                } else {
                    rkey = helloService.selectcontrolkey(zhjno, 0).getKkey();
                }
                log.info("goback info : 目标控制反馈键 {}", rkey);

                if (devList.fresh != 0) {
                    log.info("goback info : 发送复位命令 {}",  devList.getFresh());
                    setRedisVal(tkey, String.valueOf(devList.getFresh()));
                    sleep(3000);
                } else {
                    log.info("goback info : 此设备无复位命令 ");
                }


                retkey = rkey;
                setRedisVal(tkey, String.valueOf(process == 1 ? devList.getPoweron() : devList.getPoweroff()));
                setRedisProVal("timeout", String.valueOf(repeat), taction.getWait());
                log.info("goback info : 发送控制命令 {} {}",  tkey, process == 1 ? devList.getPoweron() : devList.getPoweroff());
            } else {
                //告警，无可以设备
                log.info("goback info : 撤销进程异常退出.");

            }
        }
    }

    /**
     * 开启主机/增加主机
     */
    public  void  startZJ() throws InterruptedException {


        log.info("开机进程启动......");
        helloService.resetmyerr();
        process = 1;
        taction = helloService.selectnextprocess(process,1);;


        dostep();

        //log.info("");
    }
    /**
     * 自检
     * @throws ParseException
     * @throws InterruptedException
     */
    public  void init() throws ParseException, InterruptedException {
        int cnt = 0,tid = 0;
        String val = null;
        List<TimeTask_Detial> tk_detial;
        LocalDateTime tdate = LocalDateTime.now().minusDays(31);

        LocalDateTime sdate;
        List<TimeTask> ltask;
        ltask =  helloService.selectofTimeTask(1);
        for (TimeTask t : ltask) {
            sdate = LocalDateUtil.dateToLocalDateTime(getcronstr_prv(t.getCronstr()));


            if (sdate.isAfter(tdate)) {

                tdate = sdate;
                tid = t.getId();
                val = t.getName();
            }
        }
        log.info("最后一条定时开关机任务：{} {}",val,tdate.toString());
        tk_detial = helloService.selectAllTimeTaskDetial(tid);
        val = tk_detial.get(0).getVal();

        cnt = helloService.selectdevruncount().size();
        if (cnt==0)
        {
            log.info("当前没有主机在运行");
            if (val.matches("1"))
            {
                log.info("根据定时任务 {} ,进入开机程序",tk_detial.get(0).getTaskid());
                startZJ();
            }

        }else
        {
            log.info("当前有{}台主机在运行。",cnt);
            if (val.matches("0"))
            {
                log.info("根据定时任务 {} ,进入关机程序",tk_detial.get(0).getTaskid());
            }
        }
    };
    public    void loadAna_v(){

        log.info("开始读取ana内容.......");
        try {
            objana_v = JSONObject.parseObject(helloService.selectAllData().toString().replace("[","{").replace("]","}"));

            log.warn(objana_v.toString());
        }catch (Exception e)
        {
            log.error("objana_v 初始化异常   "+e.toString()+"   "+helloService.selectAllDev().toString().replace("[","{").replace("]","}"));
        }

        for(String str:objana_v.keySet()){
            key_id.put(((JSONObject)objana_v.get(str)).getString("kkey"),str);
        }
        log.info(key_id.toJSONString());

    }
    public     void loadDevList(){

        log.info("开始读取devlist内容.......");
        try {
            dev_list = JSONObject.parseObject(helloService.selectAllDev().toString().replace("[","{").replace("]","}"));

            log.warn(dev_list.toString());
        }catch (Exception e)
        {
            log.error("objana_v 初始化异常   "+e.toString()+"   "+helloService.selectAllDev().toString().replace("[","{").replace("]","}"));
        }


    }
    /*
     * 执行sql
     * getPrepatedResultSet
     */

    public  void add_red(String sql)
    {
         log.info(sql);
        /*try{
            synchronized (FirstClass.jdbcTemplate)
            {
                int t = FirstClass.jdbcTemplate.update(sql);
            }
            // FirstClass.logger.warn(t+"  :  "+sql);
            //dbcon.getClose();
        }catch(Exception e){
            FirstClass.logger.error("出错了"+e.toString()+":"+sql);
        }*/

    }
    public  void handleTime(String key,String vals) {
        String down, up, limit = " ", dbname, rtuno, sn, msgah, mailah, altah, mobs = "", gkey = "";
        // Calendar c;
        String msg = "";
        String s = null;
        //SimpleDateFormat df,df2,df3;
        LocalDateTime rightnow = LocalDateTime.now();
        log.info(rightnow.toString());
        char[] ss = null;
        JSONObject tmap = new JSONObject();

        long rnow = rightnow.toInstant(ZoneOffset.of("+8")).toEpochMilli();

        JSONObject map = new JSONObject();

        // df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        //log.info(key);

        int vv = -2;
        int timevalid = 0;
        //
       //if (Objects.nonNull(helloService)) log.info(dev_list.toString());
        map = (JSONObject) objana_v.get(key_id.getString(key));
        DataList obj = null;//(json,Student.class);
        DevList dev = null;
        //log.info(dev_list.get(1).toString());
        tmap = (JSONObject) dev_list.get(map.get("pid"));
       // log.info(tmap.toJSONString());
        try {
            timevalid = Integer.parseInt(map.get("tvalid").toString());
        } catch (Exception e) {
            timevalid = 0;
        }
        //timevalid=1;
        if (timevalid == 1)  {







                    dbname = "devlist";
           // log.warn(map.get("tstatus").toString());
                if (Integer.parseInt(vals)==1) {

                        if (map.get("tstatus").toString().matches("0")) {
                            log.warn(key + " 开始计时 ");
                            //开始计时
                            // ((HashMap<String, String>) objana_v.get(key)).put("timestat", "1");
                            ((JSONObject) objana_v.get(key)).put("tstatus", "1");
                            ((JSONObject) objana_v.get(key)).put("tcheck", rnow);
                            obj = JSONObject.toJavaObject((JSONObject)objana_v.get(key_id.getString(key)),DataList.class);
                            helloService.updateTime(obj);
                            //add_red("UPDATE datalist SET tstatus=1 ,tcheck='" + rnow + "' where kkey='" + key + "'");
                            // log.warn("UPDATE " + dbname + " SET timestat=1 ,checktime='" + rnow + "' where kkey='" + key + "'");
                        }
                    }
                 else
                    {
                    if (map.get("tstatus").toString().matches("1") )
                     {
                        //停止计时

                        log.warn(key + " 停止计时 ");
                        //LocalDateTime Date2 = LocalDateTime.now();
                        LocalDateTime to2 = new Date((long)map.get("tcheck")).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();//LocalDateTime.ofEpochSecond((long)map.get("tcheck"),0, ZoneOffset.ofHours(8));//LocalDateTime.parse(.toString(), formatter);
                        log.info(to2.toString());
                         Duration duration = Duration.between(to2, rightnow);
                         log.info(duration.toString());
                        //相差的分钟数
                        long minutes = duration.toMinutes();
                        //float hours =  ((rightnow.get - to2) / (1000.00f * 60 * 60));
                        float tot = minutes / 60.00f + Float.parseFloat(tmap.get("runtime").toString());
                        //log.warn(df3.format(Date2)+","+df3.format(toDate2)+","+hours);
                       // ((HashMap<String, String>) objana_v.get(key)).put("timestat", "0");
                        ((JSONObject) dev_list.get(map.get("pid"))).put("runtime", "" + tot);
                         ((JSONObject) objana_v.get(key_id.getString(key))).put("tstatus", "0");
                        ((JSONObject) objana_v.get(key_id.getString(key))).put("tcheck", rnow);

                        //mjedis.set(key + ".mtot", String.valueOf(tot));
                        //mjedis.set(key + ".ytot", String.valueOf(tot2));

                        //add_red("UPDATE datalist SET tstatus=0 ,tcheck='" + rnow + "',runtime=" + tot + " where kkey='" + key + "'");
                         obj = JSONObject.toJavaObject((JSONObject)objana_v.get(key_id.getString(key)),DataList.class);
                         helloService.updateTime(obj);
                         dev = JSONObject.toJavaObject((JSONObject)dev_list.get(map.get("pid")),DevList.class);
                         helloService.updateDevTime(dev);


                    }
                }


            } else {
                // log.error("redis_key do not match mysql_kkey:" + key + ",请确认！！！");
            }

        }
    public  void setRedisVal(String kkey,String val)
    {
        Jedis tjedis= JedisUtil.getInstance().getJedis();
        tjedis.set(kkey+"_.value",val);
        tjedis.set(kkey+"_.status","1");
        JedisUtil.getInstance().returnJedis(tjedis);
    }

    public  void setRedisProVal(String kkey,String val,int tt)
    {
        Jedis tjedis= JedisUtil.getInstance().getJedis();
        tjedis.set(kkey,val,"NX","EX",tt);
        JedisUtil.getInstance().returnJedis(tjedis);
    }
    public  void handleMessage( String message ) {
        String val=null,pmessage=null;
        Jedis tjedis= JedisUtil.getInstance().getJedis();
        int ttp = 0,tv = 0;
        pmessage = message.replace("_.value","");
        //log.info(message);
        if (key_id.containsKey(pmessage)) {
            //log.info(pmessage);
            try {

                val = tjedis.get(message);
               // log.info(message);

            } catch (Exception e) {
                val = "0";
                log.warn(message + ":" + e);

            }
            //log.info(val);

            try {
                //log.info(""+((JSONObject) objana_v.get(key_id.getString(pmessage))).get("type"));
                ttp = (int) ((JSONObject) objana_v.get(key_id.getString(pmessage))).get("type");
                tv = (int) ((JSONObject) objana_v.get(key_id.getString(pmessage))).get("tvalid");
                if ( (ttp== 0 || ttp ==4 || ttp ==5) && tv ==1) {
                    handleTime(pmessage, val);
                    if (pmessage.matches(retkey) && val.matches(String.valueOf(process)))
                    {
                        log.info(pmessage);
                        repeat = 1;
                        tjedis.del("timeout");
                        st.push(devList.getId());
                        if (taction.getLast()==0) {
                            if (step>0) {
                                taction = helloService.selectnextprocess(process, taction.getStep() + 1);
                                dostep();
                            }
                            else {
                                goback();
                            }
                        }else
                        {

                            log.info("控制程序 {} 执行完成",process);
                        }
                    }
                }
                //handleEvt(pmessage,val);
            } catch (Exception e) {
                  log.error(e.toString()+"=====>"+pmessage);
            }
        }
        else if (message.matches("anaupdate")) {
            try {
                loadAna_v();
                loadDevList();
                log.warn("the anatable has been reloaded.");
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                // e1.printStackTrace();
            }
        }
        else if (message.matches("authorupdate")) {
            try {
                //load_msguser();
                //load_author();
                log.warn("the anatable has been reloaded.");
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                // e1.printStackTrace();
            }
        }
        else if (message.matches("onlinewarnupdate")) {
            try {

                //load_onlinewarn();
                log.warn("the anatable has been reloaded.");
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                // e1.printStackTrace();
            }
        }
        else if (message.matches("conditionupdate")) {
            try {
                //load_condition();
                log.warn("the conditiontable has been reloaded.");
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                //  e1.printStackTrace();
            }
        }
        else if (message.contains("active"))
        {
            //
            //log.info("延时虚拟遥信心跳：" );
            //tjedis.set(message.replace("delay","value"),"1");
        }



        val=null;
        pmessage=null;


        JedisUtil.getInstance().returnJedis(tjedis);


    }
    public  void handleExpired( String message ) throws InterruptedException {
        String val = null,pmessage = null;
        int errid = 0;

        if ("timeout".matches(message)) {

           if (repeat<taction.getRepeat())
           {
               log.info("step  {} info : {} 控制反馈 第{}次 超时",taction.getStep(),devList.getDevname(),repeat);
               repeat = repeat + 1;
               dostep();

           }else
           {
               log.info("step  {} info : {} 控制无响应 ，踢出可用序列 , 请确认！！！",devList.getDevname(),taction.getStep());
               if (devList.getType() ==4 || devList.getType()==5)
               {
                   errid = devList.getRun();
               }else
               {
                   errid = devList.getId();
               }
               helloService.setDevErr(1,errid);
               repeat = 1;
               dostep();
           }
           /* pmessage = message.replace("_.value","");
            try {
                log.info(pmessage);
                log.info(key_id.getString(pmessage));
                log.info(objana_v.get(key_id.getString(pmessage)).toString());
                if ((int) ((JSONObject) objana_v.get(key_id.getString(pmessage))).get("type") == 0 ) {
                    handleTime(pmessage, val);
                    if (pmessage.matches(retkey) && val.matches(String.valueOf(process)))
                    {
                        if (taction.getLast()==0) {
                            taction = helloService.selectnextprocess(process, taction.getStep() + 1);
                            dostep();
                        }else
                        {
                            log.info("控制程序 {} 执行完成");
                        }
                    }
                }
                //handleEvt(pmessage,val);
            } catch (Exception e) {
                log.error(e.toString()+"=====>"+pmessage);
            }*/
        }


        val=null;
        pmessage=null;





    }
    public String getcronstr(String cron)  {
        String jsonString="{\"result\":0}";
        String ncron="";
        Map<String,Object> map1 = new HashMap<String,Object>();
        String[] croncnt=cron.split(" ");
        ncron = croncnt[0];
        for(int i=1;i<6;i++)
            ncron =ncron +" "+ croncnt[i];
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(ncron);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<String> list = new ArrayList<>(10);

        Date nextTimePoint = new Date();
        for (int i = 0; i < 10; i++) {
            // 计算下次时间点的开始时间
            nextTimePoint = cronSequenceGenerator.next(nextTimePoint);
            list.add(sdf.format(nextTimePoint));
        }
        map1.put("data",list);
        map1.put("result",1);
        jsonString = JSONObject.toJSONString(map1);

        return jsonString;
    }
    public Date getcronstr_prv(String cron)throws ParseException, InterruptedException  {


        CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
        cronTriggerImpl.setCronExpression(cron);//这里写要准备猜测的cron表达式
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.DATE, -31);
        List<Date> dates = TriggerUtils.computeFireTimesBetween(cronTriggerImpl, null, calendar.getTime(), now);//这个是重点，一行代码搞定~~

        return dates.get(dates.size()-1);
    }
}
