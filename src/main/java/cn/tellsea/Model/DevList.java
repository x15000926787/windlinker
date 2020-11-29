package cn.tellsea.Model;

public class DevList {
    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
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



    public void setId(long id) {
        this.id = id;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public long type;
    public long status;
    public double runtime;
    public  String devname;

}
