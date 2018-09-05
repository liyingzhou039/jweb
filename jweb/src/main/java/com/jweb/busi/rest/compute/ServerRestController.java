package com.jweb.busi.rest.compute;

import com.jweb.busi.entity.compute.Server;
import com.jweb.busi.service.compute.ServerService;
import com.jweb.busi.service.project.ProjectService;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import com.jweb.common.session.Session;
import com.jweb.common.util.HexUtil;
import com.jweb.common.util.Pager;
import com.jweb.common.util.Result;
import com.jweb.sys.dto.identity.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/rest/compute/server")
public class ServerRestController {
    @Autowired
    ServerService serverService;
    @Autowired
    BeanService beanService;

    @Autowired
    ProjectService projectService;

    @RequestMapping(value = "/pager", method = RequestMethod.GET)
    @ResponseBody
    public Object listPager(
            @RequestParam(defaultValue = "") String condition,
            @RequestParam(defaultValue = "") String order,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) throws BusiException {
        Where where = Where.parse(HexUtil.decode(condition));
        LoginUser user = Session.getCurrentUser();
        if(where==null) {
            where = Where.create("projectId", Expression.eq, user.getProjectId());
        }else{
            where = where.and("projectId", Expression.eq, user.getProjectId());
        }
        Pager<Server> pager = beanService.getPager(Server.class, pageNumber, pageSize,where, order);
        if(pager!=null){
            pager.setRows(serverService.getSynced(pager.getRows()));
        }
        return pager;
    }

    @RequestMapping(value = "",method = RequestMethod.POST)
    @ResponseBody
    public Object create(
            @RequestBody Server server
    ){
        Result<?> r=new Result<>();
        try {
            serverService.create(server);
            r.setOk(true);
        } catch (Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }
    @RequestMapping(value = "/{serverId}",method = RequestMethod.GET)
    @ResponseBody
    public Object getById(@PathVariable String serverId){
        Result<Server> r = new Result<>();
        try {
            r.setEntity(serverService.getById(serverId));
            r.setOk(true);
            r.setMsg("获取成功");
        }catch(Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }
    @RequestMapping(value = "/{serverId}",method = RequestMethod.DELETE)
    @ResponseBody
    public Object remove(@PathVariable String serverId){
        Result<?> r = new Result<>();
        try {
            serverService.remove(serverId);
            r.setOk(true);
            r.setMsg("删除成功");
        }catch(Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }
    @RequestMapping(value = "/action/{serverId}", method = RequestMethod.PUT)
    @ResponseBody
    public Object action(@PathVariable String serverId, @RequestParam String action) {
        Result<Object> r = new Result<>();
        try {
            serverService.action(serverId, action);
            r.setOk(true);
            r.setMsg("成功");
        } catch (BusiException e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }
}
