package com.atguigu.guli.service.edu.service;

import com.atguigu.guli.service.edu.entity.CourseCollect;
import com.atguigu.guli.service.edu.entity.vo.CourseCollectVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 课程收藏 服务类
 * </p>
 *
 * @author Helen
 * @since 2020-02-12
 */
public interface CourseCollectService extends IService<CourseCollect> {
    public Map<String, Object> selectPage(Page<CourseCollectVo> pageParam, String memberId);
    public void saveCourseCollect(String courseId, String memberId);
    public boolean isCollect(String memberId, String courseId);
}
