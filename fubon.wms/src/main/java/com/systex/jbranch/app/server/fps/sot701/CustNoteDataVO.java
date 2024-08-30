package com.systex.jbranch.app.server.fps.sot701;

/**
 * Created by SebastianWu on 2016/9/19.
 */
public class CustNoteDataVO {
    private Boolean isSignRisk;     //是否簽署風險預告書
    private Boolean isInterdict;    //禁治產註記

    public Boolean getSignRisk() {
        return isSignRisk;
    }

    public void setSignRisk(Boolean signRisk) {
        isSignRisk = signRisk;
    }

    public Boolean getInterdict() {
        return isInterdict;
    }

    public void setInterdict(Boolean interdict) {
        isInterdict = interdict;
    }
}
