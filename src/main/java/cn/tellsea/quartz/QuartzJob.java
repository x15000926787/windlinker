package cn.tellsea.quartz;


import cn.tellsea.component.FirstClass;
import cn.tellsea.service.RedisService;
import cn.tellsea.utils.JedisUtil;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Autowired
	private jdbcUtil jdbcutil;
	@Autowired
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
		
		
		
		public QuartzJob() {
			//static
			//logger.warn("Myjob.jpool:"+jpool.toString());
				//logger.warn(taskarray);
			
		}
	/*
	 * 读取定时任务清单
	 *
	 */
	public Map<String, String> get_timetask_detial(String tid) throws SQLException {

		List<Map<String, Object>> tasktype1 = new ArrayList<Map<String, Object>>();
		String sql = "SELECT a.kkey kkey,a.val val from timetask_detail a,do_info b where a.kkey=b.kkey and b.timeTaskLock=1 and  a.taskid="+tid;
		tasktype1= jdbcutil.findModeResult(sql,null);
		HashMap<String, String> tmap = new HashMap<String, String>();

		int size=tasktype1.size() ;
		if (size> 0)
		{

			for (int i = 0; i<size; i++)
			{

				Map listData = (Map)tasktype1.get(i);
				tmap.put((listData.get("kkey").toString()),listData.get("val").toString());

			}


		}

		return tmap;
	}
    @SneakyThrows
	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
       //


		//mqttService = (MqttService) (arg0.getJobDetail().getJobDataMap().get("mqtt"));

    	HashMap<String, String> tidmap = (HashMap<String, String>) (arg0.getJobDetail().getJobDataMap().get("taskdetial"));
		//logger.warn(tidmap.get("vv").toString());
		HashMap<String, String> tmap = (HashMap<String, String>) get_timetask_detial(tidmap.get("vv").toString());
    	/*Map tmap=new HashMap<String, String>();
    	
    	tmap=(Map<String, String>) (arg0.getJobDetail().getJobDataMap().get("taskdetial"));
    	*/
		try {




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
