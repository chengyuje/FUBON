package com.systex.jbranch.platform.common.dataexchange;

import com.systex.jbranch.platform.common.net.FTPJobUtil;
import com.systex.jbranch.platform.common.platformdao.table.TbsysdximpmasterVO;
import com.systex.jbranch.platform.common.scheduler.JobBase;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;


public class DB2Import extends JobBase {
// ------------------------------ FIELDS ------------------------------

    static private String user;
    PreparedStatement pstmtDetil = null;
    PreparedStatement pstmtStmt2 = null;
    private String importid;
    private String filePath;
    private String tableName;
    private String action;
    private String methodtype;
    private String columnmap;
    private String fileType;
    private String commitcount;
    private String timeformat;
    private String datefromat;
    private String timestampformat;
    private String encoding;


    private String SRC_SEQ;
    private String DES_SEQ;

    private String ftpsettingid = null;

    private String processfile = null;

    private FTPJobUtil ftp = null;
    private FileUtil fileReSpace = null;
    private String ehl_01_009 = "ehl_01_common_009";//FTP存取錯誤
    private String ehl_01_010 = "ehl_01_common_010";//資料庫存取錯誤　
    private String ehl_01_011 = "ehl_01_common_011";//檔案存取錯誤

    private PreparedStatement pstmtMaster = null;
	private Logger logger = LoggerFactory.getLogger(DB2Import.class);

// -------------------------- OTHER METHODS --------------------------

    public void execute(Connection con, Map jobParaMap, Map scheduleParaMap) throws Exception {
        CallableStatement callStmt1 = null;
        CallableStatement callStmt2 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        int rows_read;
        int rows_skipped;
        int rows_rejected;
        int rows_committed;
        int rows_inserted;
        int rows_updated;
        String input_file = null;
        try {
            //取得匯入設定檔ID
            importid = (String) jobParaMap.get("importid");
            try {
                input_file = (String) jobParaMap.get("file");
            }
            catch (Exception e) {
            }

            //取得匯入設定檔資料
            pstmtMaster = con.prepareStatement("select * from Tbsysdximpmaster where importmasterid = ?");
            pstmtMaster.setString(1, importid);
            ResultSet rsMaster = pstmtMaster.executeQuery();
            TbsysdximpmasterVO masterVo = new TbsysdximpmasterVO();
            if (rsMaster.next()) {
                filePath = rsMaster.getString("filepath");
                tableName = rsMaster.getString("tablename");
                action = rsMaster.getString("action");
                methodtype = rsMaster.getString("methodtype");
                columnmap = rsMaster.getString("columnmap");
                fileType = rsMaster.getString("FILETYPE");
                commitcount = rsMaster.getString("COMMITCOUNT");
                timeformat = rsMaster.getString("TIMEFORMAT");
                datefromat = rsMaster.getString("DATEFORMAT");
                timestampformat = rsMaster.getString("TIMESTAMPFORMAT");
                ftpsettingid = rsMaster.getString("ftpsettingid");
                encoding = rsMaster.getString("ENCODING");
                if (encoding == null || "".equals(encoding)) {
                    encoding = "950";
                }

                masterVo.setfilepath(filePath);
                masterVo.settablename(tableName);
                masterVo.setaction(action);
                masterVo.setmethodtype(methodtype);
                masterVo.setcolumnmap(columnmap);
                masterVo.setfiletype(new BigDecimal(fileType));
                masterVo.setcommitcount(new BigDecimal(commitcount));
                masterVo.settimeformat(timeformat);
                masterVo.setdatefromat(datefromat);
                masterVo.settimestampformat(timestampformat);
                masterVo.setftpsettingid(ftpsettingid);
                masterVo.setencoding(encoding);
            }

            //若有傳入file,使用之
            if (input_file != null) {
                processfile = input_file;
            }
            else {
                if (ftpsettingid != null && ftpsettingid.trim().length() != 0) {//若有設定ftp來源檔案
                    ftp = new FTPJobUtil();
                    processfile = ftp.ftpGetFile(con, ftpsettingid);
                }
                else {//本機端來源檔案
                    processfile = filePath;
                }
            }

            //檔案內容去右邊全型空白，只有P類型才需要
            //charlotte mark


            if (methodtype.equals("P")) {    //小寫p不執行formatter
                fileReSpace = new FileUtil();

                //fileReSpace.removeWideSpace已由fileReSpace.formatter取代
                //控制方式請至TBSYSDXIMPDETAIL資料表
                processfile = fileReSpace.formatter(con, processfile, importid, masterVo);
            }

            long rowCount = 0;
            String rowCountStr = "";
            String timeFormat = " modified by  ";
            if (timeformat != null && !timeformat.trim().equals("")) {
                timeFormat = timeFormat + " TIMEFORMAT = \"" + timeformat + "\" ";
            }
            if (datefromat != null && !datefromat.trim().equals("")) {
                timeFormat = timeFormat + " DATEFORMAT = \"" + datefromat + "\" ";
            }
            if (timestampformat != null && !timestampformat.trim().equals("")) {
                timeFormat = timeFormat + " TIMESTAMPFORMAT = \"" + timestampformat + "\" ";
            }
            timeFormat = timeFormat + " IMPLIEDDECIMAL ";
            if (fileType.equals("3")) {
                rowCount = getLineNumber(processfile);
                rowCountStr = " ROWCOUNT " + rowCount + " ";
            }
            String skipCount = "";
            if (!fileType.equals("1")) {
                skipCount = " SKIPCOUNT 1 ";
            }
            String columnSeq = getColumnSeq(columnmap);
            String[] columnSeqArray = columnSeq.split(";");
            SRC_SEQ = columnSeqArray[0];
            DES_SEQ = columnSeqArray[1];


            String sql = "CALL SYSPROC.ADMIN_CMD(?)";
            callStmt1 = con.prepareCall(sql);
            String param = null;
            // argv[0] is the path for the file to be imported
            if (methodtype.equalsIgnoreCase("P")) {    //大小寫P皆執行同樣語法
                param = "import from " + processfile + " of del " + timeFormat + " codepage=" + encoding + " method " + methodtype + SRC_SEQ + " COMMITCOUNT " + commitcount + " " + skipCount + rowCountStr + " MESSAGES ON SERVER " + action + " into " + tableName + DES_SEQ;
            }
            else if (methodtype.equals("L")) {
                param = "import from " + processfile + " of ASC " + timeFormat + " codepage=" + encoding + " method " + methodtype + SRC_SEQ + " COMMITCOUNT " + commitcount + " " + skipCount + rowCountStr + " MESSAGES ON SERVER " + action + " into " + tableName + DES_SEQ;
            }
            // setting the imput parameter
            callStmt1.setString(1, param);
            if (logger.isInfoEnabled()) {
                logger.info("CALL ADMIN_CMD('" + param + "')");
            }

            // executing import by calling ADMIN_CMD
            callStmt1.execute();
            con.commit();
            try {
                rs1 = callStmt1.getResultSet();

                // retrieving the resultset
                if (rs1.next()) {
                    // retrieve the no of rows read
                    rows_read = rs1.getInt(1);
                    // retrieve the no of rows skipped
                    rows_skipped = rs1.getInt(2);
                    // retrieve the no of rows loaded
                    rows_inserted = rs1.getInt(3);
                    // retrieve the no of rows rejected
                    rows_updated = rs1.getInt(4);
                    // retrieve the no of rows deleted
                    rows_rejected = rs1.getInt(5);
                    // retrieve the no of rows committed
                    rows_committed = rs1.getInt(6);

                    String msg_retrieval = rs1.getString(7);

                    String msg_removal = rs1.getString(8);

                    //String msg_removal2 = rs1.getString(9);

                    this.setFailRecord(rows_rejected);
                    this.setInsertRecord(rows_inserted);
                    this.setUdpateRecord(rows_updated);
                    this.setTotalRecord(rows_committed - rows_skipped);

                    // Displaying the resultset
                    if (logger.isInfoEnabled()) {
                        logger.info("\nTotal number of rows read      : ");
                        logger.info(String.valueOf(rows_read));
                        logger.info("Total number of rows skipped   : ");
                        logger.info(String.valueOf(rows_skipped));
                        logger.info("Total number of rows insert    : ");
                        logger.info(String.valueOf(rows_inserted));
                        logger.info("Total number of rows update  : ");
                        logger.info(String.valueOf(rows_updated));
                        logger.info("Total number of rows reject   : ");
                        logger.info(String.valueOf(rows_rejected));
                        logger.info("Total number of rows committed : ");
                        logger.info(String.valueOf(rows_committed));
                        logger.info("=======================");
                        logger.info(msg_retrieval);
                        logger.info(msg_removal);
                    }
//					20160505 郵局弱掃疑慮，暫停寫audit log (modified by Angus)
//                    PreparedStatement stmt1 = con.prepareStatement(msg_retrieval);
//                    // message retrivel
//                    rs2 = stmt1.executeQuery();
//
//                    // retrieving the resultset
//                    while (rs2.next()) {
//                        try {
//                            // retrieving the sqlcode
//                            String sqlcode = rs2.getString(1);
//                            String msg = rs2.getString(2);
//                            //charlotte test
//                            this.audit(sqlcode + " " + msg);
//                        }
//                        catch (Exception e) {
//                            logger.warn(e.getMessage(), e);
//                        }
//                    }

                    callStmt2 = con.prepareCall(msg_removal);
                    callStmt2.execute();
                }
                con.commit();
            }
            catch (Exception e) {
                this.setResult(SchedulerHelper.RESULT_WARN);
                this.audit(e.getMessage());
                logger.warn(e.getMessage(), e);
                con.rollback();
            }
        }
        catch (Exception e) {
            con.rollback();
            throw e;
        }
        finally {
            try {
                ftp.distConnection();
            }
            catch (Exception x) {
            }
            //closing the statements and resultset
            try {
                callStmt1.close();
            }
            catch (Exception x) {
            }
            try {
                callStmt2.close();
            }
            catch (Exception x) {
            }
            try {
                pstmtMaster.close();
            }
            catch (Exception x) {
            }
            try {
                rs1.close();
            }
            catch (Exception x) {
            }
            try {
                rs2.close();
            }
            catch (Exception x) {
            }
        }
    }

    public long getLineNumber(String file) throws Exception {
        long count = 0;
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line = null;

        while ((line = reader.readLine()) != null) {
            count++;
        }

        if (count < 2) {
            count = 0;
        }
        else {
            //扣除type3的檔頭
            count = count - 1;
        }
        return count;
    }

    private String getColumnSeq(String column) {
        String desStr = null;
        String srcStr = null;

        String columnMap = column;
        StringBuffer desBuf = new StringBuffer();
        StringBuffer srcBuf = new StringBuffer();
        String[] mappingArray = columnMap.split(",");
        boolean firstLoop = true;
        for (int i = 0; i < mappingArray.length; i++) {
            String[] columnSrc = mappingArray[i].split("=");
            if (!firstLoop) {
                desBuf.append("," + columnSrc[0]);
                srcBuf.append("," + columnSrc[1]);
            }
            else {
                desBuf.append(columnSrc[0]);
                srcBuf.append(columnSrc[1]);
            }
            firstLoop = false;
        }
        desStr = desBuf.toString();
        srcStr = srcBuf.toString();

        return "(" + srcStr + ")" + ";" + "(" + desStr + ")";
    }

    public String getFilePath(String file) throws Exception {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            String lineSeparator = System.getProperty("line.separator");
            String outputFile = file + ".out";
            reader = new BufferedReader(new FileReader(file));
            writer = new BufferedWriter(new FileWriter(outputFile));

            String line = reader.readLine();

            if (!line.startsWith("01,")) {
                writer.write(line);
                writer.write(lineSeparator);
            }

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("99,")) {
                    break;
                }
                writer.write(line);
                writer.write(lineSeparator);
            }
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
        }
    }
}