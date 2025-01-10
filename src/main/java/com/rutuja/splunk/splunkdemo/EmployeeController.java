package com.rutuja.splunk.splunkdemo;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class EmployeeController {

    Logger logger = LogManager.getLogger(this.getClass());
@RequestMapping(value = "employee")
    public Mono<EmployeeBean> getEmployee(){
    System.out.println(System.getProperty("log4j.configurationFile"));

    logger.info("This is log message from employee service");
        return Mono.just(new EmployeeBean("Datta","Jadhav",33));
    }
}
