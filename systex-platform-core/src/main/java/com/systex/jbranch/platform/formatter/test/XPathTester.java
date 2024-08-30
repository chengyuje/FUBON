package com.systex.jbranch.platform.formatter.test;

import java.io.File;
import java.io.IOException;
import java.util.List;



import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
// import org.jdom2.xpath.XPath;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

public class XPathTester 
{
	public static void main(String...args) throws JDOMException, IOException
	{
		SAXBuilder builder = new SAXBuilder();
		File file=new File("C:\\Users\\AllenYeh\\Desktop\\中心電文\\電文\\電文\\040102_Host.xml");
		Document doc = builder.build(file);
		
		Element root=doc.getRootElement();
		//List<Element> list=XPath.selectNodes(root, "//hostInputMessages/hostInputMessage[@id='I1']/hostInputMessageFields/hIMField[@vDefault!='']");
		XPathFactory xFactory = XPathFactory.instance();
		XPathExpression<Element> expr = xFactory.compile("//hostInputMessages/hostInputMessage[@id='I1']/hostInputMessageFields/hIMField[@vDefault!='']", Filters.element());
		List<Element> list = expr.evaluate(root);
		for(int i=0;i<list.size();i++)
		{
			System.out.print(list.get(i).getAttributeValue("vFieldID"));
			System.out.print(":");
			System.out.print(list.get(i).getAttributeValue("vDefault"));
			System.out.println();
		}
		
	}
}
