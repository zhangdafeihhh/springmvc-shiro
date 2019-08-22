package mapper.bigdata.ex;

import com.zhuanche.entity.bigdata.BiSaasSupplierRankData;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BiSaasSupplierRankDataExMapper {


     BiSaasSupplierRankData findByParam(@Param("supplierId")Integer supplierId, @Param("settleStartTime")Date settleStartTime,@Param("settleEndTime") Date settleEndTime);

    List<BiSaasSupplierRankData> findByMonth(@Param("month")String month);
}