package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.service.base.dto.CourseDto;
import com.atguigu.guli.service.edu.client.OssClient;
import com.atguigu.guli.service.edu.client.VodClient;
import com.atguigu.guli.service.edu.entity.*;
import com.atguigu.guli.service.edu.entity.form.CourseInfoForm;
import com.atguigu.guli.service.edu.entity.vo.CourseQueryVo;
import com.atguigu.guli.service.edu.entity.vo.WebCourseQueryVo;
import com.atguigu.guli.service.edu.entity.vo.WebCourseVo;
import com.atguigu.guli.service.edu.mapper.*;
import com.atguigu.guli.service.edu.service.CourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2020-02-12
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    private CourseDescriptionMapper courseDescriptionMapper;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private ChapterMapper chapterMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private CourseCollectMapper courseCollectMapper;
    @Autowired
    private OssClient ossClient;
    @Autowired
    private VodClient vodClient;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveCourseInfo(CourseInfoForm courseInfoForm) {

        //保存course表
        Course course = new Course();
        BeanUtils.copyProperties(courseInfoForm, course);
        course.setStatus(Course.COURSE_DRAFT);
        baseMapper.insert(course);

        //保存course_description表：主键没有策略，需要主动赋值
        CourseDescription courseDescription = new CourseDescription();
//        BeanUtils.copyProperties(courseInfoForm, course);
        courseDescription.setDescription(courseInfoForm.getDescription());
        courseDescription.setId(course.getId());
        courseDescriptionMapper.insert(courseDescription);

        return course.getId();
    }

    @Override
    public CourseInfoForm getCourseInfoFormById(String id) {

        //从course表中取数据
        Course course = baseMapper.selectById(id);

        //从course_description表中取数据
        CourseDescription courseDescription = courseDescriptionMapper.selectById(id);

        //创建courseInfoForm对象
        CourseInfoForm courseInfoForm = new CourseInfoForm();
        BeanUtils.copyProperties(course, courseInfoForm);
        courseInfoForm.setDescription(courseDescription.getDescription());

        return courseInfoForm;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCourseInfoById(CourseInfoForm courseInfoForm) {
        //保存课程基本信息
        Course course = new Course();
        BeanUtils.copyProperties(courseInfoForm, course);
        baseMapper.updateById(course);

        //保存课程详情信息
        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setDescription(courseInfoForm.getDescription());
        courseDescription.setId(course.getId());
        courseDescriptionMapper.updateById(courseDescription);
    }

    @Override
    public void selectPage(Page<Course> pageParam, CourseQueryVo courseQueryVo) {

        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.select();
        queryWrapper.orderByDesc("gmt_create");

        if (courseQueryVo == null){
            baseMapper.selectPage(pageParam, queryWrapper);
            return;
        }

        String title = courseQueryVo.getTitle();
        String teacherId = courseQueryVo.getTeacherId();
        String subjectParentId = courseQueryVo.getSubjectParentId();
        String subjectId = courseQueryVo.getSubjectId();

        if (!StringUtils.isEmpty(title)) {
            queryWrapper.like("title", title);
        }

        if (!StringUtils.isEmpty(teacherId) ) {
            queryWrapper.eq("teacher_id", teacherId);
        }

        if (!StringUtils.isEmpty(subjectParentId)) {
            queryWrapper.eq("subject_parent_id", subjectParentId);
        }

        if (!StringUtils.isEmpty(subjectId)) {
            queryWrapper.eq("subject_id", subjectId);
        }

        baseMapper.selectPage(pageParam, queryWrapper);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeCourseById(String id) {

        //删除关联数据
        Course course = baseMapper.selectById(id);
        String url = course.getCover();
        //TODO 删除封面：OSS
        //在此处调用oss中的删除图片文件的接口
        ossClient.removeFile(url);

        //TODO 删除视频：VOD
        //在此处调用vod中的删除视频文件的接口
        QueryWrapper<Video> wrapper = new QueryWrapper<>();
        wrapper.select("video_source_id");
        wrapper.eq("course_id", id);
        List<Map<String, Object>> maps = videoMapper.selectMaps(wrapper);
        List<String> videoSourceIdList = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            String videoSourceId = (String)map.get("video_source_id");
            videoSourceIdList.add(videoSourceId);
        }
        vodClient.removeVideoByIdList(videoSourceIdList);

        //收藏信息：course_collect
        QueryWrapper<CourseCollect> courseCollectQueryWrapper = new QueryWrapper<>();
        courseCollectQueryWrapper.eq("course_id", id);
        courseCollectMapper.delete(courseCollectQueryWrapper);

        //评论信息：comment
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("course_id", id);
        commentMapper.delete(commentQueryWrapper);

        //课时信息：video
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("course_id", id);
        videoMapper.delete(videoQueryWrapper);

        //章节信息：chapter
        QueryWrapper<Chapter> chapterQueryWrapper = new QueryWrapper<>();
        chapterQueryWrapper.eq("course_id", id);
        chapterMapper.delete(chapterQueryWrapper);

        //课程详情：course_description
        courseDescriptionMapper.deleteById(id);

        //课程：course
        baseMapper.deleteById(id);
    }

    @Override
    public Map<String, Object> webSelectPage(Page<Course> pageParam, WebCourseQueryVo webCourseQueryVo) {

        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.eq("status",Course.COURSE_NORMAL);
        if(!StringUtils.isEmpty(webCourseQueryVo.getSubjectParentId())){
            wrapper.eq("subject_parent_id", webCourseQueryVo.getSubjectParentId());
        }
        if(!StringUtils.isEmpty(webCourseQueryVo.getSubjectId())){
            wrapper.eq("subject_id",webCourseQueryVo.getSubjectId());
        }
        if(!StringUtils.isEmpty(webCourseQueryVo.getBuyCountSort())){
        }
        if (!StringUtils.isEmpty(webCourseQueryVo.getBuyCountSort())) {
            wrapper.orderByDesc("buy_count");
        }

        if (!StringUtils.isEmpty(webCourseQueryVo.getGmtCreateSort())) {
            wrapper.orderByDesc("gmt_create");
        }

        if (!StringUtils.isEmpty(webCourseQueryVo.getPriceSort())) {
            wrapper.orderByDesc("price");
        }

        baseMapper.selectPage(pageParam, wrapper);


        Map<String, Object> map = new HashMap<>();
        map.put("total",pageParam.getTotal());
        map.put("size",pageParam.getSize());
        map.put("pages",pageParam.getPages());
        map.put("items",pageParam.getRecords());
        map.put("hasNext",pageParam.hasNext());
        map.put("hasPrevious",pageParam.hasPrevious());
        return map;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public WebCourseVo selectWebCourseVoById(String id) {
        //更新课程浏览数
        Course course = baseMapper.selectById(id);
        course.setViewCount(course.getViewCount() + 1);
        baseMapper.updateById(course);
        //获取课程信息
        return baseMapper.selectWebCourseVoById(id);
    }

    @Override
    public CourseDto getCourseDtoById(String courseId) {

        Course course = baseMapper.selectById(courseId);
        CourseDto courseDto = new CourseDto();
        BeanUtils.copyProperties(course, courseDto);

        Teacher teacher = teacherMapper.selectById(course.getTeacherId());
        courseDto.setTeacherName(teacher.getName());
        return courseDto;
    }
}
