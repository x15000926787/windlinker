package cn.tellsea.quartz;


import cn.tellsea.Model.DataList;
import cn.tellsea.Model.DevList;
import cn.tellsea.Model.Parameter;

import cn.tellsea.component.SpringUtil;
import cn.tellsea.service.HelloService;

import cn.tellsea.utils.anaUtil;
import cn.tellsea.utils.SpringContextUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.quartz.*;
import org.springframework.context.annotation.PropertySource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
@PropertySource({"classpath:para.properties"})
public  class TimeJob implements Job {
		private HelloService helloService;
		anaUtil anautil= (anaUtil) SpringContextUtil.getBean(Class.forName("cn.tellsea.utils.anaUtil"));
		public TimeJob() throws ClassNotFoundException {
			helloService = (HelloService) SpringUtil.getBean(Class.forName("cn.tellsea.service.HelloService"));
		}


    @SneakyThrows
	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
		LocalDateTime rightnow = LocalDateTime.now();
		DataList dt = new DataList();
		DevList dv = new DevList();
		double tot1,tot2,dis;
		long rnow = rightnow.toInstant(ZoneOffset.of("+8")).toEpochMilli();
		for(String str:anaUtil.objana_v.keySet()){

			dt = JSONObject.toJavaObject((JSONObject)anaUtil.objana_v.get(str),DataList.class);//(DataList) ;
			//
			if (dt.getTvalid()==1){
				//log.info(anaUtil.objana_v.get(str).toString());
				dv =JSONObject.toJavaObject((JSONObject)anaUtil.dev_list.get(dt.getPid()),DevList.class);// (DevList) ;
				//if (Objects.nonNull(dv)) log.info(dv.toString());

				if (dt.getTstatus()==0){
					//没在计时
					if (dv.getRun()==1){
						//在运行,开始计时
						((JSONObject) anaUtil.objana_v.get(dt.getId())).put("tstatus", 1);
						((JSONObject) anaUtil.objana_v.get(dt.getId())).put("tcheck", rnow);
						dt.setTstatus(1);
						dt.setTcheck(rnow);
						helloService.updateTime(dt);
					}
				}else{
				//在计时
					LocalDateTime to2 = new Date( Long.parseLong(String.valueOf(dt.getTcheck()))).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();//LocalDateTime.ofEpochSecond((long)map.get("tcheck"),0, ZoneOffset.ofHours(8));//LocalDateTime.parse(.toString(), formatter);

					//log.info("{} --- {}",to2.toString(),rightnow);
					Duration duration = Duration.between(to2, rightnow);

					//相差的分钟数
					long minutes = duration.toMinutes();
					//log.info(String.valueOf(minutes));

					double tot = minutes / 60.00f + dv.getRuntime();
					if (dv.getRun()==0){
						//没在运行，停止计时
						((JSONObject) anaUtil.objana_v.get(dt.getId())).put("tstatus", 0);
						dt.setTstatus(0);
						log.warn("{} 停止计时",dv.getDevname());
					}
					((JSONObject) anaUtil.objana_v.get(dt.getId())).put("tcheck", rnow);
					dt.setTcheck(rnow);
					((JSONObject) anaUtil.dev_list.get(dv.getId())).put("runtime",  tot);
					dv.setRuntime(tot);
					helloService.updateTime(dt);
					helloService.updateDevTime(dv);
					log.warn("{} 此次运行时长 {} minutes",dv.getDevname(),minutes);

				}

			}
		}

		int cnt = helloService.selectdevruncount().size();
		if (cnt==1){
			//当前只有一台在运行

			tot1 = Double.parseDouble(((JSONObject)anaUtil.dev_list.get("1")).get("runtime").toString());
			tot2 = Double.parseDouble(((JSONObject)anaUtil.dev_list.get("2")).get("runtime").toString());


			if((int)((JSONObject)anaUtil.dev_list.get("1")).get("run")==1){
				dis = tot1 - tot2;
			}else {
				dis = tot2 - tot1;
			}
			if (dis > (JSONObject.toJavaObject((JSONObject) anaUtil.para_list.get("14"), Parameter.class)).getValue()){
				anautil.stopZJ();
				anautil.startZJ();
			}
		}

		}
}
