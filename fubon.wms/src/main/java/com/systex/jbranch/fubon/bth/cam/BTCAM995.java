package com.systex.jbranch.fubon.bth.cam;

import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LEADSVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by SebastianWu on 20017/01/06.
 */
@Component("btcam995")
@Scope("prototype")
public class BTCAM995 extends BizLogic{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataAccessManager dam;

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
        List<Map> list = findDistributeList();

        //清空需重新分配名單的EMP_ID與AO_CODE
        reSetListStatus(list);
    }

    /**
     * 清空需重新分配名單的EMP_ID與AO_CODE
     *
     * @param list
     */
    private void reSetListStatus(List<Map> list) throws DAOException {
        for(Map<String, Object> map : list) {
            String leadID = (String) map.get("SFA_LEAD_ID");
            //search row data by SFA_LEAD_ID
            TBCAM_SFA_LEADSVO vo = new TBCAM_SFA_LEADSVO();
            vo.setSFA_LEAD_ID(leadID);

            vo = (TBCAM_SFA_LEADSVO) dam.findByPKey(vo.getTableuid(), vo.getSFA_LEAD_ID());

            //clear EMP_ID and AO_CODE
            vo.setEMP_ID(null);
            vo.setAO_CODE(null);

            dam.update(vo);
        }
    }

    /**
     * 撈出待分派名單
     *
     * @return
     * @throws JBranchException
     */
    private List<Map> findDistributeList() throws JBranchException {

        String sql = new StringBuffer(
                "SELECT LEADS.SFA_LEAD_ID " +
                        "  FROM TBCAM_SFA_LEADS LEADS " +
                        "  LEFT  " +
                        "  JOIN (SELECT CAMP.CAMPAIGN_ID, CAMP.STEP_ID, FIRST_CHANNEL, CAMP.LEAD_SOURCE_ID,  " +
                        "         NVL((SELECT PARAM_CODE  " +
                        "                FROM TBSYSPARAMETER  " +
                        "               WHERE PARAM_TYPE = 'CAM.CHANNEL_MAPPING'  " +
                        "                 AND PARAM_NAME = CAMP.FIRST_CHANNEL), '002') AS FIRST_CHANNEL_GROUP " +
                        "          FROM TBCAM_SFA_CAMPAIGN CAMP " +
                        "         ) LAP " +
                        "    ON LAP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID " +
                        "   AND LAP.STEP_ID = LEADS.STEP_ID " +
                        "WHERE LEADS.LEAD_STATUS < '03' " +
                        "AND DISP_FLAG = 'S' " +
                        "AND LAP.LEAD_SOURCE_ID <> '05' " +
                        "AND LEADS.EMP_ID <> (SELECT AOC.EMP_ID FROM TBCRM_CUST_MAST CM LEFT JOIN TBORG_SALES_AOCODE AOC ON CM.AO_CODE = AOC.AO_CODE WHERE CM.CUST_ID = LEADS.CUST_ID) " +
                        "AND NOT EXISTS(  " +
                        "  SELECT 1 " +
                        "    FROM TBORG_MEMBER_ROLE MR " +
                        "    LEFT JOIN TBSYSSECUROLPRIASS IASS ON MR.ROLE_ID = IASS.ROLEID " +
                        "    LEFT JOIN TBORG_ROLE R ON MR.ROLE_ID = R.ROLE_ID " +
                        "   WHERE MR.EMP_ID = LEADS.EMP_ID " +
                        "     AND IASS.PRIVILEGEID = FIRST_CHANNEL_GROUP " +
                        "     AND 1 = CASE IASS.PRIVILEGEID  " +
                        "                  WHEN '002'  " +
                        "                  THEN CASE MR.ROLE_ID  " +
                        "                            WHEN LAP.FIRST_CHANNEL  " +
                        "                            THEN 1  " +
                        "                            ELSE 0 " +
                        "                        END " +
                        "                  ELSE 1 " +
                        "              END " +
                        ")"
        ).toString();

        condition.setQueryString(sql);

        return dam.exeQuery(condition);
    }
}
