package cn.tellsea.Model;

public class ActionDetial {
    private long Id;
    private int pid;
    private int step;
    private int wait;
    private int repeat;
    private int last;
    private int type;

    @Override
    public String toString() {
        return "ActionDetial{" +
                "Id=" + Id +
                ", pid=" + pid +
                ", step=" + step +
                ", wait=" + wait +
                ", repeat=" + repeat +
                ", last=" + last +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", targettype=" + targettype +
                ", need=" + need +
                '}';
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getWait() {
        return wait;
    }

    public void setWait(int wait) {
        this.wait = wait;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTargettype() {
        return targettype;
    }

    public void setTargettype(int targettype) {
        this.targettype = targettype;
    }

    public int getNeed() {
        return need;
    }

    public void setNeed(int need) {
        this.need = need;
    }

    private String name;
    private int targettype;
    private int need;

}
