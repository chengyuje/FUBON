package com.systex.jbranch.platform.common.multiLang;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.helper.DataAccessHelper;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TbsysmultilangDaoIF;
import com.systex.jbranch.platform.common.platformdao.table.TbsysmultilangPK;
import com.systex.jbranch.platform.common.platformdao.table.TbsysmultilangVO;

public class TbsysmultilangUtil {

	    DataAccessManager dam;
	    public TbsysmultilangUtil() throws DAOException, JBranchException{
	    	dam = new DataAccessManager();
	    }
	
		public boolean deleteMultiFields(TbsysmultilangVO tbsysmultilangVO) throws DAOException, JBranchException {
			boolean flag = true;
			
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessHelper.QUERY_LANGUAGE_TYPE_SQL);
			queryCondition.setQueryString("DELETE * FROM TBSYSMULTILANG WHERE TYPE=? AND TYPE_SEQ=? AND GROUPNAME=? AND GROUP_SEQ=? AND ATTRIBUTE=? AND SEQ=?");
			
			
			queryCondition.setString(1, tbsysmultilangVO.getComp_id().getType());
			queryCondition.setString(2, tbsysmultilangVO.getComp_id().getTypeSeq());
			queryCondition.setString(3, tbsysmultilangVO.getComp_id().getGroupname());
			queryCondition.setString(4, tbsysmultilangVO.getComp_id().getGroupSeq());
			queryCondition.setString(5, tbsysmultilangVO.getComp_id().getAttribute());
			queryCondition.setString(6, tbsysmultilangVO.getComp_id().getSeq());
	        if (dam.executeUpdate(queryCondition) != 1) {
	        	flag = false;            
	        }
			return flag;
		}

		public List<String> distinctFields(String field,String condition) throws DAOException, JBranchException {

			if (!condition.trim().equals("")){
				condition = " Where " + condition;
			}		
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessHelper.QUERY_LANGUAGE_TYPE_HQL);
			queryCondition.setQueryString("select distinct " + field + " from TbsysmultilangVO" + condition);
			List<String> list = dam.executeQuery(queryCondition);
			return list;
		}

		public List<TbsysmultilangVO> findByMultiFields(TbsysmultilangVO tbsysmultilangVO) throws DAOException, JBranchException {
	
			//finding exist object first
	        TbsysmultilangPK TbsysmultilangPK = tbsysmultilangVO.getComp_id();

	        Criteria criteria =  dam.getHibernateCriteria(TbsysmultilangVO.TABLE_UID);
	        criteria.add(Restrictions.eq("comp_id", TbsysmultilangPK));
	        List<TbsysmultilangVO> list = criteria.list();
	        		
			return list;
		}
		
		public List<TbsysmultilangVO> findByGroup(TbsysmultilangVO tbsysmultilangVO) throws DAOException, JBranchException {

			Criteria criteria =  dam.getHibernateCriteria(TbsysmultilangVO.TABLE_UID);
			criteria.add(Restrictions.eq("comp_id.type",
					tbsysmultilangVO.getComp_id().getType()));
			criteria.add(Restrictions.eq("comp_id.typeSeq",
					tbsysmultilangVO.getComp_id().getTypeSeq()));
			criteria.add(Restrictions.eq("comp_id.groupname",
					tbsysmultilangVO.getComp_id().getGroupname()));
			criteria.add(Restrictions.eq("comp_id.groupSeq",
					tbsysmultilangVO.getComp_id().getGroupSeq()));
			criteria.add(Restrictions.eq("comp_id.locale",
					tbsysmultilangVO.getComp_id().getLocale()));
			criteria.addOrder(Order.asc("comp_id.attribute"))
					.addOrder(Order.asc("comp_id.seq"));				                                             
	        List<TbsysmultilangVO> list = criteria.list();
	        		
			return list;
		}

		public List<TbsysmultilangVO> findByProperties(String type, String typeSeq,	String locale) throws DAOException, JBranchException {

			Criteria criteria =  dam.getHibernateCriteria(TbsysmultilangVO.TABLE_UID);
			criteria.add(Restrictions.eq("comp_id.type", type));
			criteria.add(Restrictions.eq("comp_id.typeSeq", typeSeq));
			criteria.add(Restrictions.eq("comp_id.locale", locale));
			criteria.addOrder(Order.asc("comp_id.attribute")).addOrder(Order.desc("comp_id.seq"));

			List<TbsysmultilangVO> list = criteria.list();				
			
			return list;
		}
		


	}


