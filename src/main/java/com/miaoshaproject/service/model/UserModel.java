package com.miaoshaproject.service.model;

import javax.print.DocFlavor;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UserModel {
    private Integer id;
    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotNull(message = "性别不能为空")
    private Byte gender;

    @NotNull(message = "年龄不能为空")
    @Min(value = 0, message = "年龄须大于0")
    @Max(value = 150, message = "年龄不大于150")
    private Integer age;

    @NotNull(message = "电话不能为空")
    private String telephone;
    private String regesterMode;
    private String thirdPartyId;

    @NotNull(message = "密码不能为空")
    private String encrptPassword;

    public String getEncrptPassword() {
        return encrptPassword;
    }

    public void setEncrptPassword(String encrptPassword) {
        this.encrptPassword = encrptPassword;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getRegesterMode() {
        return regesterMode;
    }

    public void setRegesterMode(String regesterMode) {
        this.regesterMode = regesterMode;
    }

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }
}
