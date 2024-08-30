package com.systex.jbranch.app.server.fps.sot708;

import java.util.List;

/**
 * Created by SebastianWu on 2016/9/30.
 */
public class SOT708OutputVO {
    private List<CustAssetSIVO> custAssetSIList; //客戶SI資產資料

    public List<CustAssetSIVO> getCustAssetSIList() {
        return custAssetSIList;
    }

    public void setCustAssetSIList(List<CustAssetSIVO> custAssetSIList) {
        this.custAssetSIList = custAssetSIList;
    }
}
