package mapper.bigdata;

import com.zhuanche.entity.bigdata.BiSaasSupplierRankData;
import org.springframework.stereotype.Repository;

@Repository
public interface BiSaasSupplierRankDataMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BiSaasSupplierRankData record);

    int insertSelective(BiSaasSupplierRankData record);

    BiSaasSupplierRankData selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BiSaasSupplierRankData record);

    int updateByPrimaryKey(BiSaasSupplierRankData record);
}