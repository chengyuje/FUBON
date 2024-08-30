package com.systex.jbranch.fubon.bth.cam;

import com.systex.jbranch.app.common.fps.table.TBAPI_MPLUS_TRACEVO;
import com.systex.jbranch.fubon.commons.mplus.MPlusInputVO;
import com.systex.jbranch.fubon.commons.mplus.MPlusUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by SebastianWu on 2016/11/8.
 */
@Component("btcam029")
@Scope("prototype")
public class BTCAM029 {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataAccessManager dam;
    @Autowired
    private MPlusUtil mPlusUtil;

    private QueryConditionIF condition;

    /**
     * Batch Enterance
     *
     * @param body
     * @param header
     * @throws Exception
     */
    public void execute(Object body, IPrimitiveMap<?> header) throws Exception {
        condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

        //撈出待分派名單
        String sql = new StringBuffer(
            "SELECT BASE.BRANCH_ID,\n" +
            "       BASE.BRANCH_BOSS,\n" +
            "       BASE.CAMPAIGN_ID,\n" +
            "       BASE.STEP_ID,\n" +
            "       BASE.CAMPAIGN_NAME,\n" +
            "       BASE.CAMPAIGN_DESC,\n" +
            "       BASE.FIRST_CHANNEL,\n" +
            "       BASE.START_DATE,\n" +
            "       BASE.END_DATE,\n" +
            "       COUNT(1) AS COUNTS\n" +
            "  FROM\n" +
            "  (SELECT LEADS.BRANCH_ID,\n" +
            "          MEM.EMP_ID AS BRANCH_BOSS,\n" +
            "          LEADS.CUST_ID,\n" +
            "          CAMP.CAMPAIGN_ID,\n" +
            "          CAMP.STEP_ID,\n" +
            "          CAMP.CAMPAIGN_NAME,\n" +
            "          CAMP.CAMPAIGN_DESC,\n" +
            "          CAMP.FIRST_CHANNEL,\n" +
            "          CAMP.START_DATE,\n" +
            "          CAMP.END_DATE\n" +
            "     FROM TBCAM_SFA_CAMPAIGN CAMP,\n" +
            "          TBCAM_SFA_LEADS LEADS\n" +
            "     LEFT JOIN\n" +
            "    (SELECT MEM.EMP_ID,\n" +
            "            MEM.DEPT_ID\n" +
            "       FROM TBORG_MEMBER_ROLE ROL,\n" +
            "            TBORG_MEMBER MEM\n" +
            "      WHERE ROL.EMP_ID = MEM.EMP_ID\n" +
            "        AND ROLE_ID     IN\n" +
            "      (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID = '011'\n" +
            "      )\n" +
            "    ) MEM\n" +
            "     ON MEM.DEPT_ID        = LEADS.BRANCH_ID\n" +
            "  WHERE CAMP.CAMPAIGN_ID   = LEADS.CAMPAIGN_ID\n" +
            "    AND CAMP.STEP_ID       = LEADS.STEP_ID\n" +
            "    AND LEADS.AO_CODE     IS NULL\n" +
            "  ) BASE\n" +
            "GROUP BY BASE.BRANCH_ID,\n" +
            "         BASE.BRANCH_BOSS,\n" +
            "         BASE.CAMPAIGN_ID,\n" +
            "         BASE.STEP_ID,\n" +
            "         BASE.CAMPAIGN_NAME,\n" +
            "         BASE.CAMPAIGN_DESC,\n" +
            "         BASE.FIRST_CHANNEL,\n" +
            "         BASE.START_DATE,\n" +
            "         BASE.END_DATE\n" +
            "ORDER BY END_DATE"
        ).toString();

        condition.setQueryString(sql);
        List<Map> list = dam.exeQuery(condition);

        List<String> empList = new LinkedList<>();
        List<String> msgList = new LinkedList<>();

        for(Map map : list) {
            String empId = (String) map.get("BRANCH_BOSS");
            empList.add(empId);

            BigDecimal cnt = (BigDecimal) map.get("COUNTS");
            String mplusUid = getMPlusUid(empId);
            MPlusInputVO vo = new MPlusInputVO();

            if (StringUtils.isNotBlank(mplusUid)) {
                //msg content
                String msg = new String("親愛的主管:\n目前您還有 " + cnt + " 件，未審案件，麻煩請您儘速處理！");
                msgList.add(msg);

                JSONObject msgObj = new JSONObject();
                List msgs = new ArrayList();
                msgs.add(msg);
                msgObj.put("msg", msgs);

                //target file
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.write(mplusUid.getBytes());
                baos.write("".getBytes()); //FIXME: 目前測試需使用電話門號
                byte[] bytes = baos.toByteArray();

                MPlusInputVO.BinaryFile file = vo.new BinaryFile();
                file.setFileName("target.txt");
                file.setFileCxt(bytes);

                //send M+ msg
                vo.setTargetType("1");
                vo.setMsgType("T");
                vo.setText(msgObj);
                vo.setTarget(file);

                mPlusUtil.send2MPlus(vo);
            }else{
                logger.error("[M+] 員編: "+empId+", 無法對應之M+ UID");
            }
        }
        TBAPI_MPLUS_TRACEVO tracevo = new TBAPI_MPLUS_TRACEVO();
        tracevo.setEMP_ID(StringUtils.join(empList, ","));
        tracevo.setCONTENT(StringUtils.join(msgList, "\n\\\n"));
        dam.create(tracevo);
    }

    /**
     * use empid to find m+ uid
     *
     * @param empId
     * @return
     * @throws JBranchException
     */
    private String getMPlusUid(String empId) throws JBranchException {
        String sql = new StringBuffer(
            "SELECT MPLUS_UID" +
            "  FROM TBAPI_MPLUS " +
            " WHERE EMP_NUMBER = :EMP_NUMBER"
        ).toString();

        condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        condition.setQueryString(sql);
        condition.setObject("EMP_NUMBER", empId);
        List<Map> result = dam.exeQuery(condition);

        String mplusUid = null;
        if(CollectionUtils.isNotEmpty(result)){
            mplusUid = (String) result.get(0).get("MPLUS_UID");
        }

        return mplusUid;
    }
}
