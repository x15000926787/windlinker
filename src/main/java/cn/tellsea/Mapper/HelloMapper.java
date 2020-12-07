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
    @Select("select  *\n" +
            "     from (select * from devlist as a\n" +
            "                                 where  runtime=(select min(b.runtime)\n" +
            "                                                   from devlist as b\n" +
            "                                                    where a.type = b.type and a.run=b.run and a.status=b.status and a.error=b.error and a.type=#{type} and a.run=0 and a.error=0 and a.status=0\n" +
            "                                                   )\n" +
            "           ) as a\n" +
            "      group by type")
    DevList selectdev(int type);

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
     * 查询step目标电磁阀设备
     * @return
     */
    @Select("SELECT * FROM  devlist WHERE run=#{pid} and type=#{type}")
    DevList selectDcfDev(int pid,int type);

    /**
     * 查询控制键
     * @return
     */
    @Select("SELECT * FROM  datalist WHERE pid=#{pid} and type=#{type}")
    DataList selectControlKey(int pid,int type);

    // 查询全部
    @Select("SELECT * FROM hello")
    List<HelloModel> selectAll();

    // 查询全部设备
    @Select("SELECT * FROM devlist")
    List<DevList> selectAllDev();

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

    /**
     * 更新 datalist_time
      */

    @Update("UPDATE datalist SET tstatus=#{tstatus},tcheck=#{tcheck} WHERE kkey=#{kkey}")
    int updateTime(DataList data);

    @Update("UPDATE devlist SET runtime=#{runtime} WHERE id=#{id}")
    int updateDevTime(DevList data);

    /** 更新 value*/
    @Update("UPDATE hello SET value=#{value} WHERE id=#{id}")
    int updateValue(HelloModel model);

    // 根据 ID 删除
    @Delete("DELETE FROM hello WHERE id=#{id}")
    int delete(Integer id);

}