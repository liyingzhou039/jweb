package com.jweb.sys.service.identity;

import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import com.jweb.sys.entity.identity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {
    @Autowired
    private BeanService beanService;
    @Transactional
    public void remove(String roleId) throws BusiException{
        try {
            beanService.remove(Role.class, roleId);
            beanService.remove(UserRole.class,
                    Where.create("roleId", Expression.eq, roleId));
            beanService.remove(RoleMenu.class,
                    Where.create("roleId", Expression.eq, roleId));
            beanService.remove(RoleElement.class,
                    Where.create("roleId", Expression.eq, roleId));
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
    }
}
