package com.systex.jbranch.platform.common.security.privilege;


import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.helper.DataAccessHelper;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.EnumErrInputType;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;
import com.systex.jbranch.platform.common.platformdao.table.*;
import com.systex.jbranch.platform.common.security.privilege.vo.*;
import com.systex.jbranch.platform.common.security.util.SecurityErrMsgHelper;

import java.util.Arrays;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 權限管理類別，提供權限的新增、修改、刪除、查詢、權限與平台提供的模組交易功能對應設定
 *
 * @author Hong-jie
 * @version 1.0 2008/4/14
 */
public class PrivilegeManagement {
// ------------------------------ FIELDS ------------------------------

    private static final String FILTER_FLAG_ALLOWED = "A"; //accept
    private static final String FILTER_FLAG_NOT_ALLOWED = "D"; //deny

    private static final String FILTER_VALUE_ALL = "ALL";

    private static final String FUNCTION_APPLY = "Y"; //啟用功能
    private static final String FUNCTION_NOT_APPLY = "N"; //不啟用功能
    private static final String ORGANIZATION_ATTRIBUTE = "O";
    private static final String RUNTIME_ATTRIBUTE = "R";

    DataAccessManager dataAccessManager = null;

	private Logger logger = LoggerFactory.getLogger(PrivilegeManagement.class);

// -------------------------- OTHER METHODS --------------------------

    /**
     * 新增權限與runtime屬性的對應關係
     *
     * @param vo
     */
    public void addRuntimeAttribute2Privilege(com.systex.jbranch.platform.common.security.privilege.vo.PrivilegeAttributeAssociationVO vo) throws JBranchException {
        SerialNumberUtil serialNumberUtil = new SerialNumberUtil();
        long seq = Long.parseLong(serialNumberUtil.getNextSerialNumber("Tbsyssecupriattass"));
        TbsyssecupriattassVO assVO = new TbsyssecupriattassVO();
        assVO.setFilterid(seq);
        assVO.setFlag(vo.getFlag());
        assVO.setValue(vo.getValue());
        assVO.setPrivilegeid(vo.getPrivilegeid());
        assVO.setPattributeid(vo.getAttributeid());
        dataAccessManager.create(assVO);
    }

    /**
     * 建立屬性
     *
     * @param attributeVO
     */
    public void createAttribute(com.systex.jbranch.platform.common.security.privilege.vo.AttributeVO attributeVO) throws JBranchException {
        TbsyssecuattVO vo = new TbsyssecuattVO();
        vo.setPattributeid(attributeVO.getPattributeid());
        vo.setName(attributeVO.getName());
        vo.setDescription(attributeVO.getDescription());
        vo.setType(attributeVO.getType());
        dataAccessManager.create(vo);
    }

    /**
     * 新增屬性值
     *
     * @param attributeValueVO
     */
    public void createAttributeValue(com.systex.jbranch.platform.common.security.privilege.vo.AttributeValueVO attributeValueVO) throws JBranchException {
        TbsyssecuattvalVO vo = new TbsyssecuattvalVO();
        vo.setPattributeid(attributeValueVO.getPattributeid());
        vo.setDescription(attributeValueVO.getDescription());
        vo.setExtend1(attributeValueVO.getExtend1());
        vo.setExtend2(attributeValueVO.getExtend2());
        vo.setExtend3(attributeValueVO.getExtend3());
        /*
        TbsyssecuattvalVO.setCreatetime(new Timestamp(System.currentTimeMillis()));
        TbsyssecuattvalVO.setLastupdate(new Timestamp(System.currentTimeMillis()));
        TbsyssecuattvalVO.setCreator("SYSTEM");
        TbsyssecuattvalVO.setModifier("SYSTEM");
        */
        vo.setAttributeid(attributeValueVO.getAttributeid());
        dataAccessManager.create(vo);
    }

    /**
     * 新增權限
     *
     * @param vo
     */
    public void createPrivilege(com.systex.jbranch.platform.common.security.privilege.vo.PrivilegeVO privilege) {
//		try{
//			TbsyssecupriVO vo= new TbsyssecupriVO();
//			vo.setPrivilegeid(privilege.getPrivilegeid());
//			vo.setName(privilege.getName());
//			vo.setDescription(privilege.getDescription());
//			vo.setOperation(privilege.getOperation());
//			dataAccessManager.create(vo);
//		}catch(Exception e){
//			String msg = StringUtil.getStackTraceAsString(e);
//			LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_ERROR, msg);
//		}
    }

    /**
     * 新增權限與runtime屬性的對應關係
     *
     * @param vo
     */
    public void createPrivilegeFunctionMap(com.systex.jbranch.platform.common.security.privilege.vo.PrivilegeFunctionMapVO mapVO) {
        SerialNumberUtil serialNumberUtil = null;
//		try{
////			SerialNumberUtil.createNewSerial("Tbsyssecuprifunmap");
//			serialNumberUtil = new SerialNumberUtil();
//			long seq = Long.parseLong(serialNumberUtil.getNextSerialNumber("Tbsyssecuprifunmap"));
//			TbsyssecuprifunmapVO vo=new TbsyssecuprifunmapVO();
//			vo.setFunctionid(mapVO.getFunctionid());
//			vo.setItemid(mapVO.getItemid());
//			vo.setMapid(seq);
//			vo.setModuleid(mapVO.getModuleid());
//			vo.setPrivilegeid(mapVO.getPrivilegeid());
//			/*
//			TbsyssecuprifunmapVO.setCreatetime(new Timestamp(System.currentTimeMillis()));
//			TbsyssecuprifunmapVO.setLastupdate(new Timestamp(System.currentTimeMillis()));
//			TbsyssecuprifunmapVO.setCreator("SYSTEM");
//			TbsyssecuprifunmapVO.setModifier("SYSTEM");
//			*/
//			dataAccessManager.create(vo);
//		}catch (Exception e){
//			String msg = StringUtil.getStackTraceAsString(e);
//			LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_ERROR, msg);
//		}
    }

    public void createRolePriAss(com.systex.jbranch.platform.common.security.privilege.vo.RolePriAssVO rolePriAssVO) throws JBranchException {
        TbsyssecurolpriassVO vo = new TbsyssecurolpriassVO();
        TbsyssecurolpriassPK pk = new TbsyssecurolpriassPK();
        pk.setPrivilegeid(rolePriAssVO.getPrivilegeid());
        pk.setRoleid(rolePriAssVO.getRoleid());

        vo.setComp_id(pk);
        dataAccessManager.create(vo);
    }

    /**
     * 刪除屬性
     *
     * @param attributeID
     */
    public void deleteAttribute(String attributeID) throws DAOException {
        TbsyssecuattVO vo = (TbsyssecuattVO) dataAccessManager.findByPKey(TbsyssecuattVO.TABLE_UID, attributeID);
        dataAccessManager.delete(vo);
    }

    /**
     * 刪除屬性值
     *
     * @param attributeValueID
     */
    public void deleteAttributeValue(String attributeValueID) throws DAOException {
        TbsyssecuattvalVO vo = (TbsyssecuattvalVO) dataAccessManager.findByPKey(TbsyssecuattvalVO.TABLE_UID, attributeValueID);
        dataAccessManager.delete(vo);
    }

    /**
     * 刪除權限
     *
     * @param privilgeID
     */
    public void deletePrivilege(String privilegeID) throws DAOException {
        TbsyssecupriVO secupriVO = (TbsyssecupriVO) dataAccessManager.findByPKey(TbsyssecupriVO.TABLE_UID, privilegeID);
        dataAccessManager.delete(secupriVO);
    }

    /**
     * 刪除權限與模組交易功能的對應關係
     *
     * @param mapID 對應關係ID
     */
    public void deletePrivilegeFunctionMap(Long mapID) throws DAOException {
        TbsyssecuprifunmapVO vo = (TbsyssecuprifunmapVO) dataAccessManager.findByPKey(TbsyssecuprifunmapVO.TABLE_UID, mapID);
        dataAccessManager.delete(vo);
    }

    /**
     * 刪除權限與runtime屬性的對應關係
     *
     * @param vo
     */
    public void deleteRuntimeAttributeFromPrivilege(com.systex.jbranch.platform.common.security.privilege.vo.PrivilegeAttributeAssociationVO associationVO) {
    }

    /**
     * 取得指定角色，所有被賦予的其他角色
     *
     * @param roleid 指定的角色
     * @return RoleID Collection
     */
    public Set<String> getAllGivenRole(String roleid) throws Exception {
        Set<String> roleSet = new HashSet<String>();

        QueryConditionIF queryCondition = dataAccessManager.getQueryCondition(DataAccessHelper.QUERY_LANGUAGE_TYPE_SQL);
        //取得被賦與的角色ID
        String sqlStr = "select giver as GIVER from tbsyssecurolegiv where receiver=?";
        queryCondition.setQueryString(sqlStr);
        queryCondition.setString(1, roleid);
        List<Map<String, Object>> list = dataAccessManager.executeQuery(queryCondition);
        for (Map<String, Object> map : list) {
            String giverRoleId = (String) map.get("GIVER");
            if (giverRoleId != null && giverRoleId.trim().length() > 0) {
                roleSet.add(giverRoleId);
            }
        }

        return roleSet;
    }

    public Set<TbsyssecupriattassVO> getAllowPriByPriIDHql(String priID) throws DAOException {
        Set<TbsyssecupriattassVO> hashSet = new HashSet();
        TbsyssecupriattassVO vo = new TbsyssecupriattassVO();
        vo.setPrivilegeid(priID);

        for (Object o : dataAccessManager.findByFields(TbsyssecupriattassVO.TABLE_UID, TbsyssecupriattassDaoIF.FINDBY_PRIID)) {
            hashSet.add((TbsyssecupriattassVO) o);
        }
        return hashSet;
    }

    /**
     * 取得屬性設定
     *
     * @param attributeID
     * @return AttributeVO
     */
    public com.systex.jbranch.platform.common.security.privilege.vo.AttributeVO getAttribute(String attributeID) throws DAOException {
        TbsyssecuattVO TbsyssecuattVO = new TbsyssecuattVO();
        AttributeVO attributeVO = new AttributeVO();
        TbsyssecuattVO = (TbsyssecuattVO) dataAccessManager.findByPKey(TbsyssecuattVO.TABLE_UID, attributeID);
        attributeVO.setDescription(TbsyssecuattVO.getDescription());
        attributeVO.setName(TbsyssecuattVO.getName());
        attributeVO.setPattributeid(TbsyssecuattVO.getPattributeid());
        attributeVO.setType(TbsyssecuattVO.getType());
        return attributeVO;
    }

    /**
     * 取得指定權限ID所對應的所有功能模組
     *
     * @param privilegeid
     * @return Collection<PrivilegeFunctionMapVO>
     */
    public java.util.Collection getFunctionMapByPrivilege(String privilegeid) {
        Set<PrivilegeFunctionMapVO> set = new HashSet();
//		try{
//			/*
//			Iterator<TbsyssecuprifunmapVO> it=Tbsyssecuprifunmap.findByPriID(privilegeid).iterator();
//			while (it.hasNext()){
//				TbsyssecuprifunmapVO vo=it.next();
//				PrivilegeFunctionMapVO privilegeFunctionMapVO=new PrivilegeFunctionMapVO();
//				privilegeFunctionMapVO.setFunctionid(vo.getFunctionid());
//				privilegeFunctionMapVO.setItemid(vo.getItemid());
//				privilegeFunctionMapVO.setMapid(vo.getMapid());
//				privilegeFunctionMapVO.setModuleid(vo.getModuleid());
//				privilegeFunctionMapVO.setPrivilegeid(vo.getPrivilegeid());
//
//			}
//			*/
//			TbsyssecuprifunmapVO tbsyssecuprifunmapVO = new TbsyssecuprifunmapVO();
//			tbsyssecuprifunmapVO.setPrivilegeid(privilegeid);
//			List<TbsyssecuprifunmapVO> listVO = null;
//			listVO = dataAccessManager.findByFields(TbsyssecuprifunmapVO.TABLE_UID,TbsyssecuprifunmapDaoIF.FINDBY_PRI);
//
//			PrivilegeFunctionMapVO privilegeFunctionMapVO=null;
//			for (TbsyssecuprifunmapVO vo: listVO) {
//				privilegeFunctionMapVO=new PrivilegeFunctionMapVO();
//				privilegeFunctionMapVO.setFunctionid(vo.getFunctionid());
//				privilegeFunctionMapVO.setItemid(vo.getItemid());
//				privilegeFunctionMapVO.setMapid(vo.getMapid());
//				privilegeFunctionMapVO.setModuleid(vo.getModuleid());
//				privilegeFunctionMapVO.setPrivilegeid(vo.getPrivilegeid());
//			}
//
//		}catch(Exception e){
//			String msg = StringUtil.getStackTraceAsString(e);
//			LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_ERROR, msg);
//		}
        return set;
    }

    /**
     * 取得指定權限所對應的所有交易細項功能
     *
     * @param privilegeid 權限ID
     * @reutrn Collection<ItemFunctionVO>
     */
    public java.util.Collection getItemFunctionByPrivilege(String privilegeid, String module, String item) {
        Set<PrivilegeFunctionMapVO> setVO = new HashSet();
//		try{
//			TbsyssecuprifunmapVO vo=new TbsyssecuprifunmapVO();
//			vo.setPrivilegeid(privilegeid);
//			vo.setModuleid(module);
//			vo.setItemid(item);
//			Iterator<TbsyssecuprifunmapVO> iter =dataAccessManager.findByFields(TbsyssecuprifunmapVO.TABLE_UID,TbsyssecuprifunmapDaoIF.FINDBY_MOD_ITM_PRI).iterator();
//			while(iter.hasNext()){
//				TbsyssecuprifunmapVO TbsyssecuprifunmapVO2=iter.next();
//				PrivilegeFunctionMapVO privilegeFunctionMapVO=new PrivilegeFunctionMapVO();
//				privilegeFunctionMapVO.setFunctionid(TbsyssecuprifunmapVO2.getFunctionid());
//				privilegeFunctionMapVO.setItemid(TbsyssecuprifunmapVO2.getItemid());
//				setVO.add(privilegeFunctionMapVO);
//			}
//
//		}catch(Exception e){
//			String msg = StringUtil.getStackTraceAsString(e);
//			LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_ERROR, msg);
//		}
        return setVO;
    }

    /**
     * 取得指定privilege下，所有註冊的功能模組
     *
     * @param privilegeid 權限ID
     * @reutrn Collection<ModuleID>
     */
    public java.util.Collection getModuleByPrivilege(String privilegeid) {
        Set<String> set = new HashSet();
//		try{
//			/*
//			Iterator<TbsyssecuprifunmapVO> it=Tbsyssecuprifunmap.findByPriID(privilegeid).iterator();
//			while (it.hasNext()){
//				TbsyssecuprifunmapVO vo=it.next();
//				PrivilegeFunctionMapVO privilegeFunctionMapVO=new PrivilegeFunctionMapVO();
//				set.add(vo.getModuleid());
//			}
//			*/
//			TbsyssecuprifunmapVO tbsyssecuprifunmapVO = new TbsyssecuprifunmapVO();
//			tbsyssecuprifunmapVO.setPrivilegeid(privilegeid);
//			List<TbsyssecuprifunmapVO> listVO = null;
//			listVO = dataAccessManager.findByFields(TbsyssecuprifunmapVO.TABLE_UID,TbsyssecuprifunmapDaoIF.FINDBY_PRI);
//
//			PrivilegeFunctionMapVO privilegeFunctionMapVO=null;
//			for (TbsyssecuprifunmapVO vo: listVO) {
//				privilegeFunctionMapVO=new PrivilegeFunctionMapVO();
//				set.add(vo.getModuleid());
//			}
//		}catch(Exception e){
//			String msg = StringUtil.getStackTraceAsString(e);
//			LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_ERROR, msg);
//		}
        return set;
    }

    /**
     * 取得指訂權限下，所註冊的所有模組交易
     *
     * @return ModuleItemVO的Collection
     */
    public java.util.Collection getModuleItemByPrivilege(String privilegeid, String Moduleid) {
        Set<PrivilegeFunctionMapVO> setVO = new HashSet();
//		try{
//			TbsyssecuprifunmapVO vo=new TbsyssecuprifunmapVO();
//			vo.setPrivilegeid(privilegeid);
//			vo.setModuleid(Moduleid);
//			Iterator<TbsyssecuprifunmapVO> iter =dataAccessManager.findByFields(TbsyssecuprifunmapVO.TABLE_UID,TbsyssecuprifunmapDaoIF.FINDBY_MOD_PRI).iterator();
//
//			while(iter.hasNext()){
//				TbsyssecuprifunmapVO TbsyssecuprifunmapVO2=iter.next();
//				PrivilegeFunctionMapVO privilegeFunctionMapVO=new PrivilegeFunctionMapVO();
//				privilegeFunctionMapVO.setModuleid(TbsyssecuprifunmapVO2.getModuleid());
//				privilegeFunctionMapVO.setItemid(TbsyssecuprifunmapVO2.getItemid());
//				setVO.add(privilegeFunctionMapVO);
//			}
//
//		}catch(Exception e){
//			String msg = StringUtil.getStackTraceAsString(e);
//			LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_ERROR, msg);
//		}
        return setVO;
    }

    /**
     public java.util.Collection getRoleFunctionMapFilter(String roleid) throws DAOException, JBranchException{
     Set<PrivilegeFunctionMapVO> setVO=new HashSet();
     Iterator<String> itt=getAllGivenRole(roleid).iterator();
     String hqlRolPri = "from TbsyssecurolpriassVO c where c.privilegeid=";
     //		String hqlPriAttAss = "from TbsyssecupriattassVO c where ";
     String hqlPriFunMap="from TbsyssecuprifunmapVO c where c.privilegeid=";
     while(itt.hasNext()){
     hqlRolPri = hqlRolPri + "'"+itt.next()+"'or c.privilegeid=";
     }
     String hqlRolPriTmp = hqlRolPri.substring(0, hqlRolPri.lastIndexOf("or c.privilegeid="));
     LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "hqlRolPriTmp "+hqlRolPri);
     Iterator<TbsyssecurolpriassVO> rolPriAssIter=getPriByRoleIDHql(hqlRolPriTmp).iterator();
     //		while(rolPriAssIter.hasNext()){
     //			hqlPriFunMap = hqlPriFunMap + "c.TbsysSecuRolPriAttAssPK.privilegeid= "+rolPriAssIter.next().getTbsyssecurolpriassPK().getPrivilegeid();
     //		}
     //		Iterator<TbsyssecupriattassVO> rolPriAttAssIter=getAllowPriByPriIDHql(hqlPriAttAss).iterator();
     while(rolPriAssIter.hasNext()){
     hqlPriFunMap=hqlPriFunMap+"'"+rolPriAssIter.next().getComp_id().getPrivilegeid()+"' or c.privilegeid=";
     }
     String hqlPriFunMapTmp=hqlPriFunMap.substring(0, hqlPriFunMap.lastIndexOf(" or c.privilegeid="));
     LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "hqlPriFunMapTmp "+hqlPriFunMapTmp);
     Iterator<PrivilegeFunctionMapVO> Tbsyssecuprifunmap=getModuleItemByPrivilegeHql(hqlPriFunMapTmp).iterator();
     while(Tbsyssecuprifunmap.hasNext()){
     PrivilegeFunctionMapVO privilegeFunctionMapVO=new PrivilegeFunctionMapVO();
     privilegeFunctionMapVO=Tbsyssecuprifunmap.next();
     setVO.add(privilegeFunctionMapVO);
     }
     return setVO;
     }
     **/

    /**
     * 取得指定角色對應的所有PrivilegeFunction Map (Privilege,Module,Item,Function)
     * @param roleid 指定的角色ID
     * @return function map collection
     * @throws JBranchException
     * @throws DAOException
     */
    /**
     * public java.util.Collection getRoleFunctionMap(String roleid) throws DAOException, JBranchException{
     * Set<PrivilegeFunctionMapVO> setVO=new HashSet();
     * Iterator<String> itt=getAllGivenRole(roleid).iterator();
     * String hqlRolPri = "from TbsyssecurolpriassVO c where c.privilegeid=";
     * //		String hqlPriAttAss = "from TbsyssecupriattassVO c where ";
     * String hqlPriFunMap="from TbsyssecuprifunmapVO c where c.privilegeid=";
     * while(itt.hasNext()){
     * hqlRolPri = hqlRolPri + "'"+itt.next()+"'or c.privilegeid=";
     * }
     * String hqlRolPriTmp = hqlRolPri.substring(0, hqlRolPri.lastIndexOf("or c.privilegeid="));
     * LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "hqlRolPriTmp "+hqlRolPri);
     * Iterator<TbsyssecurolpriassVO> rolPriAssIter=getPriByRoleIDHql(hqlRolPriTmp).iterator();
     * //		while(rolPriAssIter.hasNext()){
     * //			hqlPriFunMap = hqlPriFunMap + "c.TbsysSecuRolPriAttAssPK.privilegeid= "+rolPriAssIter.next().getTbsyssecurolpriassPK().getPrivilegeid();
     * //		}
     * //		Iterator<TbsyssecupriattassVO> rolPriAttAssIter=getAllowPriByPriIDHql(hqlPriAttAss).iterator();
     * while(rolPriAssIter.hasNext()){
     * hqlPriFunMap=hqlPriFunMap+"'"+rolPriAssIter.next().getComp_id().getPrivilegeid()+"' or c.privilegeid=";
     * }
     * String hqlPriFunMapTmp=hqlPriFunMap.substring(0, hqlPriFunMap.lastIndexOf(" or c.privilegeid="));
     * LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "hqlPriFunMapTmp "+hqlPriFunMapTmp);
     * Iterator<PrivilegeFunctionMapVO> Tbsyssecuprifunmap=getModuleItemByPrivilegeHql(hqlPriFunMapTmp).iterator();
     * while(Tbsyssecuprifunmap.hasNext()){
     * PrivilegeFunctionMapVO privilegeFunctionMapVO=new PrivilegeFunctionMapVO();
     * privilegeFunctionMapVO=Tbsyssecuprifunmap.next();
     * setVO.add(privilegeFunctionMapVO);
     * }
     * return setVO;
     * }*
     */

    public java.util.Collection getModuleItemByPrivilegeHql(String privilegeidHql) {
        Set<PrivilegeFunctionMapVO> setVO = new HashSet();
//		try{
//			TbsyssecuprifunmapVO TbsyssecuprifunmapVO=new TbsyssecuprifunmapVO();
//			TbsyssecuprifunmapVO TbsyssecuprifunmapVO2 = null;
//			QueryConditionIF queryCondition = dataAccessManager.getQueryCondition(DataAccessHelper.QUERY_LANGUAGE_TYPE_HQL);
//			queryCondition.setQueryString(privilegeidHql);
//			List list = dataAccessManager.executeQuery(queryCondition);
//			Iterator result = list.iterator();
//			while(result.hasNext()){
//				TbsyssecuprifunmapVO2 = (TbsyssecuprifunmapVO) result.next();
//				PrivilegeFunctionMapVO privilegeFunctionMapVO=new PrivilegeFunctionMapVO();
//				privilegeFunctionMapVO.setPrivilegeid(TbsyssecuprifunmapVO2.getPrivilegeid());
//				privilegeFunctionMapVO.setModuleid(TbsyssecuprifunmapVO2.getModuleid());
//				privilegeFunctionMapVO.setFunctionid(TbsyssecuprifunmapVO2.getFunctionid());
//				privilegeFunctionMapVO.setItemid(TbsyssecuprifunmapVO2.getItemid());
//				setVO.add(privilegeFunctionMapVO);
//			}
//
//		}catch(Exception e){
//			String msg = StringUtil.getStackTraceAsString(e);
//			LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_ERROR, msg);
//		}
        return setVO;
    }

    //放入整串hql
    public Set<TbsyssecurolpriassVO> getPriByRoleIDHql(String hql) throws DAOException, JBranchException {
        Set<TbsyssecurolpriassVO> hashSet = new HashSet();
        TbsyssecurolpriassVO securolpriassVO;
        QueryConditionIF queryCondition = dataAccessManager.getQueryCondition(DataAccessHelper.QUERY_LANGUAGE_TYPE_HQL);
        queryCondition.setQueryString(hql);
        List list = dataAccessManager.executeQuery(queryCondition);
        for (Object aList : list) {
            securolpriassVO = (TbsyssecurolpriassVO) aList;
            hashSet.add(securolpriassVO);
        }
        return hashSet;
    }

    /**
     * 取得權限資料
     *
     * @param privilegeID
     * @return PrivilegeVO
     */
    public com.systex.jbranch.platform.common.security.privilege.vo.PrivilegeVO getPrivilege(String privilegeID) {
        TbsyssecupriVO vo = new TbsyssecupriVO();
        PrivilegeVO privilegeVO = new PrivilegeVO();
//		try{
//			vo=(TbsyssecupriVO) dataAccessManager.findByPKey(TbsyssecupriVO.TABLE_UID,privilegeID);
//			privilegeVO.setDescription(vo.getDescription());
//			privilegeVO.setName(vo.getName());
//			privilegeVO.setOperation(vo.getOperation());
//			privilegeVO.setPrivilegeid(vo.getPrivilegeid());
//		}catch(Exception e){
//			String msg = StringUtil.getStackTraceAsString(e);
//			LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_ERROR, msg);
//		}
        return privilegeVO;
    }

    /**
     * 取得指定權限ID所對應指定runtime屬性的值
     *
     * @param privilegeid 權限ID
     * @param attributeid 屬性ID
     * @return Collection<PrivilegeAttributeAssociationVO>
     */
    public java.util.Collection getRuntimeAttributeByPrivilege(String attributeid, String privilegeid) throws DAOException {
        Set<TbsyssecupriattassVO> hashSet = new HashSet();
        TbsyssecupriattassVO vo = new TbsyssecupriattassVO();
        vo.setPrivilegeid(privilegeid);
        vo.setPattributeid(attributeid);

        for (Object o : dataAccessManager.findByFields(TbsyssecupriattassVO.TABLE_UID, TbsyssecupriattassDaoIF.FINDBY_PRIID_PATID)) {
            hashSet.add((TbsyssecupriattassVO) o);
        }
        return hashSet;
    }

    /**
     * 取得使用者可使用的功能清單
     *
     * @param userid 使用者ID
     * @return Map<String, ModuleDTO>
     */
    public UserFunctionDTO getUserFunctionMap(String userID) throws DAOException, JBranchException {
        return getUserFunctionMap(userID, new HashMap());
    }

    /**
     * 取得使用者可使用的功能清單
     *
     * @param userid    使用者ID
     * @param runtime屬性
     * @return Map<String, ModuleDTO>
     */
    public UserFunctionDTO getUserFunctionMap(String userID, Map runtimeAttr) throws JBranchException {
        //取得user資訊
        TbsysuserVO userVO = null;
        try {
            userVO = (TbsysuserVO) dataAccessManager.findByPKey(TbsysuserVO.TABLE_UID, userID);
        }
        catch (NotFoundException e) {
        }

        if (userVO == null) {
            throw new JBranchException(EnumErrInputType.MSG, "user " + userID + " Not Found");
        }

        //抓取Runtime屬性，用來過濾權限
        Map<String, String> runtimeAttrMap = new HashMap<String, String>();
        TbsyssecuattVO secuattVO = new TbsyssecuattVO();
        secuattVO.setType(RUNTIME_ATTRIBUTE);
        try {
            for (Object o : dataAccessManager.findByFields(secuattVO, new String[]{"type"})) {
                secuattVO = (TbsyssecuattVO) o;
                runtimeAttrMap.put(secuattVO.getPattributeid(), secuattVO.getName());
            }
        }
        catch (NotFoundException e) {
        }

        //取得使用者最小權限角色
        String[] userRoleInfo = getUserRole(userID);
        if (userRoleInfo[0] == null || userRoleInfo[0].trim().length() == 0) {
            throw new JBranchException(EnumErrInputType.MSG, "user " + userID + " role not found");
        }

        //依角色取得功能清單
        RoleFunctionDTO roleFunctionDTO = getFunctionMapByRole(userRoleInfo[0]);

        UserFunctionDTO userFunction = new UserFunctionDTO();
        userFunction.setUserID(userID);
        userFunction.setModuleMap(roleFunctionDTO.getModuleMap());
        userFunction.setRoleID(roleFunctionDTO.getRoleID());
        userFunction.setRoleName(roleFunctionDTO.getRoleName());
        userFunction.setExtend1(roleFunctionDTO.getExtend1());
        userFunction.setExtend2(roleFunctionDTO.getExtend2());
        userFunction.setExtend3(roleFunctionDTO.getExtend3());

        return userFunction;

        /*
               *
               *
               * peter的code
              userVO = (TbsysuserVO) dataAccessManager.findByPKey(TbsysuserVO.TABLE_UID,userID);
              String roleID = userVO.getRuleid();
              Iterator it = getAllGivenRole(roleID).iterator();
              //get role
              ///test start
  //			String rolexx=(String) it.next();
  //			getRoleFunctionMap(rolexx);
  //			while(it.hasNext()){//get 獲得全部角色
  //				hql = hql + "or priID = "+it.next();
  //			}                   // 有全部角色找權限
  //			Iterator<TbsyssecurolpriassVO> assItTest=getPriByRoleID(hql).iterator();
  //			while(assItTest.hasNext()){
  //				hql = hql + "or priID = "+assItTest.next().getTbsyssecurolpriassPK().getPrivilegeid();
  //			}//找出可用的item
  //			Iterator<TbsyssecupriattassVO> riItTest=getAllowPriByPriID(hql).iterator();
  //			while(assItTest.hasNext()){
  //				hql = hql + "or priID = "+assItTest.next().getTbsyssecurolpriassPK().getPrivilegeid();
  //			}


              //test end

              while(it.hasNext()){
                  String roleAll=(String) it.next();
  //				get TbsyssecurolpriassVO

                  Iterator<TbsyssecurolpriassVO> assIt=getPriByRoleID(roleAll).iterator();
  //				LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "161 all rol="+roleAll);
                  while(assIt.hasNext()){
                      String priID=assIt.next().getComp_id().getPrivilegeid();
                      //get TbsyssecupriattassVO
                      Iterator<TbsyssecupriattassVO> riIt=getAllowPriByPriID(priID).iterator();

  //					 getAllowPriByPriID(priID).iterator();
  //		    		for(Iterator itr = set.iterator(); itr.hasNext(); ){
  //		    			vo = (TbsyssecurolpriassVO)itr.next();
  //		    			vo.getTbsyssecurolpriassPK().getPrivilegeid();
  //		    			hql = hql + "or priID = "+vo.getTbsyssecurolpriassPK().getPrivilegeid();
  //		    		}
                      while(riIt.hasNext()){

                          ++num;
                          TbsyssecupriattassVO TbsyssecupriattassVO=riIt.next();
                          String patID=TbsyssecupriattassVO.getPattributeid();
                          String atasval=TbsyssecupriattassVO.getValue();
                          String flag=TbsyssecupriattassVO.getFlag();
                          String pri=TbsyssecupriattassVO.getPrivilegeid();
                          TbsysuserVO TbsysuserVO=getUserInfo(userID);


                          String userVal=BeanUtils.getProperty(TbsysuserVO, (String) runtimeAttrMap.get(patID));

                          LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "BEFatpatID="+runtimeAttrMap.get(patID));
                          LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "BEFuserVal="+userVal);
                          LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "BEFatasval="+atasval);

                          if(atasval.equalsIgnoreCase("ALL") && "N".equalsIgnoreCase(flag)){
                              LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "NatpatID="+runtimeAttrMap.get(patID));
                              LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "NuserVal="+userVal);
                              LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "Natasval="+atasval);
                              priFunSet.clear();
                              break;
                          }
                          if(userVal.equalsIgnoreCase(atasval) && "N".equalsIgnoreCase(flag)){
                              LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "NatpatID="+runtimeAttrMap.get(patID));
                              LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "NuserVal="+userVal);
                              LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "Natasval="+atasval);
                              priFunSet.clear();
                              continue;
                          }
                          if((userVal.equalsIgnoreCase(atasval) || atasval.equalsIgnoreCase("ALL")) && "Y".equalsIgnoreCase(flag)){


                              //Iterator<VwsyssecumifVO> attwIt= Vwsyssecumif.findByPriID(pri).iterator();

                              VwsyssecumifVO vwsyssecumifVO = new VwsyssecumifVO();
                              vwsyssecumifVO.setPrivilegeid(pri);
                              List<VwsyssecumifVO> listVO = null;
                              listVO = dataAccessManager.findByFields(VwsyssecumifVO.TABLE_UID,VwsyssecumifDaoIF.FINDBY_PRIID);

                              LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "YatpatID="+runtimeAttrMap.get(patID));
                              LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "YuserVal="+userVal);
                              LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "Yatasval="+atasval);

                              StringBuffer str=null;
                              for (VwsyssecumifVO vo: listVO) {
                                  str=new StringBuffer();
                                  str.append(vo.getModuleid()+",");
                                  str.append(vo.getFunctionid()+",");
                                  str.append(vo.getItemid()+",");
                                  str.append(vo.getModapp()+",");
                                  str.append(vo.getItemapp()+",");
                                  str.append(vo.getFunactionapp()+",");
                                  priFunSet.add(str.toString());
                              }
                              continue;
                          }
                      }
                  }
              }


              Iterator<String> itt =priFunSet.iterator();
              int ii=0;
              while(itt.hasNext()){
                  VwsyssecumifVO vo3=new VwsyssecumifVO();
                  String[] aryStr = itt.next().split(",");
                  vo3.setFunctionid(aryStr[0]);
                  vo3.setModuleid(aryStr[1]);
                  vo3.setItemid(aryStr[2]);
                  vo3.setModapp(aryStr[3]);
                  vo3.setItemapp(aryStr[4]);
                  vo3.setFunactionapp(aryStr[5]);
                  listModFunItm.add(vo3);
                  ii=ii+1;
              }

              */
    }

    /**
     * 依使用者取得最小權限的角色
     *
     * @param userId
     * @return
     * @throws DAOException
     * @throws JBranchException
     */

    //TODO:目前用不到,待調整order即可
    public String[] getUserRole(String userId) throws DAOException, JBranchException {
        String sqlStr = "select tbsyssecurole.roleid as ROLEID,tbsyssecurole.name as ROLENAME, tbsyssecurole.extend1 as EXTEND1, tbsyssecurole.extend2 as EXTEND2, tbsyssecurole.extend3 as EXTEND3 " +
                "  from tbsyssecurole " +
                "  left join Tbsysroleuserass on tbsyssecurole.roleid = Tbsysroleuserass.roleid " +
                " where userid=?";
        QueryConditionIF query = dataAccessManager.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
        query.setQueryString(sqlStr);
        query.setString(1, userId);
        List<Map<String, Object>> list = dataAccessManager.executeQuery(query);
        int lowestPrivilege = -1;
        String[] userRoleInfo = new String[5];
        for (Map<String, Object> aList : list) {
            String roleid = (String) aList.get("ROLEID");
            String roleName = (String) aList.get("ROLENAME");
            String extend1 = (String) aList.get("EXTEND1");
            String extend2 = (String) aList.get("EXTEND2");
            String extend3 = (String) aList.get("EXTEND3");
            int currentUserPrivilege = -1;
            try {
                currentUserPrivilege = Integer.parseInt(extend1);
            }
            catch (Exception e) {
            }

            if (userRoleInfo[0] == null) {
                lowestPrivilege = currentUserPrivilege;
                userRoleInfo[0] = roleid;
                userRoleInfo[1] = roleName;
                userRoleInfo[2] = extend1;
                userRoleInfo[3] = extend2;
                userRoleInfo[4] = extend3;
                continue;
            }

            if (lowestPrivilege < currentUserPrivilege) {
                lowestPrivilege = currentUserPrivilege;
                userRoleInfo[0] = roleid;
                userRoleInfo[1] = roleName;
                userRoleInfo[2] = extend1;
                userRoleInfo[3] = extend2;
                userRoleInfo[4] = extend3;
            }
        }
        return userRoleInfo;
    }

    /**
     * 取得指定角色ID所對應的所有功能模組
     *
     * @param roleid
     * @return Collection<PrivilegeFunctionMapVO>
     */
    public RoleFunctionDTO getFunctionMapByRole(String roleid) throws JBranchException {
        RoleFunctionDTO roleFunctionDTO = new RoleFunctionDTO();
        //取得此角色資料
        TBSYSSECUROLEVO tbsyssecuroleVO = null;
        tbsyssecuroleVO = (TBSYSSECUROLEVO) dataAccessManager.findByPKey(TBSYSSECUROLEVO.TABLE_UID, roleid);
        if (tbsyssecuroleVO == null) {
            //throw new NotFoundException(EnumErrInputType.MSG,"user(" + userId + ") doesn't not exist.");
            throw new JBranchException(SecurityErrMsgHelper.ROLE_ID_NOT_FOUND);
        }

        //取得此角色所有權限ID
        List<String> priList = getRolePrivileger(roleid);
        if (priList == null || priList.size() == 0) {
            //throw new JBranchException(EnumErrInputType.MSG, "user "+userID+" privilege not found");
            throw new JBranchException(SecurityErrMsgHelper.PRIVILEGE_NOT_FOUND);
        }
        //過濾Runtime屬性取出可用的權限ID
        //priList = filterRuntimeAttribute(priList, userVO,runtimeAttrMap);

        //取出所有privilege list所對應的功能
        Map<String, ModuleDTO> map = getPrivilegeFunctionMap(priList);
        if (map == null || map.size() == 0) {
            //throw new JBranchException(EnumErrInputType.MSG, "user "+userID+" valid privilege not found");
            throw new JBranchException(SecurityErrMsgHelper.FUNCTION_NOT_FOUND);
        }

        roleFunctionDTO.setModuleMap(map);
        roleFunctionDTO.setRoleID(roleid);
        roleFunctionDTO.setRoleName(tbsyssecuroleVO.getNAME());
        roleFunctionDTO.setExtend1(tbsyssecuroleVO.getEXTEND1());
        roleFunctionDTO.setExtend2(tbsyssecuroleVO.getEXTEND2());
        roleFunctionDTO.setExtend3(tbsyssecuroleVO.getEXTEND3());
        return roleFunctionDTO;
    }

    /**
     * 取得角色所有可用privilege id
     *
     * @param roleid
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public List<String> getRolePrivileger(String roleid) throws DAOException, JBranchException {
        List<String> rolePrivilegeList = new ArrayList<String>();
        String sqlStr = "select privilegeid as PRIVILEGEID from tbsyssecurolpriass where roleid=?";
        QueryConditionIF query = dataAccessManager.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
        query.setQueryString(sqlStr);
        query.setString(1, roleid);
        List<Map<String, Object>> list = dataAccessManager.executeQuery(query);
        for (Map<String, Object> aList : list) {
            rolePrivilegeList.add((String) aList.get("PRIVILEGEID"));
        }
        return rolePrivilegeList;
    }
    
    /**
     * 取得角色所有可用權限
     *
     * @param roles
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    public List<Map> getAuthorities(List<String> roles) throws DAOException, JBranchException {
    	
		QueryConditionIF qc = dataAccessManager.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("select a.moduleid as MODULEID, a.itemid as ITEMID, ");
		sql.append("a.functionid as FUNCTIONID from tbsyssecuprifunmap a ");
		sql.append("join tbsyssecurolpriass ass on a.privilegeid = ass.PRIVILEGEID ");
		sql.append("left join tbsyssecumod b on a.moduleid = b.moduleid ");
		sql.append("left join tbsyssecumoduitem c on a.itemid = c.itemid ");
		sql.append("left join tbsyssecuitemfun d on a.functionid = d.functionid ");
		sql.append("where ass.roleid in (:ROLES) and b.apply=:APPLY and c.apply=:APPLY and d.apply=:APPLY");
		qc.setQueryString(sql.toString());
		qc.setObject("ROLES", roles);
		qc.setObject("APPLY", FUNCTION_APPLY);
		
    	return dataAccessManager.exeQuery(qc);
    }

    /**
     * 取出所有Privilege所對應的功能
     *
     * @param privilegeList
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    private Map<String, ModuleDTO> getPrivilegeFunctionMap(List<String> privilegeList) throws DAOException, JBranchException {
        Map<String, ModuleDTO> all = new HashMap<String, ModuleDTO>();

        String sqlStr = "select a.moduleid as MODULEID, a.itemid as ITEMID, " +
                "       a.functionid as FUNCTIONID from tbsyssecuprifunmap a " +
                "  left join tbsyssecumod b      on a.moduleid   = b.moduleid   " +
                "  left join tbsyssecumoduitem c on a.itemid     = c.itemid     " +
                "  left join tbsyssecuitemfun d  on a.functionid = d.functionid " +
                " where a.privilegeid=? and b.apply='" + FUNCTION_APPLY + "' and c.apply='" + FUNCTION_APPLY + "' and d.apply='" + FUNCTION_APPLY + "'" +
                " ORDER BY A.ITEMID";
        QueryConditionIF query = dataAccessManager.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
        query.setQueryString(sqlStr);

        //依序取出每個privilege所對應的功能
        for (String privilegeId : privilegeList) {
            query.setString(1, privilegeId);
            List<Map<String, Object>> list = dataAccessManager.executeQuery(query);

            for (Map<String, Object> map : list) {
                String moduleId = (String) map.get("MODULEID");
                //String moduleName    = (String) map.get("MODULENAME");
                String itemId = (String) map.get("ITEMID");
                //String itemName      = (String) map.get("ITEMNAME");
                String functionid = (String) map.get("FUNCTIONID");

                if (all.get(moduleId) == null) {//沒有存取過的module
                    //設定module資訊
                    ModuleDTO moduleDTO = new ModuleDTO();
                    moduleDTO.setModuleId(moduleId);
                    //moduleDTO.setModuleName(moduleName);
                    Map<String, ItemDTO> itemMap = moduleDTO.getItemMap();

                    //將item資訊設定至module中
                    ItemDTO itemDTO = new ItemDTO();
                    itemDTO.setItemId(itemId);
                    //itemDTO.setItemName(itemName);
                    itemMap.put(itemId, itemDTO);
                    Set<String> functionList = itemDTO.getFunctionSet();
                    functionList.add(functionid);

                    //將module設定至需回傳的資訊中
                    all.put(moduleId, moduleDTO);
                }
                else {//已存取過的module
                    ModuleDTO moduleDTO = all.get(moduleId);
                    ItemDTO itemDTO = moduleDTO.getItemMap().get(itemId);
                    if (itemDTO == null) {//若沒有設定過此item
                        itemDTO = new ItemDTO();
                        itemDTO.setItemId(itemId);
                        //itemDTO.setItemName(itemName);
                        moduleDTO.getItemMap().put(itemId, itemDTO);
                    }
                    itemDTO.getFunctionSet().add(functionid);
                }
            }
        }
        return all;
    }

    /**
     * 取得屬性值
     *
     * @param attributeValueID
     * @return AttributeValueVO
     */
    public com.systex.jbranch.platform.common.security.privilege.vo.AttributeValueVO getlAttributeValue(String attributeValueID) throws DAOException {
        TbsyssecuattvalVO vo = new TbsyssecuattvalVO();
        AttributeValueVO attributeValueVO = new AttributeValueVO();
        vo = (TbsyssecuattvalVO) dataAccessManager.findByPKey(TbsyssecuattvalVO.TABLE_UID, attributeValueID);
        attributeValueVO.setDescription(vo.getDescription());
        attributeValueVO.setAttributeid(vo.getAttributeid());
        attributeValueVO.setPattributeid(vo.getPattributeid());
        attributeValueVO.setExtend1(vo.getExtend1());
        attributeValueVO.setExtend2(vo.getExtend2());
        attributeValueVO.setExtend3(vo.getExtend3());
        attributeValueVO.setValue(vo.getValue());
        return attributeValueVO;
    }

    /**
     * 更新屬性
     *
     * @param attributeVO
     */
    public void updateAttribute(com.systex.jbranch.platform.common.security.privilege.vo.AttributeVO attributeVO) throws DAOException {
        String attributeID = attributeVO.getPattributeid();
        TbsyssecuattVO vo = (TbsyssecuattVO) dataAccessManager.findByPKey(TbsyssecuattVO.TABLE_UID, attributeID);

        vo.setName(attributeVO.getName());
        vo.setType(attributeVO.getType());
        vo.setDescription(attributeVO.getDescription());
        dataAccessManager.update(vo);
    }

    /**
     * 更新屬性值
     *
     * @param attributeValueVO
     */
    public void updateAttributeValue(com.systex.jbranch.platform.common.security.privilege.vo.AttributeValueVO attributeValueVO) throws DAOException {
        String attID = attributeValueVO.getAttributeid();
        TbsyssecuattvalVO vo = (TbsyssecuattvalVO) dataAccessManager.findByPKey(TbsyssecuattvalVO.TABLE_UID, attID);
        vo.setPattributeid(attributeValueVO.getPattributeid());
        vo.setDescription(attributeValueVO.getDescription());
        vo.setExtend1(attributeValueVO.getExtend1());
        vo.setExtend2(attributeValueVO.getExtend2());
        vo.setExtend3(attributeValueVO.getExtend3());
        vo.setValue(attributeValueVO.getValue());
        /*
        TbsyssecuattvalVO.setLastupdate(new Timestamp(System.currentTimeMillis()));
        TbsyssecuattvalVO.setModifier("SYSTEM");
        */
    }

    /**
     * 更新權限資料
     *
     * @param vo
     */
    public void updatePrivilege(com.systex.jbranch.platform.common.security.privilege.vo.PrivilegeVO privilege) {
//		try{
//			TbsyssecupriVO vo= new TbsyssecupriVO();
//			String privilegeID=privilege.getPrivilegeid();
//			vo=(TbsyssecupriVO) dataAccessManager.findByPKey(TbsyssecupriVO.TABLE_UID,privilegeID);
//
//			vo.setName(privilege.getName());
//			vo.setDescription(privilege.getDescription());
//			vo.setOperation(privilege.getOperation());
//			/*
//			TbsyssecupriVO.setLastupdate(new Timestamp(System.currentTimeMillis()));
//			TbsyssecupriVO.setModifier("SYSTEM");
//			*/
//			dataAccessManager.update(vo);
//		}catch(Exception e){
//			String msg = StringUtil.getStackTraceAsString(e);
//			LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_ERROR, msg);
//		}
    }

    /**
     * 更新權限與模組交易功能的對應關係
     *
     * @param mapVO
     */
    public void updatePrivilegeFunctionMap(com.systex.jbranch.platform.common.security.privilege.vo.PrivilegeFunctionMapVO mapVO) {
//		try{
//			Long mapID=mapVO.getMapid();
//			TbsyssecuprifunmapVO vo =(TbsyssecuprifunmapVO) dataAccessManager.findByPKey(TbsyssecuprifunmapVO.TABLE_UID,mapID);
//			vo.setMapid(mapVO.getMapid());
//			vo.setFunctionid(mapVO.getFunctionid());
//			vo.setItemid(mapVO.getItemid());
//			vo.setModuleid(mapVO.getModuleid());
//			vo.setPrivilegeid(mapVO.getPrivilegeid());
//			/*
//			TbsyssecuprifunmapVO.setLastupdate(new Timestamp(System.currentTimeMillis()));
//			TbsyssecuprifunmapVO.setModifier("SYSTEM");
//			*/
//			dataAccessManager.update(vo);
//
//		}catch(Exception e){
//			String msg = StringUtil.getStackTraceAsString(e);
//			LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_ERROR, msg);
//		}
    }

    /**
     * 過濾Runtime屬性
     *
     * @param privileges
     * @param userVO
     * @param userRuntimeAttributes
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    private List<String> filterRuntimeAttribute(List<String> privileges, TbsysuserVO userVO,
                                                Map<String, String> userRuntimeAttributes) throws DAOException, JBranchException {
        List<String> allowedPrivileges = new ArrayList();
        for (int i = 0; i < privileges.size(); i++) {
            String privilegeId = privileges.get(i);
            String sqlStr = "select filterid as FILTERID, pattributeid as PATTRIBUTEID, value as VALUE, flag as FLAG " +
                    "  from tbsyssecupriattass where privilegeid=?";
            QueryConditionIF query = dataAccessManager.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
            query.setQueryString(sqlStr);
            query.setString(1, privilegeId);
            List<Map<String, Object>> list = dataAccessManager.executeQuery(query);
            //if(list.size() == 0) return privileges;
            for (int j = 0; j < list.size(); j++) {
                Map<String, Object> filterCondition = list.get(i);
                //過濾條件
                String filterid = (String) filterCondition.get("FILTERID");
                String pattributeid = (String) filterCondition.get("PATTRIBUTEID");
                String value = (String) filterCondition.get("VALUE");
                String flag = (String) filterCondition.get("FLAG");

                //全部允許使用
                if (FILTER_VALUE_ALL.equalsIgnoreCase(value)
                        && FILTER_FLAG_ALLOWED.equalsIgnoreCase(flag)) {
                    allowedPrivileges.add(privilegeId);
                    continue;
                }

                //全部不允許使用
                if (FILTER_VALUE_ALL.equalsIgnoreCase(value)
                        && FILTER_FLAG_NOT_ALLOWED.equalsIgnoreCase(flag)) {
                    continue;
                }

                //若filter value為null，則直接允許
                if (value == null) {
                    allowedPrivileges.add(privilegeId);
                    continue;
                }

                //抓取User Runtime屬性值
                String userValue = null;
                try {
                    userValue = BeanUtils.getProperty(userVO,
                            userRuntimeAttributes.get(pattributeid));
                }
                catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

                //符合條件的則允許使用
                if (FILTER_FLAG_ALLOWED.equalsIgnoreCase(flag)) {
                    if (value != null && value.equalsIgnoreCase(userValue)) {
                        allowedPrivileges.add(privilegeId);
                        continue;
                    }
                }

                //符合條件的則不允許使用
                if (FILTER_FLAG_NOT_ALLOWED.equalsIgnoreCase(flag)) {
                    if (value != null && value.equalsIgnoreCase(userValue)) {
                    }
                }
            }
        }
        return allowedPrivileges;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setDataAccessManager(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
    }
}
