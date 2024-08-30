package com.systex.jbranch.app.server.fps.pms110;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

@Component
@Scope("request")
public class PMS110OutputVO extends PagingOutputVO {

	//	private List resultList;
	//	private List resultList2;
	//	private List caseList;
	//	private List custList;
	//	private List<Map<String,Object>> ymList;
	//	private String currentYM;
	//	private List actList; 		// 活動集合

	private List<Map<String, Object>> pipelineList; // 主查詢集合
	private List<Map<String, Object>> pipelineDtl;	// 修改
	private List<Map<String, Object>> pilelineSumList;

	public List<Map<String, Object>> getPilelineSumList() {
		return pilelineSumList;
	}

	public void setPilelineSumList(List<Map<String, Object>> pilelineSumList) {
		this.pilelineSumList = pilelineSumList;
	}

	public List<Map<String, Object>> getPipelineList() {
		return pipelineList;
	}

	public void setPipelineList(List<Map<String, Object>> pipelineList) {
		this.pipelineList = pipelineList;
	}

	public List<Map<String, Object>> getPipelineDtl() {
		return pipelineDtl;
	}

	public void setPipelineDtl(List<Map<String, Object>> pipelineDtl) {
		this.pipelineDtl = pipelineDtl;
	}

}
