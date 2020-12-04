package cn.tellsea.quartz;


import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

/**
 * Quartz定时任务util
 * 
 * @author xX
 *
 */
public class QuartzManager {
 
	private static SchedulerFactory gSchedulerFactory = new StdSchedulerFactory(); // 创建一个SchedulerFactory工厂实例
	private static String JOB_GROUP_NAME = "SHCS_JOBGROUP_NAME"; // 任务组
	private static String TRIGGER_GROUP_NAME = "SHCS_TRIGGERGROUP_NAME"; // 触发器组

	/**
	 * 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
	 * 
	 * @param jobName
	 *            任务名
	 * @param cls
	 *            任务
	 * @param time
	 *            时间设置，参考quartz说明文档
	 */
	public static void addJob(String jobName, Class<? extends Job> cls, String time) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler(); // 通过SchedulerFactory构建Scheduler对象
			JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(jobName, JOB_GROUP_NAME).build(); // 用于描叙Job实现类及其他的一些静态信息，构建一个作业实例
			CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger() // 创建一个新的TriggerBuilder来规范一个触发器
					.withIdentity(jobName, TRIGGER_GROUP_NAME) // 给触发器起一个名字和组名
					.withSchedule(CronScheduleBuilder.cronSchedule(time)).build();
			sched.scheduleJob(jobDetail, trigger);
			if (!sched.isShutdown()) {
				sched.start(); // 启动
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
 
	/**
	 * 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名 （带参数）
	 * 
	 * @param jobName
	 *            任务名
	 * @param cls
	 *            任务
	 * @param time
	 *            时间设置，参考quartz说明文档
	 */
	public static void addJob(String jobName, Class<? extends Job> cls, String time, Object  parameter) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler(); // 通过SchedulerFactory构建Scheduler对象
			JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(jobName, JOB_GROUP_NAME).build(); // 用于描叙Job实现类及其他的一些静态信息，构建一个作业实例
			jobDetail.getJobDataMap().put("taskdetial", parameter); // 传参数
			CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger() // 创建一个新的TriggerBuilder来规范一个触发器
					.withIdentity(jobName, TRIGGER_GROUP_NAME) // 给触发器起一个名字和组名
					.withSchedule(CronScheduleBuilder.cronSchedule(time)).build();
			sched.scheduleJob(jobDetail, trigger);
			if (!sched.isShutdown()) {
				sched.start(); // 启动
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * 添加一个定时任务
	 * 
	 * @param jobName
	 *            任务名
	 * @param jobGroupName
	 *            任务组名
	 * @param triggerName
	 *            触发器名
	 * @param triggerGroupName
	 *            触发器组名
	 * @param jobClass
	 *            任务
	 * @param time
	 *            时间设置，参考quartz说明文档
	 */
	public static void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
			Class<? extends Job> jobClass, String time) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();// 任务名，任务组，任务执行类
			CronTrigger trigger = (CronTrigger) TriggerBuilder // 触发器
					.newTrigger().withIdentity(triggerName, triggerGroupName)
					.withSchedule(CronScheduleBuilder.cronSchedule(time)).build();
			sched.scheduleJob(jobDetail, trigger);
			if (!sched.isShutdown()) {
				sched.start(); // 启动
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 添加一个延时任务 （带参数），可设置触发的延时时间，时间间隔，循环次数，当循环次数为0时，当作延时任务
	 *
	 * @param jobName
	 *            任务名
	 * @param jobGroupName
	 *            任务组名
	 * @param triggerName
	 *            触发器名
	 * @param triggerGroupName
	 *            触发器组名
	 * @param jobClass
	 *            任务
	 * @param delaySecend
	 *   	      延时时间 单位：秒
	 * @param intervalSecend
	 * 	   	      间隔时间 单位：秒
	 * @param repeatCount
	 * 	          执行次数 0：只初始执行一次；-1 ：循环到死
	 */
	public static void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
							  Class<? extends Job> jobClass,  Object parameter, long delaySecend,int intervalSecend ,int repeatCount ) {
		SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
		simpleScheduleBuilder.withIntervalInMilliseconds(intervalSecend*1000).withRepeatCount(repeatCount);
		simpleScheduleBuilder.withMisfireHandlingInstructionFireNow(); // 以当前时间为触发频率立即触发执行
		//延迟启动任务时间
		long tmpTime = System.currentTimeMillis() + delaySecend;
		// 启动时间

		Date statTime = new Date(tmpTime);
		System.out.println("QuartzManager 136:"+statTime.toString());
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) jobClass).withIdentity(jobName, jobGroupName)
					.build();// 任务名，任务组，任务执行类
			// 传参数
			jobDetail.getJobDataMap().put("taskdetial", parameter);
			// 触发器
			Trigger trigger =  TriggerBuilder
					.newTrigger().withIdentity(triggerName, triggerGroupName)
					//默认当前时间启动
					.startAt(statTime)
					//两秒执行一次
					.withSchedule(simpleScheduleBuilder)
			        .build();

			sched.scheduleJob(jobDetail, trigger);
			if (!sched.isShutdown()) {
				sched.start(); // 启动
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}



	/**
	 * 添加一个定时任务 （带参数）
	 * 
	 * @param jobName
	 *            任务名
	 * @param jobGroupName
	 *            任务组名
	 * @param triggerName
	 *            触发器名
	 * @param triggerGroupName
	 *            触发器组名
	 * @param jobClass
	 *            任务
	 * @param time
	 *            时间设置，参考quartz说明文档
	 */
	public static void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
			Class<? extends Job> jobClass, String time, Object parameter) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) jobClass).withIdentity(jobName, jobGroupName)
					.build();// 任务名，任务组，任务执行类
			jobDetail.getJobDataMap().put("taskdetial", parameter); // 传参数
			CronTrigger trigger = (CronTrigger) TriggerBuilder // 触发器
					.newTrigger().withIdentity(triggerName, triggerGroupName)
					.withSchedule(CronScheduleBuilder.cronSchedule(time)).build();
			sched.scheduleJob(jobDetail, trigger);
			if (!sched.isShutdown()) {
				sched.start(); // 启动
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
 
	/**
	 * 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名)
	 * 
	 * @param jobName
	 *            任务名
	 * @param time
	 *            新的时间设置
	 */
	public static void modifyJobTime(String jobName, String time) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler(); // 通过SchedulerFactory构建Scheduler对象
			TriggerKey triggerKey = TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME); // 通过触发器名和组名获取TriggerKey
			CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey); // 通过TriggerKey获取CronTrigger
			if (trigger == null) {
				return;
			}
			String oldTime = trigger.getCronExpression();
			if (!oldTime.equalsIgnoreCase(time)) {
				JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP_NAME); // 通过任务名和组名获取JobKey
				JobDetail jobDetail = sched.getJobDetail(jobKey);
				Class<? extends Job> objJobClass = jobDetail.getJobClass();
				removeJob(jobName);
				addJob(jobName, objJobClass, time);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
 
	/**
	 * 修改一个任务的触发时间
	 * 
	 * @param triggerName
	 *            任务名称
	 * @param triggerGroupName
	 *            传过来的任务名称
	 * @param time
	 *            更新后的时间规则
	 */
	public static void modifyJobTime(String triggerName, String triggerGroupName, String time) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler(); // 通过SchedulerFactory构建Scheduler对象
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName); // 通过触发器名和组名获取TriggerKey
			CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey); // 通过TriggerKey获取CronTrigger
			if (trigger == null)
				return;
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(trigger.getCronExpression());
			String oldTime = trigger.getCronExpression();
			if (!oldTime.equalsIgnoreCase(time)) {
				trigger = (CronTrigger) trigger.getTriggerBuilder() // 重新构建trigger
						.withIdentity(triggerKey).withSchedule(scheduleBuilder)
						.withSchedule(CronScheduleBuilder.cronSchedule(time)).build();
				sched.rescheduleJob(triggerKey, trigger); // 按新的trigger重新设置job执行
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
 
	/**
	 * 移除一个任务(使用默认的任务组名，触发器名，触发器组名)
	 * 
	 * @param jobName
	 *            任务名称
	 */
	public static void removeJob(String jobName) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME); // 通过触发器名和组名获取TriggerKey
			JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP_NAME); // 通过任务名和组名获取JobKey
			sched.pauseTrigger(triggerKey); // 停止触发器
			sched.unscheduleJob(triggerKey);// 移除触发器
			sched.deleteJob(jobKey); // 删除任务
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
 
	/**
	 * 移除一个任务
	 * 
	 * @param jobName
	 *            任务名
	 * @param jobGroupName
	 *            任务组名
	 * @param triggerName
	 *            触发器名
	 * @param triggerGroupName
	 *            触发器组名
	 */
	public static void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName); // 通过触发器名和组名获取TriggerKey
			JobKey jobKey = JobKey.jobKey(jobName, jobGroupName); // 通过任务名和组名获取JobKey
			sched.pauseTrigger(triggerKey); // 停止触发器
			sched.unscheduleJob(triggerKey);// 移除触发器
			sched.deleteJob(jobKey); // 删除任务
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
 
	/**
	 * 启动所有定时任务
	 */
	public static void startJobs() {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			sched.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
 
	/**
	 * 关闭所有定时任务
	 */
	public static void shutdownJobs() {

		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			if (!sched.isShutdown()) {
				sched.shutdown();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
 
}