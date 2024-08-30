package com.systex.jbranch.platform.server.integration.message.legacy;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.integration.message.Encoder;
import com.systex.jbranch.platform.server.integration.message.Processor;
import com.systex.jbranch.platform.server.integration.message.config.Configuration;
import com.systex.jbranch.platform.server.integration.message.config.DOMRepository;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.integration.annotation.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_FILENAME;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_ID;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_NAME_BY_VALUE;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_SWITCH;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_VALUE;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_V_FIELD_ID;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.TAG_COLLECTION;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.TAG_INCLUDE;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.TAG_SELECT_CASE;

/**
 * Support legacy host message, long string
 * This class is not thread-safe
 *
 * @author Alex Lin
 * @version 2011/02/14 3:51 PM
 */
@SuppressWarnings("unchecked")
public class LegacyHostMessageInTransformer {
// ------------------------------ FIELDS ------------------------------

    private static final String HEADER_TRANSACTION_CODE = "TransactionCode";
    protected DOMRepository repository;
    protected String basePath;
    protected String fieldName;
    protected List<Processor> processors;
    protected List<String> output = new ArrayList<String>();
    private Encoder encoder;

// -------------------------- OTHER METHODS --------------------------

    public byte[] transform(Object content, @Header(HEADER_TRANSACTION_CODE) String trxCode) throws JBranchException {
        Configuration config = repository.getConfigByTrxCode(trxCode);
        File currentFile = config.getFile();
        Document doc = config.getDocument();
        Node base = doc.selectSingleNode(basePath);

        handleContents(new WrapDynaBean(content), base, currentFile);

        int length = 0;
        List<byte[]> tempList = new ArrayList<byte[]>(output.size());
        for (String snippet : output) {
            byte[] encodedSnippet = encoder.encode(snippet);
            tempList.add(encodedSnippet);
            length += encodedSnippet.length;
        }

        byte[] result = new byte[length];
        int position = 0;
        for (byte[] encodedSnippet : tempList) {
            int snippetLength = encodedSnippet.length;
            System.arraycopy(encodedSnippet, 0, result, position, snippetLength);
            position += snippetLength;
        }

        return result;
    }

    private void handleContents(DynaBean content, Node base, File currentFile) throws JBranchException {
        List<Element> children = base.selectNodes("./*");
        for (Element child : children) {
            String tagName = child.getQName().getName();
            if (StringUtils.equals(tagName, fieldName)) {
                handleField(content, child);
            }
            else if (StringUtils.equals(tagName, TAG_INCLUDE)) {
                handleInclude(content, child, currentFile);
            }
            else if (StringUtils.equals(tagName, TAG_COLLECTION)) {
                handleCollection(content, child, currentFile);
            }
            else if (StringUtils.equals(tagName, TAG_SELECT_CASE)) {
                handleSelectCase(content, child, currentFile);
            }
            else {
                throw new JBranchException("Unsupported tag name : " + tagName);
            }
        }
    }

    protected void handleField(DynaBean content, Element field) throws JBranchException {
        //欄位名稱
        String vFieldID = getAttributeValue(field, ATTRIBUTE_V_FIELD_ID);
        String value = (String) content.get(vFieldID);
        List<Attribute> attributes = field.attributes();

        Map<String, String> attrMap = new HashMap<String, String>(attributes.size());
        for (Attribute attribute : attributes) {
            attrMap.put(attribute.getName(), attribute.getValue());
        }

        for (Processor processor : processors) {
            value = processor.process(value, attrMap);
        }
        output.add(value);
    }

    private void handleInclude(DynaBean content, Element include, File currentFile) throws JBranchException {
        String filename = getAttributeValue(include, ATTRIBUTE_FILENAME);
        String nameByValue = getAttributeValue(include, ATTRIBUTE_NAME_BY_VALUE);
        if (StringUtils.isNotBlank(nameByValue)) {
            //determine filename by another field at runtime
            String includeValue = (String) content.get(nameByValue);
            filename = filename.replace("%s", includeValue);
        }
        File includeFile = new File(currentFile.getParent() + "/" + filename);
        Configuration includeConfig = repository.getConfigByFileName(includeFile.getAbsolutePath());
        Document includeDoc = includeConfig.getDocument();
        Node includeBase = includeDoc.selectSingleNode(basePath);
        handleContents(content, includeBase, includeFile);
    }

    private void handleCollection(DynaBean content, Element collection, File currentFile) throws JBranchException {
        //String groupId = getAttributeValue(collection, ATTRIBUTE_GROUP_ID);
        String recordId = getAttributeValue(collection, ATTRIBUTE_ID);
        //if (groupId != null) {
        //    contentPath += "/" + groupId;
        //}
        List contentChildren = (List) content.get(recordId);
        for (Object contentChild : contentChildren) {
            //search contentpath = current node (.)
            handleContents(new WrapDynaBean(contentChild), collection, currentFile);
        }
    }

    private void handleSelectCase(DynaBean content, Element selectCase, File currentFile) throws JBranchException {
        String switchField = getAttributeValue(selectCase, ATTRIBUTE_SWITCH);
        String switchValue = (String) content.get(switchField);
        List<Element> cases = selectCase.selectNodes("./case");
        boolean contains = false;
        for (Element caze : cases) {
            String values = caze.attribute(ATTRIBUTE_VALUE).getValue();
            contains = ArrayUtils.contains(StringUtils.split(values, ","), switchValue);
            if (contains) {
                handleContents(content, caze, currentFile);
                break;
            }
        }
        if (!contains) {
            Node caseElse = selectCase.selectSingleNode("./caseElse");
            if (caseElse != null) {
                handleContents(content, caseElse, currentFile);
            }
        }
    }

    protected String getAttributeValue(Element field, String attributeName) {
        Attribute attribute = field.attribute(attributeName);
        return attribute == null ? null : attribute.getValue();
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setProcessors(List<Processor> processors) {
        this.processors = processors;
    }

    public void setRepository(DOMRepository repository) {
        this.repository = repository;
    }
}
