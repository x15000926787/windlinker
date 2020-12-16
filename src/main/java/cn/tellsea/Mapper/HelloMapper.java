package cn.tellsea.Mapper;

import cn.tellsea.Model.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface HelloMapper {

    // 插入 并查询id 赋给传入的对象
    @Insert("INSERT INTO hello(key, value) VALUES(#{key}, #{value})")
    @SelectKey(statement = "SELECT seq id FROM sqlite_sequence WHERE (name = 'hello')", before = false, keyProperty = "id", resultType = int.class)
    int insert(HelloModel model);

    // 根据 ID 查询
    @Select("SELECT * FROM hello WHERE id=#{id}")
    HelloModel select(int id);

    // 查询运行时间最短设备

    /**
     * @param type
     * @return
     */
    @Select("select * from devlist as a where  a.type=#{type} and a.run=0 and a.error=0 and a.myerr=0 and a.valid=1 and a.status=0 order by a.runtime limit 1")
    DevList selectdev(int type);

    /**
     * @param type
     * @return
     */
    @Select("select * from devlist as a where  a.type=#{type} and a.run=1 and a.error=0 and a.myerr=0 and a.valid=1 and a.status=0 order by a.runtime desc limit 1")
    DevList selectdev4s(int type);
    /**
     *
     */
    @Select("select  * from devlist where id=#{id}")
    DevList selectdevbyid(int id);
    /**
     * 查询当前主机在运行台数
     * @return
     */
    @Select("SELECT * FROM devlist WHERE run=1 and type=1")
    List<DevList> selectdevruncount();

    /**
     * 查询process
     * @return
     */
    @Select("SELECT * FROM action_detial WHERE pid=#{p} and step=#{s}")
    ActionDetial selectnextprocess(int p, int s);

    /**
     * 查询step目标电磁阀设备，开机
     * @return
     */
    @Select("SELECT * FROM  devlist WHERE run=#{pid} and type=#{type}")
    DevList selectDcfDev(int pid, int type);



    /**
     * 查询控制键
     * @return
     */
    @Select("SELECT * FROM  datalist WHERE pid=#{pid} and type=#{type}")
    DataList selectControlKey(int pid, int type);


    /**
     * 查询步骤详情
     * @return
     */
    @Select("SELECT * FROM  action_detial WHERE pid=#{pid} and targettype=#{type}")
    ActionDetial selectAction(int pid, int type);

    // 查询全部
    @Select("SELECT * FROM hello")
    List<HelloModel> selectAll();

    // 查询全部设备
    @Select("SELECT * FROM devlist")
    List<DevList> selectAllDev();
    // 查询全部设备
    @Select("SELECT * FROM devlist where type=#{type}")
    List<DevList> selectDevbyType(int type);

    // 查询全部参数
    @Select("SELECT * FROM parameter")
    List<Parameter> selectAllPara();

    // 查询定时任务
    @Select("SELECT * FROM timetask")
    List<TimeTask> selectAllTimeTask();

    // 查询定时任务
    @Select("SELECT * FROM timetask where type=#{tp}")
    List<TimeTask> selectofTimeTask(int tp);

    // 查询定时任务详情
    @Select("SELECT * FROM timetask_detial where taskid=#{pid}")
    List<TimeTask_Detial> selectAllTimeTaskDetial(int pid);

    // 查询全部数据点
    @Select("SELECT * FROM datalist")
    List<DataList> selectAllData();
    // 根据类型查询数据点
    @Select("SELECT * FROM datalist where type=#{type} and pid in (select id from devlist where type=#{pid})")
    List<DataList> selectDatabytp(int pid, int type);
    /**
     * 更新 datalist_time
      */

    @Update("UPDATE datalist SET tstatus=#{tstatus},tcheck=#{tcheck} WHERE kkey=#{kkey}")
    int updateTime(DataList data);

    /**
     * 自判异常复位
     * @return
     */
    @Update("UPDATE devlist SET myerr=0")
    int resetmyerr();

    @Update("UPDATE devlist SET runtime=#{runtime} WHERE id=#{id}")
    int updateDevTime(DevList data);

    @Update("UPDATE devlist SET run=#{run} WHERE id=#{id}")
    int updateDevRun(DevList data);

    @Update("UPDATE devlist SET status=#{status} WHERE id=#{id}")
    int updateDevStatus(DevList data);

    @Update("UPDATE devlist SET error=#{error} WHERE id=#{id}")
    int updateDevErr(DevList data);

    /** 更新 value*/
    @Update("UPDATE hello SET value=#{value} WHERE id=#{id}")
    int updateValue(HelloModel model);

    /** 更新 设置设备自判状态为异常*/
    @Update("UPDATE devlist SET myerr=#{value} WHERE id=#{id}")
    int setDevErr(int value, int id);

    // 根据 ID 删除
    @Delete("DELETE FROM hello WHERE id=#{id}")
    int delete(Integer id);

}