package cn.tellsea.Model;

public class Parameter {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    private String name;
    private String kkey;
    private float value;

    @Override
    public String toString() {
        return "'"+id+"':{" +
                " id:" + id +
                ",name:'" + name + '\'' +
                ", value:" + value +
                ",kkey:'" + kkey + '\'' +
                '}';
    }
    public String tokeyString() {
        return "'"+kkey+"':{" +
                " id:" + id +
                ",name:'" + name + '\'' +
                ", value:" + value +
                ",kkey:'" + kkey + '\'' +
                '}';
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

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
