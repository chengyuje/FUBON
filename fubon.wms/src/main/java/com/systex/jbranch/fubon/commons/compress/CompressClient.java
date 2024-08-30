package com.systex.jbranch.fubon.commons.compress;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
@SuppressWarnings({"rawtypes", "unchecked"})
class ParamForCompress {
	public static XmlInfo xmlInfo = new XmlInfo();
	private Map srcPath;
	private Map tarPath;
	private Map basisSrc;
	private Map basisTar;
	public ParamForCompress() throws JBranchException{
		srcPath = getParamMap("COMPRESS.SOURCE_PATH");
		tarPath = getParamMap("COMPRESS.TARGET_PATH");
		basisSrc = getParamMap("COMPRESS.BASIS_SOURCE");
		basisTar = getParamMap("COMPRESS.BASIS_TARGET");	
	}
	
	/**取得資料庫共用參數的MAP*/
	private Map<String, String> getParamMap(String param) throws JBranchException {
		Map m = xmlInfo.doGetVariable(param, FormatHelper.FORMAT_3);
		if (m != null) return m;
		else throw new JBranchException("請確認參數Parameter設定是否正確!");
	}
	public Map getSrcPath() {
		return srcPath;
	}
	public Map getTarPath() {
		return tarPath;
	}
	public Map getBasisSrc() {
		return basisSrc;
	}
	public Map getBasisTar() {
		return basisTar;
	}
}

/**
 * 壓縮客戶端 (提供給排程使用)
 * @author Eli
 * @date 2017/12/15
 *
 */
@Component("compressClient")
@Scope("prototype")
@SuppressWarnings({"rawtypes", "unchecked"})
public class CompressClient extends BizLogic {
	/**訊息樣板*/
	private String msgTemplate = "●【無法執行壓縮該目錄  %s，%s。】";
	
	/**從資料庫撈取壓縮資訊，進行壓縮處理*/
	public void compress() throws JBranchException {
		try {
			doCompress(new ParamForCompress());
		} catch (JBranchException e) {
			throw new JBranchException(e.getMessage());
		} catch (Exception e) {
			throw new JBranchException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**壓縮邏輯*/
	private void doCompress(ParamForCompress p) throws APException {
		CompressService service = CompressService.getService();
		Iterator srcIt = p.getSrcPath().keySet().iterator();
		StringBuffer msg = new StringBuffer();
		HashMap infoMap = null;
		while (srcIt.hasNext()){
			String src = srcIt.next().toString();
			infoMap = getCompressInfoMap((String)p.getSrcPath().get(src),
										 (String)p.getTarPath().get(src),
										 (String)p.getBasisSrc().get(src),
										 (String)p.getBasisTar().get(src));
			if(unable(infoMap, msg)) continue;
			try {
				service.compress(infoMap);
			} catch (Exception e) {
				msg.append(String.format(msgTemplate, infoMap.get("SOURCE_PATH"), "發生錯誤 : " + e.getMessage()));
			}
		}
		checkMsg(msg.toString());
	}

	/**判斷是否可以進行壓縮*/
	private boolean unable(HashMap infoMap, StringBuffer msg) {
		if (infoMap.get("TARGET_PATH") == null) {
			msg.append(String.format(msgTemplate,  infoMap.get("SOURCE_PATH"), "沒有設定壓縮目的地"));
			return true;
		}
		
		if (infoMap.get("BASIS_SRC") == null) {
			msg.append(String.format(msgTemplate,  infoMap.get("SOURCE_PATH"), "沒有設定壓縮判斷條件"));
			return true;
		}
		
		if (infoMap.get("BASIS_TAR") == null) {
			msg.append(String.format(msgTemplate,  infoMap.get("SOURCE_PATH"), "沒有設定刪除判斷條件"));
			return true;
		}
		return false;
	}

	/**將info重新整理成{@link CompressService#compress(Map)}的參數*/
	private HashMap getCompressInfoMap(String source, String target, String basisSrc, String basisTar) {
		HashMap infoMap = new HashMap();
		infoMap.put("SOURCE_PATH", source);
		infoMap.put("TARGET_PATH", target);
		infoMap.put("BASIS_SRC", basisSrc);
		infoMap.put("BASIS_TAR", basisTar);
		return infoMap;
	}

	/**檢查是否有訊息產生，有則丟出錯誤*/
	private void checkMsg(String msg) throws APException {
		if (msg.length() > 0) {
			throw new APException(msg);
		}
	}
}
