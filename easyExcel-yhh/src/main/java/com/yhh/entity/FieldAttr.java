package com.yhh.entity;

import lombok.Data;

/**  
 * <p>Description: </p>  
 * @author yhh  
 * @date 2020年9月11日  
 */
@Data
public class FieldAttr {

    private Object columnValue;
    
    private String columnName;
    
    private String field;
    
    private boolean noNull;
    
    private boolean unique;

}
