package com.nami.springbootinit.config;
//package com.nami.springbootinit.config;
//
//import com.alibaba.otter.canal.client.CanalConnector;
//import com.alibaba.otter.canal.client.CanalConnectors;
//import com.nami.springbootinit.model.entity.User;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//import top.javatool.canal.client.annotation.CanalTable;
//import top.javatool.canal.client.handler.EntryHandler;
//
//import java.net.InetSocketAddress;
//
///**
// * @author nami404
// * * @date 2025/3/16 16:20
// */
//@Component
//@CanalTable("user")
//public class CanalConfig implements EntryHandler<User> {
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    //user_test有新增就会调用此方法，获取到新增的数据
//    @Override
//    public void insert(User user) {
//        stringRedisTemplate.opsForValue().set("canalTest", user.getUserAccount());
//        EntryHandler.super.insert(user);
//    }
//
//    //修改
//    @Override
//    public void update(User before, User after) {
//        EntryHandler.super.update(before, after);
//    }
//
//    //删除
//    @Override
//    public void delete(User user) {
//        EntryHandler.super.delete(user);
//    }
//}
