package com.jweb.busi.service.compute;

import com.jweb.busi.entity.compute.Flavor;
import com.jweb.busi.service.sync.SyncBeanService;
import com.jweb.common.exception.BusiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class FlavorService {
    @Autowired
    SyncBeanService syncBeanService;

    public Flavor create(Flavor flavor) throws BusiException {
        try{
            flavor = syncBeanService.create(flavor);
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
        return flavor;
    }
    public void remove(String flavorId) throws BusiException{
        try{
            syncBeanService.remove(Flavor.class,flavorId);
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
    }
}
