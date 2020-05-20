package com.zhuanche.controller.biDriverMeasureDay;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.BaseController;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.IndexBiDriverMeasureDto;
import com.zhuanche.dto.bigdata.BiDriverMeasureDayDto;
import com.zhuanche.dto.bigdata.DisinfectPenetranceDTO;
import com.zhuanche.serv.driverMeasureDay.DriverMeasureDayService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @Author:qxx
 * @Date:2020/4/23
 * @Description:
 */

@RequestMapping("/driverMeasureDay")
@RestController
public class BiDriverMeasureDayController extends BaseController {


    @Autowired
    private DriverMeasureDayService driverMeasureDayService;


    @RequestMapping("/count")
    public AjaxResponse count(@Verify(param = "startDate", rule = "required") String startDate,
                              @Verify(param = "endDate", rule = "required") String endDate, String allianceId, String motorcadeId){
        try {
            String result = driverMeasureDayService.getResponsibleComplaintRate(startDate,endDate, allianceId);
            return AjaxResponse.success(result);
        }
        catch (Exception e){
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    @RequestMapping("/index")
    public AjaxResponse index(@Verify(param = "startDate", rule = "required") String startDate,
                              @Verify(param = "endDate", rule = "required") String endDate
            , @RequestParam(value = "allianceId",required = false) Integer allianceId
            ,  @RequestParam(value = "motorcadeId",required = false)Integer motorcadeId){
        try {
            BiDriverMeasureDayDto params = new BiDriverMeasureDayDto();
            params.setStartDate(startDate);
            params.setEndDate(endDate);
            params.setSupplierId(allianceId);
            params.setTeamId(motorcadeId);


            IndexBiDriverMeasureDto entity = driverMeasureDayService.findForStatistics(params);

            return AjaxResponse.success(entity);
        }
        catch (Exception e){
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    /**
     * 首页消毒渗透率
     * 大消渗透率 = 当日运营且消毒司机数 / 当日运营司机数
     * @param startDate
     * @param endDate
     * @param supplierId 合作商
     *
     * select a.data_date,
     *        a.supplier_id,
     *        sum(b.disinfect_driver_cnt)/sum(a.in_use_driver_num) -- 司机大消渗透率
     *   from (select data_date,
     *                supplier_id,
     *                sum(in_use_driver_num) as in_use_driver_num -- 运营司机数
     *           from bi_driver_measure_day
     *          where data_date>='2020-05-01 00:00:00'
     *            and data_date<'2020-05-12 00:00:00'
     * -- 					 and supplier_id = ''
     *        )a
     *   left join
     *        (select data_date,
     *                supplier_id,
     *                sum(disinfect_driver_cnt) as disinfect_driver_cnt -- 当日运营且大消司机数
     *           from bi_driver_disinfect_measure_day
     *          where data_date>='2020-05-01 00:00:00'
     *            and data_date<'2020-05-12 00:00:00'
     * -- 					 and supplier_id = ''
     *        )b
     *     on a.data_date = b.data_date
     *    and a.supplier_id = b.supplier_id
     *  group by
     *        a.data_date,
     *        a.supplier_id
     *
     * @return
     */
    @RequestMapping("/disinfectPenetrance")
    public AjaxResponse disinfectPenetrance(@Verify(param = "startDate", rule = "required") String startDate,
                                            @Verify(param = "endDate", rule = "required") String endDate,
                                            Integer supplierId){
        DisinfectPenetranceDTO penetranceDTO = driverMeasureDayService.disinfectPenetrance(startDate, endDate, supplierId);
        if (null == penetranceDTO){
            penetranceDTO = new DisinfectPenetranceDTO();
        }
        penetranceDTO.setPenetrance(penetranceDTO.getPenetrance().multiply(new BigDecimal(100)));
        return AjaxResponse.success(penetranceDTO);
    }
}
