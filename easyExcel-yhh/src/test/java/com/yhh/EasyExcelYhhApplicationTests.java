package com.yhh;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.alibaba.excel.EasyExcel;
import com.yhh.entity.ImportBusiField;
import com.yhh.listener.ImportListener;

@SpringBootTest
class EasyExcelYhhApplicationTests {

    @Autowired
    private DemoImportHandler handler;
    
    @Test
    void contextLoads() {
        File file = new File("C:\\Users\\Administrator\\Desktop\\demo.xlsx");
        ImportBusiField fields = new ImportBusiField();
        fields.setErrorKey("test_key");
        EasyExcel.read(file,ImportEntity.class,
                new ImportListener<ImportEntity>(handler, fields)).sheet().doRead();
    }

}
