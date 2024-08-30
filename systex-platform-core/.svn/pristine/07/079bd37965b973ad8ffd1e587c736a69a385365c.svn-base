package com.systex.jbranch.platform.server.integration.message.legacy;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.integration.message.Encoder;
import com.systex.jbranch.platform.server.integration.message.Processor;
import com.systex.jbranch.platform.server.integration.message.config.Configuration;
import com.systex.jbranch.platform.server.integration.message.config.DOMRepository;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaMap;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.integration.annotation.Header;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_FILENAME;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_ID;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_NAME_BY_VALUE;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_SWITCH;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_VALUE;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_V_FIELD_ID;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_V_FIELD_LEN;
import static com.systex.jbranch.platform.server.integration.message.HostMessageConstants.ATTRIBUTE_V_REPEAT;
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
public class LegacyHostMessageOutTransformer {
// ------------------------------ FIELDS ------------------------------

    private static final String HEADER_TRANSACTION_CODE = "TransactionCode";
    protected DOMRepository repository;
    protected String basePath;
    protected String fieldName;
    protected List<Processor> processors;
    private SourceParser parser;

// -------------------------- OTHER METHODS --------------------------

    public <T> T transform(byte[] source, @Header(HEADER_TRANSACTION_CODE) String trxCode, Class<T> clazz) throws JBranchException {
        Configuration config = repository.getConfigByTrxCode(trxCode);
        File currentFile = config.getFile();
        Document doc = config.getDocument();
        Node base = doc.selectSingleNode(basePath);

        T instance = getInstance(clazz);
        WrapDynaBean content = new WrapDynaBean(instance);

        handleContents(content, source, base, currentFile);

        return instance;
    }

    private void handleContents(WrapDynaBean content, byte[] source, Node base, File currentFile) throws JBranchException {
        List<Element> children = base.selectNodes("./*");
        for (Element child : children) {
            String tagName = child.getQName().getName();
            if (StringUtils.equals(tagName, fieldName)) {
                handleField(content, source, child);
            }
            else if (StringUtils.equals(tagName, TAG_INCLUDE)) {
                handleInclude(content, source, child, currentFile);
            }
            else if (StringUtils.equals(tagName, TAG_COLLECTION)) {
                handleCollection(content, source, child, currentFile);
            }
            else if (StringUtils.equals(tagName, TAG_SELECT_CASE)) {
                handleSelectCase(content, source, child, currentFile);
            }
            else {
                throw new JBranchException("Unsupported tag name : " + tagName);
            }
        }
    }

    protected void handleField(DynaBean content, byte[] source, Element field) throws JBranchException {
        //欄位名稱
        String vFieldID = getAttributeValue(field, ATTRIBUTE_V_FIELD_ID);

        List<Attribute> attributes = field.attributes();

        Map<String, String> attrMap = new HashMap<String, String>(attributes.size());
        for (Attribute attribute : attributes) {
            attrMap.put(attribute.getName(), attribute.getValue());
        }

        String value = parser.getNextContent(source, attrMap);

        for (Processor processor : processors) {
            value = processor.process(value, attrMap);
        }
        content.set(vFieldID, value);
    }

    private void handleInclude(WrapDynaBean content, byte[] source, Element include, File currentFile) throws JBranchException {
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
        handleContents(content, source, includeBase, includeFile);
    }

    private void handleCollection(WrapDynaBean content, byte[] source, Element collection, File currentFile) throws JBranchException {
        //String groupId = getAttributeValue(collection, ATTRIBUTE_GROUP_ID);
        String recordId = getAttributeValue(collection, ATTRIBUTE_ID);
        int vRepeat = NumberUtils.toInt(getAttributeValue(collection, ATTRIBUTE_V_REPEAT), 1);
        //if (groupId != null) {
        //    contentPath += "/" + groupId;
        //}
        List contentChildren = (List) content.get(recordId);
        Class listItemClass = getGenericClass(content.getInstance(), recordId);
        for (int i = 0; i < vRepeat; i++) {
            Object contentChild = getInstance(listItemClass);
            WrapDynaBean dynaBean;
            if (Map.class.isInstance(contentChild)) {
                dynaBean = new WrapDynaBean(new LazyDynaMap((Map) contentChild));
            }
            else {
                dynaBean = new WrapDynaBean(contentChild);
            }
            handleContents(dynaBean, source, collection, currentFile);
            contentChildren.add(contentChild);
        } //end of for (int i = 0; i < vRepeat; i++)
    }

    public Class getGenericClass(Object source, String propertyName) throws JBranchException {
        try {
            Type genericType = source.getClass().getDeclaredField(propertyName).getGenericType();
            Class elementClass;
            if (genericType instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
                elementClass = (Class) actualTypeArguments[0];
            }
            else {
                elementClass = HashMap.class;
            }

            return elementClass;
        }
        catch (NoSuchFieldException e) {
            throw new JBranchException(e);
        }
    }

    public <T> T getInstance(Class<T> clazz) throws JBranchException {
        try {
            return clazz.newInstance();
        }
        catch (InstantiationException e) {
            throw new JBranchException(e);
        }
        catch (IllegalAccessException e) {
            throw new JBranchException(e);
        }
    }

    private void handleSelectCase(WrapDynaBean content, byte[] source, Element selectCase, File currentFile) throws JBranchException {
        String switchField = getAttributeValue(selectCase, ATTRIBUTE_SWITCH);
        String switchValue = (String) content.get(switchField);
        List<Element> cases = selectCase.selectNodes("./case");
        boolean contains = false;
        for (Element caze : cases) {
            String values = caze.attribute(ATTRIBUTE_VALUE).getValue();
            contains = ArrayUtils.contains(StringUtils.split(values, ","), switchValue);
            if (contains) {
                handleContents(content, source, caze, currentFile);
                break;
            }
        }
        if (!contains) {
            Node caseElse = selectCase.selectSingleNode("./caseElse");
            if (caseElse != null) {
                handleContents(content, source, caseElse, currentFile);
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

    public void setParser(SourceParser parser) {
        this.parser = parser;
    }

    public void setProcessors(List<Processor> processors) {
        this.processors = processors;
    }

    public void setRepository(DOMRepository repository) {
        this.repository = repository;
    }
}
