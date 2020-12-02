package cn.tellsea.service;

import cn.tellsea.Mapper.HelloMapper;
import cn.tellsea.Model.DataList;
import cn.tellsea.Model.DevList;
import cn.tellsea.Model.HelloModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class HelloService {

    private final HelloMapper dao;

    @Autowired
    public HelloService(HelloMapper dao) {
        this.dao = dao;
    }

    public boolean insert(HelloModel model) {
        return dao.insert(model) > 0;
    }

    public HelloModel select(int id) {
        return dao.select(id);
    }

    public  List<HelloModel> selectAll() {

        return dao.selectAll();
    }
    public boolean updateTime(DataList data) {
        return dao.updateTime(data) > 0;
    }
    public boolean updateDevTime(DevList data) {
        return dao.updateDevTime(data) > 0;
    }

    public boolean updateValue(HelloModel model) {
        return dao.updateValue(model) > 0;
    }

    public boolean delete(Integer id) {
        return dao.delete(id) > 0;
    }


    public  List<DevList> selectAllDev() {
        return dao.selectAllDev();
    }

    public  List<DataList> selectAllData() {
        return dao.selectAllData();
    }


}