
package com.systex.jbranch.fubon.commons.cbs.service_platform.jaxws_ri;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the tw.com.fubon.xsd.tcssoapservice package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: tw.com.fubon.xsd.tcssoapservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SoapService }
     * 
     */
    public SoapService createSoapService() {
        return new SoapService();
    }

    /**
     * Create an instance of {@link SoapService.ServiceHeader }
     * 
     */
    public SoapService.ServiceHeader createSoapServiceServiceHeader() {
        return new SoapService.ServiceHeader();
    }

    /**
     * Create an instance of {@link SoapService.ServiceBody }
     * 
     */
    public SoapService.ServiceBody createSoapServiceServiceBody() {
        return new SoapService.ServiceBody();
    }

    /**
     * Create an instance of {@link SoapService.ServiceError }
     * 
     */
    public SoapService.ServiceError createSoapServiceServiceError() {
        return new SoapService.ServiceError();
    }

}
