package com.atguigu.guli.service.edu.controller.api;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.base.dto.CourseDto;
import com.atguigu.guli.service.edu.entity.Course;
import com.atguigu.guli.service.edu.entity.vo.ChapterVo;
import com.atguigu.guli.service.edu.entity.vo.WebCourseQueryVo;
import com.atguigu.guli.service.edu.entity.vo.WebCourseVo;
import com.atguigu.guli.service.edu.service.ChapterService;
import com.atguigu.guli.service.edu.service.CourseService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author Helen
 * @since 2020-02-12
 */
@RestController
@CrossOrigin
@Api(description="课程")
@RequestMapping("/api/edu/course")
public class ApiCourseController {


    @Autowired
    private CourseService courseService;

    @Autowired
    private ChapterService chapterService;


    @GetMapping("page-list")
    public R pageList(
            @RequestParam("page")Long page,
            @RequestParam("limit")Long limit,
            WebCourseQueryVo webCourseQueryVo
            ){
        Page<Course> page1 = new Page<>(page, limit);
        Map<String, Object> map = courseService.webSelectPage(page1, webCourseQueryVo);
        return  R.ok().data(map).message("查询成功");
    }

    @GetMapping("get/{courseId}")
    public R getById(@PathVariable("courseId")String courseId){
        WebCourseVo courseVo = courseService.selectWebCourseVoById(courseId);
        List<ChapterVo> chapterVos = chapterService.nestedList(courseId);
        return  R.ok().message("").data("course",courseVo).data("chapterVoList",chapterVos);
    }

    @ApiOperation(value = "根据课程id查询课程信息")
    @GetMapping(value = "inner/get-course-dto/{courseId}")
    public CourseDto getCourseDtoById(@PathVariable String courseId){
        CourseDto courseDto = courseService.getCourseDtoById(courseId);
        return courseDto;
    }

}

