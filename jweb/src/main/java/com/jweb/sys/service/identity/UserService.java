package com.jweb.sys.service.identity;

import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import com.jweb.common.util.MD5Util;
import com.jweb.common.util.StringUtil;
import com.jweb.sys.entity.identity.User;
import com.jweb.sys.entity.identity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    private BeanService beanService;

    public User create(User user,String[] roleIds) throws BusiException{
        try{
            beanService.create(user);
            for(String roleId : roleIds){
                if(StringUtil.notNull(roleId)){
                    UserRole userRole= new UserRole();
                    userRole.setUserId(user.getId());
                    userRole.setRoleId(roleId);
                    beanService.create(userRole);
                }
            }
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
        return user;
    }
    @Transactional
    public User update(User user,String[] roleIds) throws BusiException{
        try{
            //如果密码为空，则查询出密码
            if(StringUtil.isNull(user.getPassword())) {
                User oldUser = beanService.getById(User.class, user.getId());
                if (oldUser != null) {
                    user.setPassword(oldUser.getPassword());
                }
            }
            if(user.getPassword().length()<32){
                user.setPassword(MD5Util.encode(user.getPassword()));
            }
            beanService.update(user);
            beanService.remove(UserRole.class,
                    Where.create("userId", Expression.eq, user.getId()));
            for(String roleId : roleIds){
                if(StringUtil.notNull(roleId)){
                    UserRole userRole= new UserRole();
                    userRole.setUserId(user.getId());
                    userRole.setRoleId(roleId);
                    beanService.create(userRole);
                }
            }
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
        return user;
    }

    @Transactional
    public void remove(String userId) throws BusiException{
        try {
            beanService.remove(User.class, userId);
            beanService.remove(UserRole.class,
                    Where.create("userId", Expression.eq, userId));
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
    }
}
