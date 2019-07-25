package com.zhuanche.serv.supplier;

import com.github.pagehelper.PageInfo;
import com.zhuanche.entity.driver.SupplierLevel;
import com.zhuanche.entity.driver.SupplierLevelAdditional;

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
     * 根据结算日期生成“供应商等级分”，约定是"结算日+8天"开始计算结算信息，由于大数据组无法通过mq来通知这边哪些供应商的数据已经，所以这边采用
     * "定时任务+结算日+8天"的方案来实现，保证大数据组已经把数据生成的前提下进行计算这边的数据业务
     * @param supplierId  供应商id
     * @param settleStartTime  结算开始日期
     * @param settleEndTime  结算结束日期
     */
    public void doGenerateByDate(Integer supplierId,Date settleStartTime,Date settleEndTime);
}
