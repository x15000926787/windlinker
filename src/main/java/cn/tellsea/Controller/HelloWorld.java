package cn.tellsea.Controller;

import cn.tellsea.Model.HelloModel;

import cn.tellsea.service.HelloService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api(tags = "用户管理相关接口")
@RequestMapping("/user")
public class HelloWorld {

    @Autowired
    private  HelloService helloService;


   /* @GetMapping("/")
    public String Index() {
        return "Hello World";
    }*/




    @GetMapping("/{id}")
    @ApiOperation("根据id查询用户的接口")
    @ApiImplicitParam(name = "id", value = "用户id", defaultValue = "99", required = true)
    public HelloModel getUserById(@PathVariable Integer id) {
        HelloModel user = helloService.select(id);
        return user;
    }
    /*@GetMapping("/list")
    public List<HelloModel> List() {
        return HelloService.selectAll();
    }

    @PostMapping("/post")
    public String Post(
            @RequestBody ReqBody map
    ) throws  IOException {
        return "输入的姓名是" + map.getName() + ",电子邮件是:" + map.getEmail();
    }*/
}
