package com.atguigu.guli.service.order.client;

import com.atguigu.guli.service.base.dto.CourseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(value = "guli-edu")
public interface CourseClient {

    /**
     * 根据课程id获取课程信息
     * @param courseId
     * @return
     */
    @GetMapping(value = "/api/edu/course/inner/get-course-dto/{courseId}")
    CourseDto getCourseDtoById(@PathVariable(value = "courseId") String courseId);
}