package cn.katool.katooltest;

import cn.katool.util.db.nosql.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


import javax.annotation.Resource;
import java.util.concurrent.*;

@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@SpringBootApplication
public class KaToolTestApplication {


    public static void main(String[] args) {
        SpringApplication.run(KaToolTestApplication.class, args);
    }

}
