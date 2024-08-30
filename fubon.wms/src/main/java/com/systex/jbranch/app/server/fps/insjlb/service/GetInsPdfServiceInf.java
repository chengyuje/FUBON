package com.systex.jbranch.app.server.fps.insjlb.service;

import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPdfInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPdfOutputVO;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**取得保險百科條款與說明檔(url)*/
public interface GetInsPdfServiceInf {
	public static final String CASE_METHOD_NAME = "getInsPdfCase";
	public static final String LST_PDF_MAP = "LST_PDF_MAP";
	public static final String OUT_PUT_VO = "OUT_PUT_VO";

	public static final String BINARY_DATA = "BINARY_DATA";
	public static final String VICEFILE = "VICEFILE";
	public static final String VERSION = "VERSION";
	
	public static final String PRD_ID = "PRD_ID";
	public static final String PRD_NAME = "PRD_NAME";
	public static final String CONTENT_FILENAME = "CONTENT_FILENAME";
	public static final String CLAUSE_FILENAME = "CLAUSE_FILENAME";
	public static final String URL1 = "URL1";
	public static final String URL2 = "URL2";

	/**取得保險百科條款與說明檔：
	 * @param GetInsPdfInputVO：<br>
	 * --- List<String> lstInsProd : 險種清單(資訊源的商品ID)依照順序<br>
	 * --- String pdfType : 檔案種類：1.條款2.說明3.列印用(同時含條款&說明)4.條款+說明<p>
	 */
	public GetInsPdfOutputVO getInsPdf(GetInsPdfInputVO inputVo) throws JBranchException;
}
