package com.jweb.busi.rest.compute;

import com.jweb.busi.entity.compute.Volume;
import com.jweb.busi.service.compute.VolumeService;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import com.jweb.common.util.Pager;
import com.jweb.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/compute/volume")
public class VolumeRestController {
    @Autowired
    VolumeService volumeService;
    @Autowired
    BeanService beanService;
    @RequestMapping(value = "/pager", method = RequestMethod.GET)
    @ResponseBody
    public Object listPager(
            @RequestParam(required = true) String serverId,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) throws BusiException {
        Pager pager = beanService.getPager(Volume.class,pageNumber,pageSize,
                Where.create("serverId",Expression.eq,serverId),null);
        pager.setRows(volumeService.getSynced(pager.getRows()));
        return pager;
    }
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public Object create(@RequestBody Volume volume) {
        Result<Object> r = new Result<>();
        try {
            volumeService.create(volume);
            r.setOk(true);
            r.setMsg("新增成功");
        } catch (BusiException e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }
    @RequestMapping(value = "/{volumeId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Object remove(@PathVariable String volumeId) {
        Result<Object> r = new Result<>();
        try {
            volumeService.remove(volumeId);
            r.setOk(true);
            r.setMsg("删除成功");
        } catch (BusiException e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }
}
