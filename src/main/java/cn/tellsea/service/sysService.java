package cn.tellsea.service;

/**
 * @author xuxu
 * @create 2020-11-24 10:07
 */
public interface sysService {
    /**
     * 系统开启
     * @return
     */
    int sysStart();

    /**
     *系统初始化
     */
    void sysInit();

    /**
     * 增加主机
     * @return
     */
    int sysAdd();

    /**
     * 退出主机
     * @return
     */
    int sysCut();
}
