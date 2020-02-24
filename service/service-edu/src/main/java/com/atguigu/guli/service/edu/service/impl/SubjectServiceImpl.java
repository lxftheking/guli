package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.common.base.util.ExcelImportUtil;
import com.atguigu.guli.service.edu.entity.Subject;
import com.atguigu.guli.service.edu.entity.vo.SubjectVo;
import com.atguigu.guli.service.edu.mapper.SubjectMapper;
import com.atguigu.guli.service.edu.service.SubjectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2020-02-12
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchImport(InputStream inputStream) throws Exception {

        ExcelImportUtil excelImportUtil = new ExcelImportUtil(inputStream);
        HSSFSheet sheet = excelImportUtil.getSheet();

        for (Row rowData : sheet) {

            //获取当前行的索引值：标题行
            if(rowData.getRowNum() == 0){
                continue; //忽略当前行
            }

            //获取数据行
            //一级分类
            Cell levelOneCell = rowData.getCell(0);
            String levelOneCellValue = excelImportUtil.getCellValue(levelOneCell).trim();
            if(StringUtils.isEmpty(levelOneCellValue)){
                continue;
            }

            //二级分类
            Cell levelTwoCell = rowData.getCell(1);
            String levelTwoCellValue = excelImportUtil.getCellValue(levelTwoCell).trim();
            if(StringUtils.isEmpty(levelTwoCellValue)){
                continue;
            }

            //判断一级分类是否重复
            Subject subject = this.getByTitle(levelOneCellValue);
            //二级分类的parent_id
            String parentId = null;
            if(subject == null){
                //将一级分类存入数据库
                Subject subjectLevelOne = new Subject();
                subjectLevelOne.setTitle(levelOneCellValue);
                baseMapper.insert(subjectLevelOne);

                //二级分类的parent_id
                parentId = subjectLevelOne.getId();
            }else{
                parentId = subject.getId();
            }

            //判断二级分类是否重复
            Subject subjectSub = this.getSubByTitle(levelTwoCellValue, parentId);
            if(subjectSub == null){
                //将二级分类存入数据库
                Subject subjectLevelTwo = new Subject();
                subjectLevelTwo.setTitle(levelTwoCellValue);
                subjectLevelTwo.setParentId(parentId);
                baseMapper.insert(subjectLevelTwo);
            }
        }

    }

    @Override
    public List<SubjectVo> nestedList() {

        List<SubjectVo> subjectVoList = new ArrayList<>();

        //获取所有的分类数据
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort", "id");
        List<Subject> subjectList = baseMapper.selectList(queryWrapper);

        //分别获取一级分类和二级分类
        List<Subject> subjectLevelOneList = new ArrayList<>();
        List<Subject> subjectLevelTwoList = new ArrayList<>();
        for (Subject subject : subjectList) {
            if(subject.getParentId().equals("0")){
                subjectLevelOneList.add(subject);//填充一级类别
            }else {
                subjectLevelTwoList.add(subject);//填充二级类别
            }
        }

        //填充vo数据：一级类别
        for (Subject subjectLevelOne : subjectLevelOneList) {
            SubjectVo subjectVoLevelOne = new SubjectVo();
            BeanUtils.copyProperties(subjectLevelOne, subjectVoLevelOne);
            subjectVoList.add(subjectVoLevelOne);

            //填充children：二级类别
            List<SubjectVo> subjectVoLevelTwoList = new ArrayList<>();
            for (Subject subjectLevelTwo : subjectLevelTwoList) {
                if(subjectLevelOne.getId().equals(subjectLevelTwo.getParentId())){

                    SubjectVo subjectVoLevelTwo = new SubjectVo();
                    BeanUtils.copyProperties(subjectLevelTwo, subjectVoLevelTwo);
                    subjectVoLevelTwoList.add(subjectVoLevelTwo);
                }
            }

            subjectVoLevelOne.setChildren(subjectVoLevelTwoList);
        }

        return subjectVoList;
    }

    @Override
    public List<SubjectVo> nestedList2() {
        return baseMapper.selectNestedListByParentId("0");
    }

    /**
     * 判断一级分类是否重复
     * @param title
     * @return
     */
    private Subject getByTitle(String title){
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", title);
        queryWrapper.eq("parent_id", "0"); //一级分类
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 判断二级分类是否重复
     * @param title
     * @param parentId
     * @return
     */
    private Subject getSubByTitle(String title, String parentId){
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", title);
        queryWrapper.eq("parent_id", parentId); //一级分类
        return baseMapper.selectOne(queryWrapper);
    }


}
