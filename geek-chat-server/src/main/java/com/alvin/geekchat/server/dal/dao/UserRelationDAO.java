package com.alvin.geekchat.server.dal.dao;

import com.alvin.geekchat.server.dal.dataobejct.UserRelationDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserRelationDAO {
    List<UserRelationDO> query(@Param("userId") Integer userId);
}
