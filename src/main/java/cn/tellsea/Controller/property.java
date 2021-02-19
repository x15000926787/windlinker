package cn.tellsea.Controller;

import cn.tellsea.Model.HelloModel;
import cn.tellsea.Model.Parameter;
import cn.tellsea.Model.ReqBody;
import cn.tellsea.service.HelloService;
import cn.tellsea.utils.anaUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.runtime.ECMAException;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@Api(tags = "系统参数管理相关接口")
@RequestMapping("/para")
public class property {

    @Autowired
    private  HelloService helloService;

    @Autowired
    private anaUtil anautil;
   /* @GetMapping("/")
    public String Index() {
        return "Hello World";
    }*/



    @GetMapping("/list")
    @ApiOperation("查询所有参数")

    public List<Parameter> List() {
        return helloService.selectAllPara();
    }



    @PostMapping("/edit")
    @ApiOperation("修改参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "参数id", defaultValue = "1", required = true,dataType = "int"),

            @ApiImplicitParam(name = "val", value = "参数值", defaultValue = "1", required = true,dataType = "Float")
    })
    public boolean Post(Float val,int id){
        try{
            if (helloService.updatePara(val,id)) {
                anautil.loadParaList();

            }
        }catch (Exception e){
            return false;
        }
        return true;
    }
            //@RequestBody ReqBody map


}
