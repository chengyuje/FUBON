package com.systex.jbranch.app.server.fps.fpsjlb.business;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.comutil.collection.GenericMap;

/**有無投保註記**/
@Component("CustIandIMark")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CustIandIMark extends AbstractFpsjlbBusiness{

	@Override
	public GenericMap excute(GenericMap paramGenericMap) throws Exception {
		GenericMap result = new GenericMap().put(RESULT, CollectionUtils.isNotEmpty(
				fpsjlbDao.queryCustIandIMark(paramGenericMap.get(CUST_ID , String.class))));
		return result;
	}
	
}
