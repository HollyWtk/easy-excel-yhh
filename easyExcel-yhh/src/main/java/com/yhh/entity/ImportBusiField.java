package com.yhh.entity;


import java.util.Map;

import lombok.Data;

/**  
 * <p>Description: 导入数据,自定义业务数据基类</p>  
 * @author yhh  
 * @date 2020年9月14日  
 */
@Data
public class ImportBusiField {

    /**
     * 用于储存错误数据key
     */
    private String errorKey;
    
    /**
     * 自定义业务字段
     */
    private Map<String,Object> exportFiled;
}
