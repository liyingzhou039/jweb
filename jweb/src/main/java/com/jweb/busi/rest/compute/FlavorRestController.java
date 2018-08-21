package com.jweb.busi.rest.compute;

import com.jweb.busi.entity.compute.Flavor;
import com.jweb.busi.service.compute.FlavorService;
import com.jweb.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/compute/flavor")
public class FlavorRestController {
    @Autowired
    protected FlavorService flavorService;

    @RequestMapping(value = "",method = RequestMethod.POST)
    @ResponseBody
    public Object create(
            @RequestBody Flavor flavor
    ){
        Result<?> r=new Result<>();
        try {
            flavorService.create(flavor);
            r.setOk(true);
        } catch (Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/{flavorId}",method = RequestMethod.DELETE)
    @ResponseBody
    public Object remove(@PathVariable String flavorId){
        Result<?> r = new Result<>();
        try {
            flavorService.remove(flavorId);
            r.setOk(true);
            r.setMsg("删除成功");
        }catch(Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

}
