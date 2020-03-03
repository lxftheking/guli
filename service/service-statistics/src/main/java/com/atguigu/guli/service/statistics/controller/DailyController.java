package com.atguigu.guli.service.statistics.controller;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.statistics.service.DailyService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 前端控制器
 * </p>
 *
 * @author saber
 * @since 2020-03-02
 */
@Api
@CrossOrigin
@RestController
@RequestMapping("/admin/statistics/daily")
public class DailyController {

    @Autowired
    private DailyService dailyService;

    @PostMapping("create/{day}")
    public R createStatisticsByDay(@PathVariable("day") String day){
        dailyService.createStatisticsByDay(day);
        return  R.ok().data("",null);
    }

    @GetMapping("show-chart/{begin}/{end}/{type}")
    public R showChart(
            @PathVariable("begin")String begin,
            @PathVariable("end")String end,
            @PathVariable("type")String type
    ){
       Map<String,Object> map= dailyService.getChartData(begin,end,type);
        return  R.ok().message("").data(map);
    }

}

