package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.service.edu.entity.CourseCollect;
import com.atguigu.guli.service.edu.entity.vo.CourseCollectVo;
import com.atguigu.guli.service.edu.mapper.CourseCollectMapper;
import com.atguigu.guli.service.edu.service.CourseCollectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程收藏 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2020-02-12
 */
@Service
public class CourseCollectServiceImpl extends ServiceImpl<CourseCollectMapper, CourseCollect> implements CourseCollectService {
    @Autowired
    private CourseCollectMapper courseCollectMapper;

    @Override
    public Map<String, Object> selectPage(Page<CourseCollectVo> pageParam, String memberId) {

        IPage<CourseCollectVo> page = courseCollectMapper.selectPage(pageParam, memberId);
        List<CourseCollectVo> courseCollectList = page.getRecords();

        Map<String, Object> map = new HashMap<>();
        map.put("items", courseCollectList);
        map.put("current", pageParam.getCurrent());
        map.put("pages", pageParam.getPages());
        map.put("size", pageParam.getSize());
        map.put("total", pageParam.getTotal());
        map.put("hasNext", pageParam.hasNext());
        map.put("hasPrevious", pageParam.hasPrevious());
        return map;
    }

    /**
     * 收藏
     * @param courseId
     * @param memberId
     */
    @Override
    public void saveCourseCollect(String courseId, String memberId) {
        QueryWrapper<CourseCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId).eq("member_id", memberId);
        Integer count = courseCollectMapper.selectCount(queryWrapper);
        if(count == 0) {
            CourseCollect courseCollect = new CourseCollect();
            courseCollect.setCourseId(courseId);
            courseCollect.setMemberId(memberId);
            this.save(courseCollect);
        }
    }

    /**
     * 判断用户是否收藏
     * @param memberId
     * @param courseId
     * @return
     */
    @Override
    public boolean isCollect(String memberId, String courseId) {
        QueryWrapper<CourseCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId).eq("member_id", memberId);
        Integer count = courseCollectMapper.selectCount(queryWrapper);
        return count > 0;
    }
}
