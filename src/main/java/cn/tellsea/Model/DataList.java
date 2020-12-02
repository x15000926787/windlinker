package cn.tellsea.Model;

import java.time.LocalDateTime;
import java.util.Date;

public class DataList {
    public long id;
    public long pid;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getKkey() {
        return kkey;
    }

    public void setKkey(String kkey) {
        this.kkey = kkey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public long getTcheck() {
        return tcheck;
    }

    public void setTcheck(long tcheck) {
        this.tcheck = tcheck;
    }

    public double getUplimit() {
        return uplimit;
    }

    public void setUplimit(double uplimit) {
        this.uplimit = uplimit;
    }

    public double getLowerlimit() {
        return lowerlimit;
    }

    public void setLowerlimit(double lowerlimit) {
        this.lowerlimit = lowerlimit;
    }

    public String kkey;
    public String name;
    public int type;

    public int getTvalid() {
        return tvalid;
    }

    public void setTvalid(int tvalid) {
        this.tvalid = tvalid;
    }

    public int tvalid;



    public int tstatus;
    public int getTstatus() {
        return tstatus;
    }

    public void setTstatus(int tstatus) {
        this.tstatus = tstatus;
    }
    public long tcheck;
    public double uplimit;
    public double lowerlimit;

    @Override
    public String toString() {
        return "'"+kkey+"':{" +
                "id:" + id +
                ", pid:" + pid +
                ", kkey:'" + kkey + '\'' +
                ", name:'" + name + '\'' +
                ", type:" + type +
                ", tvalid:" + tvalid +
                ", tstatus:" + tstatus +
                ", tcheck:" + tcheck +
                ", uplimit:" + uplimit +
                ", lowerlimit:" + lowerlimit +
                '}';
    }
}
