package com.systex.jbranch.fubon.bth;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Java產檔範例
 * 
 * @author Rick
 * @date 2017/11/28
 *
 */
@Repository("genfilesample")
@Scope("prototype")
public class GenFileSample extends BizLogic {

	// 產檔 基本範例
	public void createFileBth(Object body, IPrimitiveMap<?> header)
			throws Exception {
		// 定義檔案名稱 副檔名 路徑,資料分隔格式 編碼
		String writeFileName = "fileName";
		String attached_name = "txt";
		String path = (String) SysInfo
				.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String separated = ",";
		String format = "MS950";
		GenFileTools gft = new GenFileTools();
		//JDBC連線
		ResultSet rs=null;
		Connection con=gft.getConnection();
		Statement s = null;
		try {
			s = con.createStatement(java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE,
	                java.sql.ResultSet.CONCUR_READ_ONLY);
			s.setFetchSize(3000);
			
			//取得SQL內資料並放入rs
			rs=gft.getRS(genSql(), s);
			
			//產檔
			gft.writeFile(writeFileName, attached_name, path, getOrder(), rs, separated, false, false);
			
			
			
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception e) {}
			if (s != null) try { s.close(); } catch (Exception e) {}
			if (con != null) try { con.close(); } catch (Exception e) {}
		} 
	}

	// 產出的SQL
	private StringBuffer genSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT 1 AS COL1,'A' AS COL2 FROM DUAL ");
		return sb;

	}

	// 需要獲取的欄位 欄位顯示與否及先後順序在這邊決定
	private String[] getOrder() {
		String[] order = { "COL1", "COL2" };
		return order;
	}
	
	
}
