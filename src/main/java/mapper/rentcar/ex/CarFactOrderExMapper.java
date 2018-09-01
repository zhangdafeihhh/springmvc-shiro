package mapper.rentcar.ex;

public interface CarFactOrderExMapper {

    /**
     *  输入订单号返回订单ID
     * @param orderNo 订单号
     * @return java.lang.String
     */
    public String selectorderIdByOrderNo(String orderNo);

}