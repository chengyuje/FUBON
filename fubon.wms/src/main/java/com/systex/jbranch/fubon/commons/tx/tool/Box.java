package com.systex.jbranch.fubon.commons.tx.tool;

import com.systex.jbranch.fubon.commons.esb.xml.parse.EsbXmlParseUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;

/**
 * 工具箱
 */
public class Box {
    /**
     * 建立電文 XML
     **/
    public static <T> String marshal(T request) throws IOException, JAXBException {
        try (StringWriter writer = new StringWriter()) {
            JAXBContext context = JAXBContext.newInstance(request.getClass());
            Marshaller marshal = context.createMarshaller();
            marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshal.marshal(request, writer);
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
    public static <T> T unmarshal(String xml, Class<T> clazz) throws XMLStreamException, JAXBException {
        try (StringReader reader = new StringReader(xml)) {
            XMLStreamReader xsr = XMLInputFactory.newFactory().createXMLStreamReader(new StreamSource(reader));
            xsr.nextTag();

            while (!clazz.getAnnotation(XmlRootElement.class)
                    .name()
                    .equals(xsr.getLocalName())) xsr.nextTag();

            JAXBElement<T> element = JAXBContext
                    .newInstance(clazz)
                    .createUnmarshaller()
                    .unmarshal(xsr, clazz);
            xsr.close();
            return element.getValue();
        }
    }

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
