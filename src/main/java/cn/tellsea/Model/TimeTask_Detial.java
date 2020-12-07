package cn.tellsea.Model;

/**
 * @author xuxu
 * @create 2020-12-04 11:07
 */
public class TimeTask_Detial {
    public int id;
    public int taskid;
    public String kkey;
    public String val;
    public int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public String getKkey() {
        return kkey;
    }

    public void setKkey(String kkey) {
        this.kkey = kkey;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
