package com.jweb.busi.rest.compute;

import com.jweb.busi.entity.compute.Server;
import com.jweb.busi.service.compute.ServerService;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import com.jweb.common.session.Session;
import com.jweb.common.util.HexUtil;
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
        return beanService.getPager(Server.class, pageNumber, pageSize,where, order);
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

}
