package cn.tellsea.quartz;


import cn.tellsea.Model.TimeTask_Detial;
import cn.tellsea.component.FirstClass;
import cn.tellsea.component.SpringUtil;
import cn.tellsea.service.HelloService;
import cn.tellsea.service.RedisService;
import cn.tellsea.utils.JedisUtil;
import cn.tellsea.utils.anaUtil;
import cn.tellsea.utils.jdbcUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.*;

/**
 * [任务类]
 * @author xx
 * @date 2019-8-20 
 * @copyright copyright (c) 2018
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public  class QuartzJob implements Job {


	private HelloService helloService;

	private RedisService redisService;


	String message = "J{" +"\"h\":{" +"\"rt\":\"skey\"" +"}," +"\"b\":{" +"\"dl\":{" +"\"ekey\":tval" +"}" +"}" +"}";
	   private static final Logger logger = LogManager.getLogger(QuartzJob.class);
	   String s = null;
	   int i=0;
	      String vals = null;
	      String keys = null;
	      String luaStr = null;

	     // String pId = FirstClass.projectId;
	  //	DBConnection dbcon=null;//
	  //	PreparedStatement pstmt=null;
		//ResultSet rs;
		
		
		
		public QuartzJob() throws ClassNotFoundException {
			helloService = (HelloService) SpringUtil.getBean(Class.forName("cn.tellsea.service.HelloService"));
			redisService = (RedisService) SpringUtil.getBean(Class.forName("cn.tellsea.service.RedisService"));
			
		}
	/*
	 * 读取定时任务清单
	 *
	 */
	public Map<String, String> get_timetask_detial(int pid) throws SQLException {

		List<TimeTask_Detial> tasktype1 = null;//new ArrayList<Map<String, Object>>();

		tasktype1= helloService.selectAllTimeTaskDetial(pid);
		HashMap<String, String> tmap = new HashMap<String, String>();

		int size=tasktype1.size() ;
		if (size> 0)
		{

			for (int i = 0; i<size; i++)
			{

				TimeTask_Detial listData = tasktype1.get(i);
				tmap.put(listData.getKkey(),listData.getVal());

			}


		}
        //log.info(tmap.toString());
		return tmap;
	}
    @SneakyThrows
	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
       //


		//mqttService = (MqttService) (arg0.getJobDetail().getJobDataMap().get("mqtt"));

    	HashMap<String, Integer> tidmap = (HashMap<String, Integer>) (arg0.getJobDetail().getJobDataMap().get("taskdetial"));
		//logger.warn(tidmap.get("vv").toString());
		HashMap<String, String> tmap = (HashMap<String, String>) get_timetask_detial(tidmap.get("vv"));
    	/*Map tmap=new HashMap<String, String>();
    	
    	tmap=(Map<String, String>) (arg0.getJobDetail().getJobDataMap().get("taskdetial"));
    	*/
		try {



			//                                                                                                                                                    if (Objects.nonNull(redisService)) log.info("dddd");
			for (Object key:tmap.keySet()) {
				vals = tmap.get((String) key);
				redisService.set(key+"_.value",vals);
				redisService.set(key+"_.status","1");
				logger.warn("执行定时任务:  "+key+"  : "+vals);
			}




		}

		catch (JedisConnectionException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    	
    	//tmap.clear();
    	//tmap=null;
		logger.warn("执行定时任务:  QuartzJob done!");

    }
    	
}
