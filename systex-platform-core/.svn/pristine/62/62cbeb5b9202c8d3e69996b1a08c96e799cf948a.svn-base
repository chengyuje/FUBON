package com.systex.jbranch.platform.common.xml;

import java.util.List;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.apache.commons.lang.StringUtils;

public class JaxbUnmarshaller implements Unmarshaller {

	private Unmarshaller unmarshaller=null;
	
	public JaxbUnmarshaller(List packages) throws JAXBException
	{
		JAXBContext context = JAXBContext.newInstance(StringUtils.join(packages, ":"));
		unmarshaller = context.createUnmarshaller();
	}
	
	public <A extends XmlAdapter> A getAdapter(Class<A> arg0) {

		return unmarshaller.getAdapter(arg0);
	}

	public AttachmentUnmarshaller getAttachmentUnmarshaller() {

		return unmarshaller.getAttachmentUnmarshaller();
	}

	public ValidationEventHandler getEventHandler() throws JAXBException {

		return unmarshaller.getEventHandler();
	}

	public Listener getListener() {

		return unmarshaller.getListener();
	}

	public Object getProperty(String arg0) throws PropertyException {

		return unmarshaller.getProperty(arg0);
	}

	public Schema getSchema() {

		return unmarshaller.getSchema();
	}

	public UnmarshallerHandler getUnmarshallerHandler() {

		return unmarshaller.getUnmarshallerHandler();
	}

	@Deprecated
	public boolean isValidating() throws JAXBException {

		return unmarshaller.isValidating();
	}

	public void setAdapter(XmlAdapter arg0) {

		unmarshaller.setAdapter(arg0);
	}

	public <A extends XmlAdapter> void setAdapter(Class<A> arg0, A arg1) {

		unmarshaller.setAdapter(arg0, arg1);
	}

	public void setAttachmentUnmarshaller(AttachmentUnmarshaller arg0) {

		unmarshaller.setAttachmentUnmarshaller(arg0);
	}

	public void setEventHandler(ValidationEventHandler arg0)
			throws JAXBException {

		unmarshaller.setEventHandler(arg0);
	}

	public void setListener(Listener arg0) {

		unmarshaller.setListener(arg0);
	}

	public void setProperty(String arg0, Object arg1) throws PropertyException {

		unmarshaller.setProperty(arg0, arg1);
	}

	public void setSchema(Schema arg0) {

		unmarshaller.setSchema(arg0);
	}
	
	@Deprecated
	public void setValidating(boolean arg0) throws JAXBException {

		unmarshaller.setValidating(arg0);
	}

	public Object unmarshal(File arg0) throws JAXBException {

		return unmarshaller.unmarshal(arg0);
	}

	public Object unmarshal(InputStream arg0) throws JAXBException {

		return unmarshaller.unmarshal(arg0);
	}

	public Object unmarshal(Reader arg0) throws JAXBException {

		return unmarshaller.unmarshal(arg0);
	}

	public Object unmarshal(URL arg0) throws JAXBException {

		return unmarshaller.unmarshal(arg0);
	}

	public Object unmarshal(InputSource arg0) throws JAXBException {

		return unmarshaller.unmarshal(arg0);
	}

	public Object unmarshal(Node arg0) throws JAXBException {

		return unmarshaller.unmarshal(arg0);
	}

	public Object unmarshal(Source arg0) throws JAXBException {

		return unmarshaller.unmarshal(arg0);
	}

	public Object unmarshal(XMLStreamReader arg0) throws JAXBException {

		return unmarshaller.unmarshal(arg0);
	}

	public Object unmarshal(XMLEventReader arg0) throws JAXBException {

		return unmarshaller.unmarshal(arg0);
	}

	public <T> JAXBElement<T> unmarshal(Node arg0, Class<T> arg1)
			throws JAXBException {

		return unmarshaller.unmarshal(arg0,arg1);
	}

	public <T> JAXBElement<T> unmarshal(Source arg0, Class<T> arg1)
			throws JAXBException {

		return unmarshaller.unmarshal(arg0,arg1);
	}

	public <T> JAXBElement<T> unmarshal(XMLStreamReader arg0, Class<T> arg1)
			throws JAXBException {

		return unmarshaller.unmarshal(arg0,arg1);
	}

	public <T> JAXBElement<T> unmarshal(XMLEventReader arg0, Class<T> arg1)
			throws JAXBException {

		return unmarshaller.unmarshal(arg0,arg1);
	}

}
