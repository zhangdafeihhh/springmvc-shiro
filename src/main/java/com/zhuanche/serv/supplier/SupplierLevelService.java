package com.zhuanche.serv.supplier;

import com.github.pagehelper.PageInfo;
import com.zhuanche.dto.driver.SupplierLevelAdditionalDto;
import com.zhuanche.entity.driver.SupplierLevel;
import com.zhuanche.entity.driver.SupplierLevelAdditional;

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
    public void doDeleteBySupplierLevelId(Integer supplierLevelAdditionalId);

    /**
     * 批量导入附加分
     * @param list
     */
    public void doImportSupplierLevelAdditional(List<SupplierLevelAdditional> list);
}
