package com.systex.jbranch.app.server.fps.crm828;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.GD320140_CUST_DATA;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.birt.report.model.api.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.gd320140.GD320140InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.gd320140.GD320140OutputDetailsVO;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * crm828
 * 
 * @author moron, Walalala
 * @date 2016/06/14, 2016/12/07
 * @spec null
 * 
 * @author sam
 * @date 20180207
 * @spec 黃金存摺電文來源更換 (GD320140(原SD120140))
 * 
 * 
 */
@Component("crm828")
@Scope("request")
public class CRM828 extends EsbUtil {
	/* const */
	private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
	private String thisClaz = this.getClass().getSimpleName() + ".";

	@Autowired
	private GD320140InputVO gd320140InputVO;

	public void inquire(Object body, IPrimitiveMap<?> header) throws Exception {
		CRM828InputVO inputVO = (CRM828InputVO) body;
		CRM828OutputVO return_VO = new CRM828OutputVO();
		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, GD320140_CUST_DATA);
		esbUtilInputVO.setModule(thisClaz + new Object() {		}.getClass().getEnclosingMethod().getName());

		// head
		setTxHead(esbUtilInputVO);
		// body
		setTxBody(inputVO, esbUtilInputVO);

		// 發送電文取得回傳結果
		return_VO.setResultList(getEsbResult(esbUtilInputVO));
		this.sendRtnObject(return_VO);
	}

	/** 發送電文取得回傳結果 */
	private List<CustAssetGD320140> getEsbResult(ESBUtilInputVO esbUtilInputVO) throws Exception {
		List<CustAssetGD320140> results = new ArrayList<CustAssetGD320140>();
		for (ESBUtilOutputVO esbUtilOutputVO : send(esbUtilInputVO)) {
			if (StringUtil.isEqual(esbUtilOutputVO.getGd320140OutputVO().getSTYLE(), "D001")) {
				for (GD320140OutputDetailsVO detail : esbUtilOutputVO.getGd320140OutputVO().getDetails()) {
//					if (decimalPoint(detail.getBAL(), 2).compareTo(new BigDecimal(0)) != 1)
//						continue; // decimalPoint(detail.getBAL(), 2).intValue()
//									// <= 0
					CustAssetGD320140 asset = new CustAssetGD320140();
					asset.setACNO(detail.getACNO().trim());
					asset.setBAL(decimalPoint(detail.getBAL(), 2));
					asset.setCUR(detail.getCUR().trim());
					asset.setBRD_PRICE(decimalPoint(detail.getBRD_PRICE(), 2));
					asset.setAVG_COST(decimalPoint(detail.getAVG_COST(), 2));
					asset.setP_VALUE(decimalPoint(detail.getP_VALUE(), 2));
					asset.setINV_AMT(decimalPoint(detail.getINV_AMT(), 2));
					asset.setYIELD_AMT(decimalPoint(detail.getYIELD_AMT(), 2));
					asset.setYIELD_S(detail.getYIELD_S().trim());
					asset.setYIELD(decimalPoint(detail.getYIELD(), 2));
					asset.setM_STS(detail.getM_STS().trim());
					asset.setM_DATE(dateExpression(detail.getM_DATE()));
					asset.setM_AMT(decimalPoint(detail.getM_AMT(), 2));

					results.add(asset);
				}
			}
		}
		return results;
	}

	/** 設定電文body */
	private void setTxBody(CRM828InputVO inputVO, ESBUtilInputVO esbUtilInputVO) {
		gd320140InputVO.setSOURCE("F");
		gd320140InputVO.setCATEGORY("1");
		gd320140InputVO.setFUNC_COD("05");
		gd320140InputVO.setIDNO(inputVO.getCust_id());// 客戶ID
		// gd320140InputVO.setIDNO("L122195624");
		// gd320140InputVO.setNAME_COD("0001");
		gd320140InputVO.setConvey_END("Y");

		esbUtilInputVO.setGd320140InputVO(gd320140InputVO);
	}

	/** 設定電文head */
	private void setTxHead(ESBUtilInputVO esbUtilInputVO) throws Exception {
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);
	}

	private String dateExpression(String dateStr) {
		int index = 0;
		StringBuffer buffer = new StringBuffer();
		for (Character c : dateStr.toCharArray()) {
			index++;
			if (c.equals('Y'))
				buffer.append(index + ",");
		}
		if (buffer.indexOf(",") == -1)
			return buffer.toString();
		return buffer.substring(0, buffer.lastIndexOf(",")).toString();
	}

}