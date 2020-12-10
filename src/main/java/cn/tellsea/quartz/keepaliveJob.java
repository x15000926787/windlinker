package cn.tellsea.quartz;


import cn.tellsea.Model.TimeTask_Detial;
import cn.tellsea.component.SpringUtil;
import cn.tellsea.service.HelloService;
import cn.tellsea.service.RedisService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.sql.SQLException;
import java.time.LocalDateTime;
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
public  class keepaliveJob implements Job {




	private RedisService redisService;


	String message = "J{" +"\"h\":{" +"\"rt\":\"skey\"" +"}," +"\"b\":{" +"\"dl\":{" +"\"ekey\":tval" +"}" +"}" +"}";
	   private static final Logger logger = LogManager.getLogger(keepaliveJob.class);
	   String s = null;
	   int i=0;
	      String vals = null;
	      String keys = null;
	      String luaStr = null;

	     // String pId = FirstClass.projectId;
	  //	DBConnection dbcon=null;//
	  //	PreparedStatement pstmt=null;
		//ResultSet rs;



		public keepaliveJob() throws ClassNotFoundException {

			redisService = (RedisService) SpringUtil.getBean(Class.forName("cn.tellsea.service.RedisService"));
			
		}
	/*
	 * 读取定时任务清单
	 *
	 */
	public Map<String, String> get_timetask_detial(int pid) throws SQLException {

		List<TimeTask_Detial> tasktype1 = null;//new ArrayList<Map<String, Object>>();


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

		try {

			redisService.get("windlinker");//, LocalDateTime.now().toString());

		}

		catch (JedisConnectionException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    	
    	//tmap.clear();
    	//tmap=null;
		//logger.warn("执行定时任务:  keepRedisAlive done!");

    }
    	
}
