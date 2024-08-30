package com.systex.jbranch.platform.server.bizLogic;

import javax.xml.bind.Unmarshaller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.systex.jbranch.platform.common.xml.JaxbUtil;
import java.util.Map;

public class EAIServiceProvider implements IServiceProvider {

	private Unmarshaller unmarshaller;
	private IServiceProvider serviceProvider;
	
	@Transactional(propagation = Propagation.REQUIRED)
	public Object invoke(Object value,Map params) throws RuntimeException 
	{
		try
		{
			Object rq=JaxbUtil.unmarshal(getUnmarshaller(), (String)value);
			Object rs=serviceProvider.invoke(rq,params);
			
			if(rs != null)
				return JaxbUtil.marshal(rs);
			else
				return null;
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public Unmarshaller getUnmarshaller() {
		return unmarshaller;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

	public IServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(IServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
}
