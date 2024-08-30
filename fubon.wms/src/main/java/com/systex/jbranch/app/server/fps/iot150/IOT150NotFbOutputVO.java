package com.systex.jbranch.app.server.fps.iot150;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class IOT150NotFbOutputVO extends PagingOutputVO {
    private List<Map<String, Object>> data;
    private Date busDate; // 工作日

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public Date getBusDate() {
        return busDate;
    }

    public void setBusDate(Date busDate) {
        this.busDate = busDate;
    }
}
