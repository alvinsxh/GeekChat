package com.alvin.geekchat.server.dal.dao;

import com.alvin.geekchat.server.dal.dataobejct.UserInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserInfoDAO {
    UserInfoDO get(@Param("userId") Integer userId, @Param("userPassword") String userPassword);
    List<UserInfoDO> query(@Param("userIds") List<Integer> userIds);
}
