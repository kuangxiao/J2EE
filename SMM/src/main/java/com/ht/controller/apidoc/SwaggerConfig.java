package com.ht.controller.apidoc;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Title:
 * @Description:
 * @Author:奋斗的大侠
 * @Since:2017年1月10日
 * @Version:1.1.0
 */
@Configuration
@EnableWebMvc
@EnableSwagger
@ComponentScan("com.cn.hnust.controller")
public class SwaggerConfig extends WebMvcConfigurerAdapter {
	
	private SpringSwaggerConfig springSwaggerConfig;

	@Autowired
	public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
		this.springSwaggerConfig = springSwaggerConfig;
	}

	@Bean
	public SwaggerSpringMvcPlugin customImplementation() {
		return new SwaggerSpringMvcPlugin(this.springSwaggerConfig).apiInfo(apiInfo()).includePatterns(".*?");
	}

	private ApiInfo apiInfo() {
		
		ApiInfo apiInfo = new ApiInfo("用户服务API", 
				"包括：用户CURD 联系人：九天",
				"用户服务API访问模块：所有模块", 
				"wangtao@163.com", 
				"FREE",
				"http://localhost:8080/SMM/license");

		return apiInfo;
	}
	
}
