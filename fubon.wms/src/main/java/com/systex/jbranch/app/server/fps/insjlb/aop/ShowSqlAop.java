package com.systex.jbranch.app.server.fps.insjlb.aop;

import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.insjlb.ws.client.PolicySoapVo;
import com.systex.jbranch.platform.common.query.impl.hibernate.SQLUtilityImpl2;

@Component
@Aspect
public class ShowSqlAop {
	@Pointcut(value="execution(* com.systex.jbranch.app.server.fps.insjlb.ws.client.*.afterSend(..))")
	public void pointcut(){}

    @Before(value="pointcut()")
    public void logBefore(JoinPoint point) {
    	//QueryConditionIF queryCondition
    	
    	if(ArrayUtils.isEmpty(point.getArgs())){
    		return;
    	}
    	
    	if(point.getArgs()[0].getClass().getName().indexOf("SQLUtilityImpl2") > -1){
    		PolicySoapVo PolicySoapVo = (PolicySoapVo)point.getArgs()[0];
//    		System.out.printlnPolicySoapVo.getSoapResponseData());
    		
    	}

    }
}



