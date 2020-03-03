package com.atguigu.guli.service.statistics.service;

import com.atguigu.guli.service.statistics.entity.Daily;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务类
 * </p>
 *
 * @author saber
 * @since 2020-03-02
 */
public interface DailyService extends IService<Daily> {
    public void createStatisticsByDay(String day);

    Map<String, Object> getChartData(String begin, String end, String type);
}
