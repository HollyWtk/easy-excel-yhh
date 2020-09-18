package com.yhh;

import java.util.List;

import org.springframework.stereotype.Component;

import com.yhh.entity.CheckResult;
import com.yhh.entity.ImportBusiField;
import com.yhh.handler.ImportHandler;

/**  
 * <p>Description: </p>  
 * @author yhh  
 * @date 2020年9月18日  
 */
@Component
public class DemoImportHandler implements ImportHandler<ImportEntity>{

    @Override
    public CheckResult checkDataUnique(ImportEntity data) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveList(List<ImportEntity> data, ImportBusiField filed) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void handleErrorData(List<ImportEntity> data, String key) {
        // TODO Auto-generated method stub
        
    }


}
