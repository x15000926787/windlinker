package cn.tellsea.task;

import cn.tellsea.utils.JedisUtil;
import cn.tellsea.utils.anaUtil;
import com.alibaba.fastjson.JSONObject;

import com.google.gson.Gson;


import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * [任务类]
 * @author xx
 * @date 2019-8-20 
 * @copyright copyright (c) 2018
 * 计算zjdy.txt
 * 计算电量增量
 * 发送 实时数据 and 定时锁状态 到前端
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Slf4j
@Component
public  class UpdateDataJob implements Runnable {
	   
	//public static ChatSocket ckt = new ChatSocket();
	  // RedisUtil jpool = new RedisUtil();
	
	


	   // public static final Logger logger = LogManager.getLogger(UpdateDataJob.class);




	@Autowired
	private anaUtil anautil;
		
		
		public UpdateDataJob()
		{

			//scriptEngine = scriptEngineManager.getEngineByName("nashorn");
		}
		 /**
	     * 检查遥测越限
	     * @param 
	     * @throws ScriptException
		  * 已经弃用
	     */  
	   /*
	   public int checkupdown(String val,String down,String up,int stat) {
		   int st=-2;
		  // logger.warn("check updown : " +  val+":"+  updown+":"+  stat );
		   String ud[]=new String[2];
		   ud[0] = down; 
		   ud[1] = up;
		   ud[1]="254";
		   ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");
	       try
	       {
	    	   if ((boolean) scriptEngine.eval(val+"<"+ud[0]) && stat>-1)
		    	   st = -1;
		       if ((boolean) scriptEngine.eval(val+">"+ud[1]) && stat<1)
		    	   st = 1; 
		       if ((boolean) scriptEngine.eval(val+">="+ud[0])&&(boolean) scriptEngine.eval(val+"<="+ud[1]) && stat!=0)
		    	   st = 0; 
   
	       }catch (ScriptException e) {
	        	logger.error("check updown err: " +  e.toString() );
	            e.printStackTrace();
	        }

		return st;
		
	} */
	   /**
	     * 检查遥信变位
	     * @param 
	     * @throws ScriptException
		* 已经弃用
	     */  
	   /*
	   public int checkevt(String val,int stat) {
		   int st = 0;
		   if (stat!=-1)
		   {
		   ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");
	       try
	       {
	    	  
		       if ((boolean) scriptEngine.eval(val+"!="+stat) )
		    	   st = 1; 
		      
  
	       }catch (ScriptException e) {
	        	logger.error("check updown err: " +  e.toString() );
	            e.printStackTrace();
	        }
		   }
		return st;
		
	} */
	    /*
	     * 插入记录
	     *

	  public void add_red(String sql) throws SQLException
	  {

		  DBConnection dbcon=new DBConnection();

		 // logger.warn(sql);
		  try{
			  dbcon.setPreparedStatement(sql);
			  int t=dbcon.getexecuteUpdate();
	          //dbcon.getClose();
			}catch(Exception e){
				logger.error("出错了"+e.toString());
			}
		  dbcon.getClose();
	  }*/
	    /*
	     * 读取定时锁状态
	     *
		 */
	  public void get_time_lock()
	  {

		 /* List<Map<String, Object>> tasktype1 = new ArrayList<Map<String, Object>>();
		  String sql = "SELECT * from do_info where timeTaskLock is not null";
		  tasktype1= dbcon.queryforList(sql);


		  int size=tasktype1.size() ;
		  if (size> 0)
		  {

			  for (int i = 0; i<size; i++)
			  {

				  Map listData = (Map)tasktype1.get(i);
				  result.put((listData.get("kkey").toString()+".lock"),listData.get("timeTaskLock").toString());
				  kk = listData.get("kkey").toString().split(",")[0];
				  if (umap.containsKey(kk))
				  {
				  	((Map)umap.get(kk)).put((listData.get("kkey").toString()+".lock"),listData.get("timeTaskLock").toString());
				  }
				  else {
					  Map<String,String> nvl = new HashMap<String,String>();
					  nvl.put((listData.get("kkey").toString()+".lock"),listData.get("timeTaskLock").toString());
				  	 umap.put(kk,nvl);
				  }
			  }


		  }*/

	  }

    @Override
    public void run()  {

		 String s = null;

		  String luaStr = null;



		 String pattern=null;




		Jedis jedis = null;

		// DBConnection dbcon=null;//new DBConnection();
		  Map<String,String> result = null;
		 Map umap=null;
		  Map<String,Response<String>> responses = null;
		Pipeline p = null;//jedis.pipelined();
		//Set<String> sinter_yc= null;//anaobj.keySet();


		pattern=".*_.value*";

		//HashMap<String,String>  map = new HashMap<String,String>();


		//logger.error("ask data...");

		   responses = new HashMap<String,Response<String>>(anautil.objana_v.keySet().size());


	      
			try {
				 jedis= JedisUtil.getInstance().getJedis();
				
				 p = jedis.pipelined();
				


		       

		        for(String key1 : anautil.objana_v.keySet()) {

		         responses.put(key1, p.get(key1));

		        }
				//logger.error("ask data 1...");
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
				//添加实时ai,di 到json字符串中
				//logger.warn(responses);
				for(String k : responses.keySet()) {
                    //logger.warn(k);
					//if (Pattern.matches(pattern, k))
					{
						try {

							log.warn("lua err: "+(k) +  responses.get(k).get().toString() );
							luaStr = responses.get(k).get().toString();
 							anautil.objana_v.getJSONObject(k).put("value",luaStr);

						} catch (Exception e) {

						}
					}
				}

				try {
					p.close();
				}catch (IOException ee){}



				JedisUtil.getInstance().returnJedis(jedis);
				// dbcon.getClose();
		         responses.clear();





				// RedisUtil jpool = new RedisUtil();



				 s = null;

				 luaStr = null;



				 pattern=null;



				 umap=null;
				 responses = null;
				 p = null;//jedis.pipelined();
				 //anaobj =null;
				// sinter_yc= null;//anaobj.keySet();

			 } 
			
			catch (JedisConnectionException e) {
	            e.printStackTrace();
	        } 
			 catch (Exception e) {
		            e.printStackTrace();
			}

		//logger.warn("end update_data.");

    }
    	
}
