package mapper.rentcar.ex;


/**
 * @author wzq
 */
public interface CarBizCarInfoExMapper {
    /**
     * 根据车牌号查询
     * @param licensePlates
     * @return
     */
    Integer checkLicensePlates(String licensePlates);
}