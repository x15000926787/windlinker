package cn.tellsea.task;

import cn.tellsea.Model.DataList;
import cn.tellsea.Model.DevList;
import cn.tellsea.Model.Parameter;
import cn.tellsea.utils.JedisUtil;
import cn.tellsea.utils.anaUtil;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.*;

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


	public DataList dat;
	public DevList dev;


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
	  public void prepare()
	  {



	  }

    @Override
    public void run()  {
	  	String luaStr = null;
		Jedis jedis = null;
		Map<String, Response<String>> responses = null;
		Pipeline p = null;
		log.info("calc ...");

		responses = new HashMap<String,Response<String>>(anautil.objana_v.keySet().size());



		try {
			jedis= JedisUtil.getInstance().getJedis();

			p = jedis.pipelined();
			for(String key1 : anautil.tkeys) {

				responses.put(key1, p.get(key1+"_.value"));

			}

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

			for(String tkey : responses.keySet()) {

				{
					try {
						luaStr = responses.get(tkey).get().toString();
						log.warn("read redis: "+(tkey) +"  " + luaStr );


						if ((anautil.coldoutwd.matches(tkey))){
							anaUtil.data[0] = Float.parseFloat(luaStr);
						}
						if ((anautil.coldinwd.matches(tkey))){
							anaUtil.data[1] = Float.parseFloat(luaStr);
						}
						if ((anautil.coldoutyl.matches(tkey))){
							anaUtil.data[2] = Float.parseFloat(luaStr);
						}
						if ((anautil.coldinyl.matches(tkey))){
							anaUtil.data[3] = Float.parseFloat(luaStr);
						}
						if ((anautil.cooloutwd.matches(tkey))){
							anaUtil.data[4] = Float.parseFloat(luaStr);
						}
						if ((anautil.coolinwd.matches(tkey))){
							anaUtil.data[5] = Float.parseFloat(luaStr);
						}
						if ((anautil.cooloutyl.matches(tkey))){
							anaUtil.data[6] = Float.parseFloat(luaStr);
						}
						if ((anautil.coldinyl.matches(tkey))){
							anaUtil.data[7] = Float.parseFloat(luaStr);
						}




					} catch (Exception e) {

					}
				}
			}
			for (int i=0;i<4;i++)
			{
				anaUtil.data_now[i] = anaUtil.data[i*2+1]-anaUtil.data[i*2];
			}
			//冻温，冻压，却温，却压
			if ((anaUtil.data_now[1]-anaUtil.data_before[1])>((Parameter)anautil.para_list.get(8)).getValue())
			{
				anautil.ajust(2,-1);
			}
			//冻温，冻压，却温，却压
			if ((anaUtil.data_now[0]-anaUtil.data_before[0])>((Parameter)anautil.para_list.get(6)).getValue())
			{
				anautil.ajust(2,1);
			}

			try {
				p.close();
			}catch (IOException ee){}



			JedisUtil.getInstance().returnJedis(jedis);

			responses.clear();


			luaStr = null;

			responses = null;
			p = null;//jedis.pipelined();


		}

		catch (JedisConnectionException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}








	}
    	
}
