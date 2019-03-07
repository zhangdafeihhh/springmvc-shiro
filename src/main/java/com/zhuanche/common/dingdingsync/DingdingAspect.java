package com.zhuanche.common.dingdingsync;

import com.zhuanche.common.rocketmq.CommonRocketProducer;
import com.zhuanche.controller.supplier.SupplierController;
import com.zhuanche.dto.CarDriverTeamDTO;
import com.zhuanche.entity.rentcar.CarBizSupplierVo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author fanht
 * @Description
 * @Date 2019/2/28 上午11:59
 * @Version 1.0
 */
@Component
@Aspect
public class DingdingAspect {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(* com.zhuanche.controller.driverteam.DriverTeamController.*(..)) ||" +
            " execution(* com.zhuanche.controller.supplier.SupplierController.*(..)) ")
    public void pointCut(){
        logger.info("含有自定义注解dingdingAnno的方法...");
    }

    @Before("pointCut()  && @annotation(dingdingAnno) ")
    public void dingdingVerify(JoinPoint joinPoint,DingdingAnno dingdingAnno){
        logger.info(joinPoint.getSignature().getName() + ",入参:{" + Arrays.asList(joinPoint.getArgs() + "}"));
    }



    @AfterReturning("pointCut() && @annotation(dingdingAnno)")
    public void finish(JoinPoint jointPoint,DingdingAnno dingdingAnno){
        Signature signature = jointPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null){
           dingdingAnno = method.getAnnotation(DingdingAnno.class);
           if (dingdingAnno != null && "2".equals(dingdingAnno.level())){
               Object[] args = jointPoint.getArgs();
               Map<String,Object> map = new HashMap<>();
               for(Object obj : args){
                   if(obj instanceof CarDriverTeamDTO){
                       CarDriverTeamDTO teamDTO = (CarDriverTeamDTO) obj;
                       String teamId = teamDTO.getId().toString();
                       map.put("city",teamDTO.getCity());
                       map.put("cityName",teamDTO.getCityName());
                       map.put("supplier",teamDTO.getSupplier());
                       map.put("teamName",teamDTO.getTeamName());
                       map.put("teamId",teamDTO.getId());
                       map.put("pId",teamDTO.getpId());
                       map.put("openCloseFlag",teamDTO.getOpenCloseFlag());
                       map.put("id",teamDTO.getId());

                       CommonRocketProducer.publishMessage("car_driver_team",dingdingAnno.method(),teamId,map);
                   }
                   }

           }if (dingdingAnno != null && "1".equals(dingdingAnno.level())){
                Object[] args = jointPoint.getArgs();
                Map<String,Object> map = new HashMap<>();
                for(Object obj : args){
                    if(obj instanceof CarBizSupplierVo){
                        CarBizSupplierVo supplierVo = (CarBizSupplierVo) obj;
                        map.put("cityId",supplierVo.getSupplierCity());
                        map.put("cityName",supplierVo.getSupplierCityName());
                        map.put("supplierId",supplierVo.getSupplierId());
                        map.put("supplierName",supplierVo.getSupplierFullName());
                        map.put("cooperationType",supplierVo.getCooperationType());
                        CommonRocketProducer.publishMessage("car_driver_supplier",dingdingAnno.method(),supplierVo.getSupplierId().toString(),map);
                    }

                }
            }
        }
        System.out.println(jointPoint.getSignature().getName());
    }
}
