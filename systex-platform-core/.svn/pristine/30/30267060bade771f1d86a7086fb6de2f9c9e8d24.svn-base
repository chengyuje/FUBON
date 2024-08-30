package com.systex.jbranch.platform.common.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;

public class JaxbUtil {

	private JaxbUtil(){}

	public static String marshal(Object object) throws JAXBException
	{
		return marshal(object,0);
	}

	public static String marshal(Object object,final int prefixBeginIndex) throws JAXBException
	{
		JAXBContext context=JAXBContext.newInstance(getClassesToBeBound(object));
		Marshaller marshaller=context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		marshaller.marshal(object, output);

		try
		{
			return output.toString("UTF-8");
		}
		catch(Exception err)
		{
			return output.toString();
		}

	}

	public static Object unmarshal(Unmarshaller unmarshaller,String source) throws JAXBException
	{
		ByteArrayInputStream input;
		try
		{
			input = new ByteArrayInputStream(source.getBytes("UTF-8"));
		}
		catch(Exception err)
		{
			input = new ByteArrayInputStream(source.getBytes());
		}
		return unmarshaller.unmarshal(input);
	}

	public static Class[] getClassesToBeBound(Object object)
	{
		List<Class> classes = new ArrayList<Class>();
		getClassesToBeBound(object.getClass().cast(object),classes,new ArrayList());
		return classes.toArray(new Class[0]);
	}

	private static void getClassesToBeBound(Object object,List<Class> classes,List refStack)
	{
		if(refStack.contains(object))
			return;
		else
			refStack.add(object);

		if(object.getClass().getAnnotation(XmlRootElement.class)!=null)
		{
			if(!classes.contains(object.getClass()))
				classes.add(object.getClass());

			Method m[] = object.getClass().getDeclaredMethods();
		    for(int i=0;i<m.length;i++)
		    {
		    	if(m[i].getName().indexOf("get")==0 && m[i].getReturnType()!=Void.TYPE && m[i].getParameterTypes().length==0)
		    	{
		    		try
		    		{
		    			Object o = m[i].invoke(object);
		    			if(o!=null)
		    				getClassesToBeBound(o.getClass().cast(o),classes,refStack);
		    		}
		    		catch(Exception err)
		    		{}
		    	}
			}
		}
		else if(object instanceof Iterable)
		{
			Iterator i = ((Iterable)object).iterator();
			while(i.hasNext())
			{
				Object o = i.next();
				getClassesToBeBound(o.getClass().cast(o),classes,refStack);
			}
		}
	}
}
