package com.ly.spring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
*@author: dt-ly
*@email:379944104@qq.com
*@version: V1.0
*@Date 2019年10月29日上午10:03:51
*
*/
@RestController
@RequestMapping("/feign")
public class Hello {

	@GetMapping("/hello")
	public String hello() {
		return "hi i am hell";
	}
}
