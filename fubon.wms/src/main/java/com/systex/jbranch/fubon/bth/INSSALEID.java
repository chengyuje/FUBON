/**
 * 
 */
package com.systex.jbranch.fubon.bth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 1. 讀取DB產出 .yyyymmdd
 * 2. 將.yyyymmdd 上傳FTP
 * 
 * @author 1600216
 * @date 2016/11/16
 *
 */
@Repository("inssaleid")
@Scope("prototype")
public class INSSALEID extends BizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private BthFtpJobUtil ftpJobUtil = new BthFtpJobUtil();
	
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	
	public void createFileBth (Object body, IPrimitiveMap<?> header) throws Exception {
		
		GenFileTools gft=new GenFileTools();
		dam=getDataAccessManager();
		String  writeFileName="INS_SALE_ID";
		
		StringBuffer sb = new StringBuffer();
		//取得總筆數
		sb.append("SELECT COUNT(1) TOTAL_COUNT FROM ( ");
		sb.append(genSql().toString());
		sb.append(")");
		//System.out.println("SQL:"+sb.toString());
		QueryConditionIF queryCondition = dam.getQueryCondition();
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> listCount = dam.exeQuery(queryCondition);	
		int dataCount=Integer.parseInt(listCount.get(0).get("TOTAL_COUNT").toString());
		
        System.out.println("totalCount:"+dataCount);
	    String[] writeFilerOrder = {"INS_KEYNO","COL_2","APPL_ID","COL_4","COL_5","COL_6","COL_7"};	
	    ResultSet rs=null;
	    Connection con=gft.getConnection();
		Statement s = null;
		try {
			s = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
	                java.sql.ResultSet.CONCUR_READ_ONLY);
			s.setFetchSize(3000);

	    rs=gft.getRS(genSql(),s);
	    gft.writeFile(writeFileName,"", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), writeFilerOrder, rs, ", ", false, false);
		rs=gft.getRS(genSql(),s);
	    List args = new ArrayList();
	    args.add(rs); 
	    updateData(rs,con);	    
		//chk檔格式
		StringBuffer chkLayout=new StringBuffer();
		chkLayout.append(gft.addBlankForString("INS_SALE_ID", 30));
		chkLayout.append(gft.addZeroForNum(String.valueOf(dataCount),15));
		chkLayout.append(sdfYYYYMMDD.format(new Date()));
		gft.writeFileByText("Z"+writeFileName, "", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), chkLayout, false);	    
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception e) {}
			if (s != null) try { s.close(); } catch (Exception e) {}
			if (con != null) try { con.close(); } catch (Exception e) {}
		}
	   // createZFile(list);
		
	}

	
	private void updateData(ResultSet rs, Connection con) throws DAOException, JBranchException, SQLException 
	{
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();
		StringBuffer sb=new StringBuffer();
	    sb.append("UPDATE TBIOT_MAIN ");
	    sb.append("SET CUST_ID =?, ");
	    sb.append("LASTUPDATE =SYSDATE, ");
	    sb.append("MODIFIER='JOBINSSALE', ");
	    sb.append("PROPOSER_NAME = (SELECT CUST_NAME FROM TBCRM_CUST_MAST WHERE CUST_ID =?) ");
	    sb.append("WHERE INS_KEYNO =? ");
		PreparedStatement ps=con.prepareStatement(sb.toString());
		while(rs.next())
		{
			String custId=String.valueOf(rs.getObject("CUST_ID"));
			String applId=String.valueOf(rs.getObject("APPL_ID"));
			String insKeyNo=String.valueOf(rs.getObject("INS_KEYNO"));
			//若兩個不相同 需進行更新		       
			if(applId!=null&&!"".equals(applId.trim())&&!custId.equals(applId))
	        {
				ps.setString(1, applId);
				ps.setString(2, applId);
				ps.setString(3, insKeyNo);
	    	    ps.executeUpdate();
	        }
		}
		if(ps!=null){ try { ps.close(); } catch (Exception e) {} }		
	}
	
	public StringBuffer genSql()
	{
		StringBuffer sb=new StringBuffer();
		sb.append("SELECT to_char(B.INS_KEYNO) INS_KEYNO, ");
		sb.append("TO_CHAR(B.APPLY_DATE, 'YYYYMMDD') APPLY_DATE,  ");
		sb.append("A.APPL_ID, ");
		sb.append("B.CUST_ID, ");         
		sb.append("B.INS_ID, "); 
		sb.append("'        ' COL_2,'   ' COL_4,'    'COL_5,'   ' COL_6,'0' COL_7 ");
		sb.append("FROM TBPMS_FWLFN110 A, ");
		sb.append("TBIOT_MAIN B ");
		sb.append("WHERE A.INS_SALE_ID IS NOT NULL AND A.INS_SALE_ID = B.INS_ID ");
		sb.append("AND A.APPL_ID <> B.CUST_ID ");
		//sb.append("AND A.SNAP_DATE =TO_DATE('20170803','YYYYMMDD') ");
		sb.append("AND A.SNAP_DATE = TRUNC(SYSDATE) ");
		/*
		sb.append("UNION ");
		sb.append("SELECT to_char(INS_KEYNO) INS_KEYNO, "); 
		sb.append("TO_CHAR(B.APPLY_DATE, 'YYYYMMDD')APPLY_DATE, ");
		sb.append("'          ' APPL_ID, ");
		sb.append("CUST_ID, ");
		sb.append("INS_ID, ");
		sb.append("'        ' COL_2,'   ' COL_4,'    'COL_5,'   ' COL_6,'0' COL_7 ");
		sb.append("FROM TBIOT_MAIN B ");
		sb.append("WHERE STATUS IN ('70', '80') ");
		sb.append("AND KEYIN_DATE = TRUNC(SYSDATE) ");
		*/
		//Java中無法使用
		//sb.append("FOR UPDATE OF CUST_ID ");
		//sb.append("and trunc(AFT_SIGN_DATE) between trunc(sysdate)-6 and trunc(sysdate) ");
		

		return sb;
	}
	
}


