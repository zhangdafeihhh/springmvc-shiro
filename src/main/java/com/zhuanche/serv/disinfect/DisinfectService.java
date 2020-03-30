package com.zhuanche.serv.disinfect;

import com.zhuanche.dto.disinfect.DisinfectResultDTO;
import com.zhuanche.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消毒
 *
 * @author admin
 */
@Service
public class DisinfectService {


    public void batchGetBaseStatis(List<DisinfectResultDTO> list, List<String> csvDataList) {
        for (DisinfectResultDTO dto : list) {
            if (csvDataList != null) {
                StringBuilder builder = new StringBuilder();
                builder.append(StringUtils.isBlank(dto.getCityName()) ? "" : dto.getCityName()).append(",");
                builder.append(StringUtils.isBlank(dto.getSupplierName()) ? "" : dto.getSupplierName()).append(",");
                builder.append(dto.getDriverId()).append(",");
                builder.append(StringUtils.isBlank(dto.getName()) ? "" : dto.getName()).append(",");
                builder.append(StringUtils.isBlank(dto.getPhone()) ? "" : dto.getPhone()).append(",");
                builder.append(StringUtils.isBlank(dto.getLicensePlates()) ? "" : dto.getLicensePlates()).append(",");
                if (1 == dto.getDisinfectStatus()) {
                    builder.append("已消毒").append(",");
                } else if (2 == dto.getDisinfectStatus()) {
                    builder.append("消毒无效").append(",");
                } else {
                    builder.append("未消毒").append(",");
                }
                builder.append(null == dto.getDisinfectTime() ? "未消毒" : DateUtil.getSdf("yyyy-MM-dd HH:mm:ss").format(dto.getDisinfectTime()));
                csvDataList.add(builder.toString());
            }
        }
    }
}
