package com.systex.jbranch.platform.server.integration.message.legacy.source;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.integration.message.legacy.SourceParser;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Map;

import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_V_FIELD_LEN;

/**
 * @author Alex Lin
 * @version 2011/03/14 5:02 PM
 */
class FixedLengthSourceParser extends SourceParser {
// ------------------------------ FIELDS ------------------------------

    private int position;

// -------------------------- OTHER METHODS --------------------------

    public String getNextContent(byte[] source, Map attrMap) throws JBranchException {
        int length = NumberUtils.toInt((String) attrMap.get(ATTRIBUTE_V_FIELD_LEN), 1);
        int endPosition = position + length;
        byte[] content = ArrayUtils.subarray(source, position, endPosition);
        position = endPosition;
        return encoder.decode(content);
    }
}
