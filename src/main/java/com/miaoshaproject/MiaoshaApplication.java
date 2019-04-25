package com.miaoshaproject;

import com.miaoshaproject.dao.UserDOMapper;
import com.miaoshaproject.dataobject.UserDO;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = {"com.miaoshaproject"})
@MapperScan("com.miaoshaproject.dao")
@RestController
public class MiaoshaApplication {

	@Autowired
	private UserDOMapper userDOMapper;

	@GetMapping("/")
	public String hello() {
		UserDO userDO = userDOMapper.selectByPrimaryKey(6);
		if (userDO == null)
			return "用户对象不存在";
		else
			return userDO.getName();
	}


	public static void main(String[] args) {
		SpringApplication.run(MiaoshaApplication.class, args);
	}

}
