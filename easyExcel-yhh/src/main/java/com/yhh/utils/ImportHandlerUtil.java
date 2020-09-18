package com.yhh.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.annotation.ExcelProperty;
import com.yhh.annotaion.CheckIgnore;
import com.yhh.annotaion.CheckNoNull;
import com.yhh.annotaion.CheckUnique;
import com.yhh.entity.CheckResult;
import com.yhh.entity.FieldAttr;

import lombok.extern.slf4j.Slf4j;

/**  
 * <p>Description: 导入表格实体类字段相关属性获取—工具类</p>
 * @author yhh   
 * @date  2020年6月9日 下午6:11:37
 */
@Slf4j
public class ImportHandlerUtil {

    /**
     * 校验excel导入数据 ,筛选空字段和重复数据
     * @param <T>
     * @param obj
     * @param data
     * @return
     */
    public static <T> CheckResult checkExcelData(T obj,List<T> data) {
        List<FieldAttr> fieldAttrs = getFieldAttr(obj);
        for (FieldAttr fieldAttr : fieldAttrs) {
            if(fieldAttr.isNoNull()) {
                if(Objects.isNull(fieldAttr.getColumnValue())) {
                    log.error("{}:{} is null in excel",fieldAttr.getField());
                    return CheckResult.FAILED(fieldAttr.getColumnName() + "数据为空");
                }
            }
            if(fieldAttr.isUnique()) {
                Object columnValue = fieldAttr.getColumnValue();
                for (T t : data) {
                    try {
                        Field field = t.getClass().getDeclaredField(fieldAttr.getField());
                        field.setAccessible(true);
                        if(Objects.equals(field.get(t), columnValue)) {
                            log.error("{}:{} is repeat in excel",fieldAttr.getField(),fieldAttr.getColumnValue());
                            return CheckResult.FAILED(fieldAttr.getColumnName() + "数据列表中重复");
                        }
                    } catch (Exception e) {
                        log.error("",e);
                    }
                }
            }
        }
        return CheckResult.SUCCESS();
    }

    /** 获取实体类字段等属性 
     * @param <T>
     * 
     * @param obj
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private static <T> List<FieldAttr> getFieldAttr(T t){
        List<FieldAttr> fieldAttrs = new ArrayList<>();
        Class<?> clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();  
        for(Field field : fields) { 
            field.setAccessible(true);  
            FieldAttr attr = new FieldAttr();
            if(field.isAnnotationPresent(CheckIgnore.class)) {
                continue;
            }
            if(field.isAnnotationPresent(ExcelProperty.class)) {
                ExcelProperty exp = field.getAnnotation(ExcelProperty.class);
                String columnName = exp.value()[0];
                try {
                    attr.setColumnValue(field.get(t));
                } catch (Exception e) {
                    log.error("",e);
                }
                attr.setColumnName(columnName);
                attr.setField(humpToLine(field.getName()));
                if(field.isAnnotationPresent(CheckNoNull.class)) {
                    attr.setNoNull(true);
                }
                if(field.isAnnotationPresent(CheckUnique.class)) {
                    attr.setUnique(true);
                }
            }
            fieldAttrs.add(attr);
        } 
        return fieldAttrs;

    }

    /** 驼峰转下划线 */
    private static String humpToLine(String str) {
        Matcher matcher = Pattern.compile("[A-Z]").matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /** 下划线转驼峰 */
    @SuppressWarnings("unused")
    private static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = Pattern.compile("_(\\w)").matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 校验表格模板与实体类是否匹配
     * @param file
     * @param clazz
     * @return
     */
    public static boolean checkExcelTemp(MultipartFile file, Class<?> clazz) {
        boolean flag = false;
        Workbook inputBook = null;
        try {
            inputBook = checkExcelType(file);
            if(Objects.isNull(inputBook))
                return flag;
            Sheet inputSheet = inputBook.getSheetAt(0);
            List<String> inputList = new LinkedList<>();
            inputSheet.forEach(k ->{
                if(k.getRowNum() == 0) {
                    k.forEach(j ->{
                        inputList.add(j.getStringCellValue());
                    });
                }
            });
            Field[] fields = clazz.getDeclaredFields();  
            List<String> temList = new ArrayList<>();
            for(int i = 0; i < fields.length; i ++){  
                Field field = fields[i];  
                field.setAccessible(true);  
                if(field.isAnnotationPresent(ExcelProperty.class)) {
                    ExcelProperty exp = field.getAnnotation(ExcelProperty.class);
                    String columnName = exp.value()[0];
                    temList.add(columnName);
                }
            } 
            log.info("传入excel表头信息:{}",inputList);
            log.info("模板excel表头信息:{}",temList);
            return checkDiffrent(inputList, temList);
        } catch (IOException e) {
            log.error("",e);
        } finally {
            try {
                if(inputBook != null) {
                    inputBook.close();
                }
            } catch (IOException e) {
                log.error("",e);
            }
        }
        return flag;

    }

    /** 
     * 使用stream流去比较两个数组是否相等
     */
    private static boolean checkDiffrent(List<String> list, List<String> list1) {    
        /** 先将集合转成stream流进行排序然后转成字符串进行比较 */
        return list.stream().filter(k -> {return list.indexOf(k) <= list1.size() - 1;})
                .collect(Collectors.toList()).stream()
                .sorted().collect(Collectors.joining())
                .equals(list1.stream().sorted().collect(Collectors.joining()));
    }

    /**
     * 校验限制条数
     * @param file
     * @param limit 如果为null 默认为1000条
     * @return
     */
    public static boolean limitExcelCount(MultipartFile file,Integer limit) {
        boolean flag = false;
        Workbook inputBook = null;
        limit = Objects.isNull(limit) ? 1000 : limit;
        try {
            inputBook = checkExcelType(file);
            if(Objects.isNull(inputBook))
                return flag;
            Sheet inputSheet = inputBook.getSheetAt(0);
            int lastNum = inputSheet.getLastRowNum();
            log.info("input excel sheet lastNum :{}",lastNum);
            if(lastNum < limit) {
                flag = true;
            }

        }catch (IOException e) {
            log.error("",e);
        } finally {
            try {
                if(inputBook != null) {
                    inputBook.close();
                }
            } catch (IOException e) {
                log.error("",e);
            }
        }
        return flag;
    }

    private static Workbook checkExcelType(MultipartFile file) throws IOException {
        Workbook inputBook = null;
        String fileType = file.getOriginalFilename().split("\\.")[1];
        if("xls".equals(fileType)) {
            inputBook = new HSSFWorkbook(file.getInputStream());
        }else if("xlsx".equals(fileType)) {
            inputBook = new XSSFWorkbook(file.getInputStream());
        }
        return inputBook;
    }

}
