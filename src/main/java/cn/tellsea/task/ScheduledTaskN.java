package cn.tellsea.task;


import cn.hutool.json.JSONObject;
import cn.tellsea.service.RedisService;
import cn.tellsea.utils.HttpClientUtils;
import cn.tellsea.utils.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@EnableScheduling
@PropertySource({"classpath:para.properties"})
public class ScheduledTaskN {



    @Autowired
    private RedisService redisService;




   //@Scheduled(cron = "${sys.cron}") // 通过在方法上加@Scheduled注解，表明该方法是一个调度任务
    protected void getUrlData() throws IOException {
        Header cookieHeader = new BasicHeader("Cookie", "_uab_collina=160405108322170915229246");
        String jsess = null;
        List<Header> headers = new ArrayList();
        headers.add(cookieHeader);
        HttpClientUtils httpClientUtils = new HttpClientUtils();
        Map map = new HashMap();
        LocalDateTime rightnow = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String rnow = rightnow.format(formatter);
        map.put("page","1");
        map.put("pageSize","10");
        //map.put("stType","");
        HttpResponse response = httpClientUtils.httpPostForm("http://www.hclink.cn",
                map, headers, null);
       // System.out.println(new StringBuilder().append("ddd:").append(response.getStatusCode()).toString());
//        if (200 == response.getStatusCode()) {
        response.getBody();


        // FileUtil.writerFile(D.getTime()+"", (response.getHeaders().toString()));
        jsess = response.getHeaders()[4].getValue().split(";")[0].split("=")[1];
       // System.out.println(response.getHeaders()[4].getValue().split(";")[0].split("=")[1]);


        cookieHeader = new BasicHeader("Cookie", "_uab_collina=160405108322170915229246; JSESSIONID="+jsess);

        // List<Header> headers = new ArrayList();
        headers.clear();
        headers.add(cookieHeader);

        //Map map = new HashMap();
        map.clear();
        map.put("loginAccount","13967059551");
        map.put("loginPassword","650551");
        map.put("companyUserId","232");
        response = httpClientUtils.httpPostForm("http://www.hclink.cn/user/login.htm",
                map, headers, null);
       // System.out.println(new StringBuilder().append("ddd:").append(response.getStatusCode()).toString());
//        if (200 == response.getStatusCode()) { loginAccount=13967059551&loginPassword=650551&companyUserId=232
        response.getBody();


        // FileUtil.writerFile(D.getTime()+"", (response.getHeaders().toString()));
       // System.out.println(response);


        headers.clear();
        headers.add(cookieHeader);

        //Map map = new HashMap();
        map.clear();
        map.put("page","1");
        map.put("pageSize","10");
        response = httpClientUtils.httpPostForm("http://www.hclink.cn/user/searchSensorsPage.htm",
                map, headers, null);
        //System.out.println(new StringBuilder().append("ddd:").append(response.getStatusCode()).toString());
//        if (200 == response.getStatusCode()) { loginAccount=13967059551&loginPassword=650551&companyUserId=232
        response.getBody();


        // FileUtil.writerFile(D.getTime()+"", (response.getHeaders().toString()));
        System.out.println(response.getBody());
        Document document = Jsoup.parse(response.getBody().toString());
        //像js一样，通过标签获取title
        System.out.println(document.getElementById("s_104261").text());
        System.out.println(document.getElementById("s_191418").text());
        redisService.set("银河水位.水位-1",document.getElementById("s_104261").text());
        redisService.set("万里源水位.雨量[天]",document.getElementById("s_191418").text());
        //像js一样，通过id 获取元素对象
        //Element postList = document.getElementById("post_list");
//        }else
//        {
//
//            System.out.println(  response.getStatusCode());
//            throw new RuntimeException("SESSIONID已过期");
//        }
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
