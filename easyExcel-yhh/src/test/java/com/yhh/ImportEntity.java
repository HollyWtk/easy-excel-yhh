package com.yhh;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.yhh.annotaion.CheckIfNull;
import com.yhh.annotaion.CheckIgnore;
import com.yhh.annotaion.CheckUnique;

import lombok.Data;

/**  
 * <p>Description: </p>  
 * @author yhh  
 * @date 2020年9月18日  
 */
@Data
public class ImportEntity {
    
    @ExcelProperty(value = "姓名")
    @CheckIfNull(noNull = true)
    private String username;
    
    @CheckIgnore
    @ExcelProperty(value = "年龄")
    private Integer age;
    
    @CheckUnique
    @ExcelProperty(value = "地址")
    private String addr;
    
    @ExcelProperty(value = "描述")
    private String desc;
    
    @ExcelIgnore
    private String errorDesc;
}
