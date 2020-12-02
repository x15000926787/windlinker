package cn.tellsea.Model;

public class DevList {
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private long id;

    @Override
    public String toString() {
        return id+":{" +
                "id:" + id +
                ", type:" + type +
                ", status:" + status +
                ", runtime:" + runtime +
                ", devname:'" + devname + '\'' +
                '}';
    }

    public long getId() {
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

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int type;
    public int run;
    public int error;
    public int status;
    public double runtime;
    public  String devname;

}
