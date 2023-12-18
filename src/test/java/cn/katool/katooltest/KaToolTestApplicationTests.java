package cn.katool.katooltest;

import cn.katool.katooltest.entity.User;
import cn.katool.util.auth.AuthUtil;
import cn.katool.util.cache.utils.CaffeineUtils;
import cn.katool.util.database.nosql.RedisUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.RedisClientInfo;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

import java.util.List;
import java.util.Map;


@SpringBootTest
@Slf4j
class KaToolTestApplicationTests {

    @Resource
    RedisUtils<String,String> redisUtils;

    @Resource
    CaffeineUtils caffeineUtils;

    @Resource
    RedisTemplate redisTemplate;

    @Test
    void test(){
        System.out.println(redisUtils);
        RedisUtils instance = RedisUtils.getInstance(redisTemplate);
        System.out.println(instance);
    }

    // 分布式锁单元测试
    @Test
    void Test(){
        redisUtils.lock("1");
        redisUtils.unlock("1");
    }

    @Test
    void testRange(){
        redisUtils.putZSet("qwe","2",3D);
        redisUtils.getZSetByRange("qwe",0L,-1L);
    }
    @Test
    void testMap(){
       redisUtils.getMap("123");

        Map map = redisUtils.getMap("123");

        (map).forEach((k,v)-> {
            System.out.println(v);
        });
        System.out.println(map);
    }
    @Test
    void DistributedLockTest() throws InterruptedException, ParseException {
        //        SpringApplication.run(KaToolTestApplication.class, args);
        //创建一个线程池
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3,10,2, TimeUnit.SECONDS,new ArrayBlockingQueue<>(5),
//                new ThreadPoolExecutor.CallerRunsPolicy());
//        threadPoolExecutor.allowCoreThreadTimeOut(true);


       while(true){
           final long[] i = {20l};
           List<FutureTask> futureTaskList=new ArrayList<>();
           new Thread(() -> {
               while(true){
                   System.err.println("i[0]="+i[0]);
                   if(i[0]==0){
                       break;
                   }
               }
           }).start();
           for (int j = 0; j < 25; j++) {
               if (i[0]==0)break;
               long l = System.currentTimeMillis();
               int finalJ = j+1;
               FutureTask futureTask = new FutureTask(() -> {
                   redisUtils.lock("lock");
//                   synchronized ("lock".intern()){
                       log.info("成功进入第{}个线程", finalJ);
                       if (i[0] > 0) {
                           Thread.sleep(1000);
                           i[0]--;
                       }
//                   }
                   redisUtils.unlock("lock");
                   return null;
               });
               futureTaskList.add(futureTask);
           }
           int k=0;
           for (FutureTask task : futureTaskList) {
               Thread thread = new Thread(task);
               thread.start();
               log.error(thread.getId() + "已开始！");
//            break;
           }
           k=0;
           for (FutureTask futureTask : futureTaskList) {
               try {
                   futureTask.get();
                   log.info("第{}个线程已完成", ++k);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               } catch (ExecutionException e) {
                   throw new RuntimeException(e);
               }
           }
           if (i[0]!=0) System.err.println("分布式锁错误，超卖问题：i[0]="+i[0]);
           else System.out.println("分布式锁正确，没有超卖问题：i[0]="+i[0]);
       }
    }

    // Caffeine内存缓存测试
    @Test
    void CaffeineTest() throws InterruptedException {
        caffeineUtils.put("key", "value");
        redisUtils.setValue("123","123");
        while(true){
            log.info("{}",redisUtils.getValue("123"));
            Thread.sleep(2000);
        }
//        return ;
    }



    @Test
    void AuthTest() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String token = AuthUtil.createToken(new User("123", "456"), User.class);
        System.out.println(AuthUtil.getUserFromToken(token, User.class));
    }

}
