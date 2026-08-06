package com.sunbird.machine.web.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class ForwardController {

	//https://spring.io/blog/2015/05/13/modularizing-the-client-angular-js-and-spring-security-part-vii#using-ldquo-natural-rdquo-routes
	//"all paths that do not contain a period (and are not explicitly mapped already) are Angular routes, and should forward to the home page"
	@RequestMapping(value = "/{[path:[^\\.]*}")
	public String redirect() {
		// Forward to home page so that route is preserved.
		return "forward:/index.html";
	}
	
}
