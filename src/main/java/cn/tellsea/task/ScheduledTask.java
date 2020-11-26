package cn.tellsea.task;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.tellsea.service.RedisService;
import cn.tellsea.utils.*;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
@EnableScheduling
@PropertySource({"classpath:para.properties"})
public class ScheduledTask {


    @Autowired
    private RedisService redisService;




    //@Scheduled(cron = "${sys.cron}") // 通过在方法上加@Scheduled注解，表明该方法是一个调度任务
    protected void getUrlData() throws IOException {
        JSONArray json = null;
        Header cookieHeader = new BasicHeader("Cookie", "rmbuser=true; txtUserName=qylxq; txtUserPwd=Qyfb6123974; systemuser=USERID=69&USERNM=qylxq&SYSTEMTP=1&USERROLE=11&ROLENAME=%e6%b0%b4%e5%ba%93%e6%99%ae%e9%80%9a%e7%94%a8%e6%88%b7&TUSERNM=%e5%ba%86%e5%85%83%e5%8e%bf%e5%85%b0%e6%ba%aa%e6%a1%a5&USERADCD=331126000000000");
        List<Header> headers = new ArrayList();
        headers.add(cookieHeader);
        HttpClientUtils httpClientUtils = new HttpClientUtils();
        Map map = new HashMap();
        LocalDateTime rightnow = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String rnow = rightnow.format(formatter);
        map.put("type","GetRealtimeWaterRainInfo");
        map.put("dscd","");
        map.put("stType","");
        Iterator iter = null;
        HttpResponse response = httpClientUtils.httpPostForm("http://61.153.63.202/xjxly/Service/WaterFallHandler.ashx",
                map, headers, null);
        if (200 == response.getStatusCode()) {
           // System.out.println(response.getBody());
            json = new JSONArray(response.getBody());
            for(int i=0;i<json.size();i++) {
                //System.out.println(json.getJSONObject(i).get("id"));
                if (json.getJSONObject(i).getStr("STTypeName").matches("水库站")) {
                    iter = ( json.getJSONObject(i)).entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        if (entry.getKey().toString().matches("ZRNow")||entry.getKey().toString().matches("TwentyFourhourRn"))
                        redisService.set(json.getJSONObject(i).getStr("Name") + "." + entry.getKey().toString()+ "." + rnow, entry.getValue().toString());

                    }

                }
            }
            //System.out.println(redisService.getRedisInfo().toString());
        }
    }

//    @Scheduled(fixedRate = 5000) ：上一次开始执行时间点之后5秒再执行
//    @Scheduled(fixedDelay = 5000) ：上一次执行完毕时间点之后5秒再执行
//    @Scheduled(initialDelay=1000, fixedRate=5000) ：第一次延迟1秒后执行，之后按fixedRate的规则每5秒执行一次
//    @Scheduled(cron=” /5 “) ：通过cron表达式定义规则，什么是cro表达式，自行搜索引擎。
//    在线cron表达式生成器：http://cron.qqe2.com/

    public void testJsonResult(String url) throws IOException {
        URL connect = isConnect(url);
        if (null != connect){
            String json = loadJson(connect.toString()).toString();
            System.out.println(json);
        }
    }
    public JSONObject getJsonResult(String url) throws IOException {
        JSONObject rut = null;
        URL connect = isConnect(url);
        if (null != connect){
            rut = loadJson(connect.toString());
           // String json = rut.toString();
            //System.out.println(rut.toString());
        }
        return  rut;
    }


    public synchronized URL isConnect(String urlStr) {
        URL url = null;
        HttpURLConnection connection = null;
        int counts = 0;
        if (urlStr == null || urlStr.length() <= 0) {
            return null;
        }
        while (counts < 5) {
            try {
                url = new URL(urlStr);
                connection = (HttpURLConnection) url.openConnection();
                int code = connection.getResponseCode();
                //System.out.println(counts +" = "+code);
               // if (code == 200) {
                //    System.out.println("URL可用！");
               // }
                break;
            } catch (Exception ex) {
                counts++;
                System.out.println("URL不可用，连接第 " + counts +"次");
                if (counts==5) System.out.println("网络异常，请检查！！！");
                url = null;
                continue;
            }
        }
        return url;
    }
    public static JSONObject loadJson(String url) {
        StringBuilder json = new StringBuilder();
        try {
            URL urlObject = new URL(url);
            URLConnection uc = urlObject.openConnection();
            // 设置为utf-8的编码 才不会中文乱码
            BufferedReader in = new BufferedReader(new InputStreamReader(uc
                    .getInputStream(), "utf-8"));
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(json.toString());
    }

    public  static String utf2gbk(String str) throws UnsupportedEncodingException {
        String t = "电动汽车";

        String utf8 = new String(t.getBytes( "UTF-8"));

        System.out.println(utf8);

        String unicode = new String(utf8.getBytes(),"UTF-8");

        System.out.println(unicode);

        String gbk = new String(unicode.getBytes("GBK"));

        System.out.println(gbk);

        return gbk;
    }

    public static void writeDT(String path,String title,String content){
        try {
            // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
            /* 写入Txt文件 */
            File writename = new File(path);// 相对路径，如果没有则要建立一个新的output。txt文件
            if(!writename.exists()){
                writename.mkdirs();
            }
            writename = new File(path+title);// 相对路径，如果没有则要建立一个新的output。txt文件
            writename.createNewFile(); // 创建新文件
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));


            //byte[] bytes = content.getBytes("utf-8");

            //byte[] bytes2 = new String(bytes, "utf-8").getBytes("gbk");

            //content=new String(bytes2, "gbk");


            out.write(content); // \r\n即为换行
            out.flush(); // 把缓存区内容压入文件
            out.close(); // 最后记得关闭文件

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
