package com.zhuanche.serv.message;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.Constants;
import com.zhuanche.constants.FtpConstants;
import com.zhuanche.dto.mdbcarmanage.CarMessageDetailDto;
import com.zhuanche.dto.mdbcarmanage.CarMessagePostDto;
import com.zhuanche.dto.mdbcarmanage.MessageDocDto;
import com.zhuanche.dto.mdbcarmanage.ReadRecordDto;
import com.zhuanche.entity.mdbcarmanage.*;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.exception.MessageException;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.util.FtpUtil;
import com.zhuanche.util.FtpUtils;
import com.zhuanche.util.dateUtil.DateUtil;
import mapper.mdbcarmanage.ex.*;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author fanht
 * @Description
 * @Date 2018/11/22 下午5:18
 * @Version 1.0
 */
@Service
public class MessageService {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CarMessagePostExMapper postExMapper;

    @Autowired
    private CarMessageReceiverExMapper receiverExMapper;

    @Autowired
    private CarMessageDocExMapper docExMapper;

    @Autowired
    private CarBizCityExMapper carBizCityExMapper;

    @Autowired
    private CarBizSupplierExMapper carBizSupplierExMapper;

    @Autowired
    private CarDriverTeamExMapper carDriverTeamExMapper;

    @Autowired
    private CarAdmUserExMapper carAdmUserExMapper;

    @Autowired
    private CarMessagePostDtoExMapper postDtoExMapper;

    @Autowired
    private FtpUtil ftpUtil;

    @Autowired
    private MessageReceiveService receiveService;


    /**
     * 发布消息和草稿
     * @param messageId
     * @param status 1 草稿 2 发布
     * @param userId
     * @param messageTitle
     * @param messageContent
     * @param level
     * @param cities
     * @param suppliers
     * @param teamId
     * @param docName
     * @param docUrl
     * @param file
     * @param request
     * @return
     * @throws MessageException
     */
    @Transactional(rollbackFor = MessageException.class)
    public int postMessage(Integer messageId,
                           Integer status,
                           Integer userId,
                           String messageTitle,
                           String messageContent,
                           Integer level,
                           String cities,
                           String suppliers,
                           String teamId,
                           String docName,
                           String docUrl,
                           MultipartFile file,
                           HttpServletRequest request) throws MessageException{

         AtomicInteger  resultOpe = new AtomicInteger(0);

        try {
            try {
                CarMessagePost post = new CarMessagePost();
                post.setCities(cities);
                post.setUpdateTime(new Date());
                post.setUserId(userId);
                post.setLevel(level);
                post.setMesageTitle(messageTitle);
                post.setMessageContent(messageContent);
                post.setStatus(status);
                post.setSuppliers(suppliers);
                post.setTeamids(teamId);

                if (messageId == null) {
                    //主表插入数据
                    post.setCreateTime(new Date());
                    postExMapper.insertSelective(post);
                    messageId = post.getId().intValue();
                    resultOpe.addAndGet(1);
                }else {
                    post.setId(messageId.longValue());
                    messageId = postExMapper.updateByPrimaryKeySelective(post);
                    resultOpe.addAndGet(1);
                }

                final   Integer newMessageId = messageId;

                if (newMessageId > 0){
                    logger.info("消息发布成功！messageId=" + messageId);
                    if (status.equals(CarMessagePost.Status.publish.getMessageStatus())){
                        ExecutorService executor = Executors.newCachedThreadPool();
                        Future<String> future = executor.submit(new Callable<String>() {
                            @Override
                            public String call() throws Exception {
                              int code =  receiveService.sendMessage(newMessageId,level,cities,suppliers,teamId);
                              logger.info("异步发送消息" + code);
                              return String.valueOf(code);
                            }
                        });
                    }

                    //上传附件
                    try {
                        if (file != null && !file.isEmpty()){
                            MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;

                            Map<String,MultipartFile> map = new HashMap<>();

                            Collection<MultipartFile> multipartFileCollection = req.getFileMap().values();

                            for (MultipartFile multFile : multipartFileCollection){
                                map.put(multFile.getName(),multFile);
                            }

                            MultipartFile multipartFile;
                            for (Map.Entry<String,MultipartFile> entry: map.entrySet()){
                                multipartFile = entry.getValue();

                                Map<String,Object> result = this.upload(multipartFile);
                                Boolean ok = (Boolean) result.get("ok");
                                if (ok== null || !ok){
                                    logger.error("消息中心-上传附件-异常");

                                }else {
                                    CarMessageDoc doc = new CarMessageDoc();
                                    doc.setDocName(result.get("fileName").toString());
                                    doc.setCreateTime(new Date());
                                    doc.setMessageId(messageId);
                                    doc.setUpdateTime(new Date());
                                    doc.setDocUrl(FtpConstants.FTP+FtpConstants.FTPURL+":"+FtpConstants.FTPPORT + result.get("oppositeUrl").toString());
                                    doc.setState(status);
                                    int code = docExMapper.insert(doc);
                                    if (code > 0 ){
                                        resultOpe.addAndGet(1);
                                        logger.info("====doc文档上传成功====");
                                    }else {
                                        logger.info("====doc上传文档失败======");
                                    }

                                }

                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (resultOpe.get() == 2){

                return 1;
            }else {
                logger.info("新建消息异常，数据回滚");
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new RuntimeException();
            }
        } catch (Exception e) {
            logger.info("新建消息异常" + e.getMessage());
            throw new MessageException(RestErrorCode.UNKNOWN_ERROR,RestErrorCode.renderMsg(RestErrorCode.UNKNOWN_ERROR));
        }
    }


    /**
     * 撤回操作
     * @param messageId
     * @return
     */
    public int withDraw(Integer messageId) throws MessageException{

        try {
             receiverExMapper.deleteByMessageId(messageId.longValue());

            //撤回后状态变为草稿
             docExMapper.updateStatus(messageId.longValue(),CarMessagePost.Status.draft.getMessageStatus());

             postExMapper.withDraw(Long.valueOf(messageId));

             logger.info("字表删除成功");

            return 1;
        } catch (Exception e) {
            logger.info("撤销异常" + e.getMessage());
            throw new MessageException(RestErrorCode.UNKNOWN_ERROR,RestErrorCode.renderMsg(RestErrorCode.UNKNOWN_ERROR));
        }
    }

    /**
     * 删除操作
     * @param messageId
     * @return
     */
    public int messageDeleteDraw(Integer messageId) throws MessageException{

        try {
            receiverExMapper.deleteByMessageId(messageId.longValue());

            //撤回后状态变为草稿
            docExMapper.deleteByMessaeId(messageId.longValue());

            postExMapper.deleteByPrimaryKey(Long.valueOf(messageId));
            logger.info("字表删除成功");

            return 1;
        } catch (Exception e) {
            logger.info("撤销异常" + e.getMessage());
            throw new MessageException(RestErrorCode.UNKNOWN_ERROR,RestErrorCode.renderMsg(RestErrorCode.UNKNOWN_ERROR));
        }
    }


    /**
     * 获取
     * @param userId
     * @param status
     * @param pageSize
     * @param pageNum
     * @return
     */
    public PageDTO messageLisByStatus(int userId,int status,int pageSize,int pageNum) throws MessageException{

        try {
            Page page = PageHelper.startPage(pageNum,pageSize,true);

            List<CarMessagePostDto> dtoList = null;

            switch (status){
                case 1://已收到的消息
                    dtoList = postDtoExMapper.listCarMessagePostBymesageIds(userId,null,CarMessagePost.Status.publish.getMessageStatus(),null);
                    break;
                case 2://未读的消息
                    dtoList = postDtoExMapper.listCarMessagePostBymesageIds(userId,status,CarMessagePost.Status.publish.getMessageStatus(),null);
                    break;
                case 3://已发布的消息
                    dtoList = postDtoExMapper.listCarMessagePostBymesageIds(null,null,CarMessagePost.Status.publish.getMessageStatus(),userId);
                    break;
                case 4://草稿
                    dtoList = postDtoExMapper.listCarMessagePostBymesageIds(null,null,CarMessagePost.Status.draft.getMessageStatus(),userId);
                    break;
                default:
                    dtoList = postDtoExMapper.listCarMessagePostBymesageIds(userId,null,CarMessagePost.Status.publish.getMessageStatus(),null);

            }

            int total = (int)page.getTotal();

            PageHelper.clearPage();

            PageDTO pageDTO = new PageDTO(pageNum, pageSize, total, dtoList);

            return pageDTO;
        } catch (Exception e) {
            throw new MessageException(RestErrorCode.UNKNOWN_ERROR,RestErrorCode.renderMsg(RestErrorCode.UNKNOWN_ERROR));
        }
    }



    /**
     * 获取未读数量
     * @param userId
     * @return
     */
    public Integer unReadCount(int userId) throws MessageException{

        try {
            Integer count = receiverExMapper.messageUnreadCount(Long.valueOf(userId));

            return count;
        } catch (Exception e) {
            throw new MessageException(RestErrorCode.UNKNOWN_ERROR,RestErrorCode.renderMsg(RestErrorCode.UNKNOWN_ERROR));
        }
    }


    /**
     * 获取详情
     * @param messaageId
     * @param userId
     * @return
     * @throws MessageException
     */
    public CarMessageDetailDto messageDetail(Integer messaageId, Integer userId) throws MessageException{

        try {
            CarMessagePost carMessagePost = postExMapper.selectByPrimaryKey(Long.valueOf(messaageId));

            CarMessageDetailDto detailDto = new CarMessageDetailDto(
                    carMessagePost.getMesageTitle(),carMessagePost.getMessageContent(),
                    carMessagePost.getCreateTime(),carMessagePost.getUpdateTime());

            switch (carMessagePost.getLevel()){
                case Constants.CONTRY:
                    detailDto.setLevel(CarMessagePost.Level.contry.getName());
                    break;
                case Constants.CITY:
                    detailDto.setLevel(CarMessagePost.Level.city.getName());
                    detailDto.setCities(this.getCityNames(carMessagePost.getCities()));
                    break;
                case Constants.SUPPY:
                    detailDto.setLevel(CarMessagePost.Level.suppy.getName());
                    detailDto.setSuppliers(this.getSuppyNames(carMessagePost.getCities(),carMessagePost.getSuppliers()));
                    break;
                case Constants.CITYANDSUPPY:
                    detailDto.setLevel(CarMessagePost.Level.cityAndSuppy.getName());
                    detailDto.setCities(this.getCityNames(carMessagePost.getCities()));
                    detailDto.setSuppliers(this.getSuppyNames(carMessagePost.getCities(),carMessagePost.getSuppliers()));
                    break;
                case Constants.TEAM:
                    detailDto.setLevel(CarMessagePost.Level.team.getName());
                    detailDto.setTeamids(this.getTeamNames(carMessagePost.getCities(),carMessagePost.getSuppliers(),carMessagePost.getTeamids()));
                    break;
                case Constants.CITYANDTEAM:
                    detailDto.setLevel(CarMessagePost.Level.cityAndTeam.getName());
                    detailDto.setCities(this.getCityNames(carMessagePost.getCities()));
                    detailDto.setTeamids(this.getTeamNames(carMessagePost.getCities(),carMessagePost.getSuppliers(),carMessagePost.getTeamids()));
                    break;
                case Constants.SUPPYANDTEAM:
                    detailDto.setLevel(CarMessagePost.Level.suppyAndTeam.getName());
                    detailDto.setSuppliers(this.getSuppyNames(carMessagePost.getCities(),carMessagePost.getSuppliers()));
                    detailDto.setTeamids(this.getTeamNames(carMessagePost.getCities(),carMessagePost.getSuppliers(),carMessagePost.getTeamids()));
                    break;
                case Constants.CITYANDSUPPYANDTEAM:
                    detailDto.setLevel(CarMessagePost.Level.cityAndSuppyAndTeam.getName());
                    detailDto.setCities(this.getCityNames(carMessagePost.getCities()));
                    detailDto.setSuppliers(this.getSuppyNames(carMessagePost.getCities(),carMessagePost.getSuppliers()));
                    detailDto.setTeamids(this.getTeamNames(carMessagePost.getCities(),carMessagePost.getSuppliers(),carMessagePost.getTeamids()));
                    break;
                default:
                    detailDto.setLevel(CarMessagePost.Level.contry.getName());

            }

            //已读
            List<CarMessageReceiver> list = receiverExMapper.carMessageReceiverList(messaageId,null,CarMessageReceiver.ReadStatus.read.getValue());
            List<Integer> listUsers = new ArrayList<>();
            for (CarMessageReceiver receiver : list){
                listUsers.add(receiver.getReceiveUserId());
            }
            List<CarAdmUser> carAdmUserList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(listUsers)){
                carAdmUserList = carAdmUserExMapper.queryUsers(listUsers,null,null,null,null);
            }

            Map<Integer,String> mapUser = new HashMap<>();
            for (CarAdmUser carAdmUser : carAdmUserList){
                mapUser.put(carAdmUser.getUserId(),carAdmUser.getUserName());
            }
            List<ReadRecordDto> readRecordDtoList = new ArrayList<>();
            for (CarMessageReceiver receiver : list){
                ReadRecordDto readRecordDto  = new ReadRecordDto(mapUser.get(receiver.getReceiveUserId()),receiver.getUpdateTime());
                readRecordDtoList.add(readRecordDto);
            }

            detailDto.setReadRecord(readRecordDtoList);

            List<CarMessageDoc> listDoc = new ArrayList<>();
            listDoc = docExMapper.listDoc(Long.valueOf(messaageId));
            List<MessageDocDto> messageDocDtoList = new ArrayList<>();
            for (CarMessageDoc doc  : listDoc){
                MessageDocDto messageDocDto = new MessageDocDto(doc.getDocName(),doc.getDocUrl());
                messageDocDtoList.add(messageDocDto);
            }
            detailDto.setMessageDocDto(messageDocDtoList);
            List<Integer> createUser = new ArrayList<>();
            createUser.add(userId);
            List<CarAdmUser> createrList = carAdmUserExMapper.queryUsers(createUser,null,null,null,null);
            if (CollectionUtils.isNotEmpty(createrList)){
                detailDto.setCreateUser(createrList.get(0).getUserName());
                detailDto.setPhone(createrList.get(0).getPhone());
            }
            detailDto.setId(messaageId.longValue());

            try {
                List<CarMessageReceiver> listUnRead = receiverExMapper.carMessageReceiverList(messaageId,userId,CarMessageReceiver.ReadStatus.unRead.getValue());
                if (CollectionUtils.isNotEmpty(listUnRead)){
                    int code = receiverExMapper.updateReadState(listUnRead.get(0).getId());
                    if (code > 0){
                        logger.info("状态更改成功");
                    }else {
                       logger.info("状态更改失败");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return detailDto;
        } catch (Exception e) {
            throw new MessageException(RestErrorCode.UNKNOWN_ERROR,RestErrorCode.renderMsg(RestErrorCode.UNKNOWN_ERROR));
        }
    }


    /**
     * 获取城市名称
     * @param cities
     * @return
     */
    private String getCityNames(String cities){
        List<CarBizCity> carBizCityList = carBizCityExMapper.queryByIds(null);
        Map<Integer,String> map = new HashMap<>();
        for (CarBizCity bizCity : carBizCityList){
            map.put(bizCity.getCityId(),bizCity.getCityName());
        }
        StringBuffer sb = new StringBuffer();
        String[] cityArray = cities.split(Constants.SEPERATER);
        for (String str : cityArray){
            sb.append(map.get(Integer.valueOf(str)));

        }

        return sb.toString();
    }


    /**
     * 获取加盟商名称
     * @param cities
     * @param suppy
     * @return
     */
    private String getSuppyNames(String cities,String suppy){
        List<CarBizSupplier> carBizSupplierList = carBizSupplierExMapper.querySuppliers(this.getCities(cities), null);
        Map<Integer,String> map = new HashMap<>();
        for (CarBizSupplier supplier : carBizSupplierList){
            map.put(supplier.getSupplierId(),supplier.getSupplierFullName());
        }
        StringBuffer sb = new StringBuffer();
        String[] suppyArray = suppy.split(Constants.SEPERATER);
        for (String str : suppyArray){
            if (StringUtils.isNotBlank(map.get(Integer.valueOf(str)))){
                sb.append(map.get(Integer.valueOf(str))).append(" ");
            }
        }
        return sb.toString();
    }


    /**
     * 获取车队名称
     * @param cities
     * @param suppy
     * @param teamIds
     * @return
     */
    private String getTeamNames(String cities,String suppy,String teamIds){
        List<CarDriverTeam> carDriverTeamList = carDriverTeamExMapper.queryDriverTeam(this.getSetMaps(cities), this.getSetMaps(suppy), null);
        Map<Integer,String> map = new HashMap<>();
        for (CarDriverTeam carDriverTeam : carDriverTeamList){
            map.put(carDriverTeam.getId(),carDriverTeam.getTeamName());
        }
        StringBuffer sb = new StringBuffer();
        String[] teamIdArray = teamIds.split(Constants.SEPERATER);
        for (String str : teamIdArray){
            if (StringUtils.isNotBlank(map.get(Integer.valueOf(str)))){
                sb.append(map.get(Integer.valueOf(str))).append(" ");

            }
        }
        return sb.toString();
    }

    private Set<Integer> getCities(String cities){
        String[] cityArray = cities.split(Constants.SEPERATER);
        Set<Integer> setCities = new HashSet<>();
        for (String str : cityArray){
            setCities.add(Integer.valueOf(str));
        }
        return setCities;

    }


    private Set<String> getSetMaps(String str){
        String[] strArray = str.split(Constants.SEPERATER);
        Set<String> setStr = new HashSet<>();
        for (String s : strArray){
            setStr.add(s);
        }
        return setStr;

    }


    /**
     * 上传文件类
     * @param uploadFile
     */
    public Map<String,Object> upload(MultipartFile uploadFile) {
        Map<String,Object> resultMap = new HashMap<>();
        try {
            String extension = FilenameUtils.getExtension(uploadFile.getOriginalFilename());
            String uuid = UUID.randomUUID().toString();
            String timeStamp = System.currentTimeMillis() + "";
            String filePathDir = getRemoteFileDir();

            //开始上传
            ftpUtil.connectServer();
            boolean uploadFlag = ftpUtil.upload(filePathDir, uuid + "_" + timeStamp + "." + extension, uploadFile.getInputStream());
            if (uploadFlag){
                Boolean  ok = true;
                String  absoluteUrl = FtpUtils.getFtpServerUrl() + filePathDir + uuid + "_" + timeStamp + "." + extension;
                String oppositeUrl = filePathDir + uuid + "_" + timeStamp + "." + extension;
                logger.info("消息中心-上传附件-absoluteUrl：{" + absoluteUrl + "},oppositeUrl：{" + oppositeUrl + "}");
                resultMap.put("ok",ok);
                resultMap.put("absoluteUrl",absoluteUrl);
                resultMap.put("oppositeUrl",oppositeUrl);
                resultMap.put("fileName",uploadFile.getOriginalFilename());

            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("消息中心-上传附件-异常 error:{"+e.getMessage() +"}");
        }finally {
            ftpUtil.closeConnect();
        }
        return  resultMap;
    }

    private String getRemoteFileDir() {
        StringBuilder sb = new StringBuilder();
        sb.append("/u01").append("/upload/");
        return sb.toString();
    }

    public PageDTO messageSearch(String range, String keyword, String startDate,
                                 String endDate, List<Integer> idList, Integer pageSize, Integer pageNum, Integer userId) {

        String[] split = range.split(",");
        int count = 0;
        Date start = (startDate != null) ? DateUtil.parseDate(startDate, Constants.DATE_FORMAT) : null;
        Date end = endDate != null ? DateUtil.parseDate(endDate, Constants.DATE_FORMAT) : null ;
        List<CarMessagePostDto> data = null;
        if (split.length > 1) {
            count = receiverExMapper.queryAllCount(keyword ,
                    start,
                    end, idList, userId);
            data = receiverExMapper.queryALlData(keyword ,
                    start,
                    end,
                    idList, userId, (pageNum - 1) * pageSize ,pageSize);
        } else {
            String rangeStr = split[0];
            if (rangeStr.equals(Constants.TITLE)) {
                count = receiverExMapper.queryCountInTitle(keyword ,
                        start,
                        end, idList, userId);
                data = receiverExMapper.queryDataInTitle(keyword ,
                        start,
                        end,
                        idList, userId, (pageNum - 1) * pageSize ,pageSize);
            }
            if (rangeStr.equals(Constants.ATTACHMENT)) {
                count = receiverExMapper.queryCountInAttachment(keyword ,
                        start,
                        end, idList, userId);
                data = receiverExMapper.queryDataInAttachment(keyword ,
                        start,
                        end,
                        idList, userId, (pageNum - 1) * pageSize ,pageSize);
            }
        }
        return new PageDTO(pageNum, pageSize, count, data);
    }
}
