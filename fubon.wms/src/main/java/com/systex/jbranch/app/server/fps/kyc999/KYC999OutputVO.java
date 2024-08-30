package com.systex.jbranch.app.server.fps.kyc999;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



@Component
@Scope("prototype")
@SuppressWarnings({"rawtypes", "unchecked"})
public class KYC999OutputVO {
	
	 private List resultList;

	 public List getResultList() {
	     return Collections.unmodifiableList(resultList);
	 }

	 public void setResultList(List resultList) {
	     this.resultList = resultList;
	 }
}
