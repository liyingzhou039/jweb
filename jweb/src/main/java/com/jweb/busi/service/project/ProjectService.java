package com.jweb.busi.service.project;

import com.jweb.busi.entity.compute.Snapshot;
import com.jweb.busi.entity.project.Project;
import com.jweb.busi.entity.project.ProjectUser;
import com.jweb.busi.entity.project.Quota;
import com.jweb.common.dto.Execute;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import com.jweb.common.session.Session;
import com.jweb.common.util.StringUtil;
import com.jweb.sys.dto.identity.LoginUser;
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

    public Quota getUserQuota(){
        LoginUser user = Session.getCurrentUser();
        return beanService.queryOne(Quota.class,
                "select * from Quota q where projectId=?"
                ,user.getProjectId());
    }

    public Quota getUserUsedQuota(){
        LoginUser user = Session.getCurrentUser();
        Quota serverQuota = beanService.queryOne(Quota.class,
                "select count(s.id) as instances,sum(f.ram) as ram,sum(f.vcpus) as cores,sum(f.disk) as gigabytes "
                        +" from Server s join Flavor f on f.id=s.flavorId where s.projectId=?",user.getProjectId());
        //再获取卷数量且将卷占用的存储加入
        Quota volumeQuota = beanService.queryOne(
                Quota.class,
                "select count(v.id) as volumes,sum(v.size) as gigabytes from Server s join Volume v on v.serverId=s.id where s.projectId=?"
                ,user.getProjectId());
        serverQuota.setVolumes(volumeQuota.getVolumes());
        serverQuota.setGigabytes(serverQuota.getGigabytes()+volumeQuota.getGigabytes());
        //快照占用的存储不计算在内
        Quota snapshotQuota = beanService.queryOne(
                Quota.class,
                "select count(p.id) as snapshots from Server s join Snapshot p on p.serverId=s.id where s.projectId=?"
                ,user.getProjectId());
        serverQuota.setSnapshots(snapshotQuota.getSnapshots());

        return serverQuota;
    }

    public void checkQuota(Quota quota) throws BusiException{
        Quota userQuota = this.getUserQuota();
        Quota userUsedQuota = this.getUserUsedQuota();
        if(quota!=null){
            if(userUsedQuota.getGigabytes()+quota.getGigabytes()>userQuota.getGigabytes() && quota.getGigabytes()>0)
                throw new BusiException("最多允许使用"+userQuota.getGigabytes()+"GB存储");
            if(userUsedQuota.getSnapshots()+quota.getSnapshots()>userQuota.getSnapshots() && quota.getSnapshots() >0)
                throw new BusiException("最多允许创建"+userQuota.getSnapshots()+"个备份");
            if(userUsedQuota.getVolumes()+quota.getVolumes()>userQuota.getVolumes() && quota.getVolumes()>0)
                throw new BusiException("最多允许创建"+userQuota.getVolumes()+"块磁盘");
            if(userUsedQuota.getCores()+quota.getCores()>userQuota.getCores() && quota.getCores()>0)
                throw new BusiException("最多允许使用"+userQuota.getCores()+"个虚拟CPU");
            if(userUsedQuota.getInstances()+quota.getInstances()>userQuota.getInstances() && quota.getInstances()>0)
                throw new BusiException("最多允许创建"+userQuota.getInstances()+"台虚拟机");
            if(userUsedQuota.getRam()+quota.getRam()>userQuota.getRam() && quota.getRam()>0)
                throw new BusiException("最多允许使用"+userQuota.getRam()+"GB内存");

        }
    }
}
