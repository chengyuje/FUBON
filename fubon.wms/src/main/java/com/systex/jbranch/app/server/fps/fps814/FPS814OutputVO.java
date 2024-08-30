package com.systex.jbranch.app.server.fps.fps814;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class FPS814OutputVO extends PagingOutputVO {
    
	private List<Map<String, Object>> outputList;
	private Integer vipA;
	private Integer vipB;
	private Integer vipV;
	
	public Integer getVipA() {
		return vipA;
	}

	public void setVipA(Integer vipA) {
		this.vipA = vipA;
	}

	public Integer getVipB() {
		return vipB;
	}

	public void setVipB(Integer vipB) {
		this.vipB = vipB;
	}

	public Integer getVipV() {
		return vipV;
	}

	public void setVipV(Integer vipV) {
		this.vipV = vipV;
	}

	public FPS814OutputVO() {}
    
    public void setOutputList(List<Map<String, Object>> outputList) {
        this.outputList = outputList;
    }

    public List<Map<String, Object>> getOutputList() {
        return outputList;
    }

}
