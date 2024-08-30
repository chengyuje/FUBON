package com.systex.jbranch.platform.server.integration.message.legacy.source;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.integration.message.legacy.SourceParser;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.ArrayUtils;

import java.util.Map;

/**
 * @author Alex Lin
 * @version 2011/03/14 5:09 PM
 */
public class DelimiterSourceParser extends SourceParser {
// ------------------------------ FIELDS ------------------------------

    private int position;
    private byte delimiter;

// -------------------------- OTHER METHODS --------------------------

    @Override
    public String getNextContent(byte[] source, Map attrMap) throws JBranchException {
        int endPosition = ArrayUtils.indexOf(source, delimiter, position);
        byte[] content = ArrayUtils.subarray(source, position, endPosition);
        position = ++endPosition;
        return encoder.decode(content);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setDelimiter(String hexDelimiter) throws DecoderException {
        this.delimiter = Hex.decodeHex(hexDelimiter.toCharArray())[0];
    }
}
