package com.systex.jbranch.common.cmrpt001;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTDETAILVO;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTMASTERVO;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Repository("cmrpt001")
@Scope("prototype")
public class CMRPT001 extends FubonWmsBizLogic {
	
	Logger logger = LoggerFactory.getLogger(CMRPT001.class);

	public void cmrpt001(Object body, IPrimitiveMap header) throws JBranchException {
		//取得畫面資料
		
		logger.debug("CMRPT001 --- " + body.toString());
		List<BigDecimal> condition = new ArrayList<BigDecimal>();
		CMRPT001InputVO vo = (CMRPT001InputVO)body;
		logger.debug("Process reportId = " + vo.getReportId());
		condition.add(new BigDecimal(vo.getReportId()));
		vo.setIsFinished(CompleteCheck(new BigDecimal(vo.getReportId())));
		vo.setReportData(query(condition,vo.getStartPage(),vo.getEndPage()));
		logger.debug("CMRPT001 --- " + vo.toString());
		sendRtnObject("displyReport", vo);
	}
	
	
	//檢查報表是否完成
	private String CompleteCheck(BigDecimal gen_id) throws JBranchException{
		String rtnFinished = "";
		String brno = DataManager.getBranch(uuid).getBrchID();
		
		Criteria criteria = this.getDataAccessManager().getHibernateCriteria(TBSYSREPORTMASTERVO.TABLE_UID);
		criteria.add(Restrictions.eq("comp_id.GEN_ID",gen_id));
		criteria.add(Restrictions.eq("comp_id.BRNO",brno));
		List<TBSYSREPORTMASTERVO> list = (List<TBSYSREPORTMASTERVO>) criteria.list();
		if(list.size() > 0){
			for(TBSYSREPORTMASTERVO data :list){
				rtnFinished = data.getFINISHED();
			}
		}
		return rtnFinished;
	}
	
	private List<String> query(List<BigDecimal> genIds,int startPage,int endPage) throws JBranchException{
		List<String> rtnList = new ArrayList<String>();
		String brno = DataManager.getBranch(uuid).getBrchID();
		
		Criteria criteria = this.getDataAccessManager().getHibernateCriteria(TBSYSREPORTDETAILVO.TABLE_UID);
		//criteria.add(Restrictions.eq("comp_id.GEN_ID", new BigDecimal(reportId) ));
		if(startPage==0&&endPage==0){
			//表示為一般列印
		}else{
			//表示為重印選擇列印頁數範圍
			criteria.setFirstResult(startPage - 1);
			criteria.setMaxResults(endPage - startPage + 1);
		}
		criteria.add(Restrictions.in("comp_id.GEN_ID", genIds));
		criteria.add(Restrictions.eq("comp_id.BRNO", brno));
		criteria.addOrder(Order.asc("comp_id.BRNO")).addOrder(Order.asc("comp_id.GEN_ID")).addOrder(Order.asc("comp_id.PAGENO"));
		
		List<TBSYSREPORTDETAILVO> list = (List<TBSYSREPORTDETAILVO>) criteria.list();
			
		for(TBSYSREPORTDETAILVO data :list){
			String pageData = "";
			try{
				byte[] tmp = ObjectUtil.blobToByteArr((Blob) data.getREPORTDATA());
				pageData = new String(tmp,"utf-8");
			}catch(Exception ex){
				throw new JBranchException(ex.getMessage(),ex);
			}		
			logger.debug("pageData = " + pageData);
			rtnList.add(pageData);
		}		
		return rtnList;
	}
	
}
