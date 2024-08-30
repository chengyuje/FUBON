
package com.systex.jbranch.fubon.webservice.ws.FBMAPPService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DataDisp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "dataDisp"
})
@XmlRootElement(name = "GetCase")
public class GetCase {

    @XmlElement(name = "DataDisp")
    protected String dataDisp;

    /**
     * Gets the value of the dataDisp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataDisp() {
        return dataDisp;
    }

    /**
     * Sets the value of the dataDisp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataDisp(String value) {
        this.dataDisp = value;
    }

}
