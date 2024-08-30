package com.systex.jbranch.app.server.fps.kyc620;

import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.LEGAL_PERSON_FINANCE_BOSS;
import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.LEGAL_PERSON_FINANCE_OP;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.util.Calendar;
import com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_MVO;
import com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_M_HISVO;
import com.systex.jbranch.app.common.fps.table.TBKYC_PROEXAM_M_HISTVO;
import com.systex.jbranch.app.server.fps.kyc.chk.KYCCheckIdentityWeights;
import com.systex.jbranch.app.server.fps.kyccons.KYCCons;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.conversation.message.EnumShowType;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author Sam
 * @date 2018/05/24
 * @spec null
 */
@Component("kyc620")
@Scope("request")
public class KYC620 extends FubonWmsBizLogic {

	public DataAccessManager dam = null;

	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {

		KYC620InputVO inputVO = (KYC620InputVO) body;
		KYC620OutputVO outputVO = new KYC620OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
//		List<Criterion> criList = new ArrayList<Criterion>();
//		if (StringUtils.isNotBlank(inputVO.getCUST_ID()))
//			criList.add(Restrictions.eq("CUST_ID", inputVO.getCUST_ID()));
//		if (StringUtils.isNotBlank(inputVO.getCUST_NAME()))
//			criList.add(Restrictions.eq("CUST_NAME", inputVO.getCUST_NAME()));
//		if (inputVO.getCREATE_DATE()!=null) {
//			criList.add(Restrictions.ge("CREATE_DATE", this.getFormattedFromDateTime(inputVO.getCREATE_DATE())));
//			criList.add(Restrictions.le("CREATE_DATE", this.getFormattedToDateTime(inputVO.getCREATE_DATE())));
//		}
//		if (StringUtils.isNotBlank(inputVO.getBranchNbr())) {
//			criList.add(Restrictions.eq("INVEST_BRANCH_NBR", inputVO.getBranchNbr()));
//		}
//		Criteria cri = dam.getHibernateCriteria(TBKYC_PROEXAM_M_HISTVO.TABLE_UID);
//		
//		List<TBKYC_PROEXAM_M_HISTVO> histVoList = (List<TBKYC_PROEXAM_M_HISTVO>)dam.findByCriteria(TBKYC_PROEXAM_M_HISTVO.TABLE_UID, criList); 
//		Collections.sort(histVoList , new Comparator<TBKYC_PROEXAM_M_HISTVO>() {
//			@Override
//			public int compare(TBKYC_PROEXAM_M_HISTVO o1, TBKYC_PROEXAM_M_HISTVO o2) {
//				if (o1.getCREATE_DATE().before(o2.getCREATE_DATE()))
//					return -1;
//				if (o1.getCREATE_DATE().after(o2.getCREATE_DATE()))
//					return 1;
//				else 
//					return 0;
//			}
//			
//		});

		StringBuilder sql = new StringBuilder();
		sql.append("	SELECT VO.*	,	CON.CHIN_FL_NAME	,	EX.EXAM_NAME	");
		sql.append("	FROM TBKYC_PROEXAM_M_HIST	VO	");
		sql.append("	LEFT JOIN  TBORG_BRH_CONTACT	CON	ON	VO.INVEST_BRANCH_NBR	=	CON.BRH_COD	");
		sql.append("	LEFT	JOIN	(	");
		sql.append("		    SELECT	DISTINCT	EXAM_NAME,EXAM_VERSION	FROM	TBSYS_QUESTIONNAIRE	)	EX	");
		sql.append("	ON	VO.EXAM_VERSION	=	EX.EXAM_VERSION	");
		sql.append("	WHERE 1=1	");
		if (StringUtils.isNotBlank(inputVO.getCUST_ID()))	{
			sql.append("	AND	VO.CUST_ID	=	:custId");
			queryCondition.setObject("custId", inputVO.getCUST_ID());
		}
//			criList.add(Restrictions.eq("CUST_ID", inputVO.getCUST_ID()));
		if (StringUtils.isNotBlank(inputVO.getCUST_NAME()))	{
			sql.append("	AND	VO.CUST_NAME	=	:custName");
			queryCondition.setObject("custName", inputVO.getCUST_NAME());
		}
//			criList.add(Restrictions.eq("CUST_NAME", inputVO.getCUST_NAME()));
		if (inputVO.getCREATE_DATE()!=null) {
			sql.append("	AND	VO.CREATE_DATE	BETWEEN	:createDate1 AND :createDate2	");
			queryCondition.setObject("createDate1", this.getFormattedFromDateTime(inputVO.getCREATE_DATE()));
			queryCondition.setObject("createDate2", this.getFormattedToDateTime(inputVO.getCREATE_DATE()));
//			criList.add(Restrictions.ge("CREATE_DATE", this.getFormattedFromDateTime(inputVO.getCREATE_DATE())));
//			criList.add(Restrictions.le("CREATE_DATE", this.getFormattedToDateTime(inputVO.getCREATE_DATE())));
		}
		if (StringUtils.isNotBlank(inputVO.getBranchNbr())) {
			sql.append("	AND	VO.INVEST_BRANCH_NBR = :braNbr	");
			queryCondition.setObject("braNbr", inputVO.getBranchNbr());
//			criList.add(Restrictions.eq("INVEST_BRANCH_NBR", inputVO.getBranchNbr()));
		}
		queryCondition.setQueryString(sql.toString());
		List histVoList = dam.exeQuery(queryCondition);
		outputVO.setResultList(histVoList);
		this.sendRtnObject(outputVO);

	}

	private Date getFormattedFromDateTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
		 
	private Date getFormattedToDateTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}
	/**
	 * 檢查Map取出欄位是否為Null
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	private String checkIsNu(Map map, String key) {

		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "0";
		}
	}
}
