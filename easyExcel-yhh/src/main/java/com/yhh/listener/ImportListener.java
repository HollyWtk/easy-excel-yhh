package com.yhh.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSONObject;
import com.yhh.entity.CheckResult;
import com.yhh.entity.ImportBusiField;
import com.yhh.handler.ImportHandler;

import lombok.extern.slf4j.Slf4j;

/**  
 * <p>Description: </p>  
 * @author yhh  
 * @date 2020年9月11日  
 */
@Slf4j
public class ImportListener<T> extends AnalysisEventListener<T>{

    /**
     * 一次插入数据库条数
     */
    private static final int BATCH_COUNT = 100;
    
    List<T> saveList = new ArrayList<>(BATCH_COUNT);
    
    List<JSONObject> errorList = new ArrayList<>();
    
    private ImportHandler<T> importHandler;
    
    private ImportBusiField fields;
    
    public ImportListener(ImportHandler<T> importHandler,ImportBusiField fields){
        this.importHandler = importHandler;
        this.fields = fields;
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        CheckResult result = importHandler.validateData(data, saveList);
        if(result.isResult()) {
            result = importHandler.checkDataUnique(data);
            if(Objects.isNull(result) || result.isResult()) {
                saveList.add(data);
            }else {
                errorList.add(importHandler.errorList(data,result));
            }
        }else {
            errorList.add(importHandler.errorList(data,result));
        }
        if (saveList.size() >= BATCH_COUNT) {
            importHandler.saveList(saveList,fields);
            saveList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if(errorList.size() > 0) {
            log.warn("error data :{}",errorList);
            importHandler.handleErrorData(errorList,fields.getErrorKey());
        }
        importHandler.saveList(saveList, fields);
    }

}
