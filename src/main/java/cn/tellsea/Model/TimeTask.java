package cn.tellsea.Model;

/**
 * @author xuxu
 * @create 2020-12-04 11:04
 */
public class TimeTask {
    public int id;
    public String  name;
    public String  cronstr;
    public int type;
    public  String luaname;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCronstr() {
        return cronstr;
    }

    public void setCronstr(String cronstr) {
        this.cronstr = cronstr;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLuaname() {
        return luaname;
    }

    public void setLuaname(String luaname) {
        this.luaname = luaname;
    }
}
