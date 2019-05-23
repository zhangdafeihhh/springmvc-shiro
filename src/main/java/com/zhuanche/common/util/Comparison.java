package com.zhuanche.common.util;  
/**  
 * ClassName:Comparison <br/>  
 * Date:     2019年4月19日 上午10:50:24 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
public class Comparison {
    //字段
    private String Field;
    //字段旧值
    private Object before;
    //字段新值
    private Object after;
    
    /**
     * @return the field
     */
    public String getField() {
        return Field;
    }
    /**
     * @param field the field to set
     */
    public void setField(String field) {
        Field = field;
    }
    /**
     * @return the before
     */
    public Object getBefore() {
        return before;
    }
    /**
     * @param before the before to set
     */
    public void setBefore(Object before) {
        this.before = before;
    }
    /**
     * @return the after
     */
    public Object getAfter() {
        return after;
    }
    /**
     * @param after the after to set
     */
    public void setAfter(Object after) {
        this.after = after;
    }
    
}
  
