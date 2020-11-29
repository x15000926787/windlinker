package cn.tellsea.utils;

import cn.tellsea.Model.DataList;
import cn.tellsea.Model.DevList;
import cn.tellsea.Model.HelloModel;
import cn.tellsea.service.HelloService;
import cn.tellsea.service.RedisService;
import cn.tellsea.service.impl.RedisServiceImpl;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
@Service
@Slf4j
public final class anaUtil {

    @Autowired
    public static HelloService helloService;

    public static RedisService tjedis;//=new RedisServiceImpl();

    public static JSONObject objana_v = new JSONObject();

    public static JSONObject dev_list = new JSONObject();
    public static JSONObject msg_author = new JSONObject();
    public static JSONObject objcondition = new JSONObject();
    public static JSONObject online_warn = new JSONObject();
    public static JSONObject ana_fullname = new JSONObject();

    public static JSONArray dn_array = new JSONArray();
    public static HashMap<String,String> saveno_kkey = new HashMap<>();

    public static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    public static ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");
    //private  final String projectName = "【"+PropertyUtil.getProperty("project_name")+"】";
    //private  final int user_divide = Integer.parseInt(PropertyUtil.getProperty("user_divide","0"));           //默认用户不分组
    // public  LocalDateTime rightnow = null;
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter ymd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public  static  String s= null;
    public anaUtil()
    {
        tjedis=new RedisServiceImpl();
    }
    public static boolean isDoubleOrFloat(String str) {
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

    public static   void loadAna_v(){

        log.info("开始读取ana内容.......");
        try {
            objana_v = JSONObject.parseObject(helloService.selectAllData().toString().replace("[","{").replace("]","}"));

            log.warn(objana_v.toString());
        }catch (Exception e)
        {
            log.error("objana_v 初始化异常   "+e.toString()+"   "+helloService.selectAllDev().toString().replace("[","{").replace("]","}"));
        }


    }
    public  static   void loadDevList(){

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

    public static void add_red(String sql)
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
    public static void handleTime(String key,String vals) {
        String down, up, limit = " ", dbname, rtuno, sn, msgah, mailah, altah, mobs = "", gkey = "";
        // Calendar c;
        String msg = "";
        String s = null;
        //SimpleDateFormat df,df2,df3;
        LocalDateTime rightnow = LocalDateTime.now();
        char[] ss = null;
        HashMap<String, Object> tmap = null;
        String rnow = rightnow.format(formatter);

        HashMap<String, Object> map = new HashMap<String, Object>();
        HashMap<String, Object> tuser = new HashMap<String, Object>();
        // df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式


        int vv = -2;
        int timevalid = 0;
        //

        map = (HashMap<String, Object>) objana_v.get(key);
        tmap = (HashMap<String, Object>) dev_list.get(objana_v.getString("pid"));
        try {
            timevalid = Integer.parseInt(map.get("tvalid").toString());
        } catch (Exception e) {
            timevalid = 0;
        }

        if (timevalid == 1)  {







                    dbname = "devlist";

                if (Integer.parseInt(vals)==1) {
                    //if (Integer.parseInt(map.get("timestat").toString()) == 0) 
                    // {
                        log.warn(key + " 开始计时 ");
                        //开始计时
                       // ((HashMap<String, String>) objana_v.get(key)).put("timestat", "1");

                        ((HashMap<String, String>) objana_v.get(key)).put("tcheck", rnow);
                        add_red("UPDATE " + dbname + " SET timestat=1 ,checktime='" + rnow + "' where saveno="+map.get("saveno").toString());
                        // log.warn("UPDATE " + dbname + " SET timestat=1 ,checktime='" + rnow + "' where kkey='" + key + "'");
                    }
                 else
                    {
                    //if (Integer.parseInt(map.get("timestat").toString()) == 1)
                     {
                        //停止计时

                        log.warn(key + " 停止计时 ");
                        //LocalDateTime Date2 = LocalDateTime.now();
                        LocalDateTime to2 = LocalDateTime.parse(map.get("tcheck").toString(), formatter);
                        Duration duration = Duration.between(to2, rightnow);
                        //相差的分钟数
                        long minutes = duration.toMinutes();
                        //float hours =  ((rightnow.get - to2) / (1000.00f * 60 * 60));
                        float tot = minutes / 60.00f + Float.parseFloat(tmap.get("runtime").toString());
                        //log.warn(df3.format(Date2)+","+df3.format(toDate2)+","+hours);
                       // ((HashMap<String, String>) objana_v.get(key)).put("timestat", "0");
                        ((HashMap<String, String>) dev_list.get(map.get("pid").toString())).put("runtime", "" + tot);
                        float tot2 = minutes / 60.00f + Float.parseFloat(map.get("warnline").toString());
                        //((HashMap<String, String>) objana_v.get(key)).put("warnline", "" + tot2);
                        ((HashMap<String, String>) objana_v.get(key)).put("tcheck", rnow);

                        //mjedis.set(key + ".mtot", String.valueOf(tot));
                        //mjedis.set(key + ".ytot", String.valueOf(tot2));

                        add_red("UPDATE " + dbname + " SET timestat=0 ,checktime='" + rnow + "',online=" + tot + ",warnline=" + tot2 + " where saveno="+map.get("saveno").toString());




                    }
                }


            } else {
                // log.error("redis_key do not match mysql_kkey:" + key + ",请确认！！！");
            }

        }

    public static void handleMessage( String message ) {
        String val=null,pmessage=null;
        if (!Objects.nonNull(tjedis)) {
            log.info(message);
        }


        if (objana_v.containsKey(message)) {

            try {

                val = tjedis.get(message);

                // log.warn(" - "+" - "+message+" - "+val);
            } catch (Exception e) {
                val = "0";
                log.warn(message + ":" + e);
                // e.printStackTrace();
            }


            pmessage = message;//.replace("_.value","");
            try {
                if ((long) ((HashMap<String, Object>) objana_v.get(pmessage)).get("type") == 1 ) {
                    handleTime(pmessage, val);

                    //handleMaxMin(pmessage, val, tjedis);


                    //只考虑遥测数据条件？？？
                   // handleCondition(val, pmessage, tjedis);
                }
                //handleEvt(pmessage,val);
            } catch (Exception e) {
                //   log.error(e.toString()+"=====>"+pmessage);
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





    }
}
