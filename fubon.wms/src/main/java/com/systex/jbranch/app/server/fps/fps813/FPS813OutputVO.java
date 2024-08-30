package com.systex.jbranch.app.server.fps.fps813;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class FPS813OutputVO extends PagingOutputVO {
    public FPS813OutputVO() {}
    
    private List<Map<String, Object>> outputList;
    
    public void setOutputList(List<Map<String, Object>> outputList) {
        this.outputList = outputList;
    }

    public List<Map<String, Object>> getOutputList() {
        return outputList;
    }
}
