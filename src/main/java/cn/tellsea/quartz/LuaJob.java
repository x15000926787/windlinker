package cn.tellsea.quartz;


import cn.tellsea.service.RedisService;
import cn.tellsea.utils.JedisUtil;
import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
/**
 * [任务类]
 * @author xx
 * @date 2019-8-20 
 * @copyright copyright (c) 2018
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public  class LuaJob implements Job {
	   
	    Jedis jedis = null;
	   //private static final log log = LogManager.getlog(LuaJob.class);
		@Autowired
		private RedisService redisService;
	   String s = null;
	      String vals = null;
	      String key = null;
	      String luaStr = null;
	     // String pId = FirstClass.projectId;
	  //	DBConnection dbcon=null;//
	  //	PreparedStatement pstmt=null;
		//ResultSet rs;
		
		
		
		public LuaJob() {
			//static
			//log.warn("Myjob.jpool:"+jpool.toString());
				//log.warn(taskarray);
			
		}
		
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
       //

		jedis= JedisUtil.getInstance().getJedis();
   	 
    	HashMap<String, Object> taskdetial = (HashMap<String, Object>) (arg0.getJobDetail().getJobDataMap().get("taskdetial"));
    	
    	String jobtype="2"; 
    	switch (jobtype) {
    	case "1":

    		break;
		case "2":
					String luaname =(String)taskdetial.get("luaname"); 
			    	log.info("执行定时脚本任务： "+luaname);
					try {
						 
						
							

				        	Reader r = new InputStreamReader(LuaJob.class.getResourceAsStream(luaname));
				            luaStr = CharStreams.toString(r);
						    jedis.eval(luaStr);
				            r.close();
				            r=null;
						   JedisUtil.getInstance().returnJedis(jedis);
				            jedis=null;
				            log.info("定时脚本任务执行完成。");
					  }
				     	catch (JedisConnectionException e) {
			             e.printStackTrace();
			           } 
				    	 catch (Exception e) {
				            e.printStackTrace();
				        }
					
			
			break;

		default:
			break;
		}
    	
    	//taskdetial.clear();
    	//taskdetial=null;

    }
    	
}
