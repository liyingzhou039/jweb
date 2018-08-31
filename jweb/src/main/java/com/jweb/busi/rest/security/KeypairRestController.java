package com.jweb.busi.rest.security;

import com.jweb.busi.entity.security.Keypair;
import com.jweb.busi.service.security.KeypairService;
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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/rest/security/keypair")
public class KeypairRestController {
    @Autowired
    protected KeypairService keypairService;
    @Autowired
    BeanService beanService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public Object list(
            @RequestParam(defaultValue = "") String condition,
            @RequestParam(defaultValue = "") String order) throws BusiException {
        Where where = Where.parse(HexUtil.decode(condition));
        LoginUser user = Session.getCurrentUser();
        if(where==null) {
            where = Where.create("projectId", Expression.eq, user.getProjectId());
        }else{
            where = where.and("projectId", Expression.eq, user.getProjectId());
        }
        return beanService.list(Keypair.class,where,order);
    }

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
        return beanService.getPager(Keypair.class, pageNumber, pageSize,where, order);
    }

    @RequestMapping(value = "",method = RequestMethod.POST)
    @ResponseBody
    public Object create(
            @RequestBody Keypair keypair
    ){
        Result<?> r=new Result<>();
        try {
            keypairService.create(keypair);
            r.setOk(true);
        } catch (Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/{keypairId}",method = RequestMethod.DELETE)
    @ResponseBody
    public Object remove(@PathVariable String keypairId){
        Result<?> r = new Result<>();
        try {
            keypairService.remove(keypairId);
            r.setOk(true);
            r.setMsg("删除成功");
        }catch(Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/exists", method = RequestMethod.GET)
    @ResponseBody
    public Object exists(@RequestParam String name) throws BusiException {
        Result<?> r = new Result<>();
        try {
            LoginUser user = Session.getCurrentUser();
            List beans = beanService.list(Keypair.class,
                    Where.create("name",Expression.eq,name)
                            .and("projectId",Expression.eq,user.getProjectId()));
            if (beans == null || beans.size() <= 0) {
                throw new BusiException("不存在");
            }
            r.setOk(true);
        } catch (Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/export/{keypairId}", method = RequestMethod.GET)
    public void export(@PathVariable String keypairId, HttpServletResponse resp) throws Exception {
        Keypair keypair = beanService.getById(Keypair.class,keypairId);
        String privateKey = keypair.getPrivateKey();
        if(null==privateKey) privateKey = "";
        ServletOutputStream out = resp.getOutputStream();
        resp.addHeader("Content-Disposition", "attachment;filename=" + keypair.getName()+ ".pem");
        resp.addHeader("Content-Length", "" + privateKey.getBytes().length);
        resp.setContentType("application/octet-stream");
        out.write(privateKey.getBytes());
        out.flush();
        out.close();
    }

}
