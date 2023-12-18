package cn.katool.katooltest;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.katool.util.database.nosql.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Resource(name = "RedisUtils")
    RedisUtils redisUtils;

    public int i=0;


    Integer q=1000;
    @GetMapping
    public String test() throws InterruptedException {
        i++;
        redisUtils.lock("this",true);
    String value = (String) redisUtils.getValue("1234");
    if (q>0){
        Thread.sleep(1);
        q--;
    }
        log.info("i={},q={}",i,q);
    if (ObjectUtil.isEmpty(value)){
        redisUtils.setValue("1234", RandomUtil.randomString(10),5L, TimeUnit.MINUTES);
        value = (String) redisUtils.getValue("1234");
    }
        redisUtils.unlock("this");
//    aThis.unlock();
    return value.toString();
//}

    }
}
