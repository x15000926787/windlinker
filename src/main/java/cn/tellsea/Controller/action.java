package cn.tellsea.Controller;


import cn.tellsea.service.HelloService;
import cn.tellsea.utils.anaUtil;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@Api(tags = "系统操作相关接口")
@RequestMapping("/action")
public class action {

    @Autowired
    private  HelloService helloService;
    @Autowired
    private anaUtil anautil;

   /* @GetMapping("/")
    public String Index() {
        return "Hello World";
    }*/




    @GetMapping("/stop")
    @ApiOperation("关所有")

    public String stop() throws InterruptedException {
        try{
            anautil.stopZJ();
            return "success";
        }catch (Exception e)
        {
            return "unsuccess" + e.toString();
        }


    }

    @GetMapping("/stopOne")
    @ApiOperation("关一台")

    public String stopOne() throws InterruptedException {
        try{
            anautil.stopOneZJ();
            return "success";
        }catch (Exception e)
        {
            return "unsuccess" + e.toString();
        }


    }

    @GetMapping("/start")
    @ApiOperation("开机")
    public String start() throws InterruptedException {
        try{
            anautil.startZJ();
            return "success";
        }catch (Exception e)
        {
            return "unsuccess" + e.toString();
        }


    }

/*
@GetMapping("/list")
    @ApiOperation("查询所有用户的接口")
    public List<HelloModel> List() {
        return helloService.selectAll();
    }

    @PostMapping("/post")
    public String Post(
            @RequestBody ReqBody map
    ) throws  IOException {
        return "输入的姓名是" + map.getName() + ",电子邮件是:" + map.getEmail();
    }*/
}
