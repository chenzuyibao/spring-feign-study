package com.ly.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ly.spring.service.HelloFeignService;
import com.ly.spring.service.LocalFeignService;

/**
*@author: dt-ly
*@email:379944104@qq.com
*@version: V1.0
*@Date 2019年10月29日上午9:53:11
*
*/
@RestController
public class HelloFeignController {

	@Autowired
	private HelloFeignService helloFeignService;
	
	@Autowired
	private LocalFeignService localFeignService;
	
	@GetMapping(value ="/serch/github")
	public String searchGithubRepoByStr(@RequestParam("str") String queryStr) {
		return helloFeignService.searchRepo(queryStr);
	}
	
	@GetMapping("/sayhi")
	public String sayhi() {
		return localFeignService.custome();
	}
}
