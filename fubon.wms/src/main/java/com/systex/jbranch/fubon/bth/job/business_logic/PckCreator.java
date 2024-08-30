package com.systex.jbranch.fubon.bth.job.business_logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**
 * 排程手動匯入package
 * 
 * @author sam
 * @date 2018/01/10
 * @spec
 * 
 * 
 */
@Component("pckCreator")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PckCreator extends FubonWmsBizLogic {

	/**
	 * 撈指定路徑檔案,並執行該檔案內容的oracle語法,新增一個package.
	 * 
	 * @throws IOException
	 * @throws DAOException
	 * @throws JBranchException
	 * @see {@link PckCreator#addEscape(BufferedReader)}
	 */
	public void createPackage() throws IOException, DAOException, JBranchException{
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		//撈出檔案路徑
		condition.setQueryString("select DESDIRECTORY, DESFILENAME from TBSYSFTP where FTPSETTINGID = 'PCK_MANUAL_CREATE'");
		List<Map> list = dam.exeQuery(condition);
		Path path = Paths.get(list.get(0).get("DESDIRECTORY").toString())
				.resolve(Paths.get(list.get(0).get("DESFILENAME").toString()));
		
		if (Files.exists(path)) {
			try (
				BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8);
			){
				//讀入全部內容
				for(Object each: addEscape(br)) {
					condition.setQueryString(each.toString());
					dam.exeUpdate(condition);   
				}
			} catch(Exception e) {
				 throw new APException("請確認SQL語法是否正確");
			}
		}
		else{
			throw new APException("無檔案可執行，請注意檔案是否存在");
		}
	}

	/**
	 * 協助確保sql語句不出錯
	 * 
	 * @param br:  撈到的指定路徑檔案
	 * @return list 回傳正確的sql語句
	 * @throws IOException
	 */
	public static List addEscape(BufferedReader br) throws IOException {
		List searchsql = new ArrayList();
		StringBuffer sql = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) {
			if (line.trim().equals("/")) {
				searchsql.add(sql.toString());
				sql.setLength(0);
			} else {
				sql.append(line.replace(":", "\\:") + "\r\n");
			}
		}
		if(sql.length()>0)searchsql.add(sql.toString());
		return searchsql;
	}
}
