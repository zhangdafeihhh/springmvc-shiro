package com.zhuanche.controller.supplier;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.driver.supplier.SupplierAccountApplyDTO;
import com.zhuanche.entity.driver.SupplierAccountApply;
import com.zhuanche.entity.driver.SupplierCheckFail;
import com.zhuanche.serv.supplier.SupplierAccountApplyService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.driver.ex.SupplierCheckFailExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

import static com.zhuanche.common.enums.MenuEnum.*;

@RequestMapping("/supplierAccountApply")
@Controller
public class SupplierAccountApplyController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierAccountApplyController.class);

    @Resource
    private SupplierAccountApplyService supplierAccountApplyService;

    @Autowired
    private SupplierCheckFailExMapper exMapper;

    /**
     * 根据供应商查询所有申请修改信息
     * @param supplierId
     * @return
     */
    @ResponseBody
    @RequestMapping("/querySupplierAccountApplyAllList")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = SUPPLIER_DETAIL)
    public AjaxResponse querySupplierAccountApplyAllList(@Verify(param = "supplierId",rule="required") Integer supplierId){
        logger.info("[供应商申请修改账户信息]查询供应商申请修改列表 supplierId={}", supplierId);
        List<SupplierAccountApply> list = supplierAccountApplyService.selectApplyAllBySupplierId(supplierId);
        return AjaxResponse.success(list);
    }


    /**
     * 根据供应商id查询处于申请中状态的申请修改信息
     * @param supplierId
     * @return
     */
//    @ResponseBody
//    @RequestMapping("/querySupplierAccountApplyList")
//    @MasterSlaveConfigs(configs = {
//            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
//    })
//    @RequestFunction(menu = SUPPLIER_LIST)
//    public AjaxResponse querySupplierAccountApplyList(@Verify(param = "supplierId",rule="required") Integer supplierId){
//        logger.info("[供应商申请修改账户信息]根据供应商查询所有申请修改信息 supplierId={}", supplierId);
//        SupplierAccountApply supplierAccountApply = supplierAccountApplyService.selectApplyBySupplierId(supplierId);
//        return AjaxResponse.success(supplierAccountApply);
//    }

    /**
     * 根据id查询所有申请修改信息
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/querySupplierAccountApplyById")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = SUPPLIER_ACCOUNT_APPLIY_DETAIL)
    public AjaxResponse querySupplierAccountApplyById(@Verify(param = "id",rule="required") Long id){
        logger.info("[供应商申请修改账户信息]根据供应商查询所有申请修改信息 id={}", id);
        SupplierAccountApply supplierAccountApply = supplierAccountApplyService.selectByPrimaryKey(id);
        List<SupplierCheckFail> list = exMapper.failList(supplierAccountApply.getSupplierId());
        supplierAccountApply.setList(list);
        return AjaxResponse.success(supplierAccountApply);
    }


    /**
     * 新增一个申请更新账户数据
     * @param supplierId 供应商id
     * @param settlementAccount 打款账户名称
     * @param bankAccount 打款银行账号
     * @param bankName 开户行名称
     * @param bankIdentify 联行号
     * @param settlementFullName 结算供应商全称
     * @return
     */
    @ResponseBody
    @RequestMapping("/addSupplierAccountApply")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = SUPPLIER_ACCOUNT_APPLY_ADD)
    public AjaxResponse updateSupplier(@Verify(param = "cityId",rule="required") Integer cityId,
                                       @Verify(param = "supplierId",rule="required") Integer supplierId,
                                       @Verify(param = "settlementAccount",rule="required") String settlementAccount,
                                       @Verify(param = "bankAccount",rule="required") String bankAccount,
                                       @Verify(param = "bankName",rule="required") String bankName,
                                       @Verify(param = "bankIdentify",rule="required") String bankIdentify,
                                       @Verify(param = "settlementFullName",rule="required") String settlementFullName){

        logger.info("[供应商申请修改账户信息]申请修改信息新增 cityId={}, supplierId={}, settlementAccount={}, bankAccount={}, " +
                "bankName={}, bankIdentify={}, settlementFullName={}",
                cityId, supplierId, settlementAccount, bankAccount, bankName, bankIdentify, settlementFullName);

        SupplierAccountApply supplierAccountApply = supplierAccountApplyService.selectApplyBySupplierId(supplierId);
        if(supplierAccountApply!=null){
            return AjaxResponse.fail(RestErrorCode.SUPPLIER_ACCOUNT_APPLY_EXIST);
        }
        SupplierAccountApply params = new SupplierAccountApply();
        params.setCityId(cityId);
        params.setSupplierId(supplierId);
        params.setSettlementAccount(settlementAccount);
        params.setBankAccount(bankAccount);
        params.setBankName(bankName);
        params.setBankIdentify(bankIdentify);
        params.setSettlementFullName(settlementFullName);
        params.setStatus((byte)1);
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        params.setCreateBy(currentLoginUser.getId());
        params.setCreateName(currentLoginUser.getName());
        params.setUpdateBy(currentLoginUser.getId());
        params.setUpdateName(currentLoginUser.getName());
        return supplierAccountApplyService.addSupplierAccountApply(params);
    }

    @ResponseBody
    @RequestMapping("/examineSupplierAccountApply")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = SUPPLIER_ACCOUNT_APPLIY_EXAMINE)
    public AjaxResponse updateSupplier(@Verify(param = "id",rule="required") Long id,
                                       @Verify(param = "cityId",rule="required") Integer cityId,
                                       @Verify(param = "supplierId",rule="required") Integer supplierId,
                                       @Verify(param = "settlementAccount",rule="required") String settlementAccount,
                                       @Verify(param = "bankAccount",rule="required") String bankAccount,
                                       @Verify(param = "bankName",rule="required") String bankName,
                                       @Verify(param = "bankIdentify",rule="required") String bankIdentify,
                                       @Verify(param = "settlementFullName",rule="required") String settlementFullName,
                                       @Verify(param = "bankPicUrl",rule = "required")String bankPicUrl,
                                       @Verify(param = "officalSealUrl",rule = "required")String officalSealUrl){

        logger.info("[供应商申请修改账户信息]申请修改信息审核 id={}, cityId={}, supplierId={}, settlementAccount={}, bankAccount={}, " +
                        "bankName={}, bankIdentify={}, settlementFullName={},bankPicUrl={},officalSealUrl={}",
                id, cityId, supplierId, settlementAccount, bankAccount, bankName, bankIdentify, settlementFullName,
                bankPicUrl,officalSealUrl);

        return supplierAccountApplyService.updateSupplierAccountApplyStatua(id, cityId, supplierId, settlementAccount,
                bankAccount, bankName, bankIdentify, settlementFullName,bankPicUrl,officalSealUrl);
    }

    /**
     * 查询供应商查询申请修改信息审核列表
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryApplyList")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    @RequestFunction(menu = SUPPLIER_ACCOUNT_APPLIY_LIST)
    public AjaxResponse queryApplyList(Integer cityId, Integer supplierId, Integer status,
                                       @RequestParam(value="page", defaultValue="0")Integer page,
                                       @RequestParam(value="pageSize", defaultValue="20")Integer pageSize){
        logger.info("[供应商申请修改账户信息]供应商查询申请修改信息审核列表 cityId={}, supplierId={}, status={}, page={}, pageSize={}",
                cityId, supplierId, status, page, pageSize);

        int total = 0;
        List<SupplierAccountApplyDTO> list =  Lists.newArrayList();

        Page p = PageHelper.startPage(page, pageSize, true);
        try {
            list = supplierAccountApplyService.queryApplyList(cityId, supplierId, status);
            total = (int)p.getTotal();
        } finally {
            PageHelper.clearPage();
        }
        PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
        return AjaxResponse.success(pageDTO);
    }
}
