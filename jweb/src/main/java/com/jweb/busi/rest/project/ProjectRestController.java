package com.jweb.busi.rest.project;

import com.jweb.busi.entity.project.Project;
import com.jweb.busi.service.project.ProjectService;
import com.jweb.common.service.BeanService;
import com.jweb.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/project/project")
public class ProjectRestController {
    @Autowired
    protected ProjectService projectService;
    @Autowired
    BeanService beanService;

    @RequestMapping(value = "",method = RequestMethod.POST)
    @ResponseBody
    public Object create(
        @RequestBody Project project
    ){
        Result<?> r=new Result<>();
        try {
            projectService.create(project);
            r.setOk(true);
        } catch (Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "",method = RequestMethod.PUT)
    @ResponseBody
    public Object update(
        @RequestBody Project project
    ){
        Result<?> r=new Result<>();
        try {
            projectService.update(project);
            r.setOk(true);
        } catch (Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/{projectId}",method = RequestMethod.DELETE)
    @ResponseBody
    public Object remove(@PathVariable String projectId){
        Result<?> r = new Result<>();
        try {
            projectService.remove(projectId);
            r.setOk(true);
            r.setMsg("删除成功");
        }catch(Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/{projectId}",method = RequestMethod.PUT)
    @ResponseBody
    public Object enabled(
            @PathVariable String projectId,
            @RequestParam Boolean enabled
    ){
        Result<?> r=new Result<>();
        try {
            Project project = beanService.getById(Project.class,projectId);
            project.setEnabled(enabled);
            beanService.update(project);
            r.setOk(true);
        } catch (Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }
}
