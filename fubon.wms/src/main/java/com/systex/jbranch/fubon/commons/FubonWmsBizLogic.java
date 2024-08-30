package com.systex.jbranch.fubon.commons;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.NullPredicate;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.security.impl.JBranchCryptology;

@Component
@Transactional
public class FubonWmsBizLogic extends FubonWmsBizLogicWithoutTranscational {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.systex.jbranch.kgi.commons.LbotBizLogicWithoutTranscational#execute
	 * (java.util.Map, java.util.Map,
	 * com.opensymphony.module.propertyset.PropertySet)
	 */
	@Override
	public void execute(Map transientVars, Map args, PropertySet ps)
			throws WorkflowException {
		super.execute(transientVars, args, ps);
	}
	
	/**
	 * 瀏覽器開新分頁呈現文件
	 */
	public void notifyClientViewDoc(String fileUrl, String docType) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("docUrl", fileUrl);
		params.put("docType", docType);
		this.sendRtnObject("viewDoc", params);
	}
	
	/**
     * 加密字串
     * 覆寫 Bizlog.encrypt
     * 不使用 keystore
     *
     * @param plainText 需要被加密的字串
     * @return 以Hex形式回傳被加密後的字串
     */
    public String encrypt(String text) throws JBranchException {
        return JBranchCryptology.encodePassword(text);
    }
    
    /**
     * 解密字串
     * 覆寫 Bizlog.decrypt
     * 不使用 keystore
     *
     * @param encryptedData Hex形式的被加密的字串
     * @return 解密後的字串
     */
    public String decrypt(String text) throws JBranchException {
    	return JBranchCryptology.decodePassword(text);
    }
    
    /**
     * map getKeyByValue one-to-one
     *
     * @param 
     * @return 
     */
    public <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
        	/**文字對應文字**/
            if (Objects.equals(value, entry.getValue()))
                return entry.getKey();
            /**數字對應數字**/
            if(Objects.equals(value, entry.getKey()))
            	return entry.getKey();
        }
        return null;
    }
    
    /**
     * map getKeyByValue many-to-one
     *
     * @param 
     * @return 
     */
    public <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<T>();
        for (Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }
    
    public <T, E> String joinListByCom(List<Map<T, E>> list, String delim) {
    	StringBuilder sb = new StringBuilder();
    	String loopDelim = "";
    	for(Map<T, E> map : list) {
    		if(CollectionUtils.countMatches(map.values(), NullPredicate.INSTANCE) != map.size()) {
    			sb.append(loopDelim);
        		String loopDelim2 = "";
        		for (Map.Entry<T, E> entry : map.entrySet()) {
        			sb.append(loopDelim2);
        			sb.append(ObjectUtils.toString(entry.getValue()));
        			loopDelim2 = ":";    			
        		}
                loopDelim = delim;
    		}
        }
        return sb.toString();
    }
    
    // utf length
    public int utf_8_length(CharSequence sequence) {
    	if(sequence == null)
    		return 0;
    	int count = 0;
		for (int i = 0, len = sequence.length(); i < len; i++) {
			char ch = sequence.charAt(i);
			if (ch <= 0x7F) {
				count++;
			} else if (ch <= 0x7FF) {
				count += 2;
			} else if (Character.isHighSurrogate(ch)) {
				count += 4;
				++i;
			} else {
				count += 3;
			}
		}
		return count;
    }
    
	// get number of digits from BigDecimal
    public int getNumOfBigDecimal(BigDecimal num) {
    	return num.signum() == 0 ? 1 : num.precision() - num.scale();
    }    

}