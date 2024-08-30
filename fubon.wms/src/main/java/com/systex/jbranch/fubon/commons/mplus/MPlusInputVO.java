package com.systex.jbranch.fubon.commons.mplus;

import java.io.File;
import java.util.Arrays;

import org.json.JSONObject;

/**
 * Created by SebastianWu on 2016/10/20.
 * M+ 上行電文欄位
 */
public class MPlusInputVO {
    private String account;         //企業帳號
    private String password;        //企業帳號密碼，需加密
    private String targetType;      //發送目標型態 0:全員發送 1:指定名單 2:群組 3:部門
    private String msgType;         //訊息型態 T:文 P:圖片 I:InfoPush
    private String sendtime;        //發送時間,格式為 yyyymmddhhmi
    private JSONObject text;        //文訊息內容,當msgType=T時為必填
    private BinaryFile image;       //圖片訊息內容,當msgType=P時為必填,一次Campaign僅能發送一張圖片
    private String infopushCount;   //InfoPush內容個數若msgType=I必填
    private String templateType;    //手機上訊息呈現的版型 '1':版型一(內嵌中繼頁,連結另開網頁) '2':版型二(另開網頁),若msgType=I,則必填
    private JSONObject headline;    //文訊息內容,當msgType=I時為必填,支援多組訊息,順序按Array中之排序
    private BinaryFile iconFile1;   //第一組 info-push圖檔,若msgType=I時必填,須為jpg檔案
    private BinaryFile iconFile2;   //第二組 info-push圖檔,若msgType=I時必填,須為jpg檔案
    private BinaryFile iconFile3;   //第三組 info-push圖檔,若msgType=I時必填,須為jpg檔案
    private JSONObject infoUrl;     //內容鏈結,若msgType=I,至少填入一組,須與Infopush內容數量對應,格式為正確的URL連結 ex:infoUrl={“url”:[“url1”,”url2”,”url3”]}
    private BinaryFile target;      //若targetType=1,則必須上傳名單檔案。名單檔案格式為一行一個門號的txt檔
    private JSONObject department;  //部門名稱(若targetType=3,則必填)
    private JSONObject group;       //群組代碼(若targetType=2,則必填)
    private String operatorId;      //有權限查詢此Campaign狀態的訊息管理員帳號。若無指定則僅有企業admin可查詢。
    private String skey;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime;
    }

    public JSONObject getText() {
        return text;
    }

    public void setText(JSONObject text) {
        this.text = text;
    }

    public BinaryFile getImage() {
        return image;
    }

    public void setImage(BinaryFile image) {
        this.image = image;
    }

    public String getInfopushCount() {
        return infopushCount;
    }

    public void setInfopushCount(String infopushCount) {
        this.infopushCount = infopushCount;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public JSONObject getHeadline() {
        return headline;
    }

    public void setHeadline(JSONObject headline) {
        this.headline = headline;
    }

    public BinaryFile getIconFile1() {
        return iconFile1;
    }

    public void setIconFile1(BinaryFile iconFile1) {
        this.iconFile1 = iconFile1;
    }

    public BinaryFile getIconFile2() {
        return iconFile2;
    }

    public void setIconFile2(BinaryFile iconFile2) {
        this.iconFile2 = iconFile2;
    }

    public BinaryFile getIconFile3() {
        return iconFile3;
    }

    public void setIconFile3(BinaryFile iconFile3) {
        this.iconFile3 = iconFile3;
    }

    public JSONObject getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(JSONObject infoUrl) {
        this.infoUrl = infoUrl;
    }

    public BinaryFile getTarget() {
        return target;
    }

    public void setTarget(BinaryFile target) {
        this.target = target;
    }

    public JSONObject getDepartment() {
        return department;
    }

    public void setDepartment(JSONObject department) {
        this.department = department;
    }

    public JSONObject getGroup() {
        return group;
    }

    public void setGroup(JSONObject group) {
        this.group = group;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getSkey() {
		return skey;
	}

	public void setSkey(String skey) {
		this.skey = skey;
	}

	@Override
    public String toString() {
        return "MPlusInputVO{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", targetType='" + targetType + '\'' +
                ", msgType='" + msgType + '\'' +
                ", sendtime='" + sendtime + '\'' +
                ", text=" + text +
                ", image=" + image +
                ", infopushCount='" + infopushCount + '\'' +
                ", templateType='" + templateType + '\'' +
                ", headline='" + headline + '\'' +
                ", iconFile1=" + iconFile1 +
                ", iconFile2=" + iconFile2 +
                ", iconFile3=" + iconFile3 +
                ", infoUrl=" + infoUrl +
                ", target=" + target +
                ", department=" + department +
                ", group=" + group +
                ", operatorId='" + operatorId + '\'' +
                '}';
    }

    public class BinaryFile {
        private String fileName;    //檔名
        private byte[] fileCxt;     //內容
        private File fileObj;		// 內容:java.io.File

        public BinaryFile() {
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public byte[] getFileCxt() {
            return fileCxt;
        }

        public void setFileCxt(byte[] fileCxt) {
            this.fileCxt = fileCxt;
        }

        public File getFileObj() {
        	return this.fileObj;
        }
        
        public void setFileObj(File fileObj) {
        	this.fileObj = fileObj;
        }
        
        @Override
        public String toString() {
            return "BinaryFile{" +
                    "fileName='" + fileName + '\'' +
                    ", fileCxt=" + Arrays.toString(fileCxt) +
                    '}';
        }
    }
}
