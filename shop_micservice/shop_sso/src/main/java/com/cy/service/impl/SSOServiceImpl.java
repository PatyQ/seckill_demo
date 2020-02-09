package com.cy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cy.User;
import com.cy.dao.ISSODao;
import com.cy.service.ISSOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SSOServiceImpl implements ISSOServer {

    @Autowired
    private ISSODao issoDao;

    @Override
    public User isThisUser(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        User user = issoDao.selectOne(queryWrapper);
        return user;
    }
}
