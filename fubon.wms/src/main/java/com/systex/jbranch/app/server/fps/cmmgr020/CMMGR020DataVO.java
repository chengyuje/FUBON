package com.systex.jbranch.app.server.fps.cmmgr020;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CMMGR020DataVO extends PagingOutputVO {

	private List meunTreeLS;
	private List data;

	public List getMeunTreeLS() {
		return meunTreeLS;
	}
	public void setMeunTreeLS(List meunTreeLS) {
		this.meunTreeLS = meunTreeLS;
	}
	public List getData() {
		return data;
	}
	public void setData(List data) {
		this.data = data;
	}

}
