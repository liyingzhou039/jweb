package com.jweb.busi.service.project;

import com.jweb.busi.entity.project.Project;
import com.jweb.busi.entity.project.ProjectUser;
import com.jweb.busi.entity.project.Quota;
import com.jweb.common.dto.Execute;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import com.jweb.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private BeanService beanService;

    @Transactional
    public Project create(Project project) throws BusiException{
        try{
            project = beanService.create(project);
            //项目成员
            beanService.remove(
                    ProjectUser.class,
                    Where.create("userId",Expression.in,project.getUserIds())
            );
            List<String> userIds = project.getUserIds();
            if(null != userIds) {
                List<Execute> executes = new ArrayList<>();
                for (String userId : userIds) {
                    if (StringUtil.notNull(userId)) {
                        ProjectUser projectUser = new ProjectUser();
                        projectUser.setUserId(userId);
                        projectUser.setProjectId(project.getId());
                        Execute execute = new Execute(Execute.CREATE, projectUser);
                        executes.add(execute);
                    }
                }
                beanService.executeBatch(executes);
            }
            //项目配额
            Quota quota = project.getQuota();
            if(null != quota){
                quota.setProjectId(project.getId());
                beanService.create(quota);
            }
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
        return project;
    }

    @Transactional
    public Project update(Project project) throws BusiException{
        try{
            project = beanService.update(project);
            //项目成员
            beanService.remove(
                    ProjectUser.class,
                    Where.create("projectId",Expression.eq,project.getId())
                            .or("userId",Expression.in,project.getUserIds())
            );
            List<String> userIds = project.getUserIds();
            if(null != userIds) {
                List<Execute> executes = new ArrayList<>();
                for (String userId : userIds) {
                    if (StringUtil.notNull(userId)) {
                        ProjectUser projectUser = new ProjectUser();
                        projectUser.setUserId(userId);
                        projectUser.setProjectId(project.getId());
                        Execute execute = new Execute(Execute.CREATE, projectUser);
                        executes.add(execute);
                    }
                }
                beanService.executeBatch(executes);
            }
            //项目配额
            beanService.remove(
                    Quota.class,
                    Where.create("projectId",Expression.eq,project.getId())
            );
            Quota quota = project.getQuota();
            if(null != quota){
                quota.setProjectId(project.getId());
                beanService.create(quota);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new BusiException(e.getMessage());
        }
        return project;
    }

    @Transactional
    public void remove(String id) throws BusiException {
        try {
            beanService.remove(Project.class, id);
            beanService.remove(ProjectUser.class,
                    Where.create("projectId", Expression.eq, id));
            beanService.remove(Quota.class,
                    Where.create("projectId",Expression.eq,id));
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
    }
}
