package com.yhh.entity;

import lombok.Builder;
import lombok.Data;

/**  
 * <p>Description: </p>  
 * @author yhh  
 * @date 2020年9月11日  
 */
@Data
@Builder
public class CheckResult {
    
    private boolean result;
    
    private String message;
    
    public static CheckResult SUCCESS() {
        return CheckResult.builder().result(true).message("success").build();
    }
    
    public static CheckResult FAILED(String message) {
        return CheckResult.builder().result(false).message(message).build();
    }
}
