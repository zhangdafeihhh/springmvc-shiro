package com.zhuanche.serv.supplier;

import com.github.pagehelper.PageInfo;
import com.zhuanche.entity.driver.SupplierLevel;
import com.zhuanche.entity.driver.SupplierLevelAdditional;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface SupplierLevelService {

    public PageInfo<SupplierLevel> findPage(int pageNo,int pageSize,SupplierLevel params);

    /**
     * 发布供应商等级积分
     * @param idList
     */
    public void doPublishSupplierLevel(List<Integer> idList);

    /**
     * 取消发布等级积分
     * @param idList
     */
    public void doUnPublishSupplierLevel(List<Integer> idList);

    /**
     *
     * 查看供应商的附加项目
     * @param supplierLevelId
     * @return
     */
    public List<SupplierLevelAdditional> findSupplierLevelAdditionalBySupplierLevelId(Integer supplierLevelId);

    /**
     *
     * 删除供应商等级-附加分项目
     * @param supplierLevelAdditionalId
     */
    public void doDeleteBySupplierLevelAdditionalId(Integer supplierLevelAdditionalId);

    /**
     * 批量导入附加分
     * @param list
     */
    public void doImportSupplierLevelAdditional(List<SupplierLevelAdditional> list);

    /**
     * 根据供应商名称和月份来查询供应商等级信息
     * @param month
     * @param supplierName
     * @return
     */
    SupplierLevel findByMonthAndSupplierName(String month, String supplierName);


    public SupplierLevelAdditional findBySupplierLevelIdAndSupplierLevelAdditionalName(Integer supplierLevelId,String supplierLevelAdditionalName);


    /**
     * @param month  供应商id
     */
    public void doGenerateByDate(String month ) throws ParseException;

    /**
     * 保存修改后的附加分
     * @param delIds
     * @param saveJson
     */
    void doSaveSupplierLevelAdditionScore(String delIds, String saveJson);
}
