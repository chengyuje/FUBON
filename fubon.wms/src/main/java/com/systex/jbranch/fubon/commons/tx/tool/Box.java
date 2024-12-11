package com.systex.jbranch.fubon.commons.tx.tool;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.JaxbInstance;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.xml.parse.EsbXmlParseUtil;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**
 * 工具箱
 */
@Component
public class Box {
	protected static Logger logger = LoggerFactory.getLogger(Box.class);
	private static Map<Integer, JaxbInstance> CBSjaxbMap = new HashMap<>();
	private static Map<Integer, JaxbInstance> ESBjaxbMap = new HashMap<>();
	
	public Box() {
		this.initPool();
	}
	
	private synchronized void initPool() {
		try {
			//int MAX_JAXB_INSTANCES = "AIX".equals(System.getProperty("os.name")) ? 5 : 2;
			int MAX_JAXB_INSTANCES = 5;
			if (CBSjaxbMap.isEmpty()) {
				for (int i=1; i<=MAX_JAXB_INSTANCES; i++) {
					JAXBContext inContext = JAXBContext.newInstance(CBSUtilInputVO.class);
					JaxbInstance instance = new JaxbInstance();
					instance.id = i;
					instance.inContext = inContext;
					CBSjaxbMap.put(i, instance);
					logger.info("CBSjaxbMap newInstance JaxbInstance #" + i);
				}
			}
			if (ESBjaxbMap.isEmpty()) {
				for (int i=1; i<=MAX_JAXB_INSTANCES; i++) {
					JAXBContext inContext = JAXBContext.newInstance(ESBUtilInputVO.class);
					JaxbInstance instance = new JaxbInstance();
					instance.id = i;
					instance.inContext = inContext;
					ESBjaxbMap.put(i, instance);
					logger.info("ESBjaxbMap newInstance JaxbInstance #" + i);
				}
			}
		} catch (Exception e) {

		}
	}
	
	private synchronized static void increaseJaxbInstance(String type) {
		try {
			if("CBSUtilInputVO".equals(type)) {
				JAXBContext inContext = JAXBContext.newInstance(CBSUtilInputVO.class);
				JaxbInstance instance = new JaxbInstance();
				instance.id = CBSjaxbMap.size() + 1;
				instance.inContext = inContext;
				instance.used = false;
				CBSjaxbMap.put(instance.id, instance);
				logger.info("CBSjaxbMap increase JaxbInstance #" + instance.id);
				
			} else if("ESBUtilInputVO".equals(type)) {
				JAXBContext inContext = JAXBContext.newInstance(ESBUtilInputVO.class);
				JaxbInstance instance = new JaxbInstance();
				instance.id = ESBjaxbMap.size() + 1;
				instance.inContext = inContext;
				instance.used = false;
				ESBjaxbMap.put(instance.id, instance);
				logger.info("CBSjaxbMap increase JaxbInstance #" + instance.id);
			}
		} catch (Exception e) {
			logger.warn("CBSUtilInputVO".equals(type) ? "CBSjaxbMap" : "ESBjaxbMap" + "increase JaxbInstance #" 
		+ ("CBSUtilInputVO".equals(type) ? (CBSjaxbMap.size() + 1) : (ESBjaxbMap.size() + 1)) +  " fail", e);
		}
	}
	
	private static int countFree(String type) {
		int cnt = 0;
		if("CBSUtilInputVO".equals(type)) {
			for (JaxbInstance instance : CBSjaxbMap.values()) {
				if (!instance.used) cnt++;
			}
			if (cnt == 0) {	// 用完, 就主動加一instance
				increaseJaxbInstance(type);
			}
		} else if("ESBUtilInputVO".equals(type)) {
			for (JaxbInstance instance : ESBjaxbMap.values()) {
				if (!instance.used) cnt++;
			}
			if (cnt == 0) {	// 用完, 就主動加一instance
				increaseJaxbInstance(type);
			}
		}
		return cnt;
	}

	
	private static JaxbInstance getFreeInstance(String type) {
		if("CBSUtilInputVO".equals(type)) {
			for (JaxbInstance instance : CBSjaxbMap.values()) {
				if (!instance.used) {
					instance.used = true;
					int cnt = countFree(type);
					logger.info("CBSjaxbMap Get #{}, free: {}", instance.id, cnt);
					return instance;
				}
			}
		} else if("ESBUtilInputVO".equals(type)) {
			for (JaxbInstance instance : ESBjaxbMap.values()) {
				if (!instance.used) {
					instance.used = true;
					int cnt = countFree(type);
					logger.info("ESBjaxbMap Get #{}, free: {}", instance.id, cnt);
					return instance;
				}
			}
		}

		return null;
	}
	
	private static void freeJaxbInstance(JaxbInstance instance) {
		instance.used = false;
	}
    /**
     * 建立電文 XML
     * 因應效能 參考Jemmy #2239的改動
     * @throws JBranchException 
     **/
    public static <T> String marshal(T request) throws IOException, JAXBException, JBranchException {
        try (StringWriter writer = new StringWriter()) {
        	JaxbInstance instance = null;
        	String type[] = request.getClass().getName().split("\\.");  
        	instance = getFreeInstance(type[type.length-1]);
			if (instance == null) throw new JBranchException("電文系統忙碌中，請稍侯再試");
			JAXBContext context = instance.inContext;
//            JAXBContext context = JAXBContext.newInstance(request.getClass());
            Marshaller marshal = context.createMarshaller();
            marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshal.marshal(request, writer);
            freeJaxbInstance(instance);
            return writer.toString();
        }
    }

    /**
     * 將電文 xml Unmarshal 為指定物件型態，該物件必須設定 XmlRootElement Annotation
     * Issue: 本機端可成功 unmarshal 所有成員，但銀行環境部分成員無法成功。目前改用 {@link EsbXmlParseUtil}。
     * 請參照 Box.unmarshal(String xml, Class<T> clazz, String[] tags)
     * @param xml   電文 XML
     * @param clazz unmarshal 的容器類別
     * @param <T>
     * @return
     * @throws XMLStreamException
     * @throws JAXBException
     */
    @Deprecated
//    public static <T> T unmarshal(String xml, Class<T> clazz) throws XMLStreamException, JAXBException {
//        try (StringReader reader = new StringReader(xml)) {
//            XMLStreamReader xsr = XMLInputFactory.newFactory().createXMLStreamReader(new StreamSource(reader));
//            xsr.nextTag();
//
//            while (!clazz.getAnnotation(XmlRootElement.class)
//                    .name()
//                    .equals(xsr.getLocalName())) xsr.nextTag();
//
//            JAXBElement<T> element = JAXBContext
//                    .newInstance(clazz)
//                    .createUnmarshaller()
//                    .unmarshal(xsr, clazz);
//            xsr.close();
//            return element.getValue();
//        }
//    }

    /**
     * 使用 EsbXmlParseUtil 進行 unmarshal
     * @param xml 電文 XML
     * @param clazz unmarshal 的容器類別
     * @param tags 欲解析的標籤名稱 Ex: {TxHead, TxBody, itemId}
     */
    public static <T> T unmarshal(String xml, Class<T> clazz, String[] tags) throws Exception {
        T t = clazz.newInstance();
        EsbXmlParseUtil xmlUtil = new EsbXmlParseUtil(clazz);
        for (String key : tags) {
            Field field = xmlUtil.MESSAGE_MAP.get(key);

            if (field == null) continue;

            field.setAccessible(true);
            field.set(t, xmlUtil.xmlToObject(xml, key));
        }
        return t;
    }

    /**
     * 將 XML 格式化，發生錯誤則返回原參數
     *
     * @param xml XML
     * @return
     */
    public static String xmlFormat(String xml) throws IOException {
        try (StringWriter out = new StringWriter()) {
            Document document = DocumentHelper.parseText(xml);

            /** XML Style **/
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setNewLineAfterDeclaration(false);
            format.setTrimText(false);

            XMLWriter writer = new XMLWriter(out, format);
            writer.write(document);
            writer.close();
            return out.toString();
        } catch (DocumentException e) {
            return xml;
        }
    }
}
