package cn.tellsea.Model;

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

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }


    public Date getTcheck() {
        return tcheck;
    }

    public void setTcheck(Date tcheck) {
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
    public long type;

    public long getTvalid() {
        return tvalid;
    }

    public void setTvalid(long tvalid) {
        this.tvalid = tvalid;
    }

    public long tvalid;
    public Date tcheck;
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
                ", tcheck:" + tcheck +
                ", uplimit:" + uplimit +
                ", lowerlimit:" + lowerlimit +
                '}';
    }
}
