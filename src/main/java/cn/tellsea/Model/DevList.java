package cn.tellsea.Model;

public class DevList {
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int id;

    @Override
    public String toString() {
        return "'"+id+"':{" +
                " id:" + id +
                ", devname:'" + devname + '\'' +
                ", type:" + type +
                ", run:" + run +
                ", error:" + error +
                ", myerr:" + myerr +
                ", valid:" + valid +
                ", status:" + status +
                ", runtime:" + runtime +
                ", poweron:" + poweron +
                ", poweroff:" + poweroff +
                ", reton:" + reton +
                ", retoff:" + retoff +
                ", fresh:" + fresh +
                '}';
    }

    public int getId() {
        return id;
    }

    public double getRuntime() {
        return runtime;
    }

    public void setRuntime(double runtime) {
        this.runtime = runtime;
    }

    public String getDevname() {
        return devname;
    }

    public void setDevname(String devname) {
        this.devname = devname;
    }

    public int getRun() {
        return run;
    }

    public void setRun(int run) {
        this.run = run;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }

    public int type;
    //启停
    public int run;
    //故障
    public int error;
    //自判故障
    public int myerr;
    //手自动
    public int status;
    //可用
    public int valid;



    public int getFresh() {
        return fresh;
    }

    public void setFresh(int fresh) {
        this.fresh = fresh;
    }

    public double runtime;
    public  String devname;

    public int getMyerr() {
        return myerr;
    }

    public void setMyerr(int myerr) {
        this.myerr = myerr;
    }

    public int getPoweron() {
        return poweron;
    }

    public void setPoweron(int poweron) {
        this.poweron = poweron;
    }

    public int getReton() {
        return reton;
    }

    public void setReton(int reton) {
        this.reton = reton;
    }

    public int getRetoff() {
        return retoff;
    }

    public void setRetoff(int retoff) {
        this.retoff = retoff;
    }

    public int getPoweroff() {
        return poweroff;
    }

    public void setPoweroff(int poweroff) {
        this.poweroff = poweroff;
    }

    public int poweron;
    public int poweroff;
    public int reton;
    public int retoff;
    public int fresh;



}
