package com.jweb;

import com.jweb.busi.entity.center.Center;
import com.jweb.busi.service.center.CenterService;
import com.jweb.common.service.BeanService;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.QuotaSet;
import org.openstack4j.model.compute.QuotaSetUpdate;
import org.openstack4j.model.compute.ext.HypervisorStatistics;
import org.openstack4j.model.identity.v3.Project;
import org.openstack4j.model.network.NetQuota;
import org.openstack4j.model.storage.block.BlockQuotaSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @ClassName: BootRunner 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:19:16  
 */
@Component
public class BootRunner implements CommandLineRunner{
	@Autowired
	BeanService beanService;
	@Autowired
	CenterService centerService;
	@Override
	public void run(String... args){
		System.out.println(">>>>>>>>>>>>>>>>>创建表<<<<<<<<<<<<<<<");
		beanService.createTables();
		System.out.println(">>>>>>>>>>>>初始化管理员配额<<<<<<<<<<<<");
		try{
			for(Center center:centerService.all()){
				OSClient.OSClientV3 os = centerService.os(center);
				HypervisorStatistics hst = os.compute().hypervisors().statistics();
				Project project = os.getToken().getProject();
				QuotaSetUpdate computeQsUpdate = Builders.quotaSet()
						.metadataItems(10000)
						.cores(hst.getVirtualCPU())
						.instances(hst.getVirtualCPU())
						.injectedFiles(10000)
						.injectedFileContentBytes(10000*1024*1024)
						.keyPairs(100000)
						.injectedFilePathBytes(10000*1024)
						.ram(hst.getMemory())
						.build();
				QuotaSet computeQs = os.compute().quotaSets().updateForTenant(project.getId(), computeQsUpdate);
				//存储配额
				BlockQuotaSet blockQs = Builders.blockQuotaSet()
						.volumes(hst.getLocal())
						.snapshots(hst.getLocal())
						.gigabytes(hst.getLocal())
						.build();
				blockQs = os.blockStorage().quotaSets().updateForTenant(project.getId(), blockQs);

				//网络配额
				NetQuota networkQs = Builders.netQuota()
						.securityGroup(10000)
						.securityGroupRule(100000)
						.floatingIP(100000)
						.network(1000)
						.port(100000)
						.router(10000)
						.subnet(10000)
						.build();
				networkQs = os.networking().quotas().updateForTenant(project.getId(), networkQs);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
