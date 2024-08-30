package com.systex.jbranch.fubon.commons.cbs.vo._000454_032081;

import org.apache.commons.lang.StringUtils;

import com.systex.jbranch.fubon.commons.cbs.vo.basic.CbsSpec;

/**
 * 000454_032081 規格
 */
public class Spec000454_032081 extends CbsSpec {
	@Override
	/*
	 * 0188: 0479: 無效的選擇 (non-Javadoc)
	 * 0344：已無下一筆資料可顯示
	 *
	 * @see com.systex.jbranch.fubon.commons.cbs.vo.basic.CbsSpec#process()
	 */
	public void process() throws Exception {
		if ("0188".equals(getErrorCode()) || "0479".equals(getErrorCode()) || "0344".equals(getErrorCode())) {
			setHasCustomErrorProcess(true);
		} else if (StringUtils.isNotBlank(getTxData().getCbs032081OutputVO().getINCT_SOC())) {

			String nextKey = getNextKey(getTxData().getCbs032081OutputVO());
			if (!nextKey.substring(0, 6).equals("999999")) {
				setMultiple(true);
				request.getCbs000454InputVO().setNextKey(nextKey);
			} else {
				setMultiple(false);
			}

		}
	}

	private String getNextKey(CBS032081OutputVO cbs032081OutputVO) {

		StringBuffer sql = new StringBuffer();
		sql.append(cbs032081OutputVO.getINCT_SOC()).append(cbs032081OutputVO.getINCT_ACC()).append(cbs032081OutputVO.getINCT_RECNO()).append(cbs032081OutputVO.getINCT_DATE()).append(cbs032081OutputVO.getINCT_TIME()).append(cbs032081OutputVO.getSUBCNT());
		return sql.toString();

	}
}
