package cn.tellsea.utils;

import cn.tellsea.Model.*;
import cn.tellsea.service.HelloService;

import cn.tellsea.service.RedisService;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.*;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

@Slf4j
@Component
@PropertySource({"classpath:para.properties"})
public  class anaUtil {

    @Autowired
    public  HelloService helloService;
    @Autowired
    public RedisService xjedis;//=new RedisServiceImpl();*/

    public static JSONObject objana_v = new JSONObject();

    public static JSONObject dev_list = new JSONObject();

    public static JSONObject key_id = new JSONObject();

    public static JSONObject para_list = new JSONObject();
    public static JSONObject sync_list = new JSONObject();
    //public  JSONObject tkeys = new JSONObject();
    public  static Stack<Integer> st = new Stack<Integer>();
    public  static  int process= -1;
    public  static  int repeat= 1;
    public static ActionDetial taction;
    public static DevList devList;
    public  static  int zhjno= -1;
    public  static  int  step= -1;
    public  static  int  stop= -1;        //标记关机度 ，0：关闭所有在运行的主机，1：关闭一台
    public  static  int tzjno= -1;       //在关机进程中记录关闭的主机的ID
    public  static  String retkey = "empty";
    public  static  String retkey2 = "empty";
    public  static  String retVal = "0";
    public  static  float tmax = -999.9f;
    public  static  float tmin = 999.9f;
    public  static  float[] data = new float[]{0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f}; //COLDWC_NOW = 0.0f;
    public  static  float[] data_now = new float[]{0.0f,0.0f,0.0f,0.0f}; //COLDWC_NOW = 0.0f;

    public  static  float[] data_before = new float[]{0.0f,0.0f,0.0f,0.0f};



    @Value("${sys.msguser}")
    public  String msguser ;




    @Value("${sys.coldoutwdset}")
    public  String coldoutwdset ;

    @Value("${sys.coldoutwd}")
    public  String coldoutwd ;
    @Value("${sys.coldinwd}")
    public  String coldinwd ;
    @Value("${sys.coldoutyl}")
    public  String coldoutyl ;
    @Value("${sys.coldinyl}")
    public  String coldinyl ;
    @Value("${sys.cooloutwd}")
    public  String cooloutwd ;
    @Value("${sys.coolinwd}")
    public  String coolinwd ;
    @Value("${sys.cooloutyl}")
    public  String cooloutyl ;
    @Value("${sys.coolinyl}")
    public  String coolinyl ;

    @Value("${sys.coldpl}")
    public  String coldpl ;
    @Value("${sys.coolpl}")
    public  String coolpl ;

    @Value("${sys.reset}")
    public  int wait ;

    @Value("${sys.reback}")
    public  int back ;

    /**
     * 加机条件判断，回水温度开始下降标志
     */
    public static int down = 0;


    /**
     * 上一次的回水温度
     */
    public static float pfcoldinwd = 0.0f;

    public static int errzj = 0;

    /**
     * 三选一
     */
    public static int tcnt = 0;


    public   String[] tkeys= new String[] {coldoutwd,coldinwd,coldoutyl,coldinyl,cooloutwd,coolinwd,cooloutyl,coolinyl};



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
        log.info(taction.getName());

        int ttp = taction.getTargettype();
        String tkey = null,rkey = null,sendv=null;
        //log.info(""+helloService.selectdev(1).getId());
        /*查找目标设备*/
//log.info("ccc");
        try {
            if ((taction.getType()) ==1) {

                if (ttp == 4 || ttp == 5) {
                    if (Objects.isNull(helloService.selectdev(1))) {
                        log.info("未找到符合条件主机，进程退出");
                        devList = null;
                    } else {

                    }
                    if (Objects.isNull(helloService.selectDcfDev(helloService.selectdev(1).getId(), ttp))) {
                        log.info("未找到符合条件电磁阀，进程退出");
                        devList = null;
                    } else {
                        devList = helloService.selectDcfDev(helloService.selectdev(1).getId(), ttp);
                    }
                }
                else{
                        devList = helloService.selectdev(ttp);
                       // log.info(devList.toString());
                    }

            }else
            {

                if (ttp == 4 || ttp == 5)
                    devList = helloService.selectDcfDev(tzjno, ttp);
                else {
                    devList = helloService.selectdev4s(ttp);

                    if (ttp == 1) {
                        if (errzj!=0) devList = helloService.selectdevbyid(errzj);
                        tzjno = devList.getId();
                    }

                }
            }
        }catch (Exception e)
        {
            devList = null;
            e.printStackTrace();
            log.info(e.toString());

        }

       if (Objects.nonNull(devList)) {
           //log.info(devList.toString());


        zhjno = devList.getId();

        log.info("step {} info : 目标设备 ID：{}  名称：{}",step,zhjno,devList.getDevname());
        /*查找目标设备控制键*/

         tkey = helloService.selectcontrolkey(zhjno,3).getTkey();
        log.info("step {} info : 目标控制键 {}",step,tkey);
        /*查找目标设备控制反馈键*/
        if (ttp == 4 || ttp == 5)
        {
            /*if (taction.getType()==1) rkey = helloService.selectcontrolkey(zhjno, 0).getKkey();
            else rkey = helloService.selectcontrolkey(zhjno, 5).getKkey();*/
            if (taction.getType()==1){
                rkey = helloService.selectcontrolkey(zhjno, 0).getKkey();
                retkey2 = helloService.selectcontrolkey(zhjno, 5).getKkey();
            }else {
                rkey = helloService.selectcontrolkey(zhjno, 5).getKkey();
                retkey2 = helloService.selectcontrolkey(zhjno, 0).getKkey();
            }
        }
        else {
            rkey = helloService.selectcontrolkey(zhjno, 0).getKkey();
        }
        log.info("step {} info : 目标控制反馈键 {}",step,rkey);

        if (devList.fresh!=0 && taction.getType()!=0)
        {
            log.info("step {} info : 发送复位命令 {}",step,devList.getFresh());
            setRedisVal(tkey,String.valueOf(devList.getFresh()));
            sleep(wait);
        }else
        {
            //log.info("step {} info : 此设备无复位命令 ",step);
        }


        retkey = rkey;
           if(taction.getType()==1) {
               retVal = String.valueOf(devList.getReton());
               sendv = String.valueOf(devList.getPoweron());
           }
           else {
               retVal = String.valueOf(devList.getRetoff());
               sendv = String.valueOf(devList.getPoweroff());
           }
        if (process==1  && taction.getTargettype()==1)
        {
            if (tcnt>0) {
                setRedisVal(tkey,(sendv));
                setRedisProVal("timeout",String.valueOf(repeat),taction.getWait());
                log.info("step {} info : 发送控制命令 {} {}",step,tkey,process==1?devList.getPoweron():devList.getPoweroff());
                if (back ==1) {
                    sleep(5000);

                    log.info("发送模拟反馈 {} {}", rkey, retVal);
                    setRedisVal(rkey, retVal);
                    if (ttp == 4 || ttp == 5) setRedisVal(retkey2, retVal.matches("1")?"0":"1");
                }
            }else
            {
                log.info("开主机的先决条件未达成，进程退出");
            }
        }else
        {
            setRedisVal(tkey,(sendv));
            setRedisProVal("timeout",String.valueOf(repeat),taction.getWait());
            log.info("step {} info : 发送控制命令 {} {}",step,tkey,process==1?devList.getPoweron():devList.getPoweroff());
            if (back ==1){
                sleep(5000);

                log.info("发送模拟反馈 {} {}", rkey, retVal);
                setRedisVal(rkey, retVal);
                if (ttp == 4 || ttp == 5) setRedisVal(retkey2, retVal.matches("1")?"0":"1");
            }
        }


       /* setRedisVal(tkey,String.valueOf(process==1?devList.getPoweron():devList.getPoweroff()));
        setRedisProVal("timeout",String.valueOf(repeat),taction.getWait());
        log.info("step {} info : 发送控制命令 {} {}",step,tkey,process==1?devList.getPoweron():devList.getPoweroff());*/


       }else
       {
           //告警，无可用设备
         //  log.info(taction.getNeed()+"");
           if (taction.getNeed()==0){
               log.info("step {} info : 无此类型可用设备, 设备类型号 {} ,进程跳过.",step,ttp);
               taction = helloService.selectnextprocess(process, taction.getStep() + 1);
               dostep();
           }else{
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
    }

   /* *//**
     * 执行关闭故障泵、变频器Action
     *//*
    public  void  dodevaction(int id,int pro) throws InterruptedException {


        devList = helloService.selectdevbyid(id);

        String tkey = null,rkey = null;
        // log.info(""+helloService.selectdev(1).getId());
        *//*查找目标设备*//*

        int ttp = devList.getType();
        if (Objects.nonNull(devList)) {
            //log.info(devList.toString());

            zhjno = devList.getId();

            log.info("step  info : 目标设备 ID：{}  名称：{}",zhjno,devList.getDevname());
            *//*查找目标设备控制键*//*

            tkey = helloService.selectcontrolkey(zhjno,3).getTkey();
            log.info("step  info : 目标控制键 {}",tkey);
            *//*查找目标设备控制反馈键*//*
            if (ttp == 4 || ttp == 5)
            {
                if (pro==1) rkey = helloService.selectcontrolkey(zhjno, 4).getKkey();
                else rkey = helloService.selectcontrolkey(zhjno, 5).getKkey();
            }
            else {
                rkey = helloService.selectcontrolkey(zhjno, 0).getKkey();
            }
            log.info("step  info : 目标控制反馈键 {}",rkey);

            if (devList.getFresh()!=0)
            {
                log.info("step  info : 发送复位命令 {}",devList.getFresh());
                setRedisVal(tkey,String.valueOf(devList.getFresh()));
                sleep(3000);
            }else
            {
                //log.info("step {} info : 此设备无复位命令 ",step);
            }


            retkey = rkey;
            setRedisVal(tkey,String.valueOf(pro==1?devList.getPoweron():devList.getPoweroff()));
            setRedisProVal("timeout",String.valueOf(repeat),taction.getWait());
            log.info("step  info : 发送控制命令 {} {}",tkey,process==1?devList.getPoweron():devList.getPoweroff());

            sleep(5000);

            log.info("发送模拟反馈 {} {}",rkey,process);
            setRedisVal(rkey,String.valueOf(process));
        }else
        {
            //告警，无可用设备
            log.info("step  info : 没找到该设备, 设备ID号 {} ,进程退出.",id);

        }
    }*/
    /**
     * 撤销,只考虑开机进程的撤销
     */
    public  void  goback() throws InterruptedException {


        if (!st.empty()) {
            zhjno = st.pop();
            int ttp = 0;
            String tkey = null,rkey = null;

            devList = helloService.selectdevbyid(zhjno);
            //taction = helloService.selectAction(process,st.size()+1);



            if (Objects.nonNull(devList)) {
                log.info(devList.toString());

                zhjno = devList.getId();

                log.info("goback info : 目标设备 ID：{}  名称：{}", zhjno, devList.getDevname());
                /*查找目标设备控制键*/

                tkey = helloService.selectcontrolkey(zhjno, 3).getKkey();
                log.info("goback info : 目标控制键 {}", tkey);
                /*查找目标设备控制反馈键*/
                if (ttp == 4 || ttp == 5) {
                    if (taction.getType() == 1) rkey = helloService.selectcontrolkey(zhjno, 4).getKkey();
                    else rkey = helloService.selectcontrolkey(zhjno, 5).getKkey();
                } else {
                    rkey = helloService.selectcontrolkey(zhjno, 0).getKkey();
                }
                log.info("goback info : 目标控制反馈键 {}", rkey);

               /* if (devList.fresh != 0) {
                    log.info("goback info : 发送复位命令 {}",  devList.getFresh());
                    setRedisVal(tkey, String.valueOf(devList.getFresh()));
                    sleep(3000);
                } else {
                    log.info("goback info : 此设备无复位命令 ");
                }*/


                retkey = rkey;
                if(taction.getType()==1)
                    retVal = String.valueOf(devList.getReton());
                else
                    retVal = String.valueOf(devList.getRetoff());

                setRedisVal(tkey, String.valueOf(devList.getPoweroff()));
                //setRedisProVal("timeout", String.valueOf(repeat), taction.getWait());
                log.info("goback info : 发送控制命令 {} {}",  tkey, devList.getPoweroff());
                if (back ==1){
                    sleep(5000);

                    log.info("发送模拟反馈 {} {}", rkey, retVal);
                    setRedisVal(rkey, retVal);
                }
            } else {
                //告警，无可以设备
                log.info("goback info : 撤销进程异常退出.");

            }
        }
    }

    /**
     * 开启一台主机
     */
    public  void  startZJ() throws InterruptedException {
        log.info("开机进程启动......");
        if ((JSONObject.toJavaObject((JSONObject)para_list.get("15"),Parameter.class)).getValue()==0)
        {
            log.info("手自动模式为 0，跳出......");
            return;
        }

        helloService.resetmyerr();
        xjedis.del("add","mid");

        int  cnt = helloService.selectdevruncount().size();
        int tot = helloService.selectzjcount().size();
        if (0 < tot){
            if (cnt>0){
                process = 3;


            }else
            {
                process = 1;
            }

            taction = helloService.selectnextprocess(process,1);;
//log.info(taction.getName());

            dostep();
        }else {
            log.info("已经达到上限，进程退出 {} of {}",cnt,tot);
        }


        //log.info("");
    }

    /**
     * 关闭所有在运行主机
     */

    public  void  stopZJ() throws InterruptedException {

        log.info("关机进程启动......");
        if ((JSONObject.toJavaObject((JSONObject)para_list.get("15"),Parameter.class)).getValue()==0)
        {
            log.info("手自动模式为 0，跳出......");
            return;
        }
        process = 0;
        stop = 0 ;
        helloService.resetmyerr();
        xjedis.del("add","mid");
        int  cnt = helloService.selectdevruncount().size();
        if (cnt>1){
            process = 2;
            log.info("当前有 {} 台主机在运行，将逐步关闭.",cnt);
        }

        taction = helloService.selectnextprocess(process,1);
        dostep();


    }
    /**
     * 调频
     *
     */
    public void calcpl() throws InterruptedException {
        //
        if ((JSONObject.toJavaObject((JSONObject)para_list.get("15"),Parameter.class)).getValue()==0)
        {
            log.info("手自动模式为 0，跳出调频......");
            return;
        }
        try {
            log.info("冷却水温差 {},目标温差 {}", data_now[2], (JSONObject.toJavaObject((JSONObject) para_list.get("7"), Parameter.class)).getValue());
            if (data_now[2] > (JSONObject.toJavaObject((JSONObject) para_list.get("7"), Parameter.class)).getValue()) {

                ajust(3, 1);
            } else {

                ajust(3, -1);
            }
            log.info("冷冻水温差 {},目标温差 {}", data_now[0], (JSONObject.toJavaObject((JSONObject) para_list.get("6"), Parameter.class)).getValue());
            if (data_now[0] > (JSONObject.toJavaObject((JSONObject) para_list.get("6"), Parameter.class)).getValue()) {
                ajust(2, 1);
            } else {
                ajust(2, -1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 调频率
     */
    public  void  ajust(int type,int vv) throws InterruptedException {

        //
        //log.info("调频进程启动");

        Jedis tjedis= JedisUtil.getJedis();

        List<DataList> tarlist = helloService.selectDatabytype(type,16);
        //if (Objects.nonNull(tarlist)) log.info(tarlist.toString());
        //List<DevList> tarlist= helloService.selectdevbytype(type);
        Float val=0.0f;
        try {
            if (type==2) val=Float.parseFloat(tjedis.get(coldpl));
            else val=Float.parseFloat(tjedis.get(coolpl));
            log.info("设备类型id :{} 当前频率：{} ",type,val);
            if (vv>0)
            {
                val= (val+((JSONObject.toJavaObject((JSONObject)para_list.get("10"),Parameter.class)).getValue()));
            }else
                val= (val-((JSONObject.toJavaObject((JSONObject)para_list.get("10"),Parameter.class)).getValue()));
            log.info("目标频率：{} ",val);
            if (val>35 && val<50)
            {
                if (Objects.nonNull(tarlist)){
                    for (DataList dl :tarlist)
                    {
                        log.info(dl.getTkey()+","+String.valueOf(val));
                        tjedis.set(dl.getTkey()+"_.value",String.valueOf(val));
                        tjedis.set(dl.getTkey()+"_.status",String.valueOf(1));
                    }
                }
                /*if (type==2)
                {
                    tarlist = null;// helloService.selectDcfDev()
                    tjedis.set("5.4.2.ao_.value",""+val);
                    tjedis.set("5.4.2.ao_.status","1");
                    tjedis.set("5.5.2.ao_.value",""+val);
                    tjedis.set("5.5.2.ao_.status","1");
                    tjedis.set("5.6.2.ao_.value",""+val);
                    tjedis.set("5.6.2.ao_.status","1");
                }
                if (type==3)
                {
                    tjedis.set("5.1.2.ao_.value",""+val);
                    tjedis.set("5.1.2.ao_.status","1");
                    tjedis.set("5.2.2.ao_.value",""+val);
                    tjedis.set("5.2.2.ao_.status","1");
                    tjedis.set("5.3.2.ao_.value",""+val);
                    tjedis.set("5.3.2.ao_.status","1");
                }*/
            }
            else
            {
                log.info("不在可设置范围内，跳出。");
            }
        }catch (Exception e){}





        JedisUtil.returnJedis(tjedis);

    }
    /**
     * 减少一台主机
     */
    public  void  stopOneZJ() throws InterruptedException {

        log.info("关机进程启动......");
        if ((JSONObject.toJavaObject((JSONObject)para_list.get("15"),Parameter.class)).getValue()==0)
        {
            log.info("手自动模式为 0，跳出......");
            return;
        }
        process = 2;
        stop = 1;
        helloService.resetmyerr();
        xjedis.del("add","mid");
        int  cnt = helloService.selectdevruncount().size();
        if (cnt>0)
        {
            if (errzj>0) {
                log.info("将关闭 {}", helloService.selectdevbyid(errzj).getDevname());
                taction = helloService.selectnextprocess(process,(int)((JSONObject)dev_list.get(errzj)).get("error"));
            }
            else {
                log.info("当前有 {} 台主机在运行，将关闭一台", cnt);
                taction = helloService.selectnextprocess(process,1);
            }
        }


        dostep();


    }

    /**
     * 关闭一台水泵，启动备用
     */
    public  void  stopTheB(DevList tdev) throws InterruptedException {
        String tkey;
        DevList sdev;

        if ((JSONObject.toJavaObject((JSONObject)para_list.get("15"),Parameter.class)).getValue()==0)
        {
            log.info("手自动模式为 0，跳出......");
            return;
        }

        log.info("开泵进程启动......");

        sdev = helloService.selectdev(tdev.getType());

        tkey = helloService.selectcontrolkey(sdev.getId(),3).getTkey();

        setRedisVal(tkey,"1");
        sleep(wait);
        setRedisVal(tkey,"1");
        sleep(wait);
        setRedisVal(tkey,"1");

        log.info("关泵进程启动......");

        tkey = helloService.selectcontrolkey(tdev.getId(),3).getTkey();

        setRedisVal(tkey,"0");
        sleep(wait);
        setRedisVal(tkey,"0");
        sleep(wait);
        setRedisVal(tkey,"0");


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
       // log.info(ltask.size()+"");
        if ((ltask.size()>0)) {
            for (TimeTask t : ltask) {
                sdate = LocalDateUtil.dateToLocalDateTime(getcronstr_prv(t.getCronstr()));


                if (sdate.isAfter(tdate)) {

                    tdate = sdate;
                    tid = t.getId();
                    val = t.getName();
                }
            }
            log.info("最后一条定时开关机任务：{} {}", val, tdate.toString());
            tk_detial = helloService.selectAllTimeTaskDetial(tid);
            val = tk_detial.get(0).getVal();

            cnt = helloService.selectdevruncount().size();
            if (cnt == 0) {
                log.info("当前没有主机在运行");
                if (val.matches("1")) {
                    log.info("根据定时任务 {} ,进入开机进程", tk_detial.get(0).getTaskid());
                    startZJ();
                }

            } else {
                log.info("当前有{}台主机在运行。", cnt);
                if (val.matches("0")) {
                    log.info("根据定时任务 {} ,进入关机进程", tk_detial.get(0).getTaskid());
                    stopZJ();
                }
            }
        }else
        {
            log.info("当前没有定时开关机任务。");
        }

    };
    public    void loadAna_v(){

        log.info("开始读取ana内容.......");
        try {
            objana_v = JSONObject.parseObject(helloService.selectAllData().toString().replace("[","{").replace("]","}"));

            log.warn(String.valueOf(objana_v.size()));
        }catch (Exception e)
        {
            log.error("objana_v 初始化异常   "+e.toString()+"   "+helloService.selectAllDev().toString().replace("[","{").replace("]","}"));
        }

        for(String str:objana_v.keySet()){
            key_id.put(((JSONObject)objana_v.get(str)).getString("kkey"),str);
        }
        log.info(String.valueOf(key_id.size()));

    }

    public     void loadDevList(){

        log.info("开始读取devlist内容.......");
        try {
            dev_list = JSONObject.parseObject(helloService.selectAllDev().toString().replace("[","{").replace("]","}"));

            log.warn(String.valueOf(dev_list.size()));
        }catch (Exception e)
        {
            log.error("devlist 初始化异常   "+e.toString()+"   "+helloService.selectAllDev().toString().replace("[","{").replace("]","}"));
        }


    }

    public  void loadParaList(){

        log.info("开始读取parameter内容.......");
        try {
            para_list = JSONObject.parseObject(helloService.selectAllPara().toString().replace("[","{").replace("]","}"));

            log.warn(String.valueOf(para_list.size()));
        }catch (Exception e)
        {
            log.error("para_list 初始化异常   "+e.toString()+"   "+helloService.selectAllPara().toString().replace("[","{").replace("]","}"));
        }


    }
    public  void loadSyncParaList(){
        JSONObject temp_list = new JSONObject();
        log.info("开始读取sync_parameter内容.......");
        try {
            temp_list = JSONObject.parseObject(helloService.selectSyncPara().toString().replace("[","{").replace("]","}"));

            for(String str:temp_list.keySet()){
                //System.out.println(str + ":" +obj.get(str));
                sync_list.put(((Map)temp_list.get(str)).get("kkey").toString(),str);
            }
            log.warn((sync_list.toJSONString()));
        }catch (Exception e)
        {
            e.printStackTrace();
            log.error("para_list 初始化异常   "+e.toString()+"   "+helloService.selectSyncPara().toString().replace("[","{").replace("]","}"));
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
        //String down, up, limit = " ", dbname, rtuno, sn, msgah, mailah, altah, mobs = "", gkey = "";
        // Calendar c;
       // String msg = "";
       // String s = null;
        //SimpleDateFormat df,df2,df3;
        LocalDateTime rightnow = LocalDateTime.now();
        //log.info(rightnow.toString());

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

                   // dbname = "devlist";

                if (Integer.parseInt(vals)==1) {

                        if (map.get("tstatus").toString().matches("0")) {

                            ((JSONObject) objana_v.get(key_id.getString(key))).put("tstatus", "1");
                            ((JSONObject) objana_v.get(key_id.getString(key))).put("tcheck", rnow);
                            obj = JSONObject.toJavaObject((JSONObject)objana_v.get(key_id.getString(key)),DataList.class);
                            helloService.updateTime(obj);
                            dev = JSONObject.toJavaObject((JSONObject)dev_list.get(map.get("pid")),DevList.class);
                            log.warn("{} 开始计时 ",dev.getDevname());
                        }
                    }
                 else
                    {
                    if (map.get("tstatus").toString().matches("1") )
                     {
                        //停止计时


                        //LocalDateTime Date2 = LocalDateTime.now();
                        LocalDateTime to2 = new Date( Long.parseLong(map.get("tcheck").toString())).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();//LocalDateTime.ofEpochSecond((long)map.get("tcheck"),0, ZoneOffset.ofHours(8));//LocalDateTime.parse(.toString(), formatter);
                        //log.info(to2.toString());
                         Duration duration = Duration.between(to2, rightnow);
                        // log.info(duration.toString());
                        //相差的分钟数
                        long minutes = duration.toMinutes();
                        // log.info(minutes+","+tmap.get("runtime").toString());
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
                         //log.info(obj.toString());
                         helloService.updateTime(obj);
                         dev = JSONObject.toJavaObject((JSONObject)dev_list.get(map.get("pid")),DevList.class);
                         //log.info(dev.toString());
                         helloService.updateDevTime(dev);
                         log.warn("{} 停止计时,此次运行时长 {} minutes",dev.getDevname(),minutes);

                    }
                }


            } else {
                // log.error("redis_key do not match mysql_kkey:" + key + ",请确认！！！");
            }

        }
    public  void setRedisVal(String kkey,String val)
    {
       // String tkey =
        Jedis tjedis= JedisUtil.getJedis();
        tjedis.set(kkey+"_.value",val);
        if (kkey.contains("ao")||kkey.contains("do"))
            tjedis.set(kkey+"_.status","1");
        JedisUtil.returnJedis(tjedis);
    }

    public  void setRedisProVal(String kkey,String val,int tt)
    {
        Jedis tjedis= JedisUtil.getJedis();
        tjedis.set(kkey,val,"NX","EX",tt);
        JedisUtil.returnJedis(tjedis);
    }
    public  void handleMessage( String message ) {
        String val=null,pmessage=null;
        Jedis tjedis= JedisUtil.getJedis();
        int ttp = 0,tv = 0,stt = 0,vl=0;
        float vv = 0.0f,vs = 0.0f;




        pmessage = message.replace("_.value","");
        //收到同步虚点
        if (sync_list.containsKey(message)){

            try {
                if (Objects.nonNull(tjedis)){
                    val = tjedis.get(message).trim().replace(".000","");

                }

                else{

                    val="0";
                }



            } catch (Exception e) {
                val = "0";
                log.warn(message + ":" + e.toString());
                e.printStackTrace();

            }
            log.info("rcv {},{}",message,val);
            helloService.syncPara(Float.parseFloat(val),message);
            loadParaList();
        }
        if (key_id.containsKey(pmessage)) {

            try {
                if (Objects.nonNull(tjedis)){
                    val = tjedis.get(message).trim().replace(".000","");

                }

                else{

                    val="0";
                }



            } catch (Exception e) {
                val = "0";
                log.warn(message + ":" + e.toString());
                e.printStackTrace();

            }



           // log.info(pmessage+","+val+","+retkey+","+retVal);
            JSONObject map = new JSONObject();



            DevList dev = null;
            try {
                map = (JSONObject) objana_v.get(key_id.getString(pmessage));
                ttp = (int) ((JSONObject) objana_v.get(key_id.getString(pmessage))).get("type");
                tv = (int) ((JSONObject) objana_v.get(key_id.getString(pmessage))).get("tvalid");
                dev = JSONObject.toJavaObject((JSONObject)dev_list.get(map.get("pid")),DevList.class);

                try {
                    vv = Float.parseFloat(val);
                } catch (Exception e) {
                    log.warn(message +","+vv+ ":" + e.toString());
                    vv = 0;
                }
                try {
                        vs = Float.parseFloat(tjedis.get(coldoutwdset));
                    }catch (Exception e)
                    {
                        vs = 7.0f;
                        log.warn(coldoutwdset + ":" + e.toString());
                    }
                if (message.matches(coldinwd)){
                    log.info("rcv coldinwd {} which is limited at {} {}",vv,(JSONObject.toJavaObject((JSONObject)para_list.get("1"),Parameter.class)).getValue()+vs,((JSONObject.toJavaObject((JSONObject)para_list.get("12"),Parameter.class)).getValue()+vs));

                    if (!tjedis.exists("add")&&(vv > ((JSONObject.toJavaObject((JSONObject)para_list.get("1"),Parameter.class)).getValue()+vs)) && (0<helloService.selectzjcount().size()))
                    {
                        log.info("回水温度大于设定温度，开启 {}秒 加机监控进程",(JSONObject.toJavaObject((JSONObject)para_list.get("2"),Parameter.class)).getValue());
                        tjedis.set("add","1st","NX","EX",(int)(JSONObject.toJavaObject((JSONObject)para_list.get("2"),Parameter.class)).getValue());
                    }

                    if (!tjedis.exists("critical_add")&&(vv > ((JSONObject.toJavaObject((JSONObject)para_list.get("12"),Parameter.class)).getValue()+vs)) && (0<helloService.selectzjcount().size()))
                    {
                        log.info("回水温度大于紧急加机设定温度，开启 {}秒 紧急加机监控进程",(JSONObject.toJavaObject((JSONObject)para_list.get("13"),Parameter.class)).getValue());
                        tjedis.set("critical_add","1st","NX","EX",(int)(JSONObject.toJavaObject((JSONObject)para_list.get("13"),Parameter.class)).getValue());
                    }
                    if (tjedis.exists("critical_add")&&(vv < ((JSONObject.toJavaObject((JSONObject)para_list.get("12"),Parameter.class)).getValue()+vs)) )
                    {
                        log.info("紧急加机条件未达成，退出紧急加机监控进程");
                        tjedis.del("critical_add");
                    }
                    if (tjedis.exists("add")&&(vv < ((JSONObject.toJavaObject((JSONObject)para_list.get("1"),Parameter.class)).getValue()+vs)) )
                    {
                        log.info("加机条件未达成，退出加机监控进程");
                        tjedis.del("add");
                    }



                    if (vv<(JSONObject.toJavaObject((JSONObject)para_list.get("3"),Parameter.class)).getValue()+vs  && (!tjedis.exists("mid")) && (helloService.selectdevruncount().size()>1))
                    {
                        //log.info("减机条件达成，开启减机进程");
                        log.info("减机条件达成，开启 {}秒 监控进程",(JSONObject.toJavaObject((JSONObject)para_list.get("4"),Parameter.class)).getValue());

                        tjedis.set("mid","1","NX","EX",(int)(JSONObject.toJavaObject((JSONObject)para_list.get("4"),Parameter.class)).getValue());
                    }
                    if (vv>(JSONObject.toJavaObject((JSONObject)para_list.get("3"),Parameter.class)).getValue()+vs  && (tjedis.exists("mid")) )
                    {
                        log.info("减机条件未达成，退出加减监控进程");
                        tjedis.del("mid");
                    }
                }
//log.info("ddd");
                /**
                 * 本地远程
                 */
                if ( (ttp == 2) ) {


                    log.info("recv : {}  本地远程 {}",dev.getDevname(),val);

                    stt = Integer.parseInt(val);
                    if (stt==dev.getStatuson())
                        dev.setStatus(0);
                    else
                        dev.setStatus(1);
                    ((JSONObject)dev_list.get(dev.getId())).put("status",dev.getStatus());
                    helloService.updateDevStatus(dev);
                }

                /**
                 * 计时信息处理
                 */
                if ( (ttp == 0) ) {

                    log.info("recv : {}  运行状态码 {}", dev.getDevname(), val);
                    vl = Integer.parseInt(val);
                    if (vl == dev.getReton()) {
                        dev.setRun(1);
                    } else {
                        dev.setRun(0);
                    }
                    ((JSONObject) dev_list.get(dev.getId())).put("run", dev.getRun());
                    helloService.updateDevRun(dev);
                        if (tv == 1)
                            handleTime(pmessage, val);

                }

                /**
                 * 故障信息处理
                 */
                if ( (ttp == 1) ) {
                    log.info("recv : {}  故障码 {}",dev.getDevname(),val);
                    vl = Integer.parseInt(val);
                    if (vl==dev.getErron()){
                        dev.setError(1);
                    }else{
                        dev.setError(0);
                    }


                   // log.info("{}:{} {}",dev.getDevname(),dev.getErron(),val);
                    ((JSONObject)dev_list.get(dev.getId())).put("error",dev.getError());
                    helloService.updateDevErr(dev);

                    if (dev.getError() == 1) {

                            if (dev.getStatus()==1) {
                                log.info(dev.getDevname() + " 报故障,运行模式为本地，不处理");
                            }else if (dev.getRun()==0 ){
                                log.info(dev.getDevname() + " 报故障,未运行，不处理");
                            }else {


                                    log.info(dev.getDevname() + " 发生故障");
                                    if (!msguser.matches("0"))
                                        SendViaWs.sendmsg(msguser, dev.getDevname() + " 发生故障");
                                    switch (dev.getType()) {
                                        case 1:
                                            if (vl == 1) {
                                                log.info("{}带故障运行，关闭，重新开启一台", dev.getDevname());
                                                errzj = dev.getId();
                                                process = 0;
                                                taction = helloService.selectnextprocess(process, 1);
                                                stopOneZJ();


                                            }
                                            if (vl == 2) {
                                                log.info("{}故障停机，重新开启一台", dev.getDevname());
                                                dev.setRun(0);

                                                ((JSONObject)dev_list.get(dev.getId())).put("run",dev.getRun());
                                                helloService.updateDevRun(dev);
                                                errzj = dev.getId();
                                                tzjno = dev.getId();
                                                process = 0;
                                                taction = helloService.selectnextprocess(process, 2);
                                                stopOneZJ();

                                            }
                                            break;
                                        case 2:
                                            log.info("{}带故障运行，关闭，开启备用", dev.getDevname());
                                            stopTheB(dev);
                                            break;
                                        case 4:
                                            log.info("{}带故障运行，关闭主机，重新开启一台", dev.getDevname());
                                            errzj = dev.getRun();
                                            process = 0;
                                            taction = helloService.selectnextprocess(process, 1);
                                            stopOneZJ();
                                            break;
                                        case 5:
                                            log.info("{}带故障运行，关闭主机，重新开启一台", dev.getDevname());
                                            errzj = dev.getRun();
                                            process = 0;
                                            taction = helloService.selectnextprocess(process, 1);
                                            stopOneZJ();
                                            break;

                                        default:
                                            break;
                                    }
                                }
                    }
                }
                /**
                 * 调频
                 */
                if ((coldoutwd.matches(message))){
                    anaUtil.data[0] = (vv);
                }
                if ((coldinwd.matches(message))){
                    anaUtil.data[1] = (vv);
                }
                if ((coldinyl.matches(message))){
                    anaUtil.data[2] = (vv);
                }
                if ((coldoutyl.matches(message))){
                    anaUtil.data[3] = (vv);
                }
                if ((coolinwd.matches(message))){
                    anaUtil.data[4] = (vv);
                }
                if ((cooloutwd.matches(message))){
                    anaUtil.data[5] = (vv);
                }
                if ((coolinyl.matches(message))){
                    anaUtil.data[6] = (vv);
                }
                if ((coldoutyl.matches(message))){
                    anaUtil.data[7] = (vv);
                }
                /*for (int i=0;i<8;i++) {

                    log.info(String.valueOf(data[i])+" : "+i);
                }*/
                 for (int i=0;i<4;i++)
                {
                data_now[i] = anaUtil.data[i*2+1]-anaUtil.data[i*2];
                }
                /*if (calctp==1)
                calcpl();*/


                /**
                 * 控制返回信息处理
                 */
                if (pmessage.matches(retkey) && val.matches(String.valueOf(retVal)))
                {
//                    log.info(pmessage);
                    repeat = 1;
                    tjedis.del("timeout");

                    if (taction.getNeed()==0) tcnt=tcnt | (int)Math.pow(2,dev.getType()-1);
//                    log.info(""+tcnt);
                    st.push(dev.getId());
                    /*if (dev.getType()!=4 && dev.getType()!=5) {
                        dev.setRun(taction.getType());
                        ((JSONObject)dev_list.get(dev.getId())).put("run",dev.getRun());
                        helloService.updateDevRun(dev);
                    }*/
//                    log.info(""+tcnt);
                    if (taction.getLast()==0) {
//                        log.info(""+tcnt);
                        if (step>0) {
                            taction = helloService.selectnextprocess(process, taction.getStep() + 1);
                            dostep();
                        }
                        else {
                            goback();
                        }
                    }else {
                       //log.info(""+tcnt);
                          if ( taction.getType() == 0) {
                            //关全部
                            if (helloService.selectdevruncount().size()>0 && stop == 0) {
                                process = 0 ;
                                taction = helloService.selectnextprocess(process,1);
                                dostep();
                            }
                            if (errzj>0) {
                                log.info("关机进程执行完成 , 将进入开机进程");
                                process =1;
                                taction = helloService.selectnextprocess(process,1);
                                dostep();

                            }
                            //log.info("{} 控制程序 {} 执行完成",devList.getDevname(),process);
                        }
                       if (errzj==0) {
                           log.info("控制进程 {} 已执行完成",process);
                       }else {
                           log.info("控制进程 {} 已执行完成",process);
                           errzj = 0;
                       }


                    }
                }

                //handleEvt(pmessage,val);
            } catch (Exception e) {
                e.printStackTrace();
                  log.error(e.toString()+"=====>"+pmessage);
            }
        }
        else if (message.matches("anaupdate")) {
            try {
                loadAna_v();
                loadDevList();
                loadParaList();
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


        JedisUtil.returnJedis(tjedis);


    }
    public  void handleExpired( String message ) throws InterruptedException {
        String val = null,pmessage = null;
        int errid = 0;
        Jedis tjedis= JedisUtil.getJedis();
        log.info(message );
        if ("timeout".matches(message)) {

            if (tjedis.exists(retkey+"_.value")) {
                if (Integer.parseInt(tjedis.get(retkey + "_.value")) != Integer.parseInt(retVal)) {

                    if (repeat < taction.getRepeat()) {
                        log.info("step  {} info : {} 控制反馈 第{}次 超时", taction.getStep(), devList.getDevname(), repeat);
                        repeat = repeat + 1;
                        dostep();

                    } else {
                        log.info("step  {} info : {} 控制无响应  第{}次 超时，踢出可用序列 , 请确认！！！", taction.getStep(), devList.getDevname(), repeat);
                        if (devList.getType() == 4 || devList.getType() == 5) {
                            //errid = devList.getRun();
                            log.info("电磁阀异常，程序退出。");
                        } else if (devList.getType() == 2 || devList.getType() == 3) {
                            errid = devList.getId();
                            helloService.setDevErr(1, errid);
                            repeat = 1;
                            dostep();
                        } else {
                            goback();
                        }

                    }

                } else {
                    handleMessage(retkey + "_.value");
                }
            }else{
                log.info("redis  找不到 {}",retkey+ "_.value");
            }

        }
        if ("add".matches(message) && (0<helloService.selectzjcount().size()) ) {
                log.info("加机条件满足，启动加机进程.");
            if ((JSONObject.toJavaObject((JSONObject)para_list.get("15"),Parameter.class)).getValue()==0)
            {
                log.info("手自动模式为 0，跳出......");
                JedisUtil.returnJedis(tjedis);
                return;
            }
                if (tjedis.exists("critical_add")) tjedis.del("critical_add");
                startZJ();
        }
        if ("critical_add".matches(message) && (0<helloService.selectzjcount().size()) ) {
            log.info("紧急加机条件满足，启动加机进程.");
            if ((JSONObject.toJavaObject((JSONObject)para_list.get("15"),Parameter.class)).getValue()==0)
            {
                log.info("手自动模式为 0，跳出......");
                JedisUtil.returnJedis(tjedis);
                return;
            }
            if (tjedis.exists("add")) tjedis.del("add");
            startZJ();
        }

        if ("mid".matches(message) && (helloService.selectdevruncount().size()>1)) {
            log.info("减机条件满足，启动减机进程.");
            if ((JSONObject.toJavaObject((JSONObject)para_list.get("15"),Parameter.class)).getValue()==0)
            {
                log.info("手自动模式为 0，跳出......");
                JedisUtil.returnJedis(tjedis);
                return;
            }
            stopOneZJ();
        }

        val=null;
        pmessage=null;

        JedisUtil.returnJedis(tjedis);



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
