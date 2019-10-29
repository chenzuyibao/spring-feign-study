package com.ly.spring.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
*@author: dt-ly
*@email:379944104@qq.com
*@version: V1.0
*@Date 2019年10月29日上午9:25:40
*
*/
@FeignClient(name="github-client", url ="https://api.github.com")
public interface HelloFeignService {
	@RequestMapping(value="/search/repositories", method = RequestMethod.GET)
	String searchRepo(@RequestParam("q") String queryStr);
}
