package com.systex.jbranch.app.server.fps.fps350;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class FPS350OutputVO extends PagingOutputVO {
    public FPS350OutputVO() {
    }
    
    private List<Map<String, Object>> outputList;
    private int checker;
    private String message;
    private String base64;
    
    public void setOutputList(List<Map<String, Object>> outputList) {
        this.outputList = outputList;
    }

    public List<Map<String, Object>> getOutputList() {
        return outputList;
    }

    public int getChecker() {
        return checker;
    }

    public void setChecker(int checker) {
        this.checker = checker;
    }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getBase64() {
		return base64;
	}

	public void setBase64(String base64) {
		this.base64 = base64;
	}

}
