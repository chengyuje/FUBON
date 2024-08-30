package com.systex.jbranch.fubon.commons.esb.dao;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.BOND_RESULT_SEARCH;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.OBU_BOND_RESULT_SEARCH;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.app.server.fps.sot330.SOT330InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc3.NJBRVC3InputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Esb;

/**
 * 海外債成交結果查詢
 * SamTu
 * 2020.11.05
 */
@Repository
public class NJBRVC3DAO {

    public List<ESBUtilOutputVO> search(SOT330InputVO inputVO) throws Exception {
        List<ESBUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.ESB, inputVO.getIsOBU() ? OBU_BOND_RESULT_SEARCH : BOND_RESULT_SEARCH )
                .setRequest(Esb.createRequestVO()
                        .setModule("NJBRVC3DAO.search")
                        .setTxHeadVO(Esb.createTxHeadVO())
                        .setNjbrvc3InputVO(getNJBRVC3InputVO(inputVO)))
                .work()
                .getResponse(list);
        return list;
    }

	private NJBRVC3InputVO getNJBRVC3InputVO(SOT330InputVO inputVO) {
		SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");
		NJBRVC3InputVO TxInputVO = new NJBRVC3InputVO();
		TxInputVO.setCustId(inputVO.getCustID());
		TxInputVO.setTxnType(inputVO.getTradeType());
		TxInputVO.setStartDate(changeACtoROC(sdf.format(inputVO.getsDate())));
		TxInputVO.setEndDate(changeACtoROC(sdf.format(inputVO.geteDate())));
		if(StringUtils.isNotBlank(inputVO.getProdID())){
			TxInputVO.setBondNo(inputVO.getProdID());
		}
		return TxInputVO;
		
	}
	/**
	 * 西元轉民國
	 */
	private String changeACtoROC(String AC){
		String MMdd = AC.substring(4);
        int yyyytemp = Integer.parseInt(AC.substring(0, 4)) - 1911;
        String yyyy = yyyytemp >= 1000 ? String.valueOf(yyyytemp) : "0" + String.valueOf(yyyytemp);
        		
		return yyyy + MMdd;
		
	}

}
