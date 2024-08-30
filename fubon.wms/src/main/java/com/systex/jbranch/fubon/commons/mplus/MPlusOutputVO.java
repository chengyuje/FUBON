package com.systex.jbranch.fubon.commons.mplus;

import org.json.JSONString;

/**
 * Created by SebastianWu on 2016/10/20.
 * M+ 下行電文欄位
 */
public class MPlusOutputVO {
    /**
     * 00:success
     * 20:參數錯誤
     * 21:無操作權限
     * 30:該用戶未加入企業帳號
     * 33:群組不在或已解散
     * 35:部門不在
     * 38:用戶未註冊M+
     * 50:未上傳圖片
     * 51:圖片格式錯誤
     * 52:InfoPush無對應連結內容
     * 53:InfoPush連結格式錯誤
     * 54:發送名單內容錯誤
     * 90:系統錯誤
     */
    private String result;      //狀態代碼
    private String text;        //代碼訊息
    private String campaignId;  //Campaign序號(當result=00時回傳)
    private JSONString status;  //上傳名單檢核結果。(當result=00&&targetType=1時回傳)

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public JSONString getStatus() {
        return status;
    }

    public void setStatus(JSONString status) {
        this.status = status;
    }
}
