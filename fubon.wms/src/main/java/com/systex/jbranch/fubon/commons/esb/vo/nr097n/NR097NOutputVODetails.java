package com.systex.jbranch.fubon.commons.esb.vo.nr097n;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/11/18.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NR097NOutputVODetails {
    @XmlElement
    private String ProductType;
    @XmlElement
    private String ProductTypeMean;
    @XmlElement
    private String TypeNumber;
    @XmlElement
    private String TypeNumberMean;

    public String getProductType() {
        return ProductType;
    }

    public void setProductType(String productType) {
        ProductType = productType;
    }

    public String getProductTypeMean() {
        return ProductTypeMean;
    }

    public void setProductTypeMean(String productTypeMean) {
        ProductTypeMean = productTypeMean;
    }

    public String getTypeNumber() {
        return TypeNumber;
    }

    public void setTypeNumber(String typeNumber) {
        TypeNumber = typeNumber;
    }

    public String getTypeNumberMean() {
        return TypeNumberMean;
    }

    public void setTypeNumberMean(String typeNumberMean) {
        TypeNumberMean = typeNumberMean;
    }
}
