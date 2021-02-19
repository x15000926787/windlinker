package cn.tellsea.quartz;


import cn.tellsea.Model.DataList;
import cn.tellsea.Model.Parameter;
import cn.tellsea.Model.TimeTask_Detial;
import cn.tellsea.component.SpringUtil;
import cn.tellsea.service.HelloService;
import cn.tellsea.service.RedisService;
import cn.tellsea.utils.JedisUtil;
import cn.tellsea.utils.anaUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
import java.util.Objects;

/**
 * [任务类]
 * @author xx
 * @date 2019-8-20 
 * @copyright copyright (c) 2018
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@PropertySource({"classpath:para.properties"})
public  class calcJob implements Job {




	private HelloService helloService;





	   private static final Logger logger = LogManager.getLogger(calcJob.class);




		public calcJob() throws ClassNotFoundException {
			helloService = (HelloService) SpringUtil.getBean(Class.forName("cn.tellsea.service.HelloService"));

			
		}
	/**
	 * 调频率
	 */
	public  void  ajust(int type,int vv) throws InterruptedException {

		//
		//log.info("调频进程启动");

		String plfk = null;



		Jedis tjedis= JedisUtil.getJedis();

		List<DataList> tarlist = helloService.selectDatabytype(type,16);
		//if (Objects.nonNull(tarlist)) log.info(tarlist.toString());
		//List<DevList> tarlist= helloService.selectdevbytype(type);
		Float val=0.0f;
		try {
			try {
				if (type == 2)
				plfk = (helloService.selectDatabyttype(3,14)).get(0).getKkey();
				else
					plfk = (helloService.selectDatabyttype(6,14)).get(0).getKkey();
				val=Float.parseFloat(tjedis.get(plfk+"_.value"));
			}catch (Exception e){
				log.info("redis 找不到 plfk {}",plfk);
				JedisUtil.returnJedis(tjedis);
				return;
			}

			log.info("设备类型id :{} 当前频率：{} ",type,val);
			if (vv>0)
			{
				val= (val+((JSONObject.toJavaObject((JSONObject)anaUtil.para_list.get("10"),Parameter.class)).getValue()));
			}else
				val= (val-((JSONObject.toJavaObject((JSONObject)anaUtil.para_list.get("10"),Parameter.class)).getValue()));
			log.info("目标频率：{} ",val);
			if (val>=35 && val<=50)
			{
				if (Objects.nonNull(tarlist)){
					for (DataList dl :tarlist)
					{
						log.info(dl.getTkey()+","+String.valueOf(val));

						JedisUtil.Cmd(dl.getTkey(),String.valueOf(val),((JSONObject.toJavaObject((JSONObject)anaUtil.para_list.get("15"),Parameter.class)).getValue()));

					}
				}

			}
			else
			{
				log.info("不在可设置范围内，跳出。");
			}
		}catch (Exception e){
			e.printStackTrace();
		}





		JedisUtil.returnJedis(tjedis);

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
		log.info("calc ajust...");
		HashMap<String,String> paraobj =(HashMap) (arg0.getJobDetail().getJobDataMap().get("taskdetial"));
		responses = new HashMap<String,Response<String>>(paraobj.size());

		if ((JSONObject.toJavaObject((JSONObject)anaUtil.para_list.get("15"),Parameter.class)).getValue()==0)
		{
			log.info("手自动模式为 0，跳出......");

			return;
		}

		try {
			jedis= JedisUtil.getJedis();

			p = jedis.pipelined();

			for(String key1 : paraobj.keySet()) {
               //log.info(key1);
				//log.info(paraobj.get(key1));
				responses.put(paraobj.get(key1)+"_.value", p.get(paraobj.get(key1)+"_.value"));

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



//coldoutwd,coldinwd,coldoutyl,coldinyl,cooloutwd,coolinwd,cooloutyl,coolinyl
			String tkey = null;
			try {
				tkey = paraobj.get("冷冻水出水温度")+"_.value";
				luaStr = responses.get(tkey).get();
				anaUtil.data[0] = Float.parseFloat(luaStr);
			}catch (Exception e)
			{
				//e.printStackTrace();
				anaUtil.data[0] = 0;
				log.info("redis 找不到 {}",tkey);
			}

			try {
				tkey = paraobj.get("冷冻水进水温度")+"_.value";
				luaStr = responses.get(tkey).get();
				anaUtil.data[1] = Float.parseFloat(luaStr);
			}catch (Exception e)
			{
				anaUtil.data[1] = 0;
				log.info("redis 找不到 {}",tkey);
			}
			try {
				tkey = paraobj.get("冷冻水出水压力")+"_.value";
				luaStr = responses.get(tkey).get();
				anaUtil.data[2] = Float.parseFloat(luaStr);
			}catch (Exception e)
			{
				anaUtil.data[2] = 0;
				log.info("redis 找不到 {}",tkey);
			}

			try {
				tkey = paraobj.get("冷冻水进水压力")+"_.value";
				luaStr = responses.get(tkey).get();
				anaUtil.data[3] = Float.parseFloat(luaStr);
			}catch (Exception e)
			{
				anaUtil.data[3] = 0;
				log.info("redis 找不到 {}",tkey);
			}
			try {
				tkey = paraobj.get("冷却水进水温度")+"_.value";
				luaStr = responses.get(tkey).get();
				anaUtil.data[4] = Float.parseFloat(luaStr);
			}catch (Exception e)
			{
				anaUtil.data[4] = 0;
				log.info("redis 找不到 {}",tkey);
			}

			try {
				tkey = paraobj.get("冷却水出水温度")+"_.value";
				luaStr = responses.get(tkey).get();
				anaUtil.data[5] = Float.parseFloat(luaStr);
			}catch (Exception e)
			{
				anaUtil.data[5] = 0;
				log.info("redis 找不到 {}",tkey);
			}
			try {
				tkey = paraobj.get("冷却水出水压力")+"_.value";
				luaStr = responses.get(tkey).get();
				anaUtil.data[6] = Float.parseFloat(luaStr);
			}catch (Exception e)
			{
				anaUtil.data[6] = 0;
				log.info("redis 找不到 {}",tkey);
			}

			try {
				tkey = paraobj.get("冷却水进水压力")+"_.value";
				luaStr = responses.get(tkey).get();
				anaUtil.data[7] = Float.parseFloat(luaStr);
			}catch (Exception e)
			{
				anaUtil.data[7] = 0;
				log.info("redis 找不到 {}",tkey);
			}
			/*for(String tkey : responses.keySet()) {

				{
					try {
						luaStr = responses.get(tkey).get().toString();
						log.warn("read redis: "+(tkey) +"  " + luaStr );

log.info(anautil.coldoutwd);
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
						e.printStackTrace();

					}
				}
			}*/
			for (int i=0;i<4;i++)
			{
				anaUtil.data_now[i] = anaUtil.data[i*2+1]-anaUtil.data[i*2];
			}
			//anautil.calcpl();


			try {
				log.info("冷却水温差 {},临界温差 {}", anaUtil.data_now[2], (JSONObject.toJavaObject((JSONObject) anaUtil.para_list.get("7"), Parameter.class)).getValue());
				if (anaUtil.data_now[2] > (JSONObject.toJavaObject((JSONObject) anaUtil.para_list.get("7"), Parameter.class)).getValue()) {

					ajust(3, 1);
				} else {

					ajust(3, -1);
				}
				log.info("冷冻水温差 {},临界温差 {}", anaUtil.data_now[0], (JSONObject.toJavaObject((JSONObject) anaUtil.para_list.get("6"), Parameter.class)).getValue());
				if (anaUtil.data_now[0] > (JSONObject.toJavaObject((JSONObject) anaUtil.para_list.get("6"), Parameter.class)).getValue()) {
					ajust(2, 1);
				} else {
					ajust(2, -1);
				}
			}catch (Exception e){
				e.printStackTrace();
			}

			try {
				p.close();
			}catch (IOException ee){}



			JedisUtil.returnJedis(jedis);

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




		logger.warn("执行定时任务:  调频监测 done!");

    }
    	
}
