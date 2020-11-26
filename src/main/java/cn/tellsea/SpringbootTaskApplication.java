package cn.tellsea;


import cn.tellsea.component.FirstClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


/*@SpringBootApplication
public class SpringbootTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootTaskApplication.class, args);
    }

}*/



import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;



@Slf4j
@SpringBootApplication

public class SpringbootTaskApplication implements CommandLineRunner {
   // protected static final Logger LOGGER = LoggerFactory.getLogger(SpringbootTaskApplication.class);

    /*@Autowired
    NettyConfig nettyConfig;*/

    @Autowired
    FirstClass firstClass;
    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringbootTaskApplication.class).run(args);
              //  .web(WebApplicationType.NONE)

        //SpringApplication.run(Aws310Application.class, args);
    }

    /**
     * spring boot启动后，会进入该方法
     */
    @Override
    public void run(String... args) throws Exception {

        init();
    }

    /**
     * 初始化信息
     */
    @SuppressWarnings("unused")
    private void init() {

       // Consts.SERVER_PORT = nettyConfig.getPort();
       // Consts.MODULE_TYPE = nettyConfig.getModuleType();
        log.info("应用初始化完成");
    }
}
