package com.systex.jbranch.platform.server.eclient.conversation;

import java.util.Enumeration;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.server.conversation.TiaHelperIF;
import com.systex.jbranch.platform.server.conversation.TiaIF;
import com.systex.jbranch.platform.server.conversation.message.MapTia;
import com.systex.jbranch.platform.server.conversation.message.EnumTiaHeader;
import com.systex.jbranch.platform.common.util.ThreadDataPool;

public class TIA implements TiaHelperIF {

	private MapTia tiaData = new MapTia();
	/**
	 * 由 PipeLine 呼叫，傳入 Client 上傳的 TIA
	 * 
	 * @param tiaHeader
	 *            上傳的 Header
	 * @param tiaData
	 *            上傳的 Data
	 * @throws Exception
	 * @throws
	 * @see
	 * @since 1.01
	 */
	@SuppressWarnings("unchecked")
	public void parseTIA(HttpServletRequest req) throws Exception {

		// 取得Header存入tiaHeaderMap
		if (req.getContentType() == null
				|| req.getContentType()
						.indexOf(Conversation.TIA_HEAD) == -1) {
			throw new Exception("TIA Header Parse Error");
		}
		String header = req.getContentType();
		//System.out.println("Header=" + header);
		header = header.substring(header.indexOf("eClient"));
		String token = null;
		String[] strMap = null;
		StringTokenizer st = new StringTokenizer(header, "&");
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if (token.indexOf("=") == -1) {
				throw new Exception("TIA Header Parse Error");
			}
			strMap = token.split("=");
			tiaData.Headers().put(strMap[0], strMap[1]);
			//System.out.println(strMap[0] + "=" + strMap[1]);
		}

		// 取得Data存入tiaHeaderMap
		Enumeration e = req.getParameterNames();
		String key = null;
		while (e.hasMoreElements()) {
			key = (String) e.nextElement();
			tiaData.Body().put(key, req.getParameter(key));
		}	
	}

	/**
	 * 由 PipeLine 呼叫，取得實際的 UUID 值
	 * 
	 * @throws
	 * @see
	 * @since 1.01
	 */
	public UUID getUUID() {
		// 依實際需求再改		
		UUID obj = new UUID();
		obj.setBranchID(tiaData.Headers().getStr(EnumTiaHeader.BranchID));
		obj.setWsId(tiaData.Headers().getStr(EnumTiaHeader.WsID));
		obj.setSectionID(tiaData.Headers().getStr(EnumTiaHeader.SectionID));
		ThreadDataPool.setData(ThreadDataPool.KEY_UUID, obj);
		return obj;		
	}

	/**
	 * 
	 * @param 
	 * @return 根據 Request 內的 Header 資料，取得此次交易代號 
	 * @throws
	 * @see
	 * @since 1.01
	 */		
	public String getTxnCode() {
		return tiaData.Headers().getStr(EnumTiaHeader.TxnCode);
	}
	/**
	 * 
	 * @param 
	 * @return 根據 Request 內的 Header 資料，取得此次交易 Biz 代號 
	 * @throws
	 * @see
	 * @since 1.01
	 */	
	public String getBizCode() {
		return tiaData.Headers().getStr(EnumTiaHeader.BizCode);
	}

	public String changeClientTransaction()
	{
		return tiaData.Headers().getStr(EnumTiaHeader.ClientTransaction);
	}


	/**
	 * 由 LocalAP 呼叫，取出 Data 中適當的資料
	 * 
	 * @param Key
	 *            欲取出資料的對應 Key 值
	 * @throws
	 * @see
	 * @since 1.01
	 */
	/*
	public String getData(String Key) {
	 	if (tiaDataMap.containsKey(Key)) {
	 		return tiaDataMap.get(Key).toString();
	 	}
			return null;
	}
	 */
	/**
	 * 由入口程式呼叫，取出並放至 DataManager
	 * 
	 * @param 
	 * @throws
	 * @see
	 * @since 1.01
	 */	
	//public Map<String, String> getInputFields() {
	//	return tiaDataMap;
	//}
	public TiaIF getTia()
	{
		return tiaData;
	}
}
