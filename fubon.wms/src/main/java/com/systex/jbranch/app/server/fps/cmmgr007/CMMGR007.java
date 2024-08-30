package com.systex.jbranch.app.server.fps.cmmgr007;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobVO;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * JOB維護:修改歷程及說明
 *
 *@author 劉麗麗
 *@date 2009-9-07
 *@since Version
 *@spec 修改註記 V0.1 2009/9/1 初版 余嘉雯 2009/9/5 修改3.4 3.5紅字 余嘉雯 2009/9/9 修改3.2
 *       3.6余嘉雯
 *
 *@author William
 *@date 2016-05-21
 *
 *@author Sebastian
 *@date 2016-08-01
 *
 * @author Eli
 * @date 20181224 查詢條件部分條件沒有發揮功能
 *
 */
@Component("cmmgr007")
@Scope("request")
public class CMMGR007 extends FubonWmsBizLogic {

    private DataAccessManager dam = null;

    private static String CREATE = "Create";
    private static String UPDATE = "Update";
    private static String DELETE = "Delete";

    @Autowired
    private CMMGR007OutputVO outputVO;

    private CMMGR007InputVO inputVO;

    private void setInputVO(Object body) {
        inputVO = (CMMGR007InputVO) body;
    }

    /**
     * 對IT02的操作:新增、修改、刪除、查詢
     *
     * @param body
     * @param header
     * @throws JBranchException
     */
    public void operation(Object body, IPrimitiveMap<?> header) throws JBranchException {
        setInputVO(body);
        String actType = inputVO.getType();

        dam = this.getDataAccessManager();

        // 新增
        if (CREATE.equals(actType)) {
            TbsysschdjobVO jobVO = (TbsysschdjobVO) dam.findByPKey(TbsysschdjobVO.TABLE_UID, inputVO.getJobid());

            if (jobVO != null) {
                throw new APException("ehl_01_common_016");
            } else {
                jobVO = new TbsysschdjobVO();

                jobVO.setjobid(inputVO.getJobid());
                jobVO.setjobname(inputVO.getJobname());
                jobVO.setdescription(inputVO.getDescription());
                jobVO.setprecondition(inputVO.getPrecondition());
                jobVO.setbeanid(inputVO.getBeanid());
                jobVO.setclassid(inputVO.getJobid());
                jobVO.setparameters(inputVO.getParameters());
                jobVO.setpostcondition(inputVO.getPostcondition());

                dam.create(jobVO);
            }

            // 新增IT04
            setTBSYSSCHDJOBCLASS(inputVO);
//			TbsysschdjobclassVO classVO = (TbsysschdjobclassVO) dam.findByPKey(TbsysschdjobclassVO.TABLE_UID, inputVO.getJobid());
//			
//			if (classVO == null) {
//				classVO = new TbsysschdjobclassVO();
//				
//				//已經存在則不進行更改
//				classVO.setclassid(inputVO.getJobid());
//				classVO.setclassname(inputVO.getClassname());
//
//				dam.create(classVO);
//			}
        }

        // 刪除
        else if (DELETE.equals(actType)) {
            QueryConditionIF deleteCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

            deleteCondition.setQueryString(" delete from TBSYSSCHDJOB where JOBID=:jobID ");
            deleteCondition.setObject("jobID", inputVO.getJobid());

            int exsit_i = dam.exeUpdate(deleteCondition);

            if (exsit_i == 0) {
                throw new APException("ehl_01_common_017"); // 顯示資料不存在
            }

            // 刪除IT04
            deleteCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            deleteCondition.setQueryString(" delete from TBSYSSCHDJOBCLASS where CLASSID=:classID ");
            deleteCondition.setObject("classID", inputVO.getJobid());

            dam.exeUpdate(deleteCondition);
        }

        // 修改
        // 2016.08.01; Sebastian; 修改欄位為PK時,須先DELET再CREATE
        else if (UPDATE.equals(actType)) {
            TbsysschdjobVO vo;
            // general update
            if (inputVO.getJobid().equals(inputVO.getUpdatejobid())) {
                vo = (TbsysschdjobVO) dam.findByPKey(TbsysschdjobVO.TABLE_UID, inputVO.getJobid());
            }
            //2016.08.01; Sebastian; update PK
            else {
                String originPK = inputVO.getUpdatejobid();
                String newPK = inputVO.getJobid();

                //delete original data
                inputVO.setType(DELETE);
                inputVO.setJobid(originPK);
                operation(inputVO, header);

                //new vo instance & set new PK
                vo = new TbsysschdjobVO();
                inputVO.setJobid(newPK);
            }

            if (vo != null) {
                vo.setjobid(inputVO.getJobid());
                vo.setjobname(inputVO.getJobname());
                vo.setdescription(inputVO.getDescription());
                vo.setprecondition(inputVO.getPrecondition());
                vo.setbeanid(inputVO.getBeanid());
                vo.setclassid(inputVO.getJobid());
                vo.setparameters(inputVO.getParameters());
                vo.setpostcondition(inputVO.getPostcondition());

                dam.update(vo);
            } else {
                throw new APException("ehl_01_common_017"); // 顯示資料不存在
            }

            // 更新TBSYSSCHDJOBCLASS
            setTBSYSSCHDJOBCLASS(inputVO);

//			TbsysschdjobclassVO classvo =(TbsysschdjobclassVO) dam.findByPKey(TbsysschdjobclassVO.TABLE_UID, inputVO.getJobid());
//			
//			if (classvo != null) {
//
//				classvo.setclassname(inputVO.getClassname());
//
//				dam.update(classvo);
//			} else {
//				//不存在則新增
//				classvo = new TbsysschdjobclassVO();
//				classvo.setclassid(inputVO.getJobid());
//				classvo.setclassname(inputVO.getClassname());
//
//				dam.create(classvo);
//			}
        }

        this.sendRtnObject(null);
    }

    /**
     * 對IT02的操作:查詢
     *
     * @param body
     * @param header
     * @throws JBranchException
     */
    public void inquire(Object body, IPrimitiveMap<?> header) throws JBranchException {
        setInputVO(body);
        StringBuilder sql = new StringBuilder();
        Map params = new HashMap();
        configureInquireSql(sql, params);
        outputVO.setResultList(exeQueryForMap(sql.toString(), params));
        this.sendRtnObject(outputVO);
    }

    /**
     * 返回like語法
     **/
    private String like(String param) {
        return "%" + param + "%";
    }

    /**
     * 配置查詢的SQL與參數
     **/
    private void configureInquireSql(StringBuilder sql, Map params) {
        sql.append(" select A.JOBID, A.JOBNAME, A.BEANID, B.CLASSNAME, A.DESCRIPTION, A.PARAMETERS, A.POSTCONDITION, A.PRECONDITION ");
        sql.append(" from TBSYSSCHDJOB A ");
        sql.append(" left join TBSYSSCHDJOBCLASS B on A.CLASSID = B.CLASSID ");
        sql.append(" where 1=1 ");

        if (StringUtils.isNotBlank(inputVO.getJobid())) {
            sql.append("and A.JOBID like :prdId ");
            params.put("prdId", like(inputVO.getJobid()));
        }

        if (StringUtils.isNotBlank(inputVO.getJobname())) {
            sql.append("and A.JOBNAME like :jobName ");
            params.put("jobName", like(inputVO.getJobname()));
        }

        if (StringUtils.isNotBlank(inputVO.getDescription())) {
            sql.append(" and A.DESCRIPTION like :desc ");
            params.put("desc", like(inputVO.getDescription()));
        }

        if (StringUtils.isNotBlank(inputVO.getBeanid())) {
            sql.append(" and A.BEANID like :beanId ");
            params.put("beanId", like(inputVO.getBeanid()));
        }

        if (StringUtils.isNotBlank(inputVO.getParameters())) {
            sql.append("and A.PARAMETERS like :parameters ");
            params.put("parameters", like(inputVO.getParameters()));
        }

        if (StringUtils.isNotBlank(inputVO.getPostcondition())) {
            sql.append(" and A.POSTCONDITION like :postCondition ");
            params.put("postCondition", like(inputVO.getPostcondition()));
        }

        if (StringUtils.isNotBlank(inputVO.getPrecondition())) {
            sql.append(" and A.PRECONDITION like :preCondition ");
            params.put("preCondition", like(inputVO.getPrecondition()));
        }
        sql.append(" order BY A.JOBID");
    }

    /*
     * 新增/修改TBSYSSCHDJOBCLASS
     */
    private void setTBSYSSCHDJOBCLASS(CMMGR007InputVO inputVO) throws JBranchException {
        String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
        QueryConditionIF cond = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();

        sb.append(" select 1 from TBSYSSCHDJOBCLASS where CLASSID=:classID ");
        cond.setObject("classID", inputVO.getJobid());
        cond.setQueryString(sb.toString());

        List<?> result = dam.exeQuery(cond);

        sb = new StringBuffer();
        if (result.isEmpty()) {
            sb.append(" insert into TBSYSSCHDJOBCLASS ");
            sb.append(" (CLASSID, CLASSNAME, BEANNAME, VERSION, CREATOR, CREATETIME, MODIFIER, LASTUPDATE) ");
            sb.append(" values (:classID, :className, :beanName, 1, :loginID, SYSDATE, :loginID, SYSDATE) ");
        } else {
            sb.append(" update TBSYSSCHDJOBCLASS ");
            sb.append(" set CLASSNAME=:className, BEANNAME=:beanName, VERSION=VERSION+1, MODIFIER=:loginID, LASTUPDATE=SYSDATE ");
            sb.append(" where CLASSID=:classID ");
        }

        cond.setObject("classID", inputVO.getJobid());
        cond.setObject("className", inputVO.getJobname());
        cond.setObject("beanName", inputVO.getBeanid());
        cond.setObject("loginID", loginID);

        cond.setQueryString(sb.toString());

        dam.exeUpdate(cond);
    }
}
