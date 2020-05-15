package com.zhuanche.entity.driver;

import lombok.Data;

/**
 *
 * @author kjeakiry
 */
@Data
public class PunishRecordVoiceDTO {
    private Long id;
    private String orderNo;
    private String space;
    private String path;
    private String recordTime;
    private String recordTimeBegin;
    private String recordTimeEnd;
    private String filePath;
    private String orderDay;
}
