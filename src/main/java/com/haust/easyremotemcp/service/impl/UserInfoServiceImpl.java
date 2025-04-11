package com.haust.easyremotemcp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haust.easyremotemcp.entity.UserInfo;
import com.haust.easyremotemcp.service.UserInfoService;
import com.haust.easyremotemcp.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author liyongbin
* @description 针对表【user_info】的数据库操作Service实现
* @createDate 2025-04-11 12:02:41
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{

}




