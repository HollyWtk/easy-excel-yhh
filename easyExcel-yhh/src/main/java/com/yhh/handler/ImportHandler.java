package com.yhh.handler;

import java.lang.reflect.Field;
import java.util.List;

import com.yhh.entity.CheckResult;
import com.yhh.entity.ImportBusiField;
import com.yhh.utils.ImportHandlerUtil;

/**  
 * <p>Description: excel导入数据处理接口</p>
 * @author yhh  
 * @date  2020年6月8日 下午3:25:58
 */
public interface ImportHandler<T> {

    /**
     * 校验导入excel数据
     * @param <T>
     * @param <T>
     * @param obj
     */
    default CheckResult validateData(T t,List<T> data) {
        return ImportHandlerUtil.checkExcelData(t,data);
    };

    /**
     * 判断DB数据是否重复
     * @param data
     * @return
     */
    CheckResult checkDataUnique(T data);


    /**
     * 保存数据
     * @param <T>
     * @param data
     * @param filed 自定义业务参数
     */
    void saveList(List<T> data,ImportBusiField filed);

    /**
     * 生成错误数据JSONObject
     * @param data
     * @param result
     * @return
     */
    default T errorList(T data,CheckResult result){
        Field errorFields = null;
        try {
            errorFields = data.getClass().getDeclaredField("errorDesc");
            errorFields.setAccessible(true);
            errorFields.set(data, result.getMessage());
        } catch (NoSuchFieldException e) {
            System.out.println("no such Field : " + e.getMessage());
            return data;
        } catch (SecurityException e) {
            e.printStackTrace();
        }catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return data;
    };

    /**
     * @param data
     * @param key  save Key
     */
    void handleErrorData(List<T> data,String key);

}
