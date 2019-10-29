package com.ly.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
*@author: dt-ly
*@email:379944104@qq.com
*@version: V1.0
*@Date 2019年10月29日上午9:21:32
*
*/
@SpringBootApplication
@EnableFeignClients
public class FeignApplication {
	public static void main(String[] args) {
		SpringApplication.run(FeignApplication.class, args);
	}
}
