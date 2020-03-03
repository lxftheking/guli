package com.atguigu.guli.service.edu.controller.api;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.edu.entity.vo.SubjectVo;
import com.atguigu.guli.service.edu.service.SubjectService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
@Api(description="课程分类")
@RequestMapping("/api/edu/subject")
public class ApiSubjectController {


     @Autowired
    private SubjectService subjectService;

     @GetMapping("subject-nested-list")
     public R nestedList(){

         List<SubjectVo> subjectVos = subjectService.nestedList();

         return R.ok().data("items",subjectVos);
     }


}

