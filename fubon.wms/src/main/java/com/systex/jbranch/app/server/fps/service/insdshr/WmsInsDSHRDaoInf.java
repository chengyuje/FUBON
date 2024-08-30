package com.systex.jbranch.app.server.fps.service.insdshr;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;

public interface WmsInsDSHRDaoInf {
	public void setCaseSaveData(String caseId, String uploadDatetime, String string, String string2) throws Exception;
	public Map<String , Object> validateInsData(GenericMap paramGmap) throws Exception;
	public List<Map<String, Object>> getEmpInfo(String string) throws DAOException, JBranchException;
	public boolean isCaseIdExisted(String caseId) throws NotFoundException, DAOException;
}
