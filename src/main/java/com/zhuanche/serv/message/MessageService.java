package com.zhuanche.serv.message;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.mdbcarmanage.CarMessageDetailDto;
import com.zhuanche.dto.mdbcarmanage.CarMessagePostDto;
import com.zhuanche.dto.mdbcarmanage.MessageDocDto;
import com.zhuanche.dto.mdbcarmanage.ReadRecordDto;
import com.zhuanche.entity.mdbcarmanage.*;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.exception.MessageException;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.FtpUtil;
import com.zhuanche.util.FtpUtils;
import com.zhuanche.util.HtmlFilterUtil;
import com.zhuanche.util.dateUtil.DateUtil;
import mapper.mdbcarmanage.ex.*;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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


                boolean isUpdate = false;
                if (messageId == null) {
                    //主表插入数据
                    post.setCreateTime(new Date());
                    postExMapper.insertSelective(post);
                    messageId = post.getId().intValue();
                }else {
                    post.setId(messageId.longValue());
                    postExMapper.updateByPrimaryKeySelective(post);
                    isUpdate = true;
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
                            //查询出来原来上次的附件

                            List<CarMessageDoc> listDoc = null;
                            if (isUpdate)
                                listDoc = docExMapper.listDoc(messageId.longValue());


                            MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;


                            MultiValueMap<String, MultipartFile> multiFileMap = req.getMultiFileMap();


                            //多文件上传
                            MultipartFile multipartFile;
                            for (Map.Entry<String, List<MultipartFile>> entry: multiFileMap.entrySet()){

                                //
                                List<MultipartFile> list = entry.getValue();
                                for (MultipartFile fileDetail : list) {
                                    multipartFile = fileDetail;
                                    Map<String, Object> mapResult = this.fileUpload(multipartFile);
                                    Boolean ok = (Boolean) mapResult.get("ok");
                                    if (ok == null || !ok) {
                                        logger.error("消息中心-上传附件-异常");

                                    } else {
                                        CarMessageDoc doc = new CarMessageDoc();
                                        doc.setDocName(mapResult.get("fileName").toString());
                                        doc.setCreateTime(new Date());
                                        doc.setMessageId(messageId);
                                        doc.setUpdateTime(new Date());
                                        doc.setDocUrl(mapResult.get("fileUrl").toString());
                                        // doc.setDocUrl(FtpConstants.FTP+FtpConstants.FTPURL+":"+FtpConstants.FTPPORT + result.get("oppositeUrl").toString());
                                        doc.setState(status);
                                        int code = docExMapper.insert(doc);
                                        if (code > 0) {
                                            logger.info("====doc文档上传成功====");

                                            if (CollectionUtils.isNotEmpty(listDoc)) {
                                                Iterator<CarMessageDoc> iterator = listDoc.iterator();
                                                while (iterator.hasNext()) {
                                                    CarMessageDoc docDel = (CarMessageDoc) iterator.next();
                                                    logger.info("删除上次上传文档" + docDel.getId());
                                                    docExMapper.deleteByPrimaryKey(docDel.getId());
                                                }
                                                listDoc.clear();
                                            }
                                        } else {
                                            logger.info("====doc上传文档失败======");
                                        }

                                    }
                                }

                            }
                        }
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                        throw new RuntimeException();
                    }

                }

            } catch (Exception e) {
                logger.info(e.getMessage());
                throw new RuntimeException();
            }
                return 1;
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

             logger.info("撤回操作成功，messageId:" + messageId);

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
     * 获取列表
     * @param userId
     * @param status
     * @param pageSize
     * @param pageNum
     * @return
     */
    public PageDTO messageLisByStatus(int userId,int status,int pageNum,int pageSize) throws MessageException{

        try {
            Page page = PageHelper.startPage(pageNum,pageSize,true);

            List<CarMessagePostDto> dtoList = null;

            switch (status){
                case 1://已收到的消息
                    dtoList = postDtoExMapper.listCarMessagePostBymesageIds(userId,null);
                    break;
                case 2://未读的消息
                    dtoList = postDtoExMapper.listCarMessagePostBymesageIds(userId,status);
                    break;
                case 3://已发布的消息
                    dtoList = postDtoExMapper.listDraftOrPublish(userId,CarMessagePost.Status.publish.getMessageStatus());
                    break;
                case 4://草稿
                    dtoList = postDtoExMapper.listDraftOrPublish(userId,CarMessagePost.Status.draft.getMessageStatus());
                    break;
                default:
                    dtoList = postDtoExMapper.listCarMessagePostBymesageIds(userId,status);
            }

            int total = (int)page.getTotal();

            PageHelper.clearPage();

            SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
            List<CarMessagePostDto> removeHtmlList = new ArrayList<>();
            for (CarMessagePostDto post : dtoList){
                post.setMessageContent(HtmlFilterUtil.HTMLTagSpirit(post.getMessageContent()));
                post.setMesageTitle(HtmlFilterUtil.HTMLTagSpirit(post.getMesageTitle()));
                post.setMessageStatus(user.getId().equals(post.getCreateId()) ?
                                CarMessagePost.Status.publish.getMessageStatus() : CarMessagePost.Status.receive.getMessageStatus());
                removeHtmlList.add(post);
            }

            PageDTO pageDTO = new PageDTO(pageNum, pageSize, total, removeHtmlList);

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
            return receiverExMapper.messageUnreadCount((long) userId);
        } catch (Exception e) {
            throw new MessageException(RestErrorCode.UNKNOWN_ERROR,RestErrorCode.renderMsg(RestErrorCode.UNKNOWN_ERROR));
        }
    }


    /**
     * 获取详情
     * @param messageId
     * @param userId
     * @return
     * @throws MessageException
     */
    public CarMessageDetailDto messageDetail(Integer messageId, Integer userId) throws MessageException{

        try {
            CarMessagePost carMessagePost = postExMapper.selectByPrimaryKey(Long.valueOf(messageId));

            CarMessageDetailDto detailDto = new CarMessageDetailDto(
                    carMessagePost.getMesageTitle(),carMessagePost.getMessageContent(),
                    carMessagePost.getCreateTime(),carMessagePost.getUpdateTime());

            detailDto.setLevel(carMessagePost.getLevel().toString());
            detailDto.setCities(carMessagePost.getCities());
            detailDto.setSuppliers(carMessagePost.getSuppliers());
            detailDto.setTeamids(carMessagePost.getTeamids());
            String str = Integer.toBinaryString(carMessagePost.getLevel());
            String levelToStr = "";
            String[] levelStr = {"1","2","4","8"};
            for(int t = 0; t < str.length(); t++){
                char c = str.charAt(t);
                if (c=='1'){
                    levelToStr += levelStr[str.length()-t-1] +",";
                }
            }
            if (StringUtils.isNotBlank(levelToStr)){
                detailDto.setLevelToStr(levelToStr.substring(0,levelToStr.length()-1));
            }
            //创建人才能查看阅读记录
            SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();

            if (user.getId().equals(carMessagePost.getUserId())){

            //有时间了再做优化
            switch (carMessagePost.getLevel()){
                case Constants.CONTRY:
                    detailDto.setLevelName(CarMessagePost.Level.contry.getName());
                    break;
                case Constants.CITY:
                    detailDto.setLevelName(CarMessagePost.Level.city.getName());
                    detailDto.setCitiesName(this.getCityNames(carMessagePost.getCities()));
                    break;
                case Constants.CONTRYANDCITY:
                    detailDto.setLevelName(CarMessagePost.Level.contryAndCity.getName());
                    detailDto.setCitiesName(this.getCityNames(carMessagePost.getCities()));
                    break;
                case Constants.SUPPY:
                    detailDto.setLevelName(CarMessagePost.Level.suppy.getName());
                    detailDto.setSuppliersName(this.getSupplierNames(carMessagePost.getCities(),carMessagePost.getSuppliers()));
                    break;
                case Constants.CONTRYANDSUPPY:
                    detailDto.setLevelName(CarMessagePost.Level.contryAndSuppy.getName());
                    detailDto.setSuppliersName(this.getSupplierNames(carMessagePost.getCities(),carMessagePost.getSuppliers()));
                    break;
                case Constants.CITYANDSUPPY:
                    detailDto.setLevelName(CarMessagePost.Level.cityAndSuppy.getName());
                    detailDto.setCitiesName(this.getCityNames(carMessagePost.getCities()));
                    detailDto.setSuppliersName(this.getSupplierNames(carMessagePost.getCities(),carMessagePost.getSuppliers()));
                    break;
                case Constants.CONTRYANDCITYANDSUPPY:
                    detailDto.setLevelName(CarMessagePost.Level.contryAndCityAndSuppy.getName());
                    detailDto.setCitiesName(this.getCityNames(carMessagePost.getCities()));
                    detailDto.setSuppliersName(this.getSupplierNames(carMessagePost.getCities(),carMessagePost.getSuppliers()));
                    break;
                case Constants.TEAM:
                    detailDto.setLevelName(CarMessagePost.Level.team.getName());
                    detailDto.setTeamidsName(this.getTeamNames(carMessagePost.getCities(),carMessagePost.getSuppliers(),carMessagePost.getTeamids()));
                    break;
                case Constants.CONTRYANDTEAM:
                    detailDto.setLevelName(CarMessagePost.Level.contryAndTeam.getName());
                    detailDto.setTeamidsName(this.getTeamNames(carMessagePost.getCities(),carMessagePost.getSuppliers(),carMessagePost.getTeamids()));
                    break;
                case Constants.CITYANDTEAM:
                    detailDto.setLevelName(CarMessagePost.Level.cityAndTeam.getName());
                    detailDto.setCitiesName(this.getCityNames(carMessagePost.getCities()));
                    detailDto.setTeamidsName(this.getTeamNames(carMessagePost.getCities(),carMessagePost.getSuppliers(),carMessagePost.getTeamids()));
                    break;
                case Constants.CONTRYANDCITYANDTEAM:
                    detailDto.setLevelName(CarMessagePost.Level.contryAndCityAndTeam.getName());
                    detailDto.setCitiesName(this.getCityNames(carMessagePost.getCities()));
                    detailDto.setTeamidsName(this.getTeamNames(carMessagePost.getCities(),carMessagePost.getSuppliers(),carMessagePost.getTeamids()));
                    break;
                case Constants.SUPPYANDTEAM:
                    detailDto.setLevelName(CarMessagePost.Level.suppyAndTeam.getName());
                    detailDto.setSuppliersName(this.getSupplierNames(carMessagePost.getCities(),carMessagePost.getSuppliers()));
                    detailDto.setTeamidsName(this.getTeamNames(carMessagePost.getCities(),carMessagePost.getSuppliers(),carMessagePost.getTeamids()));
                    break;
                case Constants.CONTRYANDSUPPYANDTEAM:
                    detailDto.setLevelName(CarMessagePost.Level.contryAndSuppyAndTeam.getName());
                    detailDto.setSuppliersName(this.getSupplierNames(carMessagePost.getCities(),carMessagePost.getSuppliers()));
                    detailDto.setTeamidsName(this.getTeamNames(carMessagePost.getCities(),carMessagePost.getSuppliers(),carMessagePost.getTeamids()));
                    break;
                case Constants.CITYANDSUPPYANDTEAM:
                    detailDto.setLevelName(CarMessagePost.Level.cityAndSuppyAndTeam.getName());
                    detailDto.setCitiesName(this.getCityNames(carMessagePost.getCities()));
                    detailDto.setSuppliersName(this.getSupplierNames(carMessagePost.getCities(),carMessagePost.getSuppliers()));
                    detailDto.setTeamidsName(this.getTeamNames(carMessagePost.getCities(),carMessagePost.getSuppliers(),carMessagePost.getTeamids()));
                    break;
                case Constants.CONTRYANDCITYANDSUPPYANDTEAM:
                    detailDto.setLevelName(CarMessagePost.Level.counryAndCityAndSuppyAndTeam.getName());
                    detailDto.setCitiesName(this.getCityNames(carMessagePost.getCities()));
                    detailDto.setSuppliersName(this.getSupplierNames(carMessagePost.getCities(),carMessagePost.getSuppliers()));
                    detailDto.setTeamidsName(this.getTeamNames(carMessagePost.getCities(),carMessagePost.getSuppliers(),carMessagePost.getTeamids()));
                    break;
                default:
                    detailDto.setLevelName(CarMessagePost.Level.contry.getName());

            }


                //已读
                List<CarMessageReceiver> list = receiverExMapper.carMessageReceiverList(messageId,null,CarMessageReceiver.ReadStatus.read.getValue());
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
            }


            List<CarMessageDoc> listDoc;
            listDoc = docExMapper.listDoc(Long.valueOf(messageId));
            List<MessageDocDto> messageDocDtoList = new ArrayList<>();
            for (CarMessageDoc doc  : listDoc){
                MessageDocDto messageDocDto = new MessageDocDto(doc.getDocName(),doc.getDocUrl());
                messageDocDtoList.add(messageDocDto);
            }
            detailDto.setMessageDocDto(messageDocDtoList);
            List<Integer> createUser = new ArrayList<>();
            createUser.add(carMessagePost.getUserId());
            List<CarAdmUser> createrList = carAdmUserExMapper.queryUsers(createUser,null,null,null,null);
            if (CollectionUtils.isNotEmpty(createrList)){
                detailDto.setCreateUser(createrList.get(0).getUserName());
                detailDto.setPhone(createrList.get(0).getPhone());
            }
            detailDto.setId(messageId.longValue());

            try {
                List<CarMessageReceiver> listUnRead = receiverExMapper.carMessageReceiverList(messageId,userId,CarMessageReceiver.ReadStatus.unRead.getValue());
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
        if (StringUtils.isEmpty(cities))
            return null;
        List<CarBizCity> carBizCityList = carBizCityExMapper.queryByIds(null);
        Map<Integer,String> map = new HashMap<>();
        for (CarBizCity bizCity : carBizCityList){
            map.put(bizCity.getCityId(),bizCity.getCityName());
        }
        StringBuffer sb = new StringBuffer();
        String[] cityArray = cities.split(Constants.SEPERATER);
        for (String str : cityArray){
            sb.append(map.get(Integer.valueOf(str))).append(" ");
        }

        return sb.toString();
    }


    /**
     * 获取加盟商名称
     * @param cities
     * @param suppliers
     * @return
     */
    private String getSupplierNames(String cities, String suppliers){
        List<CarBizSupplier> carBizSupplierList = carBizSupplierExMapper.querySuppliers(this.getCities(cities), null);
        Map<Integer,String> map = new HashMap<>();
        for (CarBizSupplier supplier : carBizSupplierList){
            map.put(supplier.getSupplierId(),supplier.getSupplierFullName());
        }
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotEmpty(suppliers)){
            String[] suppyArray = suppliers.split(Constants.SEPERATER);
            for (String str : suppyArray){
                if (StringUtils.isNotBlank(map.get(Integer.valueOf(str)))){
                    sb.append(map.get(Integer.valueOf(str))).append(" ");
                }
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
        if (StringUtils.isNotEmpty(teamIds)){
            String[] teamIdArray = teamIds.split(Constants.SEPERATER);
            for (String str : teamIdArray){
                if (StringUtils.isNotBlank(map.get(Integer.valueOf(str)))){
                    sb.append(map.get(Integer.valueOf(str))).append(" ");

                }
            }
            return sb.toString();
        }
        return null;

    }

    private Set<Integer> getCities(String cities){
        if (StringUtils.isEmpty(cities))
            return null;
        String[] cityArray = cities.split(Constants.SEPERATER);
        Set<Integer> setCities = new HashSet<>();
        for (String str : cityArray){
            setCities.add(Integer.valueOf(str));
        }
        return setCities;

    }


    private Set<String> getSetMaps(String str){
        if (StringUtils.isEmpty(str))
            return null;
        String[] strArray = str.split(Constants.SEPERATER);
        Set<String> setStr = new HashSet<>();
        Collections.addAll(setStr, strArray);
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


    /**
     * 文件服务器上传
     * @param file
     * @return
     */
    public Map<String,Object> fileUpload(MultipartFile file){
        Map<String,Object> map = new HashMap<>();
        if (!file.isEmpty()) {
            try {
                String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                String uuid = UUID.randomUUID().toString();
                String timeStamp = System.currentTimeMillis() + "";
                // 文件存放服务端的位置
                String rootPath = this.getRemoteFileDir();
                File filePath = new File(rootPath);

                logger.info("文件路径:" +rootPath);
                if (!filePath.exists())
                    filePath.mkdirs();
                // 写文件到服务器
                String  absoluteUrl = rootPath + uuid + "_" + timeStamp + "." + extension;
                File serverFile = new File(absoluteUrl);
                file.transferTo(serverFile);
                logger.info("消息附件上传地址：" + absoluteUrl);
                map.put("ok",true);
                map.put("fileUrl",absoluteUrl);
                map.put("fileName",file.getOriginalFilename());
            } catch (Exception e) {
                logger.info("文件上传失败");
            }
        } else {
            logger.info("文件上传失败");
        }
        return map;
    }

    private String getRemoteFileDir() {
        Calendar now = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(File.separator).append("u01").append(File.separator).append("upload").append(File.separator).append("message")
        .append(File.separator).append(now.get(Calendar.YEAR)).append(File.separator).append(now.get(Calendar.MONTH))
        .append(File.separator);
        return sb.toString();
    }

    public PageDTO messageSearch(String range, String keyword, String startDate,
                                 String endDate, List<Integer> idList, Integer pageSize, Integer pageNum, Integer userId) {

        String[] split = range.split(",");
        int count = 0;
        Date start = (startDate != null) ? DateUtil.parseDate(startDate, Constants.DATE_FORMAT) : null;
        Date end = endDate != null ? DateUtil.parseDate(endDate, Constants.DATE_FORMAT) : null ;
        if (end != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(end);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            end = calendar.getTime();
        }
        List<CarMessagePostDto> data = new ArrayList<>();
        if (idList != null && idList.size() == 0){
            return new PageDTO(pageNum, pageSize, count, data);
        }
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
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();

        for (CarMessagePostDto dto : data){
            if (dto.getMessageStatus().equals(CarMessagePost.Status.publish.getMessageStatus())){
                dto.setMessageStatus(user.getId().equals(dto.getCreateId()) ?
                        CarMessagePost.Status.publish.getMessageStatus() : CarMessagePost.Status.receive.getMessageStatus());
            }
        }
        return new PageDTO(pageNum, pageSize, count, data);
    }
}
