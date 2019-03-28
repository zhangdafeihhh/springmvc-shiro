package mapper.mdbcarmanage.ex;

import com.zhuanche.dto.busManage.BusDriverViolatorsQueryDTO;
import com.zhuanche.vo.busManage.BusBizDriverViolatorsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BusBizDriverViolatorsExMapper {

    //根据参数dto查询违规司机数据列表
    List<BusBizDriverViolatorsVO> selectDriverViolatorsByQueryDTO(BusDriverViolatorsQueryDTO queryDTO);

    /**
     * 将司机恢复正常状态
     */
    int recoverDriverStatus(@Param("id") Integer id);
}