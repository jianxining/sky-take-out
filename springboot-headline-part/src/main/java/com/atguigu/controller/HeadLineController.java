package com.atguigu.controller;


import com.atguigu.pojo.Headline;
import com.atguigu.service.HeadlineService;
import com.atguigu.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/headline")
public class HeadLineController {

    @Autowired
    private HeadlineService headlineService;


    //登录以后才可以访问
    @PostMapping("/publish")
    public Result publish(@RequestBody Headline headline,@RequestHeader String token){
        //验证token是否合法
        Result result = headlineService.publish(headline,token);
        return result;
    }
    @PostMapping("/findHeadlineById")
    public Result findHeadlineById(Integer hid){
        Headline headline = headlineService.getById(hid);
        Map data = new HashMap();
        data.put("headline",headline);
        return Result.ok(data);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Headline headline){
        Result result = headlineService.updateheadline(headline);
        return result;
    }

    @PostMapping("/removeByhid")
    public Result removeById(Integer hid){
        headlineService.removeById(hid);
        return Result.ok(null);
    }
}
