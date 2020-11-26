package cn.tellsea.service.impl;

import cn.tellsea.service.sysService;
import org.springframework.stereotype.Service;

/**
 * @author xuxu
 * @create 2020-11-24 11:03
 */
@Service
public class sysServiceImpl implements sysService {
    @Override
    public  int sysStart()
    {
        return 1;
    }

    @Override
    public  void sysInit()
    {

    }

    @Override
    public  int sysAdd()
    {
        return 1;
    }


    @Override
    public  int sysCut()
    {
        return 1;
    }

}
