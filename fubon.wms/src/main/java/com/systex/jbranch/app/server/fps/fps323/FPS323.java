package com.systex.jbranch.app.server.fps.fps323;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.systex.jbranch.app.server.fps.fpsjlb.FPSJLB;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 特定目的投資組合計劃(STEP 2)績效計算
 * 
 * @author Geoffrey
 */
@Component("fps323")
@Scope("request")
public class FPS323 extends FubonWmsBizLogic {
  @Autowired
  @Qualifier("fpsjlb")
  private FPSJLB fpsjlb;
  SimpleDateFormat sdf = new SimpleDateFormat("yyMMmmss");
  Logger logger = LoggerFactory.getLogger(FPS323.class);

  /**
   * 投組試算
   * 
   * @param body
   * @param header
   * @throws Exception
   */
  @SuppressWarnings({ "rawtypes" })
  public void inquire(Object body, IPrimitiveMap header) throws Exception {
    FPS323InputVO inputVO = (FPS323InputVO) body;
    FPS323OutputVO outputVO = new FPS323OutputVO();
    try {
    	
    	DataAccessManager dam = this.getDataAccessManager();
        
    	// 前置作業準備
    	Map<String, Object> afterPrepareDataMap = FPSUtils.beforeDoPortRtnSim(dam, inputVO.getRet(), inputVO.getCustID(), inputVO.getPlanID());
    	logger.info(new Gson().toJson(inputVO.getRet()));
        logger.info("GET SPP_ID: " + inputVO.getPlanID());
        
        // 取得剩餘月份
        int remainingMonth = FPSUtils.getRemainingMonth(FPSUtils.getPurchasedDate(dam, inputVO.getPlanID()), inputVO.getYear());
        logger.info("GET REMAINING MONTH: " + remainingMonth);
        
        // 取得分組資料
        List<Map<String, Object>>[] purchasedListArray = (List<Map<String, Object>>[])afterPrepareDataMap.get("purchasedListArray");
        
        // 取得單筆總投入 & 定額總投入 
        BigDecimal[] purchasedValueArray = (BigDecimal[])afterPrepareDataMap.get("purchasedValueArray");
        BigDecimal totalMoney = purchasedValueArray[0].add(purchasedValueArray[1].multiply(new BigDecimal(remainingMonth)));
        
        logger.info("GET SINGLE & PERIOD TOTAL VALUE: " + totalMoney);
        
        String[] invalid = FPSUtils.checkValidYear(dam, inputVO.getRet(), 3, null);
        if (invalid.length == 0) {
          GenericMap pt = fpsjlb.getPortRtnSim(purchasedListArray, purchasedValueArray, inputVO.getYear(), 36, 120, remainingMonth);
        logger.debug("FPS323 result: " + Arrays.deepToString((Double[]) pt.get("resultArray")));
        ((Double[])pt.get("resultArray"))[3] = totalMoney.doubleValue();
        for (Double oneD : (Double[]) pt.get("resultArray")) {
          if (Double.isNaN(oneD)) {
            sendRtnObject("");
            return;
          }
        }
        sendRtnObject(pt.get("resultArray"));
      } else {
        StringBuffer sbf = new StringBuffer();
        for (String inv : invalid) {
          for (Map<String, Object> map : inputVO.getRet()) {
            if (inv.equals(map.get("PRD_ID").toString()) || (map.get("TARGETS") != null && map.get("TARGETS").toString().indexOf(inv) >= 0)) {
              sbf.append((sbf.length() > 0 ? "," : "") + inv + map.get("PRD_CNAME").toString());
            }
          }
        }
        outputVO.setErrCode(sbf.toString());
        sendRtnObject(outputVO);
      }

    } catch (Exception e) {
      e.printStackTrace();
      logger.error("FPS323 error: " + e.toString());
      outputVO.setErrCode("error");
      sendRtnObject(outputVO);
      
    }
  }

}
