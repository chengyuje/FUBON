package com.systex.jbranch.platform.server.integration.message;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.integration.message.config.Configuration;
import com.systex.jbranch.platform.server.integration.message.config.DOMRepository;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.DOMReader;
import org.dom4j.io.DOMWriter;
import org.springframework.integration.annotation.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_FILENAME;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_GROUP_ID;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_ID;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_NAME_BY_VALUE;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_SWITCH;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_VALUE;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_V_FIELD_ID;

/**
 * @author Alex Lin
 * @version 2010/12/20 3:59 PM
 */
@SuppressWarnings("unchecked")
public class HostMessageTransformer {
// ------------------------------ FIELDS ------------------------------

    private static final String HEADER_TRANSACTION_CODE = "TransactionCode";
    protected DOMRepository repository;
    protected String basePath;
    protected String fieldName;
    protected List<Processor> processors;

// -------------------------- OTHER METHODS --------------------------

    public org.w3c.dom.Document transform(org.w3c.dom.Document document, @Header(HEADER_TRANSACTION_CODE) String trxCode)
            throws DocumentException, FileNotFoundException, UnsupportedEncodingException, JBranchException {
        Configuration config = repository.getConfigByTrxCode(trxCode);
        File currentFile = config.getFile();
        Document doc = config.getDocument();
        Node base = doc.selectSingleNode(basePath);
        //unknown root element
        String contentPath = "/*";

        org.dom4j.Document content = new DOMReader().read(document);

        handleContents(content, base, contentPath, currentFile);

        DOMWriter domWriter = new DOMWriter();
        return domWriter.write(content);
    }

    private void handleContents(Node content, Node base, String contentPath, File currentFile) throws JBranchException {
        handleIncludes(content, base, contentPath, currentFile);
        handleSelectCases(content, base, contentPath, currentFile);
        handleFields(content, base, contentPath);
        handleCollections(content, base, contentPath, currentFile);
    }

    private void handleIncludes(Node content, Node base, String contentPath, File currentFile) throws JBranchException {
        List<Element> includes = base.selectNodes("./include");
        for (Element include : includes) {
            String filename = getAttributeValue(include, ATTRIBUTE_FILENAME);
            String nameByValue = getAttributeValue(include, ATTRIBUTE_NAME_BY_VALUE);
            if (StringUtils.isNotBlank(nameByValue)) {
                //determine filename by another field at runtime
                String xpathExpression = contentPath + "/" + nameByValue;
                Node includeValueNode = content.selectSingleNode(xpathExpression);
                String includeValue = getNodeText(includeValueNode);
                filename = filename.replace("%s", includeValue);
            }
            File includeFile = new File(currentFile.getParent(), filename);
            Configuration includeConfig = repository.getConfigByFileName(includeFile.getAbsolutePath());
            Document includeDoc = includeConfig.getDocument();
            Node includeBase = includeDoc.selectSingleNode(basePath);
            handleContents(content, includeBase, contentPath, includeFile);
        }
    }

    private void handleSelectCases(Node content, Node base, String contentPath, File currentFile) throws JBranchException {
        List<Element> selectCases = base.selectNodes("./selectCase");
        for (Element selectCase : selectCases) {
            String switchField = getAttributeValue(selectCase, ATTRIBUTE_SWITCH);
            String xpathExpression = contentPath + "/" + switchField;
            Node switchValueNode = content.selectSingleNode(xpathExpression);
            String switchValue = getNodeText(switchValueNode);
            List<Element> cases = selectCase.selectNodes("./case");
            boolean contains = false;
            for (Element caze : cases) {
                String values = caze.attribute(ATTRIBUTE_VALUE).getValue();
                contains = ArrayUtils.contains(StringUtils.split(values, ","), switchValue);
                if (contains) {
                    handleContents(content, caze, contentPath, currentFile);
                    break;
                }
            }
            if (!contains) {
                Node caseElse = selectCase.selectSingleNode("./caseElse");
                if (caseElse != null) {
                    handleContents(content, caseElse, contentPath, currentFile);
                }
            }
        }
    }

    protected String getNodeText(Node node) {
        return node == null ? null : node.getText();
    }

    protected void handleFields(Node content, Node base, String contentPath) throws JBranchException {
        List<Element> fields = base.selectNodes("./" + fieldName);
        for (Element field : fields) {
            //欄位名稱
            String vFieldID = getAttributeValue(field, ATTRIBUTE_V_FIELD_ID);
            Node contentNode = content.selectSingleNode(contentPath + "/" + vFieldID);

            if (contentNode == null) {
                throw new JBranchException("vFieldID = " + vFieldID + " should not be null");
            }

            String value = contentNode.getText();
            List<Attribute> attributes = field.attributes();

            Map<String, String> attrMap = new HashMap<String, String>(attributes.size());
            for (Attribute attribute : attributes) {
                attrMap.put(attribute.getName(), attribute.getValue());
            }

            for (Processor processor : processors) {
                value = processor.process(value, attrMap);
            }
            contentNode.setText(value);
        }
    }

    private void handleCollections(Node content, Node base, String contentPath, File currentFile) throws JBranchException {
        List<Element> collections = base.selectNodes("./collection");
        for (Element collection : collections) {
            String groupId = getAttributeValue(collection, ATTRIBUTE_GROUP_ID);
            String recordId = getAttributeValue(collection, ATTRIBUTE_ID);
            if (groupId != null) {
                contentPath += "/" + groupId;
            }
            contentPath += "/" + recordId;
            List<Element> contentChildren = content.selectNodes(contentPath);
            for (Element contentChild : contentChildren) {
                //search contentpath = current node (.)
                handleContents(contentChild, collection, ".", currentFile);
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
