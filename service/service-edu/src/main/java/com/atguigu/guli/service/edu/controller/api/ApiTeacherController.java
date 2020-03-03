package com.atguigu.guli.service.edu.controller.api;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.edu.entity.Teacher;
import com.atguigu.guli.service.edu.service.TeacherService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/api/edu/teacher")
public class ApiTeacherController {

    @Autowired
    private TeacherService teacherService;

    @GetMapping("list")
    public R listAll(){


        return R.ok().message("").data("items",null);

    }

    @ApiOperation(value = "分页讲师列表")
    @GetMapping(value = "page-list")
    public R pageList(
            @RequestParam("page")Long page,
            @RequestParam("limit")Long limit
    ){

        Page<Teacher> teacherPage = new Page<>(page,limit);
      Map<String ,Object> map= teacherService.webSelectPage(teacherPage);
        return  R.ok().message("查询成功").data(map);
    }

    @GetMapping("get/{id}")
    public R selectTeacherInfoById(@PathVariable("id")String id){

        Map<String, Object> map=teacherService.selectTeacherInfoById(id);

        return  R.ok().message("查询成功").data(map);
    }




}

