package cn.tellsea.quartz;


import cn.tellsea.Model.Parameter;
import cn.tellsea.Model.TimeTask_Detial;
import cn.tellsea.component.SpringUtil;
import cn.tellsea.service.HelloService;
import cn.tellsea.service.RedisService;
import cn.tellsea.utils.JedisUtil;
import cn.tellsea.utils.anaUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisConnectionException;
import cn.tellsea.utils.anaUtil;
import java.io.IOException;
import java.sql.SQLException;
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
public  class calcJob implements Job {

	@Autowired
	private anaUtil anautil;
	private HelloService helloService;

	private RedisService redisService;


	String message = "J{" +"\"h\":{" +"\"rt\":\"skey\"" +"}," +"\"b\":{" +"\"dl\":{" +"\"ekey\":tval" +"}" +"}" +"}";
	   private static final Logger logger = LogManager.getLogger(calcJob.class);
	   String s = null;
	   int i=0;
	      String vals = null;
	      String keys = null;
	      String luaStr = null;

	     // String pId = FirstClass.projectId;
	  //	DBConnection dbcon=null;//
	  //	PreparedStatement pstmt=null;
		//ResultSet rs;



		public calcJob() throws ClassNotFoundException {
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
						if ((anautil.coldinyl.matches(tkey))){
							anaUtil.data[2] = Float.parseFloat(luaStr);
						}
						if ((anautil.coldoutyl.matches(tkey))){
							anaUtil.data[3] = Float.parseFloat(luaStr);
						}
						if ((anautil.coolinwd.matches(tkey))){
							anaUtil.data[4] = Float.parseFloat(luaStr);
						}
						if ((anautil.cooloutwd.matches(tkey))){
							anaUtil.data[5] = Float.parseFloat(luaStr);
						}
						if ((anautil.coolinyl.matches(tkey))){
							anaUtil.data[6] = Float.parseFloat(luaStr);
						}
						if ((anautil.coldoutyl.matches(tkey))){
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
			anautil.calcpl();

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




		logger.warn("执行定时任务:  QuartzJob done!");

    }
    	
}
