package mapper.bigdata;

import com.zhuanche.dto.disinfect.DisinfectParamDTO;
import com.zhuanche.dto.disinfect.DisinfectResultDTO;
import com.zhuanche.entity.bigdata.BigDataCarBizDriverInfo;

import java.util.List;

public interface BigDataCarBizDriverInfoMapper {
    int insert(BigDataCarBizDriverInfo record);

    int insertSelective(BigDataCarBizDriverInfo record);

    BigDataCarBizDriverInfo selectByPrimaryKey(Integer driverId);

    int updateByPrimaryKeySelective(BigDataCarBizDriverInfo record);

    int updateByPrimaryKeyWithBLOBs(BigDataCarBizDriverInfo record);

    int updateByPrimaryKey(BigDataCarBizDriverInfo record);

    List<DisinfectResultDTO> list(DisinfectParamDTO disinfectParamDTO);
}