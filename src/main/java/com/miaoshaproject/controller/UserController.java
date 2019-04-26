package com.miaoshaproject.controller;

import com.miaoshaproject.controller.viewobject.UserVO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

@Controller("user")
@RequestMapping("/user")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    /** 以Bean的方式注入了进来，就代表 httpServletRequest是单例模式。
     * 那怎么支持多个用户的并发访问呢？
     * 通过Spring Bean包装的httpServletRequest，实际上是一个 proxy，内部有 ThreadLocal方式的map，不同的用户有不同的线程来区分。
     */
    @Autowired
    private HttpServletRequest httpServletRequest;

    // 用户获取otpCode短信
    @RequestMapping("/getotp")
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telphone") String telphone) {
        // 生成 otpCode
        Random random = new Random();
        int randomInt = random.nextInt(99999);  // [0, 99999)
        randomInt += 10000;  // [10000, 109999)
        String otpCode = String.valueOf(randomInt);

        // 将 otpCode 同对应的用户手机号关联。使用 httpSession方式，分布式适合使用 Redis
        httpServletRequest.getSession().setAttribute(telphone, otpCode);

        // 发送 otpCode 给用户
        System.out.println("telphone = " + telphone + " & otpCode = " + otpCode);

        return CommonReturnType.create(null);
    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        // 调用service服务获取对应id的用户对象，并返回给前端
        UserModel userModel = userService.getUserById(id);
        if (userModel == null) {
//            userModel.setEncrptPassword("a"); // 测试返回'未知错误'
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        UserVO userVO = convertFromModel(userModel);
        return CommonReturnType.create(userVO);
    }

    public UserVO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }
}
