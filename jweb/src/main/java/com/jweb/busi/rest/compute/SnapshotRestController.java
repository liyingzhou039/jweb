package com.jweb.busi.rest.compute;

import com.jweb.busi.entity.compute.Snapshot;
import com.jweb.busi.service.compute.SnapshotService;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import com.jweb.common.util.Pager;
import com.jweb.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/compute/snapshot")
public class SnapshotRestController {
    @Autowired
    SnapshotService snapshotService;
    @Autowired
    BeanService beanService;
    @RequestMapping(value = "/pager", method = RequestMethod.GET)
    @ResponseBody
    public Object listPager(
            @RequestParam(required = true) String serverId,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) throws BusiException {
        Pager pager = beanService.getPager(Snapshot.class,pageNumber,pageSize,
                Where.create("serverId",Expression.eq,serverId),"name desc");
        pager.setRows(snapshotService.getSynced(pager.getRows()));
        return pager;
    }
    @RequestMapping(value = "/{serverId}", method = RequestMethod.POST)
    @ResponseBody
    public Object create(@PathVariable String serverId) {
        Result<Object> r = new Result<>();
        try {
            snapshotService.create(serverId);
            r.setOk(true);
            r.setMsg("备份成功");
        } catch (BusiException e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/image/{serverId}", method = RequestMethod.POST)
    @ResponseBody
    public Object createImage(@PathVariable String serverId) {
        Result<Object> r = new Result<>();
        try {
            snapshotService.createImage(serverId,null);
            r.setOk(true);
            r.setMsg("制作镜像成功");
        } catch (BusiException e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/{snapshotId}", method = RequestMethod.PUT)
    @ResponseBody
    public Object restore(@PathVariable String snapshotId) {
        Result<Object> r = new Result<>();
        try {
            snapshotService.restore(snapshotId);
            r.setOk(true);
            r.setMsg("还原成功");
        } catch (BusiException e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/{snapshotId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Object remove(@PathVariable String snapshotId) {
        Result<Object> r = new Result<>();
        try {
            snapshotService.remove(snapshotId);
            r.setOk(true);
            r.setMsg("删除成功");
        } catch (BusiException e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }
}
