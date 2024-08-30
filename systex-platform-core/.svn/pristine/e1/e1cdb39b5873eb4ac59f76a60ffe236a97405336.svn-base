package com.systex.jbranch.platform.common.dataexchange;

import com.systex.jbranch.platform.common.platformdao.table.TbsysdximpmasterVO;
import com.systex.jbranch.platform.common.util.StringUtil;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {
// ------------------------------ FIELDS ------------------------------

    final public static String[] DESTINATION_CODE_TYPE2 = {"a1af"};

    final public static String[] ORIGINAL_CODE_TYPE2 = {"9db2"};

    final public static int CHECK_DATE = 1;
    final public static int REPLACE_CODE = 2;
    final public static int REPLACE_CODE_ENCODING = 3;    //big5 to utf8

    public static Map<String, String> DB2_TO_JAVA_DATE_FORMAT = new HashMap<String, String>();
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    //private static String lineSeparator = "\r\n";
    private static String lineSeparator = System.getProperty("line.separator");
    private String datefromat = "";

// -------------------------- STATIC METHODS --------------------------

    static {
        DB2_TO_JAVA_DATE_FORMAT.put("DD/MM/YYYY", "dd/MM/yyyy");
        DB2_TO_JAVA_DATE_FORMAT.put("YYYY.MM.DD", "yyyy.MM.dd");
        DB2_TO_JAVA_DATE_FORMAT.put("YYYY/MM/DD", "yyyy/MM/dd");
        DB2_TO_JAVA_DATE_FORMAT.put("D-M-YYYY", "d-M-yyyy");
        DB2_TO_JAVA_DATE_FORMAT.put("MM.DD.YYYY", "MM.dd.yyyy");
        DB2_TO_JAVA_DATE_FORMAT.put("YYYYDDD", "yyyydd");
        DB2_TO_JAVA_DATE_FORMAT.put("MM-DD", "MM-dd");
    }

// -------------------------- OTHER METHODS --------------------------

    public String formatter(Connection conn, String file, String importmasterID, TbsysdximpmasterVO masterVo) throws Exception {
        this.datefromat = masterVo.getdatefromat();
        BufferedReader reader = null;
        BufferedWriter writer = null;
        List<Map<String, String>> ruleList = getFormatterRule(conn, importmasterID);
        String outputFile = file + ".out";
        try {
            //reader = new BufferedReader(new FileReader(file));
            //reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "ISO-8859-1"));
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            //writer = new BufferedWriter(new FileWriter(outputFile));
            //writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile),"ISO-8859-1"));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));

            //讀取第一行
            String line = reader.readLine();
            while (line != null) {
                String[] lineArray = line.split(",");
                String newline = "";
                for (int i = 0; i < lineArray.length; i++) {
                    String tmpData = "";//去全型空白暫存資料
                    String endData = "";//去半型空白暫存資料
                    tmpData = lineArray[i];

                    //去掉中文前面的1E跟最後面的1F
                    tmpData = trim1E1F_(tmpData);
                    //tmpData=trim1E1F(tmpData);

                    //去掉右邊ＨＥＸ值為00
                    tmpData = tirm00Code(tmpData);

                    //去右邊半型空白
                    endData = trimRightS(tmpData);

                    //去右邊全型空白
                    endData = trimRightWS(endData);

                    //因資料有的是中間有1E 1F的，所以再跑一次去1E 1F
                    //endData=trim1E1F(endData);

                    //去掉右邊ＨＥＸ值為00
                    endData = tirm00Code(endData);

                    //依TBSYSDXIMPDETAIL資料表規則執行
                    endData = columnRuleCheck(ruleList, i, endData);


                    if (isCHECK_DATE(ruleList, i) && lineArray[i].startsWith("\"")) {
                        if (endData.startsWith("\"")) {
                            endData = endData.substring(1);
                        }
                    }
                    else if (lineArray[i].startsWith("\"")) {//因為讀資料時，會把"去掉，所以組回去時，再加上去
                        if (endData.length() == 0) {
                            endData = endData + "\"\"";
                        }
                        else if (!endData.endsWith("\"")) {
                            endData = endData + "\"";
                        }
                        else if (endData.equals("\"")) {
                            endData = "\"\"";
                        }
                    }
                    if (newline.equals("")) {
                        newline = endData;
                    }
                    else {
                        newline = newline + "," + endData;
                    }
                }
                line = reader.readLine();

                //當有表尾時(type=3)，略過表尾
                if (line == null && "3".equals(String.valueOf(masterVo.getfiletype()))) {
                    continue;
                }
                writer.write(newline);
                writer.write(lineSeparator);
                writer.flush();
            }
            openReadCommand(outputFile);
            return outputFile;
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) {
            }
            try {
                writer.close();
            }
            catch (Exception e) {
            }
            try {
                checkReplaceCode(ruleList, outputFile);
            }
            catch (Exception e) {
                //ingore
            }
        }
    }

    private List<Map<String, String>> getFormatterRule(Connection conn,
                                                       String importmasterID) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            PreparedStatement pstat = conn
                    .prepareStatement("select FIELDNO, FUNCID from TBSYSDXIMPDETAIL where IMPORTMASTERID=? order by ORDER");
            pstat.setString(1, importmasterID);
            ResultSet rs = pstat.executeQuery();

            while (rs.next()) {
                String FIELDNO = rs.getString("FIELDNO");
                String FUNCID = rs.getString("FUNCID");
                Map map = new HashMap();
                map.put("FIELDNO", FIELDNO);
                map.put("FUNCID", FUNCID);
                list.add(map);
            }
        }
        catch (SQLException e) {
            logger.warn(e.getMessage(), e);
        }
        return list;
    }

    public String trim1E1F_(String data) throws Exception {
        byte[] dataArray = null;
        String tmpData = "";
        dataArray = data.getBytes();
        //去掉中文前面的1E跟後面的1F
        if (dataArray.length != 0) {
            List<Byte> arrayList = new ArrayList<Byte>();
            for (int i = 0; i < dataArray.length; i++) {
                if (dataArray[i] != 30 && dataArray[i] != 31) {
                    arrayList.add(dataArray[i]);
                }
            }
            Byte[] byteArray = arrayList.toArray(new Byte[0]);
            byte[] tmpArray = new byte[byteArray.length];
            for (int i = 0; i < byteArray.length; i++) {
                tmpArray[i] = byteArray[i].byteValue();
            }
            tmpData = new String(tmpArray);
        }
        return tmpData;
    }

    //去右邊半型空白
    public String trimRightS(String data) throws Exception {
        String endData = data;
        int endDataLen = 0;

        if (endData.endsWith(" ") || endData.endsWith(" \"")) {
            for (int j = 0; j < data.length(); j++) {
                endDataLen = endData.lastIndexOf(" ");
                if (endDataLen == -1) {
                    j = data.length();
                }
                else {
                    endData = endData.substring(0, endDataLen);
                }
                if (!endData.endsWith(" ")) {
                    j = data.length();
                }
            }
        }
        return endData;
    }

    //去右邊全型空白
    public String trimRightWS(String data) throws Exception {
        String hexStr = new String(Hex.encodeHex(data.getBytes()));

        if (!hexStr.endsWith("a140")) {
            return data;
        }

        while (hexStr.endsWith("a140")) {
            hexStr = hexStr.substring(0, hexStr.length() - 4);
        }
        return new String(Hex.decodeHex(hexStr.toCharArray()));
    }

    //去掉右邊ＨＥＸ值為00
    public String tirm00Code(String data) throws Exception {
        byte[] tmp00Code = data.getBytes();
        byte[] trim00Code = new byte[tmp00Code.length];
        int tempLen = 0;
        if (tmp00Code.length != 0) {
            for (int k = tmp00Code.length; k > 0; k--) {
                tempLen = k - 1;
                if (tmp00Code[k - 1] != 00) {
                    //ＨＥＸ值為22資料為"  要再讀前一筆資料，再確定是否跳出回圈
                    if (tmp00Code[tmp00Code.length - 1] != 34) {
                        k = -1;
                    }
                    else {
                        if (k != tmp00Code.length) {
                            k = -1;
                        }
                    }
                }
            }
            //若整筆資料的HEX值都為00，就傳空的回去
            //if (tempLen!=0){
            if (tempLen != 0 || tmp00Code.length == 1) {
                trim00Code = new byte[tempLen + 1];
                System.arraycopy(tmp00Code, 0, trim00Code, 0, tempLen + 1);
            }
            else {
                trim00Code = new byte[0];
            }
            data = new String(trim00Code);
        }
        return data;
    }

    private String columnRuleCheck(List<Map<String, String>> ruleList, int i,
                                   String tmpData) {
        for (int j = 0; j < ruleList.size(); j++) {
            Map<String, String> map = ruleList.get(j);
            String FIELDNO = map.get("FIELDNO");
            if (String.valueOf(i).equals(FIELDNO)) {
                String FUNCID = map.get("FUNCID");
                int intFUNCID = -1;
                try {
                    intFUNCID = Integer.parseInt(FUNCID);
                }
                catch (Exception e) {
                    // ingore
                }
                tmpData = ruleDispatcher(tmpData, intFUNCID);
            }
        }
        return tmpData;
    }

    private String ruleDispatcher(String tmpData, int intFUNCID) {
        switch (intFUNCID) {
            case CHECK_DATE:
                tmpData = checkDate(tmpData);
                break;
            case REPLACE_CODE:
                tmpData = StringUtil.replaceCode(ORIGINAL_CODE_TYPE2, "MS950", DESTINATION_CODE_TYPE2, "UTF-8", tmpData);
                break;
        }
        return tmpData;
    }

    private String checkDate(String tmpData) {
        String format = null;
        if (datefromat != null) {
            format = DB2_TO_JAVA_DATE_FORMAT.get(datefromat);
        }

        if (format == null) {
            format = "yyyyMMdd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try {
            sdf.parse(tmpData);
        }
        catch (ParseException e) {
            boolean invalid = true;

            sdf = new SimpleDateFormat("\"" + format);
            sdf.setLenient(false);
            try {
                sdf.parse(tmpData);
                invalid = false;
            }
            catch (ParseException e2) {
                // ignore
            }
            if (invalid) {
                tmpData = "";
                // LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "日期格式不正確[" + tmpData + "]");
            }
        }
        return tmpData;
    }

    private boolean isCHECK_DATE(List<Map<String, String>> ruleList, int i) {
        for (int j = 0; j < ruleList.size(); j++) {
            Map<String, String> map = ruleList.get(j);
            String FIELDNO = map.get("FIELDNO");
            if (String.valueOf(i).equals(FIELDNO)) {
                String FUNCID = map.get("FUNCID");
                int intFUNCID = -1;
                try {
                    intFUNCID = Integer.parseInt(FUNCID);
                }
                catch (Exception e) {
                    // ingore
                }
                if (intFUNCID == CHECK_DATE) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void openReadCommand(String file) throws Exception {
        String os = System.getProperty("os.name");
        if ("AIX".equalsIgnoreCase(os) || "Linux".equalsIgnoreCase(os)) {
            String command = "chmod 644 " + file;
            if (logger.isDebugEnabled()) {
                logger.debug("command = " + command);
            }
            Process process = Runtime.getRuntime().exec(command);
            InputStream in = process.getInputStream();
            OutputStream out = process.getOutputStream();

            out.flush();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new Exception("chmod return " + exitCode);
            }
        }
    }

    private void checkReplaceCode(List<Map<String, String>> ruleList, String file) {
        try {
            for (int j = 0; j < ruleList.size(); j++) {
                Map<String, String> map = ruleList.get(j);

                String FUNCID = map.get("FUNCID");
                int intFUNCID = -1;
                try {
                    intFUNCID = Integer.parseInt(FUNCID);
                }
                catch (Exception e) {
                    // ingore
                }
                if (intFUNCID == REPLACE_CODE_ENCODING) {
                    String srcEncoding = "MS950";
                    String desEncoding = "UTF-8";
                    StringUtil.replaceCodeAndEncoding(file,
                            srcEncoding,
                            file,
                            desEncoding);
                    break;
                }
            }
        }
        catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public String trim1E1F(String data) throws Exception {
        byte[] dataArray = null;
        String tmpData = "";
        dataArray = data.getBytes();
        //去掉中文前面的1E跟最後面的1F
        if (dataArray.length != 0) {
            if (dataArray[0] == 30 && dataArray[dataArray.length - 1] == 31) {
                byte[] tmpdataArray = new byte[dataArray.length - 2];
                System.arraycopy(dataArray, 1, tmpdataArray, 0, dataArray.length - 2);
                tmpData = new String(tmpdataArray);
            }
            else {
                tmpData = new String(dataArray);
            }
        }
        return tmpData;
    }
}
