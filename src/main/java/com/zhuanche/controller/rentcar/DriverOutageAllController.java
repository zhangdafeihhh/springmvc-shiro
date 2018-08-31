package com.zhuanche.controller.rentcar;

import com.zhuanche.serv.rentcar.DriverOutageAllService;
import com.zhuanche.serv.rentcar.DriverOutageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/driverOutageAll")
public class DriverOutageAllController {

    @Autowired
    DriverOutageAllService driverOutageAllService;


}
