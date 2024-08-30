package com.systex.jbranch.platform.common.multiLang;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TbsysmultilangPK;
import com.systex.jbranch.platform.common.platformdao.table.TbsysmultilangVO;

/**
 *此類別的功能描述：多國語系的共用函式
 * Notices: 此程式的注意事項：無
 *
 *@author  Benson Chen
 *@since   此Class 從哪個版本開始加入  (外部的version)
 *@see     			
 **/
public class MultiLangUtil {

    /**
     *此Method的功能描述：將多國語系編號轉換成MultiLangIdType
     * 
     *@param  傳入多國語系編號
     *@return  MultiLangIdType類別
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   MultiLangIdType
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public static MultiLangIdType parsingId(String id_s) {
        //"msg_01_validation_001_mapping_001";
        MultiLangIdType multiLangIdType = new MultiLangIdType();
        String[] ids = id_s.split("_");
        if (ids.length >= 1) {
            multiLangIdType.setType_s(ids[0]);
        } else {
            multiLangIdType.setType_s("");
        }
        if (ids.length >= 2) {
            multiLangIdType.setTypeSeq_s(ids[1]);
        } else {
            multiLangIdType.setTypeSeq_s("");
        }
        if (ids.length >= 3) {
            multiLangIdType.setGroupname_s(ids[2]);
        } else {
            multiLangIdType.setGroupname_s("");
        }
        if (ids.length >= 4) {
            multiLangIdType.setGroupSeq_s(ids[3]);
        } else {
            multiLangIdType.setGroupSeq_s("");
        }
        if (ids.length >= 5) {
            multiLangIdType.setAttribute_s(ids[4]);
        } else {
            multiLangIdType.setAttribute_s("");
        }
        if (ids.length >= 6) {
            multiLangIdType.setSeq_s(ids[5]);
        } else {
            multiLangIdType.setSeq_s("");
        }

        return multiLangIdType;
    }

    /**
     *此Method的功能描述：將MultiLangIdType轉換成多國語系編號
     * 
     *@param  傳入MultiLangIdType類別
     *@return  多國語系編號
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   MultiLangIdType
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public static String combineId(MultiLangIdType multiLangIdType) {
        StringBuffer sb = new StringBuffer();
        sb.append(multiLangIdType.getType_s()).append("_").append(multiLangIdType.getTypeSeq_s()).append("_");
        sb.append(multiLangIdType.getGroupname_s()).append("_").append(multiLangIdType.getGroupSeq_s()).append("_");
        sb.append(multiLangIdType.getAttribute_s()).append("_").append(multiLangIdType.getSeq_s());
        return sb.toString();
    }

    /**
     *此Method的功能描述：將MultiLangIdType轉換成多國語系編號
     * 
     *@param  傳入MultiLangIdType類別
     *@return  多國語系編號
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   MultiLangIdType
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public static String combineId(TbsysmultilangVO Tbsysmultilang) {
        MultiLangIdType multiLangIdType = new MultiLangIdType();
        if (Tbsysmultilang.getComp_id().getType() != null) {
            multiLangIdType.setType_s(Tbsysmultilang.getComp_id().getType());
        }
        if (Tbsysmultilang.getComp_id().getTypeSeq() != null) {
            multiLangIdType.setTypeSeq_s(Tbsysmultilang.getComp_id().getTypeSeq());
        }
        if (Tbsysmultilang.getComp_id().getGroupname() != null) {
            multiLangIdType.setGroupname_s(Tbsysmultilang.getComp_id().getGroupname());
        }
        if (Tbsysmultilang.getComp_id().getGroupSeq() != null) {
            multiLangIdType.setGroupSeq_s(Tbsysmultilang.getComp_id().getGroupSeq());
        }
        if (Tbsysmultilang.getComp_id().getAttribute() != null) {
            multiLangIdType.setAttribute_s(Tbsysmultilang.getComp_id().getAttribute());
        }
        if (Tbsysmultilang.getComp_id().getSeq() != null) {
            multiLangIdType.setSeq_s(Tbsysmultilang.getComp_id().getSeq());
        }

        return MultiLangUtil.combineId(multiLangIdType);
    }

    public static TbsysmultilangVO parsingMultiLangIdType(MultiLangIdType multiLangIdType) {
        TbsysmultilangVO Tbsysmultilang = new TbsysmultilangVO();
        TbsysmultilangPK TbsysmultilangPK = new TbsysmultilangPK();

        if (multiLangIdType.getType_s() != null) {
            TbsysmultilangPK.setType(multiLangIdType.getType_s());
        }
        if (multiLangIdType.getTypeSeq_s() != null) {
            TbsysmultilangPK.setTypeSeq(multiLangIdType.getTypeSeq_s());
        }
        if (multiLangIdType.getGroupname_s() != null) {
            TbsysmultilangPK.setGroupname(multiLangIdType.getGroupname_s());
        }
        if (multiLangIdType.getGroupSeq_s() != null) {
            TbsysmultilangPK.setGroupSeq(multiLangIdType.getGroupSeq_s());
        }
        if (multiLangIdType.getAttribute_s() != null) {
            TbsysmultilangPK.setAttribute(multiLangIdType.getAttribute_s());
        }
        if (multiLangIdType.getSeq_s() != null) {
            TbsysmultilangPK.setSeq(multiLangIdType.getSeq_s());
        }

        if (multiLangIdType != null) {
            Tbsysmultilang.setComp_id(TbsysmultilangPK);
        }

        return Tbsysmultilang;
    }

    public static String getMultiLangConfigFilePath() throws IOException {
        //get MULTILANG_FILE_PATH
        Properties p = new Properties();

        String systemRoot = DataManager.getSystem().getRoot();
        
        String fileName = (String)DataManager.getSystem().getConfigFileName().get("multiLang");            
        InputStream ins = new FileInputStream( systemRoot + "Config" + System.getProperty("file.separator")  + fileName);
        p.load(ins);
        ins.close();
        return systemRoot + System.getProperty("file.separator") + p.getProperty("MULTILANG_FILE_PATH")  ;
    }

    public static String getMultiLangConfigPropertiesClassPath() throws IOException {
    	 String langPath = (String)DataManager.getSystem().getPath().get("language");
         return langPath  ;
    }

    public static boolean checkPropertiesFileIsExist(String id_s, String locale_s) {
        try {
            //conbine file name, e.g.,ml_table_01_zh_TW.properties
            MultiLangIdType multiLangIdType = MultiLangUtil.parsingId(id_s);
            String pathName = MultiLangUtil.getMultiLangConfigFilePath() + System.getProperty("file.separator") + locale_s + System.getProperty("file.separator") + "ml_" + multiLangIdType.getType_s() + "_" + multiLangIdType.getTypeSeq_s() + "_" + locale_s + ".txt";
            File file = new File(pathName);
            return file.exists();
        } catch (IOException ex) {
            return false;
        }
    }

    public static boolean createPropertiesFile(String id_s, String locale_s) {
        //conbine file name, e.g.,ml_table_01_zh_TW.properties
        MultiLangIdType multiLangIdType = MultiLangUtil.parsingId(id_s);
        String pathName = "ml_" + multiLangIdType.getType_s() + "_" + multiLangIdType.getTypeSeq_s() + "_" + locale_s + ".txt";
        File file = new File(pathName);
        boolean is_b = false;
        if (!file.exists()) {
            try {
                file = new File(MultiLangUtil.getMultiLangConfigFilePath() + System.getProperty("file.separator") + locale_s);
                file.mkdirs();
                file = new File(MultiLangUtil.getMultiLangConfigFilePath() + System.getProperty("file.separator") + locale_s + System.getProperty("file.separator") + pathName);
                file.createNewFile();
                is_b = true;
            } catch (IOException ex) {
                is_b = false;
            }
        }

        return is_b;

    }

    public static boolean checkPropertiesFileIsExist(String pathName) {
        File file = new File(pathName);    
        return file.exists();
    }

    public static boolean createPropertiesFile(String pathName) {
        int endPath = pathName.lastIndexOf(System.getProperty("file.separator"), pathName.length());
        File file = new File(pathName);
        boolean is_b = false;
        if (!file.exists()) {
            try {
                file = new File(pathName.substring(0, endPath));
                file.mkdirs();
                file = new File(pathName);
                file.createNewFile();
                is_b = true;
            } catch (IOException ex) {
                is_b = false;
            }
        }

        return is_b;
    }

    public static String passMultiLangContent(List<String> list, String content) {
        if (list != null) {
            String field = "#{field1}#";            
            for (int i = 0; i < list.size(); i++) {
                field = "#{field" + (i + 1) + "}#";
                content = content.replace(field, list.get(i));
            }
        }
        return content;
    }
    
    public static String getMessage(UUID uuid, String msg){
    	JBranchException  jb=new JBranchException(msg); 
    	return jb.getMessage(uuid);
    }
    
    public static String getMessage(UUID uuid, String msg, List args){
    	JBranchException  jb=new JBranchException(msg, args); 
    	return jb.getMessage(uuid);
    }
    
    public static String getMessageContext(UUID uuid, String msg){
    	JBranchException  jb=new JBranchException(msg); 
    	return jb.getMessageContext(uuid);
    }
    
    public static String getMessageContext(UUID uuid, String msg, List args){
    	JBranchException  jb=new JBranchException(msg, args); 
    	return jb.getMessageContext(uuid);
    }
}
