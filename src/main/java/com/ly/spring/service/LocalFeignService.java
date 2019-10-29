package com.ly.spring.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
*@author: dt-ly
*@email:379944104@qq.com
*@version: V1.0
*@Date 2019年10月29日下午3:24:28
*
*/
@FeignClient(name = "SPRING-EUREKA-PRODUCER")
public interface LocalFeignService {

	@GetMapping("/produce/hello")
	public String custome();
}
