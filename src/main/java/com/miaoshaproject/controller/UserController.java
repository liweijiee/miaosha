package com.miaoshaproject.controller;

import com.miaoshaproject.controller.viewobject.UserVO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*", origins = {"*"})
public class UserController extends BaseController{
    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //用户登录接口
    @RequestMapping(value = "/login",method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telephone")String telephone,
                                  @RequestParam(name = "password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //入参校验
        if(StringUtils.isEmpty(telephone) || StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"参数不能为空");
        }

        //校验登陆是否合法
        UserModel userModel = userService.validateLogin(telephone,this.EncodedByMD5(password));

        //将登陆凭证加入session
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);

        return CommonReturnType.create(null);
    }


    //用户注册接口
    @RequestMapping(value = "/register",method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telephone")String telephone,
                                     @RequestParam(name = "otpCode")String otpCode,
                                     @RequestParam(name = "name")String name,
                                     @RequestParam(name = "gender")Integer gender,
                                     @RequestParam(name = "age")Integer age,
                                     @RequestParam(name = "password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证otpcode与telephone是否匹配
        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telephone);
        if (!com.alibaba.druid.util.StringUtils.equals(inSessionOtpCode,otpCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机或验证码有误");
        }

        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setAge(age);
        userModel.setGender(new Byte(String.valueOf(gender.intValue())));
        userModel.setRegesterMode("byphone");
        userModel.setTelephone(telephone);
        userModel.setEncrptPassword(this.EncodedByMD5(password));
        userService.register(userModel);
        return CommonReturnType.create(null);
    }


    public String EncodedByMD5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();

        String newstr = base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }


    //用户获取OTP短信接口
    @RequestMapping(value = "/getotp",method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telephone")String telephone){
        //生成OTP
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt+=10000;
        String otpCode = String.valueOf(randomInt);

        //将OTP与telephone绑定,采用HTTPSession的方式
        httpServletRequest.getSession().setAttribute(telephone,otpCode);

        //向用户用短信发送OTP
        System.out.println("telephone = " + telephone + ", otpCode = " + otpCode);

        return CommonReturnType.create(null);
    }


    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id")Integer id) throws BusinessException {
        UserModel userModel = userService.getUserById(id);

        //若用户信息不存在
        if (userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        //将核心领域模型转换为UI可用的viewobject
        UserVO userVO = convertFromModel(userModel);

        //返回通用返回对象
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }




}
