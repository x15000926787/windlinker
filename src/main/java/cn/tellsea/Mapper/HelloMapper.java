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
     * select  *
     * from (select * from devlist as a
     *                            where  runtime=(select min(b.runtime)
     *                                               from devlist as b
     *                                               where a.type = b.type and a.run=b.run and a.status=b.status and a.error=b.error and a.type=2 and a.run=0 and a.error=0 and a.status=0
     *                                              )
     *      ) as a
     * group by type
     * @param type
     * @return
     */
    @Select("SELECT * FROM devlist WHERE id=#{id}")
    DevList selectdev(int type);

    // 查询全部
    @Select("SELECT * FROM hello")
    List<HelloModel> selectAll();

    // 查询全部设备
    @Select("SELECT * FROM devlist")
    List<DevList> selectAllDev();

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