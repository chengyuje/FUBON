package com.systex.jbranch.fubon.bth.job.proxy;

import static com.systex.jbranch.fubon.bth.job.sevEntity.IntegrationService.exeQueryOracleWMS;
import static com.systex.jbranch.fubon.bth.job.sevEntity.IntegrationService.generateFile;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.bth.job.context.StoredContext;
import com.systex.jbranch.fubon.bth.job.inf.Proxy;

/**
 * 產生逗點分隔 File檔案 
 * @author Eli
 * @date 20180331
 * 
 *
 */
public class FileByDotOracle implements Proxy {

	@Override
	public void execute(Object obj) throws Exception {
		StoredContext c = new StoredContext(obj.getClass().getSimpleName());
		generateFile(c, exeQueryOracleWMS(c), AccessContext.DOT);
		
	}
	
}
