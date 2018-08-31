package com.jweb.busi.service.security;
import com.jweb.busi.entity.security.Keypair;
import com.jweb.busi.service.sync.SyncBeanService;
import com.jweb.common.exception.BusiException;
import com.jweb.common.session.Session;
import com.jweb.sys.dto.identity.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeypairService {
    @Autowired
    SyncBeanService syncBeanService;
    public Keypair create(Keypair keypair) throws BusiException {
        try{
            LoginUser user = Session.getCurrentUser();
            keypair.setUserId(user.getId());
            keypair.setProjectId(user.getProjectId());
            keypair = syncBeanService.create(keypair);
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
        return keypair;
    }
    public void remove(String keypairId) throws BusiException{
        try{
            syncBeanService.remove(Keypair.class,keypairId);
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
    }
}
