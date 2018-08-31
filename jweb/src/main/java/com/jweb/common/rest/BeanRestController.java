package com.jweb.common.rest;

import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.BeanPool;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import com.jweb.common.util.HexUtil;
import com.jweb.common.util.JsonUtil;
import com.jweb.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName: BeanRestController
 * @Description: 模型操作通用Rest接口
 * @author: liyz
 * @date: 2018年1月31日 下午4:19:09
 */
@RestController
@RequestMapping("/rest/bean")
public class BeanRestController {
    @Autowired
    protected BeanService beanService;



    @RequestMapping(value = "/{beanName}Pager", method = RequestMethod.GET)
    @ResponseBody
    public Object listPager(
            @PathVariable String beanName,
            @RequestParam(defaultValue = "") String condition,
            @RequestParam(defaultValue = "") String order,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) throws BusiException {
        Class<?> beanClass = BeanPool.getBeanClassBySimpleName(beanName);
        Where where = Where.parse(HexUtil.decode(condition));
        return beanService.getPager(beanClass, pageNumber, pageSize,where,order);
    }

    @RequestMapping(value = "/{beanName}", method = RequestMethod.GET)
    @ResponseBody
    public Object list(
            @PathVariable String beanName,
            @RequestParam(defaultValue = "") String condition,
            @RequestParam(defaultValue = "") String order
    ) throws BusiException {
        Class<?> beanClass = BeanPool.getBeanClassBySimpleName(beanName);
        Where where = Where.parse(HexUtil.decode(condition));
        return beanService.list(beanClass,where,order);
    }

    @RequestMapping(value = "/{beanName}/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Object getById(
            @PathVariable String beanName,
            @PathVariable String id
    ) throws BusiException {
        Class<?> beanClass = BeanPool.getBeanClassBySimpleName(beanName);
        return beanService.getById(beanClass, id);
    }

    @RequestMapping(value = "/{beanName}", method = RequestMethod.POST)
    @ResponseBody
    public Object create(@PathVariable String beanName, @RequestParam(defaultValue = "{}") String beanJson) throws BusiException {
        Result<?> r = new Result<>();
        try {
            Class<?> beanClass = BeanPool.getBeanClassBySimpleName(beanName);
            Object bean = JsonUtil.jsonToBean(beanJson, beanClass);
            Method method = beanClass.getDeclaredMethod("setId", String.class);
            method.invoke(bean, UUID.randomUUID().toString().replace("-", ""));
            //加入后端验证
            beanService.create(bean);
            r.setOk(true);
        } catch (Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/{beanName}", method = RequestMethod.PUT)
    @ResponseBody
    public Object update(@PathVariable String beanName, @RequestParam(defaultValue = "{}") String beanJson) throws BusiException {
        Result<?> r = new Result<>();
        try {
            Class<?> beanClass = BeanPool.getBeanClassBySimpleName(beanName);
            Object bean = JsonUtil.jsonToBean(beanJson, beanClass);
            beanService.update(bean);
            r.setOk(true);
        } catch (Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }


    @RequestMapping(value = "/{beanName}/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Object remove(@PathVariable String beanName, @PathVariable String id) throws BusiException {
        Class<?> beanClass = BeanPool.getBeanClassBySimpleName(beanName);
        Result<?> r = new Result<>();
        try {
            beanService.remove(beanClass, id);
            r.setOk(true);
        } catch (Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/exists{beanName}", method = RequestMethod.GET)
    @ResponseBody
    public Object exists(@PathVariable String beanName, HttpServletRequest req) throws BusiException {
        Class<?> beanClass = BeanPool.getBeanClassBySimpleName(beanName);
        Result<?> r = new Result<>();
        try {
            Where where = null;
            Enumeration<String> params = req.getParameterNames();
            while (params != null && params.hasMoreElements()) {
                String param = params.nextElement();
                if (!param.startsWith("_")) {
                    if(null==where){
                        where = Where.create(param, Expression.eq, req.getParameter(param));
                    }else {
                        where.and(param, Expression.eq, req.getParameter(param));
                    }
                }
            }
            List beans = beanService.list(beanClass, where, null);
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
}