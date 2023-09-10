package cn.katool.katooltest;

import cn.hutool.core.date.DateUtil;
import cn.katool.lock.LockMessageWatchDog;
import cn.katool.util.cache.utils.CaffeineUtils;
import cn.katool.util.db.nosql.RedisUtils;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
@Slf4j
class KaToolTestApplicationTests {

    @Resource
    RedisUtils redisUtils;

    @Resource
    CaffeineUtils caffeineUtils;

    // 分布式锁单元测试
    @Test
    void Test(){
        redisUtils.lock("1");
        redisUtils.unlock("1");
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

}
