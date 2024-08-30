package com.systex.jbranch.app.server.fps.insjlb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.systex.jbranch.app.server.fps.ins142.Ins142ReportInf;
import com.systex.jbranch.app.server.fps.ins142.Ins143ReportInf;
import com.systex.jbranch.app.server.fps.ins142.Ins144ReportInf;
import com.systex.jbranch.app.server.fps.ins142.Ins145ReportInf;
import com.systex.jbranch.app.server.fps.insjlb.service.GetCoverage01ServiceInf;
import com.systex.jbranch.app.server.fps.insjlb.service.GetCoverage03ServiceInf;
import com.systex.jbranch.app.server.fps.insjlb.service.GetInsCompareServiceInf;
import com.systex.jbranch.app.server.fps.insjlb.service.GetInsLocalPolicyServiceInf;
import com.systex.jbranch.app.server.fps.insjlb.service.GetInsPdfServiceInf;
import com.systex.jbranch.app.server.fps.insjlb.service.GetOldItemListServiceInf;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

abstract public class AbstractInsjlb extends BizLogic implements InsjlbBusinessInf{		
	@Autowired @Qualifier("GetCoverage01Service")
	private GetCoverage01ServiceInf getCoverage01Service;
	
	@Autowired @Qualifier("GetCoverage03Service")
	private GetCoverage03ServiceInf getCoverage03Service;
	
	@Autowired @Qualifier("GetInsPdfService")
	private GetInsPdfServiceInf getInsPdfService;
	
	@Autowired @Qualifier("GetInsLocalPolicyService")
	private GetInsLocalPolicyServiceInf getInsLocalPolicyService;
	
	@Autowired @Qualifier("GetOldItemListService")
	private GetOldItemListServiceInf getOldItemListService;
	
	@Autowired @Qualifier("GetInsCompareService")
	private GetInsCompareServiceInf getInsCompareService;
	
	@Autowired @Qualifier("Ins142Report")
	private Ins142ReportInf ins142Report;
	
	@Autowired @Qualifier("Ins143Report")
	private Ins143ReportInf ins143Report;
	
	@Autowired @Qualifier("Ins144Report")
	private Ins144ReportInf ins144Report;

	@Autowired @Qualifier("Ins145Report")
	private Ins145ReportInf ins145Report;
	
	//setter & getter start
	public GetCoverage01ServiceInf getGetCoverage01Service() {
		return getCoverage01Service;
	}

	public void setGetCoverage01Service(GetCoverage01ServiceInf getCoverage01Service) {
		this.getCoverage01Service = getCoverage01Service;
	}

	public GetCoverage03ServiceInf getGetCoverage03Service() {
		return getCoverage03Service;
	}

	public void setGetCoverage03Service(GetCoverage03ServiceInf getCoverage03Service) {
		this.getCoverage03Service = getCoverage03Service;
	}

	public GetInsPdfServiceInf getGetInsPdfService() {
		return getInsPdfService;
	}

	public void setGetInsPdfService(GetInsPdfServiceInf getInsPdfService) {
		this.getInsPdfService = getInsPdfService;
	}

	public GetInsLocalPolicyServiceInf getGetInsLocalPolicyService() {
		return getInsLocalPolicyService;
	}

	public void setGetInsLocalPolicyService(GetInsLocalPolicyServiceInf getInsLocalPolicyService) {
		this.getInsLocalPolicyService = getInsLocalPolicyService;
	}

	public GetOldItemListServiceInf getGetOldItemListService() {
		return getOldItemListService;
	}

	public void setGetOldItemListService(GetOldItemListServiceInf getOldItemListService) {
		this.getOldItemListService = getOldItemListService;
	}

	public GetInsCompareServiceInf getGetInsCompareService() {
		return getInsCompareService;
	}

	public void setGetInsCompareService(GetInsCompareServiceInf getInsCompareService) {
		this.getInsCompareService = getInsCompareService;
	}
	
	public Ins145ReportInf getIns145Report() {
		return ins145Report;
	}

	public void setIns145Report(Ins145ReportInf ins145Report) {
		this.ins145Report = ins145Report;
	}

	public Ins142ReportInf getIns142Report() {
		return ins142Report;
	}

	public void setIns142Report(Ins142ReportInf ins142Report) {
		this.ins142Report = ins142Report;
	}

	public Ins143ReportInf getIns143Report() {
		return ins143Report;
	}

	public void setIns143Report(Ins143ReportInf ins143Report) {
		this.ins143Report = ins143Report;
	}

	public Ins144ReportInf getIns144Report() {
		return ins144Report;
	}

	public void setIns144Report(Ins144ReportInf ins144Report) {
		this.ins144Report = ins144Report;
	}
}
