package com.zhuanche.serv.punish;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.zhuanche.common.enums.EnumCallReleaseCause;
import com.zhuanche.common.enums.PunishEventEnum;
import com.zhuanche.common.enums.PunishStatusEnum;
import com.zhuanche.common.exception.ServiceException;
import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalDTO;
import com.zhuanche.entity.bigdata.MaxAndMinId;
import com.zhuanche.entity.driver.DriverAppealRecord;
import com.zhuanche.entity.driver.DriverPunish;
import com.zhuanche.entity.driver.DriverPunishDto;
import com.zhuanche.entity.driver.PunishRecordVoiceDTO;
import com.zhuanche.entity.driver.appraisa.UpdateAppraisalVo;
import com.zhuanche.entity.rentcar.DriverOutage;
import com.zhuanche.entity.rentcar.OrderVideoVO;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.serv.CustomerAppraisalService;
import com.zhuanche.serv.rentcar.DriverOutageService;
import com.zhuanche.serv.third.*;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.SignatureUtils;
import lombok.extern.slf4j.Slf4j;
import mapper.driver.DriverAppealRecordMapper;
import mapper.driver.DriverPunishMapper;
import mapper.driver.ex.DriverPunishExMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @Author:qxx
 * @Date:2020/4/9
 * @Description:
 */
@Slf4j
public class DriverPunishService {

    @Value("${virtual.url}")
    private String virtualUrl;

    /**
     * 未坐车生产费用处罚类型
     * config_punish_type_base 表主键
     */
    @Value("${punishType23}")
    private Integer punishType23;

    @Value("${prorate.new.url}")
    private String prorateNewUrl;

    @Autowired
    private DriverOutageService driverOutageService;
    @Resource
    private CustomerAppraisalService customerAppraisalService;
    @Resource
    private RiskOrderAppealClient riskOrderAppealClient;
    @Resource
    private CsApiClient csApiClient;
    @Resource
    private DriverIntegralClient driverIntegralClient;
    @Resource
    private MpRestApiClient mpRestApiClient;
    @Resource
    private MpManageRestClient mpManageRestClient;
    @Resource
    private MpConfigClient mpConfigClient;

    @Resource
    private DriverPunishMapper driverPunishMapper;
    @Resource
    private DriverPunishExMapper driverPunishExMapper;
    @Resource
    private DriverAppealRecordMapper driverAppealRecordMapper;

    public static final String TRIPARTITE_INSPECTION = "三方稽查";

    public static final String SEND_MSG_PREFIX = "尊敬的首约司机，您服务的订单";

    public static final String SEND_MSG_MIDDLE = "被乘客投诉";

    public static final String APPEAL_PASS_MSG = "该投诉已申诉通过，平台将不会再对您进行处罚。";

    public static final String BAD_APPEAL_PASS_MSG = "师傅您好！您的差评申诉成功，该差评会置为无效，平台不会扣除您的派单分，感谢您对平台的付出。";

    public static final String APPEAL_UN_PASS_MSG = "由于您的申诉未通过，平台将依据规则对您做出处罚。";

    public static final String BAD_APPEAL_UN_PASS_MSG = "师傅您好！您的差评申诉失败，该差评生效，平台将会扣除您的派单分，请您遵守服务准则，提升服务意识。";

    public static final String APPEAL_RETURN_MSG = "该申诉被驳回，请您及时补充材料进行二次申诉，逾时未申诉平台将依据规则对您做出处罚。";

    /**
     * 车管后台
     */
    public static final byte PUNISH_AUDIT_CAR_MANAGE = 1;

    /******* 司机处罚类型 driver_punish punishType ************/
    public static final Integer PUNISH_TYPE_1 = 1;
    public static final Integer PUNISH_TYPE_2 = 2;
    public static final Integer PUNISH_TYPE_3 = 3;
    public static final Integer PUNISH_TYPE_4 = 4;
    public static final Integer PUNISH_TYPE_5 = 5;




    public PageInfo<DriverPunishDto> selectList(DriverPunishDto params) {
        return selectList(params, true);
    }

    public PageInfo<DriverPunishDto> selectList(DriverPunishDto params, Boolean isCount) {
        int total = 0;
        List<DriverPunishDto> rows = null;
        if (params.getPage() == null) {
            params.setPage(1);
        }
        if (params.getPagesize() == null) {
            params.setPagesize(10);
        }
        Page p = PageHelper.startPage(params.getPage(), params.getPagesize(), isCount);
        PageInfo<DriverPunishDto> pageInfo = null;
        try {
            rows = driverPunishExMapper.selectList(params);
            total = (int) p.getTotal();
        } catch (Exception e) {
            log.error("出租订单信息", e);
        } finally {
            PageHelper.clearPage();
        }
        if (rows == null || rows.size() == 0) {
            return new PageInfo<>(new ArrayList<>());
        }
        pageInfo = new PageInfo<>(rows);
        pageInfo.setTotal(total);
        return pageInfo;
    }

    public DriverPunishDto getDetail(Integer punishId) {
        if (punishId != null) {
            DriverPunishDto driverPunishDto = driverPunishExMapper.getDetail(punishId);
            if (driverPunishDto.getCreateDate() != null) {
                driverPunishDto.setCreateDateStr(DateUtils.formatDateTime(driverPunishDto.getCreateDate()));
            }
            return driverPunishDto;
        }
        return null;
    }



    /**
     * 查询司机录音
     * @param orderNo 订单
     * @return
     */
    public List<PunishRecordVoiceDTO> videoRecordQuery(String orderNo) {
        return mpManageRestClient.driverPunishVideoQuery(orderNo);
    }


    /**
     * 处罚审核操作
     *
     * @param punishId
     * @param status   3:通过, 4:拒绝, 5:驳回
     * @param cgReason
     */
    public void doAudit(Integer punishId, Integer status, String cgReason, String cgPictures) {
        DriverPunishDto punishEntity = driverPunishExMapper.getDetail(punishId);
        if (Objects.isNull(punishEntity) || !Objects.equals(PUNISH_AUDIT_CAR_MANAGE, punishEntity.getCurrentAuditNode())) {
            throw new IllegalArgumentException("没有需求车管审核的记录");
        }

        Map<String, Object> params = new HashMap<>();
        Integer currentAuditNode = 0;
        String auditNode = "无";
        String phone = punishEntity.getPhone();
        String orderNo = punishEntity.getOrderNo();

        if (status == PunishEventEnum.AUDIT_PASS.getStatus()) {
            //审核通过
            logicIfPass(punishId, status, cgReason, punishEntity, params, currentAuditNode, auditNode, status, phone, orderNo);
        } else if (status == PunishEventEnum.AUDIT_REFUSE.getStatus()) {
            //审核拒绝,处罚和申述记录都要更新为审核不通过
            logicIfRefuse(punishId, status, cgReason, punishEntity, params, currentAuditNode, auditNode, phone, orderNo);
        } else if (status == PunishEventEnum.REJECT.getStatus()) {
            // 审核驳回
            logicIfReject(punishId, status, cgReason, punishEntity, params, currentAuditNode, auditNode, phone, orderNo);
        }
    }

    /**
     * 审核通过逻辑处理
     */
    private void logicIfPass(Integer punishId, Integer status, String cgReason, DriverPunish punishEntity, Map<String, Object> params, Integer currentAuditNode, String auditNode, Integer cgStatus, String phone, String orderNo) {
        // 查询策略配置
        Map<String, String> configMap = mpRestApiClient.getPunishConfig(punishEntity);
        // 处理后台(0-车管后台 1-业务后台)
        String dealBackground = Optional.ofNullable(configMap.get("dealBackground")).map(String::valueOf).orElse("");

        String[] deals = dealBackground.split(",");
        if (StringUtils.isNotBlank(dealBackground) && deals.length > 1) {
            //如果需要，处罚和申述记录都要保持状态为待审核,currentAuditNode = 2; auditNode = "业务后台";
            status = 2;
            currentAuditNode = 2;
            auditNode = "业务后台";
            params.put("ywStatus", 2);
            // 后台处理时长(车管,业务)
            String dealDuration = Optional.of(configMap.get("dealDuration")).map(String::valueOf).orElse("");
            String[] dealDurations = dealDuration.split(",");
            if (dealDurations.length > 1) {
                dealDuration = dealDurations[1];
            }
            log.info("车管审批之后处理超时时间,dealDuration=" + dealDuration);
            Date expire = org.apache.commons.lang3.time.DateUtils.addHours(new Date(), Integer.parseInt(dealDuration));
            params.put("expireDate", expire);
            // 更新状态
            this.carManageSave(punishId, status, cgReason, params, currentAuditNode, auditNode, cgStatus);
            // 同步状态到风控系统
            riskOrderAppealClient.updateStatus(punishEntity, 2);

            if (punishEntity.getPunishTypeName().contains(TRIPARTITE_INSPECTION)) {
                csApiClient.notifyKefuUpdate(punishEntity, 4);
            }
        } else {
            //如果不需要，处罚和申述记录都要更新为审核通过,并且需要调用接口，把处罚内容重新加回去
            // 更新状态
            this.carManageSave(punishId, status, cgReason, params, currentAuditNode, auditNode, cgStatus);
            doWithPunishTypeWhenPass(punishEntity, phone, orderNo);
        }
    }

    private void doWithPunishTypeWhenPass(DriverPunish punishEntity, String phone, String orderNo) {
        Integer punishType = punishEntity.getPunishType();
        if (punishType.equals(PUNISH_TYPE_2)) {
            // 取消风控处理
            riskOrderAppealClient.updateStatus(punishEntity, 3);
        }
        // 如果是差评处罚，审核通过后，需要将差评置为无效，并且通知司机不会再扣分
        else if (PUNISH_TYPE_5.equals(punishEntity.getPunishType())) {
            CarBizCustomerAppraisalDTO customerAppraisalEntity = customerAppraisalService.queryForObject(orderNo);
            if (customerAppraisalEntity == null) {
                throw new ServiceException(RestErrorCode.RECORD_DEAL_FAILURE, "根据订单号未找到对应的差评记录");
            }
            UpdateAppraisalVo vo = new UpdateAppraisalVo();
            vo.setAppraisalId(customerAppraisalEntity.getAppraisalId());
            vo.setStatus(1);
            vo.setOrderNo(orderNo);
            SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
            vo.setModifyBy(currentLoginUser.getId());
            vo.setModifyName(currentLoginUser.getName());
            mpRestApiClient.updateAppraisalStatus(vo);
            this.sendSingleAndMessageForBadAppeal("申诉结果通知", BAD_APPEAL_PASS_MSG, punishEntity.getDriverId(), phone);

        } else {
            if (punishEntity.getPunishType() != null && punishEntity.getPunishType() == 1) {
                //解除司机停运（如果有停运）；
                csApiClient.kefuCancelPunishNew(punishEntity.getStopId(), 1);
                //给司机发送短信和站内信
                log.info("车管后台司机申诉通过，对司机发送短信和站内信，司机id=" + punishEntity.getDriverId());
                this.sendSingleAndMessage("申诉结果通知", orderNo, punishEntity.getDriverId(), phone, punishEntity.getPunishReason(), APPEAL_PASS_MSG);
            }
        }
    }


    /**
     * 审核拒绝逻辑处理
     */
    private void logicIfRefuse(Integer punishId, Integer status, String cgReason, DriverPunish punishEntity, Map<String, Object> params, Integer currentAuditNode, String auditNode, String phone, String orderNo) {
        // 更新状态
        this.carManageSave(punishId, status, cgReason, params, currentAuditNode, auditNode, status);
        // case punishType
        this.doWithPunishTypeWhenRefuse(punishEntity, phone, orderNo);
        //司机保障计划要求客服和风控订单调用策略工具的接口
        this.guaranteePlanOrder(punishEntity.getPunishType(), punishEntity.getDriverId(), punishEntity.getOrderNo());

    }

    private void doWithPunishTypeWhenRefuse(DriverPunish punishEntity, String phone, String orderNo) {
        Integer punishType = punishEntity.getPunishType();
        //未乘车产生费用
        if (punishType.equals(punishType23)){
            log.info("未乘车产生费用punishId:{}", punishEntity.getPunishId());
            //1.处罚金额
            if (punishEntity.getPunishPrice().compareTo(new BigDecimal(0)) > 0) {
                log.info("doPunish处罚金额 punishId:{}", punishEntity.getPunishId());
                try {
                    String result1 = HttpClientUtil.buildPostRequest(prorateNewUrl + "/rewardPunishInput/add")
                            .setConnectTimeOut(3000)
                            .setReadTimeOut(3000)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .addParam("driverId", punishEntity.getDriverId())
                            .addParam("rewardPunishId", "P1722971111584461981188096")
                            .addParam("orderNo", punishEntity.getOrderNo())
                            .addParam("amount", "-" + punishEntity.getPunishPrice())
                            .addParam("summary", punishEntity.getPunishReason())
                            .execute();
                    log.info("rewardPunishInput/add return:{}", result1);
                } catch (HttpException e) {
                    log.error("rewardPunishInput/add punishId:{} exception: ", punishEntity.getPunishId(), e);
                }
            }
            //2.停运
            DriverOutage outageEntity = new DriverOutage();
            outageEntity.setDriverId(punishEntity.getDriverId());
            outageEntity.setOutStopLongTime(punishEntity.getStopDay().doubleValue());
            outageEntity.setOutageReason(punishEntity.getPunishReason());
            outageEntity.setOutStartDateStr(DateUtil.getTimeString(new Date()));
            driverOutageService.saveDriverOutage(outageEntity);
            //3.司机积分调用策略工具接口
            driverIntegralClient.paidanfen2(punishEntity.getDriverId(), punishEntity.getOrderNo());
            this.sendSingleAndMessageForBadAppeal("申诉结果通知", APPEAL_UN_PASS_MSG, punishEntity.getDriverId(), phone);
        } else
        if (PUNISH_TYPE_2.equals(punishType)) {
            // 风控处理
            riskOrderAppealClient.updateStatus(punishEntity, 4);
        }
        // 如果是差评处罚，审核拒绝后，按照规则对司机进行扣分处罚
        else if (PUNISH_TYPE_5.equals(punishEntity.getPunishType())) {
            // 通过mp-rest api发送消息到driverScore topic,进行扣分处理
            this.sendDriverScoreMsg(orderNo);
            this.sendSingleAndMessageForBadAppeal("申诉结果通知", BAD_APPEAL_UN_PASS_MSG, punishEntity.getDriverId(), phone);
        } else {
            if (punishEntity.getPunishType() != null && PUNISH_TYPE_1.equals(punishEntity.getPunishType())) {
                //按照策略对司机进行相关处罚
                csApiClient.kefuCancelPunishNew(punishEntity.getStopId(), 2);
                //给司机发送短信和站内信
                log.info("车管后台司机申诉拒绝，对司机发送短信和站内信，司机id=" + punishEntity.getDriverId());
                this.sendSingleAndMessage("申诉结果通知", orderNo, punishEntity.getDriverId(), phone, punishEntity.getPunishReason(), APPEAL_UN_PASS_MSG);
                //司机积分调用策略工具接口
                driverIntegralClient.driverIntegralStrategyUrl(punishEntity.getDriverId(), punishEntity.getOrderNo(), punishEntity.getCreateDate(), punishEntity.getPunishReason());
            } else if (punishEntity.getPunishTypeName().contains(TRIPARTITE_INSPECTION)) {
                csApiClient.notifyKefuUpdate(punishEntity, 6);
            }
        }
    }

    /**
     * 审核驳回逻辑处理
     */
    private void logicIfReject(Integer punishId, Integer status, String cgReason, DriverPunish punishEntity, Map<String, Object> params, Integer currentAuditNode, String auditNode, String phone, String orderNo) {
        // 1、查询策略配置
        Map<String, String> configMap = mpRestApiClient.getPunishConfig(punishEntity);

        // 2、更新
        int appealSecond = 0;
        if (null != configMap && StringUtils.isNotBlank(String.valueOf(configMap.get("appealDurationSecond")))) {
            appealSecond = Integer.parseInt(String.valueOf(configMap.get("appealDurationSecond")));
            log.info("驳回添加时长.punishId=" + punishId + "appealSecond=" + appealSecond);
        }
        Date expire = org.apache.commons.lang.time.DateUtils.addHours(new Date(), appealSecond);
        params.put("expireDate", expire);
        this.carManageSave(punishId, status, cgReason, params, currentAuditNode, auditNode, status);

        //3、三方稽查
        if (punishEntity.getPunishTypeName().contains(TRIPARTITE_INSPECTION)) {
            csApiClient.notifyKefuUpdate(punishEntity, 7);
        }
        //4、给司机发送短信和站内信
        log.info("车管后台司机申诉驳回，对司机发送短信和站内信，司机id=" + punishEntity.getDriverId());
        this.sendSingleAndMessage("审核结果通知", orderNo, punishEntity.getDriverId(), phone, punishEntity.getPunishReason(), APPEAL_RETURN_MSG);
    }


    /**
     * 给司机发送短信和站内信
     */
    private void sendSingleAndMessage(String title, String orderNo, Integer driverId, String phone, String punishReason, String msgEnd) {
        String content = SEND_MSG_PREFIX + "[" + orderNo + "]" + SEND_MSG_MIDDLE + "[" + punishReason + "]。" + msgEnd;
        log.info("司机申诉未通过，对司机发送短信和站内信，司机id=" + driverId);
        try {
            //发送站内信
            mpConfigClient.singleDriverPush(title, content, content, driverId, phone);
            //发送短信
            SmsSendUtil.send(phone, content);
        } catch (Exception e) {
            log.error("短信、站内信发送错误   error:" + e);
        }
    }

    public void sendSingleAndMessageForBadAppeal(String title, String content, Integer driverId, String phone) {
        try {
            log.info("司机申诉结果通知，对司机发送短信和站内信，司机id=" + driverId);
            //发送站内信
            mpConfigClient.singleDriverPush(title, content, content, driverId, phone);
            //发送短信
            SmsSendUtil.send(phone, content);
        } catch (Exception e) {
            log.error("短信、站内信发送错误   error:" + e);
        }
    }


    /**
     * 车管审核保存
     */
    private void carManageSave(Integer punishId, Integer status, String cgReason,
                               Map<String, Object> params, Integer currentAuditNode, String auditNode, Integer cgStatus) {
        log.info("车管审核 punishId:{},params :{},status:{},cgReason:{},cgStatus:{}", punishId, JSONObject.toJSONString(params), status, cgReason, cgStatus);
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        Date expireDate = (Date) params.get("expireDate");
        Byte ywStatus = Optional.ofNullable(params.get("ywStatus")).map(e -> ((Integer) e).byteValue()).orElse(null);

        DriverPunish driverPunish = DriverPunish.builder()
                .currentAuditNode(currentAuditNode.byteValue())
                .auditNode(auditNode)
                .status(status.byteValue())
                .punishId(punishId)
                .expireDate(expireDate)
                .build();
        log.info("update driverPunish ,param :{}", JSONObject.toJSONString(driverPunish));
        driverPunishMapper.updateByPrimaryKeySelective(driverPunish);

        DriverAppealRecord appealRecord = DriverAppealRecord.builder()
                .status(status.byteValue())
                .cgReason(cgReason)
                .cgStatus(cgStatus.byteValue())
                .cgOperator(currentLoginUser.getName())
                .cgOperateDate(new Date())
                .ywStatus(ywStatus)
                .ywExpireDate(expireDate)
                .punishId(punishId)
                .build();
        log.info("update appealRecord ,param :{}", JSONObject.toJSONString(appealRecord));
        driverAppealRecordMapper.updateNotRejectedByPunishId(appealRecord);
    }


    /**
     * 发送差评处罚信息到mp-restapi,进行扣分处理
     *
     * @param orderNo 订单号
     */
    private void sendDriverScoreMsg(String orderNo) {
        log.info("sendDriverScoreMsg 发送消息 /api/punish/sendDriverScoreMsg");
        CarBizCustomerAppraisalDTO customerAppraisalEntity = customerAppraisalService.queryForObject(orderNo);
        if (customerAppraisalEntity == null) {
            log.info("sendDriverScoreMsg 根据订单号{}未找到对应的差评记录", orderNo);
            return;
        }
        mpRestApiClient.sendDriverScoreMsg(customerAppraisalEntity);
    }


    /**
     * 保障计划订单状态接口
     * @param driverId
     * @param orderNo
     * http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=31808289
     */
    private void guaranteePlanOrder(Integer punishType,Integer driverId,String orderNo){
        try {
            if (punishType != null && (punishType.equals(PUNISH_TYPE_1) || punishType.equals(PUNISH_TYPE_2))) {
                //调用策略工具接口
                if (punishType.equals(PUNISH_TYPE_1)) {
                    driverIntegralClient.guaranteePlanOrderUrl(driverId, orderNo, 2);
                } else {
                    driverIntegralClient.guaranteePlanOrderUrl(driverId, orderNo, 3);
                }
            }
        } catch (Exception e) {
            log.info("调用保障计划订单状态接口出错  error:" + e);
        }

    }


}
