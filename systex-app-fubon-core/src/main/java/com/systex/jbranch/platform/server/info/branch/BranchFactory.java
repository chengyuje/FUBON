package com.systex.jbranch.platform.server.info.branch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataManager.Branch;
import com.systex.jbranch.platform.common.dataManager.BranchFactoryIF;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public class BranchFactory implements BranchFactoryIF {

	@Override
	public List<Branch> getBranchList() throws JBranchException {
		  DataAccessManager dam = new DataAccessManager();
        //  若用cirtia,需自行new transaction
 		List<Branch> branhList = new ArrayList<Branch>();
		QueryConditionIF qc = dam.getQueryCondition(dam.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setQueryString("SELECT BRCHID, NAME FROM TBSYSBRANCH order by BRCHID");
		List result = dam.exeQuery(qc);
		return branhList;
	}

	@Override
	public Branch getBranch(String branchId) throws JBranchException {
      Branch brch = new Branch();

	  DataAccessManager dam = new DataAccessManager();
      //  若用cirtia,需自行new transaction
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setQueryString("SELECT BRCHID, NAME FROM TBSYSBRANCH where BRCHID=:branchId");
		qc.setObject("branchId", branchId);
		List result = dam.exeQuery(qc);
		for(int i=0;i<result.size();i++){
			
			Map recordMap = (Map) result.get(i);

			brch.setBrchID((String)recordMap.get("BRCHID"));
			brch.setName((String)recordMap.get("NAME"));
			brch.setOpDate("00000000");
			brch.setTxnFlag("0");
		}	
		return brch;
	}

	@Override
	public List<Map<String, String>> getBranchLabelList()
			throws JBranchException {
 		DataAccessManager dam= new DataAccessManager();    		
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		qc.setQueryString("select distinct BRCHID as DATA, NAME as LABEL from TBSYSBRANCH order by BRCHID");
		
		return dam.exeQuery(qc);
	}

}
