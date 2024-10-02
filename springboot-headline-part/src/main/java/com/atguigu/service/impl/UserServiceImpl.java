package com.atguigu.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ManagedDataSource;
import com.alibaba.druid.util.StringUtils;
import com.atguigu.utils.JwtHelper;
import com.atguigu.utils.MD5Util;
import com.atguigu.utils.Result;
import com.atguigu.utils.ResultCodeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.pojo.User;
import com.atguigu.service.UserService;
import com.atguigu.mapper.UserMapper;
import net.sf.jsqlparser.schema.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
* @author 陶孟春
* @description 针对表【news_user】的数据库操作Service实现
* @createDate 2024-09-26 10:11:15
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserMapper userMapper;
    /*
    * 登录业务
    * 1.根据账号，去查询用户对象  -LoginUSer
    * 2.如果用户对象为null，则查询失败，返回账号错误 501
    * 3.对比密码，若失败，返回503
    * 4.根据用户id生成token，把token放入result中，返回
    *
    * */
    @Override
    public Result login(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,user.getUsername());
        User loginuser = userMapper.selectOne(queryWrapper);
        if(loginuser == null)
            return Result.build(null, ResultCodeEnum.USERNAME_ERROR);
        if(!StringUtils.isEmpty(user.getUserPwd()) &&
                MD5Util.encrypt(user.getUserPwd()).equals(loginuser.getUserPwd())){
            String token = jwtHelper.createToken(Long.valueOf(loginuser.getUid()));
            Map map = new HashMap();
            map.put("token",token);
            return Result.ok(map);
        }
        return Result.build(null,ResultCodeEnum.PASSWORD_ERROR);
    }


    /*
    * 根据token获取用户数据
    * 1.检查token是否在有效期
    * 2.根据token获取用户id
    * 3.根据用户id查询用户数据
    * 4.去掉密码，封装为result，返回
    * */
    @Override
    public Result getUserInfo(String token) {
        boolean expiration = jwtHelper.isExpiration(token);
        if (expiration == true){
            //token失效，默认是未登录状态
            return Result.build(null,ResultCodeEnum.NOTLOGIN);
        }
        int userId = jwtHelper.getUserId(token).intValue();
        User user = userMapper.selectById(userId);
        user.setUserPwd(null);
        Map data = new HashMap();
        data.put("loginUser",user);
        return Result.ok(data);
    }

    /*
    * 检查用户名是否可用
    * 1.根据账号进行count查询
    * 2.如果count>0，则用户名不可用，返回501
    * */
    @Override
    public Result checkUserName(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,username);
        int count = userMapper.selectCount(queryWrapper).intValue();
        if(count > 0)
            return Result.build(null,ResultCodeEnum.USERNAME_USED);
        return Result.ok(null);
    }


    /*
    * 注册业务
    * 1.检查用户名是否可用
    * 2.可用，再对密码加密处理
    * 3.保存用户数据
    */
    @Override
    public Result regist(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,user.getUsername());
        int count = userMapper.selectCount(queryWrapper).intValue();
        if(count > 0)
            return Result.build(null,ResultCodeEnum.USERNAME_USED);

        //此时用户名没有被注册，可以进行注册
        //把加密的密码复制到user对象上
        user.setUserPwd(MD5Util.encrypt(user.getUserPwd()));
        userMapper.insert(user);
        return Result.ok(null);
    }

    @Override
    public Result checkLogin(String token) {
        boolean expiration = jwtHelper.isExpiration(token);
        if(expiration)
                return Result.build(null,ResultCodeEnum.NOTLOGIN);
        return Result.ok(null);
    }
}




