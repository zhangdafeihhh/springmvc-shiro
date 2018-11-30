package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.BusBizSupplierDetail;

public interface BusBizSupplierDetailMapper {
    int insert(BusBizSupplierDetail record);

    int insertSelective(BusBizSupplierDetail record);

    BusBizSupplierDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BusBizSupplierDetail record);

    int updateByPrimaryKey(BusBizSupplierDetail record);
}