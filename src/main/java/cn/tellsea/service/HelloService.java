package cn.tellsea.service;

import cn.tellsea.Mapper.HelloMapper;
import cn.tellsea.Model.*;
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
    public  List<TimeTask> selectAllTimeTask() {

        return dao.selectAllTimeTask();
    }
    public  List<TimeTask> selectofTimeTask(int tp) {

        return dao.selectofTimeTask(tp);
    }
    public  List<TimeTask_Detial> selectAllTimeTaskDetial(int pid) {

        return dao.selectAllTimeTaskDetial(pid);
    }
    public boolean updateTime(DataList data) {
        return dao.updateTime(data) > 0;
    }
    public boolean resetmyerr() {
        return dao.resetmyerr() > 0;
    }
    public boolean setDevErr(int value,int id) {
        return dao.setDevErr(value,id) > 0;
    }
    public boolean updateDevTime(DevList data) {
        return dao.updateDevTime(data) > 0;
    }
    public boolean updateDevRun(DevList data) {
        log.info("run:"+data.toString());
        return dao.updateDevRun(data) > 0;
    }
    public boolean updateDevErr(DevList data) {
        log.info("err:"+data.toString());
        return dao.updateDevErr(data) > 0;
    }
    public boolean updateDevStatus(DevList data) {
        log.info("status:"+data.toString());
        return dao.updateDevStatus(data) > 0;
    }
    public boolean updateValue(HelloModel model) {
        return dao.updateValue(model) > 0;
    }

    public boolean delete(Integer id) {
        return dao.delete(id) > 0;
    }
    public  List<Parameter> selectAllPara() {
        return dao.selectAllPara();
    }
    public  List<DevList> selectdevruncount() {
        return dao.selectdevruncount();
    }
    public  DevList selectdev(int type) {
        return dao.selectdev(type);
    }
    public  List<DevList> selectdevbytype(int type) {
        return dao.selectDevbyType(type);
    }
    public  DevList selectdev4s(int type) {
        return dao.selectdev4s(type);
    }
    public  DevList selectdevbyid(int id) {
        return dao.selectdevbyid(id);
    }
    public  DataList selectcontrolkey(int pid,int type) {
        return dao.selectControlKey(pid,type);
    }
    public  DevList selectDcfDev(int pid,int type) {
        return dao.selectDcfDev(pid,type);
    }
    public  ActionDetial selectnextprocess(int p,int s) {
        return dao.selectnextprocess(p,s);
    }

    public  ActionDetial selectAction(int p,int s) {
        return dao.selectAction(p,s);
    }
    public  List<DevList> selectAllDev() {
        return dao.selectAllDev();
    }

    public  List<DataList> selectAllData() {
        return dao.selectAllData();
    }
    public  List<DataList> selectDatabytype(int pid,int type) {
        return dao.selectDatabytp(pid,type);
    }



}