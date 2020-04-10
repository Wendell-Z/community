package com.nowcoder.community.config;

import com.nowcoder.community.quartz.AlphaJob;
import com.nowcoder.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

// 配置 -> 数据库 -> 调用
@Configuration
public class QuartzConfig {
    //多个job就配置多个jobdetail 和 trigger

    // FactoryBean可简化Bean的实例化过程:
    // 1.通过FactoryBean封装Bean的实例化过程.
    // 2.将FactoryBean装配到Spring容器里.
    // 3.将FactoryBean注入给其他的Bean.
    // 4.该Bean得到的是FactoryBean所管理的对象实例.

    // 配置JobDetail 配置job名 job所属的group
    //@Bean
    public JobDetailFactoryBean alphaJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        //这里设置了执行哪个Job
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup");
        //设置任务是否运行
        factoryBean.setDurability(true);
        //是否可恢复
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    // 配置Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean)
    //触发器 只运行一次 配置就进表了  表里数据删了的话就不会执行任务
    //@Bean//注释了启动就不执行trigger
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        //设置周期
        factoryBean.setRepeatInterval(3000);
        factoryBean.setJobDataMap(new JobDataMap());
//        factoryBean.setPriority();
//        factoryBean.setRepeatCount();
//        factoryBean.setStartDelay();
//        factoryBean.setStartTime();
//        factoryBean.setMisfireInstructionName();
//        factoryBean.setMisfireInstruction();
        return factoryBean;
    }

    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        //这里设置了执行哪个Job
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postJob");
        factoryBean.setGroup("postJobGroup");
        //设置任务是否运行
        factoryBean.setDurability(true);
        //是否可恢复
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    @Bean//注释了启动就不执行trigger
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postTrigger");
        factoryBean.setGroup("postTriggerGroup");
        //设置周期
        factoryBean.setRepeatInterval(10 * 60 * 5);
        factoryBean.setJobDataMap(new JobDataMap());
//        factoryBean.setPriority();
//        factoryBean.setRepeatCount();
//        factoryBean.setStartDelay();
//        factoryBean.setStartTime();
//        factoryBean.setMisfireInstructionName();
//        factoryBean.setMisfireInstruction();
        return factoryBean;
    }

}
