package com.alvin.geekchat.server.component;

import com.alvin.geekchat.model.User;
import com.alvin.geekchat.server.dal.dao.UserInfoDAO;
import com.alvin.geekchat.server.dal.dao.UserRelationDAO;
import com.alvin.geekchat.server.dal.dataobejct.UserInfoDO;
import com.alvin.geekchat.server.dal.dataobejct.UserRelationDO;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserComponent {
    @Resource
    UserInfoDAO userInfoDAO;

    @Resource
    UserRelationDAO userRelationDAO;

    public User getUser(Integer userId, String userPassword) {
        UserInfoDO userInfoDO = userInfoDAO.get(userId, userPassword);
        if (userInfoDO == null) {
            return null;
        }
        return convert(userInfoDO);
    }

    public List<User> queryRelatedUsers(Integer userId) {
        List<User> users = new ArrayList<>();
        List<UserRelationDO> userInfoDO = userRelationDAO.query(userId);
        if (CollectionUtils.isEmpty(userInfoDO)) {
            return users;
        }
        List<Integer> userIds = userInfoDO.stream().map(userRelationDO ->
                (int)userRelationDO.getRelationUser1() == (int)userId ? userRelationDO.getRelationUser2() : userRelationDO.getRelationUser1())
                .collect(Collectors.toList());
        List<UserInfoDO> userInfoDOList = userInfoDAO.query(userIds);
        if (CollectionUtils.isEmpty(userInfoDOList)) {
            return users;
        }
        users = userInfoDOList.stream().map(this::convert).collect(Collectors.toList());
        return users;
    }

    private User convert(UserInfoDO userInfoDO) {
        return new User(userInfoDO.getUserId(), userInfoDO.getUserName());
    }
}
