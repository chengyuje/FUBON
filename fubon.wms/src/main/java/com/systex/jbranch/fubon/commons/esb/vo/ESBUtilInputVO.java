package com.systex.jbranch.fubon.commons.esb.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.systex.jbranch.fubon.commons.esb.vo.MVC110001.MVC110001InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.MVC310001.MVC310001InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.MVC310002.MVC310002InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.afbrn8.AFBRN8InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.afbrn9.AFBRN9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.afee011.AFEE011InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrva2.AJBRVA2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrva3.AJBRVA3InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrva9.AJBRVA9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrvb1.AJBRVB1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrvb9.AJBRVB9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrvc1.AJBRVC1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrvc2.AJBRVC2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrvc9.AJBRVC9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrvd9.AJBRVD9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajw084.AJW084InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.aml004.AML004InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.bkdcd003.BKDCD003InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ccm002.CCM002InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ccm7818.CCM7818InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ce6200r.CE6220RInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.cew012r.CEW012RInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.clm032151.CLM032151InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.cm061435cr.CM061435CRInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.cm6220r.CM6220RInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb032168.EB032168InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb032282.EB032282InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb12020002.EB12020002InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb172656.EB172656InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb202650.EB202650InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb202674.EB202674InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb312201.EB312201InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb372602.EB372602InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb382201.EB382201InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ebpmn.EBPMN2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ebpmn.EBPMNInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032151.FC032151InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032153.FC032153InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032154.FC032154InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032275.FC032275InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032659.FC032659InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032671.FC032671InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032675.FC032675InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc81.FC81InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032151.FP032151InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032671.FP032671InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032675.FP032675InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp052650.FP052650InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.gd320140.GD320140InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.hd00070000.HD00070000InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.hd00070000.SpecHD00070000;
import com.systex.jbranch.fubon.commons.esb.vo.nb052650.NB052650InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn1.NFBRN1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn2.NFBRN2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn3.NFBRN3InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn4.NFBRN4InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn5.NFBRN5InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn6.NFBRN6InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn7.NFBRN7InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn8.NFBRN8InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn9.NFBRN9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrne.NFBRNEInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrnf.NFBRNFInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrng.NFBRNGInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrnh.NFBRNHInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrni.NFBRNIInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrx7.NFBRX7InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrx9.NFBRX9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee001.NFEE001InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee002.NFEE002InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee011.NFEE011InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee012.NFEE012InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee086.NFEE086InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfei001.NFEI001InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfei002.NFEI002InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfei003.NFEI003InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfvipa.NFVIPAInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrva1.NJBRVA1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrva2.NJBRVA2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrva3.NJBRVA3InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrva9.NJBRVA9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvb1.NJBRVB1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvb9.NJBRVB9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc1.NJBRVC1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc2.NJBRVC2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc3.NJBRVC3InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc3.SpecNJBRVC3;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc9.NJBRVC9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvd9.NJBRVD9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvh3.NJBRVH3InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvx1.NJBRVX1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvx2.NJBRVX2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvx3.NJBRVX3InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njchklc2.NJCHKLC2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njw084.NJW084InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njweea60.NJWEEA60InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njweea70.NJWEEA70InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nkne01.NKNE01InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmi001.NMI001InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmi002.NMI002InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmi003.NMI003InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmp8yb.NMP8YBInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvipa.NMVIPAInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp3a.NMVP3AInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp4a.NMVP4AInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp5a.NMVP5AInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp6a.NMVP6AInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp7a.NMVP7AInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp8a.NMVP8AInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp9a.NMVP9AInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr070n.NR070NInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr074n.NR074NInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr080n.NR080NInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr096n.NR096NInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr098n.NR098NInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrq01.NRBRQ01InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrva1.NRBRVA1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrva2.NRBRVA2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrva3.NRBRVA3InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrva9.NRBRVA9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc1.NRBRVC1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc2.NRBRVC2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc3.NRBRVC3InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc4.NRBRVC4InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sc120100.SC120100InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sd120140.SD120140InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq20.SDACTQ20InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq3.SDACTQ3InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq4.SDACTQ4InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq5.SDACTQ5InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq8.SDACTQ8InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdprc09a.SDPRC09AInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.spwebq1.SPWEBQ1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.tp032675.TP032675InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.tp552697.TP552697InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.uk084n.UK084NInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.vip032153.VIP032153InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.vn067n.VN067NInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.vn084n.VN084NInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.vn084n1.VN084N1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.vn085n.VN085NInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.vr032675.VR032675InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032154.WMS032154InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032275.WMS032275InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032675.WMS032675InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms552697.WMS552697InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wmshacr.WMSHACRInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wmshaia.WMSHAIAVO;

/**
 * Created by SebastianWu on 2016/8/19.
 * <p>
 * set default attribute in default constructor
 */
@XmlRootElement(name = "Tx")
@XmlAccessorType(XmlAccessType.FIELD)
public class ESBUtilInputVO {
    @XmlTransient
    private String module;  //交易模組代號

    @XmlAttribute
    private String txid;
    @XmlAttribute
    private String encoding;
    @XmlAttribute
    private String fontremapping;

    @XmlElement(name = "TxHead")
    private TxHeadVO txHeadVO;
    @XmlElement(name = "TxBody")
    private FC032675InputVO fc032675InputVO;
    @XmlElement(name = "TxBody")
    private FC032659InputVO fc032659InputVO;
    @XmlElement(name = "TxBody")
    private TP032675InputVO tp032675InputVO;
    @XmlElement(name = "TxBody")
    private AFEE011InputVO afee011InputVO;
    @XmlElement(name = "TxBody")
    private NFEE011InputVO nfee011InputVO;
    @XmlElement(name = "TxBody")
    private FC032275InputVO fc032275InputVO;
    @XmlElement(name = "TxBody")
    private NFEE012InputVO nfee012InputVO;
    @XmlElement(name = "TxBody")
    private NKNE01InputVO nkne01InputVO;
    @XmlElement(name = "TxBody")
    private NRBRVA3InputVO nrbrva3InputVO;
    @XmlElement(name = "TxBody")
    private NRBRVA9InputVO nrbrva9InputVO;
    @XmlElement(name = "TxBody")
    private NRBRVA1InputVO nrbrva1InputVO;
    @XmlElement(name = "TxBody")
    private NRBRVA2InputVO nrbrva2InputVO;
    @XmlElement(name = "TxBody")
    private NR096NInputVO nr096NInputVO;
    @XmlElement(name = "TxBody")
    private NJBRVA9InputVO njbrva9InputVO;
    @XmlElement(name = "TxBody")
    private NJBRVB9InputVO njbrvb9InputVO;
    @XmlElement(name = "TxBody")
    private NJBRVB1InputVO njbrvb1InputVO;
    @XmlElement(name = "TxBody")
    private NJBRVC1InputVO njbrvc1InputVO;
    @XmlElement(name = "TxBody")
    private NJBRVC2InputVO njbrvc2InputVO;
    @XmlElement(name = "TxBody")
    private AJBRVC1InputVO ajbrvc1InputVO;
    @XmlElement(name = "TxBody")
    private AJBRVC2InputVO ajbrvc2InputVO;
    @XmlElement(name = "TxBody")
    private NJBRVC9InputVO njbrvc9InputVO;
    @XmlElement(name = "TxBody")
    private NJBRVD9InputVO njbrvd9InputVO;

    //add by James 2017/07/13
    @XmlElement(name = "TxBody")
    private AJBRVB1InputVO ajbrvb1InputVO;

    @XmlElement(name = "TxBody")
    private NFVIPAInputVO nfvipaInputVO;
    @XmlElement(name = "TxBody")
    private NJBRVA1InputVO njbrva1InputVO;
    @XmlElement(name = "TxBody")
    private NJBRVA2InputVO njbrva2InputVO;
    @XmlElement(name = "TxBody")
    private NRBRVC1InputVO nrbrvc1InputVO;
    @XmlElement(name = "TxBody")
    private NRBRVC2InputVO nrbrvc2InputVO;
    @XmlElement(name = "TxBody")
    private NRBRVC3InputVO nrbrvc3InputVO;
    @XmlElement(name = "TxBody")
    private NRBRVC4InputVO nrbrvc4InputVO;
    @XmlElement(name = "TxBody")
    private NJBRVA3InputVO njbrva3InputVO;
    @XmlElement(name = "TxBody")
    private SDACTQ20InputVO sdactq20InputVO;
    @XmlElement(name = "TxBody")
    private SDACTQ3InputVO sdactq3InputVO;
    @XmlElement(name = "TxBody")
    private SDACTQ4InputVO sdactq4InputVO;
    @XmlElement(name = "TxBody")
    private SDACTQ5InputVO sdactq5InputVO;
    @XmlElement(name = "TxBody")
    private NFBRN1InputVO nfbrn1InputVO;
    @XmlElement(name = "TxBody")
    private NFBRN7InputVO nfbrn7InputVO;
    @XmlElement(name = "TxBody")
    private NFEE002InputVO nfee002InputVO;
    @XmlElement(name = "TxBody")
    private VN085NInputVO vn085nInputVO;
    @XmlElement(name = "TxBody")
    private NFBRN6InputVO nfbrn6InputVO;
    @XmlElement(name = "TxBody")
    private NFBRN8InputVO nfbrn8InputVO;
    @XmlElement(name = "TxBody")
    private AFBRN8InputVO afbrn8InputVO;
    @XmlElement(name = "TxBody")
    private NR098NInputVO nr098NInputVO;
    @XmlElement(name = "TxBody")
    private NFBRN2InputVO nfbrn2InputVO;
    @XmlElement(name = "TxBody")
    private NFBRN3InputVO nfbrn3InputVO;
    @XmlElement(name = "TxBody")
    private NFBRN5InputVO nfbrn5InputVO;
    @XmlElement(name = "TxBody")
    private NFBRN9InputVO nfbrn9InputVO;

    //add by James 2017/06/09
    @XmlElement(name = "TxBody")
    private AFBRN9InputVO afbrn9InputVO;

    @XmlElement(name = "TxBody")
    private FC81InputVO fc81InputVO;
    @XmlElement(name = "TxBody")
    private FP052650InputVO fp052650InputVO;
    @XmlElement(name = "TxBody")
    private NB052650InputVO nb052650InputVO;
    @XmlElement(name = "TxBody")
    private NFEE086InputVO nfee086InputVO;
    @XmlElement(name = "TxBody")
    private NFBRN4InputVO nfbrn4InputVO;
    @XmlElement(name = "TxBody")
    private FC032151InputVO fc032151InputVO;

    //add by walalala 2016/12/06
    @XmlElement(name = "TxBody")
    private FC032671InputVO fc032671InputVO;

    @XmlElement(name = "TxBody")
    private CCM7818InputVO ccm7818InputVO;

    @XmlElement(name = "TxBody")
    private CM6220RInputVO cm6220rInputVO;

    @XmlElement(name = "TxBody")
    private EB172656InputVO eb172656InputVO;

    @XmlElement(name = "TxBody")
    private EB202650InputVO eb202650InputVO;

    @XmlElement(name = "TxBody")
    private EB202674InputVO eb202674InputVO;

    @XmlElement(name = "TxBody")
    private SD120140InputVO sd120140InputVO;

    @XmlElement(name = "TxBody")
    private GD320140InputVO gd320140InputVO;

    @XmlElement(name = "TxBody")
    private BKDCD003InputVO bkdcd003InputVO;

    @XmlElement(name = "TxBody")
    private EB312201InputVO eb312201InputVO;

    @XmlElement(name = "TxBody")
    private EB382201InputVO eb382201InputVO;

    @XmlElement(name = "TxBody")
    private EB12020002InputVO eb12020002InputVO;

    @XmlElement(name = "TxBody")
    private NMVIPAInputVO nmvipaInputVO;

    @XmlElement(name = "TxBody")
    private NMVP3AInputVO nmvp3aInputVO;

    @XmlElement(name = "TxBody")
    private FC032154InputVO fc032154InputVO;

    @XmlElement(name = "TxBody")
    private SDPRC09AInputVO sdprc09aInputVO;

    @XmlElement(name = "TxBody")
    private CCM002InputVO ccm002InputVO;

    //add by Stella 2017/01/05
    @XmlElement(name = "TxBody")
    private CEW012RInputVO cew012rInputVO;

    @XmlElement(name = "TxBody")
    private TP552697InputVO tp552697InputVO;


    @XmlElement(name = "TxBody")
    private FC032153InputVO fc032153InputVO;


    @XmlElement(name = "TxBody")
    private SC120100InputVO sc120100InputVO;

    @XmlElement(name = "TxBody")
    private NFEE001InputVO nfee001InputVO;

    @XmlElement(name = "TxBody")
    private NJCHKLC2InputVO njchklc2InputVO;

    @XmlElement(name = "TxBody")
    private VN067NInputVO vn067nInputVO; //網路快速下單 - 基金申購檢核
    @XmlElement(name = "TxBody")
    private EBPMNInputVO ebpmnInputVO;   //網路快速下單 – 發送電文至網銀讓網銀行事曆表單上可以出現快速申購之訊息
    @XmlElement(name = "TxBody")
    private EBPMN2InputVO ebpmn2InputVO; //AI BANK快速下單 – 發送電文至AI BANK, 行事曆表單上可以出現快速申購之訊息
    
    
    @XmlElement(name = "TxBody")
    private NRBRQ01InputVO nrbrq01InputVO;

    @XmlElement(name = "TxBody")
    private EB372602InputVO eb372602InputVO;

    @XmlElement(name = "TxBody")// 海外ETF/股票可用餘額查詢
    private NR070NInputVO nr070nInputVO;

    @XmlElement(name = "TxBody")// 股票委託明細
    private NR080NInputVO nr080nInputVO;

    @XmlElement(name = "TxBody")// 即時成交交易資訊
    private NR074NInputVO nr074nInputVO;

    @XmlElement(name = "TxBody")// 結構型商品推介同意註記
    private SDACTQ8InputVO sdactq8InputVO;

    @XmlElement(name = "TxBody")// 奈米投庫存查詢
    private NMP8YBInputVO nmp8ybInputVO;

    @XmlElement(name = "TxBody")// 奈米投個人風險屬性、卡片標籤查詢
    private NMI001InputVO nmi001InputVO;
    
    @XmlElement(name = "TxBody")// 奈米投庫存明細查詢
    private NMI002InputVO nmi002InputVO;
    
    @XmlElement(name = "TxBody")// 奈米投契約交易紀錄查詢
    private NMI003InputVO nmi003InputVO;

    @XmlElement(name = "TxBody")// 金錢信託_台外幣活存明細查詢
    private NMVP4AInputVO nmvp4aInputVO;

    @XmlElement(name = "TxBody")// 金錢信託_台外幣定存明細查詢
    private NMVP5AInputVO nmvp5aInputVO;

    @XmlElement(name = "TxBody")// 解除學歷待確認註記
    private EB032282InputVO eb032282InputVO;
    
    @XmlElement(name = "TxBody") // 金錢信託_首購檢核
    private NMVP7AInputVO nmvp7aInputVO;
    
    @XmlElement(name = "TxBody") // 金錢信託_客戶統編查詢契約
    private NMVP6AInputVO nmvp6aInputVO;
    
    @XmlElement(name = "TxBody") // 金錢信託_查詢關係人-管理運用人
    private NMVP8AInputVO nmvp8aInputVO;
    
    @XmlElement(name = "TxBody") // 金錢信託_ETF查詢客戶庫存
    private NMVP9AInputVO nmvp9aInputVO;
    
    @XmlElement(name = "TxBody") // 債券手續費議價-金錢信託
    private NJBRVX1InputVO njbrvx1InputVO;
    
    @XmlElement(name = "TxBody") // 債券申購交易-金錢信託
    private NJBRVX2InputVO njbrvx2InputVO;
    
    @XmlElement(name = "TxBody") // 債券贖回庫存查詢-金錢信託
    private NJBRVX3InputVO njbrvx3InputVO;

    @XmlElement(name = "TxBody") // 基金手續費議價-金錢信託
    private NJBRVX1InputVO nfbrx1InputVO;
    
    @XmlElement(name = "TxBody") // 基金申購交易-金錢信託
    private NFBRX7InputVO nfbrx7InputVO;
    
    @XmlElement(name = "TxBody") // 基金庫存查詢-金錢信託
    private NFBRX9InputVO nfbrx9InputVO;

    
    // add by SamTu 2018.02.13~
    @XmlElement(name = "TxBody")
    private FP032671InputVO fp032671InputVO;
    @XmlElement(name = "TxBody")
    private WMS032275InputVO wms032275InputVO;
    @XmlElement(name = "TxBody")
    private CLM032151InputVO clm032151InputVO;
    @XmlElement(name = "TxBody")
    private FP032675InputVO fp032675InputVO;
    @XmlElement(name = "TxBody")
    private VR032675InputVO vr032675InputVO;
    @XmlElement(name = "TxBody")
    private WMS032675InputVO wms032675InputVO;
    @XmlElement(name = "TxBody")
    private FP032151InputVO fp032151InputVO;
    @XmlElement(name = "TxBody")
    private WMS552697InputVO wms552697InputVO;
    @XmlElement(name = "TxBody")
    private WMS032154InputVO wms032154InputVO;
    @XmlElement(name = "TxBody")
    private VIP032153InputVO vip032153InputVO;

    @XmlElement(name = "TxBody")    //(信託推介同意書查詢)
    private NFEI002InputVO nfei002InputVO;
    @XmlElement(name = "TxBody")    // (客戶368帳號查詢)
    private NFEI001InputVO nfei001InputVO;
    @XmlElement(name = "TxBody")    // (客戶168、368帳號查詢)
    private NFEI003InputVO nfei003InputVO;
    
    @XmlElement(name = "TxBody") // 洗錢防制電文（Precheck）
    private EB032168InputVO eb032168InputVO;
    
    @XmlElement(name = "TxBody") // 交易明細歷史查詢
    private HD00070000InputVO hd00070000InputVO;

    @XmlElement(name = "TxBody") // 洗錢防制電文（AML）
    private AML004InputVO aml004InputVO;
    @XmlElement(name = "TxBody") // 海外債成交結果查詢
    private NJBRVC3InputVO njbrvc3InputVO;
    @XmlElement(name = "TxBody")
    private MVC110001InputVO mvc110001InputVO;
    @XmlElement(name = "TxBody")
    private MVC310001InputVO mvc310001InputVO;
    @XmlElement(name = "TxBody")
    private MVC310002InputVO mvc310002InputVO;

    @XmlElement(name = "TxBody") // 海外債快速申購手續費查詢
    private NJWEEA60InputVO njweea60InputVO;
    
    @XmlElement(name = "TxBody") // 高資產客戶註記
    private CM061435CRInputVO cm061435crInputVO;
    @XmlElement(name = "TxBody") // 高風險商品承作系統發查微服務，取得集中度資訊
    private WMSHACRInputVO wmshacrInputVO;
    @XmlElement(name = "TxBody") // 業管發查各交易系統，取得客戶已委託高風險投資明細資訊
    private NJBRVH3InputVO njbrvh3InputVO;
    @XmlElement(name = "TxBody") // 高資產客戶投組適配承作，取得風險檢核資訊
    private WMSHAIAVO wmshaiaInputVO;
    @XmlElement(name = "TxBody") // 國外私募基金贖回修正單位數交易電文
    private NFBRNEInputVO nfbrneInputVO;
    @XmlElement(name = "TxBody") // 動態鎖利基金申購交易電文
    private NFBRNFInputVO nfbrnfInputVO;
    @XmlElement(name = "TxBody") // 動態鎖利母基金加碼交易電文
    private NFBRNGInputVO nfbrngInputVO;
    @XmlElement(name = "TxBody") // 動態鎖利贖回交易電文
    private NFBRNHInputVO nfbrnhInputVO;
    @XmlElement(name = "TxBody") // 動態鎖利事件變更交易電文
    private NFBRNIInputVO nfbrniInputVO;
    // #1913
    @XmlElement(name = "TxBody") 
    private VN084NInputVO vn084nInputVO; 	// 基金 DBU
    @XmlElement(name = "TxBody") 
    private VN084N1InputVO vn084n1InputVO; 	// 基金 OBU
    @XmlElement(name = "TxBody") 
    private UK084NInputVO uk084nInputVO; 	// 海外債
    @XmlElement(name = "TxBody") 
    private AJW084InputVO ajw084InputVO;	// 海外債+SN OBU
    @XmlElement(name = "TxBody") 
    private NJW084InputVO njw084InputVO; 	// 海外債+SN DBU
    @XmlElement(name = "TxBody") 
    private SPWEBQ1InputVO spwebq1InputVO; 	//SI
    
    // #1976
    @XmlElement(name = "TxBody") 
    private CE6220RInputVO ce6220rInputVO; 

    // #2056：WMS-CR-20210923-02_新增海外債預約交易功能
    @XmlElement(name = "TxBody") 
    private NJWEEA70InputVO njweea70InputVO; 
    
    // #：WMS-CR-20240311-01_海外債券及SN，OBU申購及贖回套表功能
    @XmlElement(name = "TxBody") 
    private AJBRVA2InputVO ajbrva2InputVO; 	// 理專鍵機債券手續費議價OBU
    @XmlElement(name = "TxBody") 
    private AJBRVA3InputVO ajbrva3InputVO; 	// 理專鍵機債券首購查詢OBU
    @XmlElement(name = "TxBody") 
    private AJBRVA9InputVO ajbrva9InputVO; 	// 理專鍵機債券申購交易OBU
    @XmlElement(name = "TxBody") 
    private AJBRVB9InputVO ajbrvb9InputVO; 	// 理專鍵機債券贖回交易OBU
    @XmlElement(name = "TxBody") 
    private AJBRVC9InputVO ajbrvc9InputVO; 	// 理專鍵機債券長效單申購交易OBU
    @XmlElement(name = "TxBody") 
    private AJBRVD9InputVO ajbrvd9InputVO; 	// 理專鍵機債券長效單贖回交易OBU

    /**
     * 電文交易的特殊規格
     **/
    @XmlTransient
    private EsbSpec spec = null;

    public NMVP7AInputVO getNmvp7aInputVO() {
		return nmvp7aInputVO;
	}

	public void setNmvp7aInputVO(NMVP7AInputVO nmvp7aInputVO) {
		this.nmvp7aInputVO = nmvp7aInputVO;
	}

	public NMVP6AInputVO getNmvp6aInputVO() {
		return nmvp6aInputVO;
	}

	public void setNmvp6aInputVO(NMVP6AInputVO nmvp6aInputVO) {
		this.nmvp6aInputVO = nmvp6aInputVO;
	}

	public NMVP8AInputVO getNmvp8aInputVO() {
		return nmvp8aInputVO;
	}

	public void setNmvp8aInputVO(NMVP8AInputVO nmvp8aInputVO) {
		this.nmvp8aInputVO = nmvp8aInputVO;
	}

	public NMVP9AInputVO getNmvp9aInputVO() {
		return nmvp9aInputVO;
	}

	public void setNmvp9aInputVO(NMVP9AInputVO nmvp9aInputVO) {
		this.nmvp9aInputVO = nmvp9aInputVO;
	}

	public NJBRVX1InputVO getNjbrvx1InputVO() {
		return njbrvx1InputVO;
	}

	public void setNjbrvx1InputVO(NJBRVX1InputVO njbrvx1InputVO) {
		this.njbrvx1InputVO = njbrvx1InputVO;
	}

	public NJBRVX2InputVO getNjbrvx2InputVO() {
		return njbrvx2InputVO;
	}

	public void setNjbrvx2InputVO(NJBRVX2InputVO njbrvx2InputVO) {
		this.njbrvx2InputVO = njbrvx2InputVO;
	}

	public NJBRVX3InputVO getNjbrvx3InputVO() {
		return njbrvx3InputVO;
	}

	public void setNjbrvx3InputVO(NJBRVX3InputVO njbrvx3InputVO) {
		this.njbrvx3InputVO = njbrvx3InputVO;
	}

	public NJBRVX1InputVO getNfbrx1InputVO() {
		return nfbrx1InputVO;
	}

	public void setNfbrx1InputVO(NJBRVX1InputVO nfbrx1InputVO) {
		this.nfbrx1InputVO = nfbrx1InputVO;
	}

	public NFBRX7InputVO getNfbrx7InputVO() {
		return nfbrx7InputVO;
	}

	public void setNfbrx7InputVO(NFBRX7InputVO nfbrx7InputVO) {
		this.nfbrx7InputVO = nfbrx7InputVO;
	}

	public NFBRX9InputVO getNfbrx9InputVO() {
		return nfbrx9InputVO;
	}

	public void setNfbrx9InputVO(NFBRX9InputVO nfbrx9InputVO) {
		this.nfbrx9InputVO = nfbrx9InputVO;
	}

	public NFEI003InputVO getNfei003InputVO() {
		return nfei003InputVO;
	}

	public void setNfei003InputVO(NFEI003InputVO nfei003InputVO) {
		this.nfei003InputVO = nfei003InputVO;
	}

	public NFEI001InputVO getNfei001InputVO() {
        return nfei001InputVO;
    }

    public void setNfei001InputVO(NFEI001InputVO nfei001InputVO) {
        this.nfei001InputVO = nfei001InputVO;
    }

    public NFEI002InputVO getNfei002InputVO() {
        return nfei002InputVO;
    }

    public void setNfei002InputVO(NFEI002InputVO nfei002InputVO) {
        this.nfei002InputVO = nfei002InputVO;
    }

    public VIP032153InputVO getVip032153InputVO() {
        return vip032153InputVO;
    }

    public void setVip032153InputVO(VIP032153InputVO vip032153InputVO) {
        this.vip032153InputVO = vip032153InputVO;
    }

    public WMS552697InputVO getWms552697InputVO() {
        return wms552697InputVO;
    }

    public void setWms552697InputVO(WMS552697InputVO wms552697InputVO) {
        this.wms552697InputVO = wms552697InputVO;
    }

    public WMS032154InputVO getWms032154InputVO() {
        return wms032154InputVO;
    }

    public void setWms032154InputVO(WMS032154InputVO wms032154InputVO) {
        this.wms032154InputVO = wms032154InputVO;
    }

    public FP032151InputVO getFp032151InputVO() {
        return fp032151InputVO;
    }

    public void setFp032151InputVO(FP032151InputVO fp032151InputVO) {
        this.fp032151InputVO = fp032151InputVO;
    }

    public WMS032675InputVO getWms032675InputVO() {
        return wms032675InputVO;
    }

    public void setWms032675InputVO(WMS032675InputVO wms032675InputVO) {
        this.wms032675InputVO = wms032675InputVO;
    }

    public FP032675InputVO getFp032675InputVO() {
        return fp032675InputVO;
    }

    public void setFp032675InputVO(FP032675InputVO fp032675InputVO) {
        this.fp032675InputVO = fp032675InputVO;
    }

    public VR032675InputVO getVr032675InputVO() {
        return vr032675InputVO;
    }

    public void setVr032675InputVO(VR032675InputVO vr032675InputVO) {
        this.vr032675InputVO = vr032675InputVO;
    }

    public CLM032151InputVO getClm032151InputVO() {
        return clm032151InputVO;
    }

    public void setClm032151InputVO(CLM032151InputVO clm032151InputVO) {
        this.clm032151InputVO = clm032151InputVO;
    }

    public WMS032275InputVO getWms032275InputVO() {
        return wms032275InputVO;
    }

    public void setWms032275InputVO(WMS032275InputVO wms032275InputVO) {
        this.wms032275InputVO = wms032275InputVO;
    }

    public FP032671InputVO getFp032671InputVO() {
        return fp032671InputVO;
    }

    public void setFp032671InputVO(FP032671InputVO fp032671InputVO) {
        this.fp032671InputVO = fp032671InputVO;
    }

    public NJCHKLC2InputVO getNjchklc2InputVO() {
        return njchklc2InputVO;
    }

    public void setNjchklc2InputVO(NJCHKLC2InputVO njchklc2InputVO) {
        this.njchklc2InputVO = njchklc2InputVO;
    }

    public SC120100InputVO getSc120100InputVO() {
        return sc120100InputVO;
    }

    public void setSc120100InputVO(SC120100InputVO sc120100InputVO) {
        this.sc120100InputVO = sc120100InputVO;
    }

    public FC032153InputVO getFc032153InputVO() {
        return fc032153InputVO;
    }

    public void setFc032153InputVO(FC032153InputVO fc032153InputVO) {
        this.fc032153InputVO = fc032153InputVO;
    }

    public void setBkdcd003InputVO(BKDCD003InputVO bkdcd003InputVO) {
        this.bkdcd003InputVO = bkdcd003InputVO;
    }

    public FC032151InputVO getFc032151InputVO() {
        return fc032151InputVO;
    }

    public ESBUtilInputVO setFc032151InputVO(FC032151InputVO fc032151InputVO) {
        this.fc032151InputVO = fc032151InputVO;
        return this;
    }

    public NFBRN4InputVO getNfbrn4InputVO() {
        return nfbrn4InputVO;
    }

    public void setNfbrn4InputVO(NFBRN4InputVO nfbrn4InputVO) {
        this.nfbrn4InputVO = nfbrn4InputVO;
    }

    public NFEE086InputVO getNfee086InputVO() {
        return nfee086InputVO;
    }

    public void setNfee086InputVO(NFEE086InputVO nfee086InputVO) {
        this.nfee086InputVO = nfee086InputVO;
    }

    public NFBRN7InputVO getNfbrn7InputVO() {
        return nfbrn7InputVO;
    }

    public NFEE002InputVO getNfee002InputVO() {
        return nfee002InputVO;
    }

    public VN085NInputVO getVn085nInputVO() {
        return vn085nInputVO;
    }

    public NFBRN6InputVO getNfbrn6InputVO() {
        return nfbrn6InputVO;
    }

    public NFBRN8InputVO getNfbrn8InputVO() {
        return nfbrn8InputVO;
    }

    public AFBRN8InputVO getAfbrn8InputVO() {
        return afbrn8InputVO;
    }

    public void setNfbrn7InputVO(NFBRN7InputVO nfbrn7InputVO) {
        this.nfbrn7InputVO = nfbrn7InputVO;
    }

    public void setNfee002InputVO(NFEE002InputVO nfee002InputVO) {
        this.nfee002InputVO = nfee002InputVO;
    }

    public void setVn085nInputVO(VN085NInputVO vn085nInputVO) {
        this.vn085nInputVO = vn085nInputVO;
    }

    public void setNfbrn6InputVO(NFBRN6InputVO nfbrn6InputVO) {
        this.nfbrn6InputVO = nfbrn6InputVO;
    }

    public void setNfbrn8InputVO(NFBRN8InputVO nfbrn8InputVO) {
        this.nfbrn8InputVO = nfbrn8InputVO;
    }

    public void setAfbrn8InputVO(AFBRN8InputVO afbrn8InputVO) {
        this.afbrn8InputVO = afbrn8InputVO;
    }

    public NRBRVC4InputVO getNrbrvc4InputVO() {
        return nrbrvc4InputVO;
    }

    public void setNrbrvc4InputVO(NRBRVC4InputVO nrbrvc4InputVO) {
        this.nrbrvc4InputVO = nrbrvc4InputVO;
    }

    public String getModule() {
        return module;
    }

    public ESBUtilInputVO setModule(String module) {
        this.module = module;
        return this;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getFontremapping() {
        return fontremapping;
    }

    public void setFontremapping(String fontremapping) {
        this.fontremapping = fontremapping;
    }

    public TxHeadVO getTxHeadVO() {
        return txHeadVO;
    }

    public ESBUtilInputVO setTxHeadVO(TxHeadVO txHeadVO) {
        this.txHeadVO = txHeadVO;
        return this;
    }

    public FC032675InputVO getFc032675InputVO() {
        return fc032675InputVO;
    }

    public void setFc032675InputVO(FC032675InputVO fc032675InputVO) {
        this.fc032675InputVO = fc032675InputVO;
    }

    public TP032675InputVO getTp032675InputVO() {
        return tp032675InputVO;
    }

    public void setTp032675InputVO(TP032675InputVO tp032675InputVO) {
        this.tp032675InputVO = tp032675InputVO;
    }

    public AFEE011InputVO getAfee011InputVO() {
        return afee011InputVO;
    }

    public void setAfee011InputVO(AFEE011InputVO afee011InputVO) {
        this.afee011InputVO = afee011InputVO;
    }

    public NFEE011InputVO getNfee011InputVO() {
        return nfee011InputVO;
    }

    public void setNfee011InputVO(NFEE011InputVO nfee011InputVO) {
        this.nfee011InputVO = nfee011InputVO;
    }

    public FC032275InputVO getFc032275InputVO() {
        return fc032275InputVO;
    }

    public void setFc032275InputVO(FC032275InputVO fc032275InputVO) {
        this.fc032275InputVO = fc032275InputVO;
    }

    public NFEE012InputVO getNfee012InputVO() {
        return nfee012InputVO;
    }

    public void setNfee012InputVO(NFEE012InputVO nfee012InputVO) {
        this.nfee012InputVO = nfee012InputVO;
    }

    public NKNE01InputVO getNkne01InputVO() {
        return nkne01InputVO;
    }

    public void setNkne01InputVO(NKNE01InputVO nkne01InputVO) {
        this.nkne01InputVO = nkne01InputVO;
    }

    public NRBRVA3InputVO getNrbrva3InputVO() {
        return nrbrva3InputVO;
    }

    public void setNrbrva3InputVO(NRBRVA3InputVO nrbrva3InputVO) {
        this.nrbrva3InputVO = nrbrva3InputVO;
    }

    public NRBRVA9InputVO getNrbrva9InputVO() {
        return nrbrva9InputVO;
    }

    public void setNrbrva9InputVO(NRBRVA9InputVO nrbrva9InputVO) {
        this.nrbrva9InputVO = nrbrva9InputVO;
    }

    public NRBRVA1InputVO getNrbrva1InputVO() {
        return nrbrva1InputVO;
    }

    public void setNrbrva1InputVO(NRBRVA1InputVO nrbrva1InputVO) {
        this.nrbrva1InputVO = nrbrva1InputVO;
    }

    public NRBRVA2InputVO getNrbrva2InputVO() {
        return nrbrva2InputVO;
    }

    public void setNrbrva2InputVO(NRBRVA2InputVO nrbrva2InputVO) {
        this.nrbrva2InputVO = nrbrva2InputVO;
    }

    public NR096NInputVO getNr096NInputVO() {
        return nr096NInputVO;
    }

    public void setNr096NInputVO(NR096NInputVO nr096NInputVO) {
        this.nr096NInputVO = nr096NInputVO;
    }

    public NJBRVA9InputVO getNjbrva9InputVO() {
        return njbrva9InputVO;
    }

    public void setNjbrva9InputVO(NJBRVA9InputVO njbrva9InputVO) {
        this.njbrva9InputVO = njbrva9InputVO;
    }

    public NJBRVB9InputVO getNjbrvb9InputVO() {
        return njbrvb9InputVO;
    }

    public void setNjbrvb9InputVO(NJBRVB9InputVO njbrvb9InputVO) {
        this.njbrvb9InputVO = njbrvb9InputVO;
    }

    public NJBRVB1InputVO getNjbrvb1InputVO() {
        return njbrvb1InputVO;
    }

    public void setNjbrvb1InputVO(NJBRVB1InputVO njbrvb1InputVO) {
        this.njbrvb1InputVO = njbrvb1InputVO;
    }

    public NJBRVC1InputVO getNjbrvc1InputVO() {
        return njbrvc1InputVO;
    }

    public void setNjbrvc1InputVO(NJBRVC1InputVO njbrvc1InputVO) {
        this.njbrvc1InputVO = njbrvc1InputVO;
    }

    public NJBRVC2InputVO getNjbrvc2InputVO() {
        return njbrvc2InputVO;
    }

    public void setNjbrvc2InputVO(NJBRVC2InputVO njbrvc2InputVO) {
        this.njbrvc2InputVO = njbrvc2InputVO;
    }

    public AJBRVC1InputVO getAjbrvc1InputVO() {
        return ajbrvc1InputVO;
    }

    public void setAjbrvc1InputVO(AJBRVC1InputVO ajbrvc1InputVO) {
        this.ajbrvc1InputVO = ajbrvc1InputVO;
    }

    public AJBRVC2InputVO getAjbrvc2InputVO() {
        return ajbrvc2InputVO;
    }

    public void setAjbrvc2InputVO(AJBRVC2InputVO ajbrvc2InputVO) {
        this.ajbrvc2InputVO = ajbrvc2InputVO;
    }

    public NJBRVC9InputVO getNjbrvc9InputVO() {
        return njbrvc9InputVO;
    }

    public void setNjbrvc9InputVO(NJBRVC9InputVO njbrvc9InputVO) {
        this.njbrvc9InputVO = njbrvc9InputVO;
    }

    public NJBRVD9InputVO getNjbrvd9InputVO() {
        return njbrvd9InputVO;
    }

    public void setNjbrvd9InputVO(NJBRVD9InputVO njbrvd9InputVO) {
        this.njbrvd9InputVO = njbrvd9InputVO;
    }

    public AJBRVB1InputVO getAjbrvb1InputVO() {
        return ajbrvb1InputVO;
    }

    public void setAjbrvb1InputVO(AJBRVB1InputVO ajbrvb1InputVO) {
        this.ajbrvb1InputVO = ajbrvb1InputVO;
    }

    public NFVIPAInputVO getNfvipaInputVO() {
        return nfvipaInputVO;
    }

    public void setNfvipaInputVO(NFVIPAInputVO nfvipaInputVO) {
        this.nfvipaInputVO = nfvipaInputVO;
    }

    public NJBRVA1InputVO getNjbrva1InputVO() {
        return njbrva1InputVO;
    }

    public void setNjbrva1InputVO(NJBRVA1InputVO njbrva1InputVO) {
        this.njbrva1InputVO = njbrva1InputVO;
    }

    public NJBRVA2InputVO getNjbrva2InputVO() {
        return njbrva2InputVO;
    }

    public void setNjbrva2InputVO(NJBRVA2InputVO njbrva2InputVO) {
        this.njbrva2InputVO = njbrva2InputVO;
    }

    public NRBRVC1InputVO getNrbrvc1InputVO() {
        return nrbrvc1InputVO;
    }

    public void setNrbrvc1InputVO(NRBRVC1InputVO nrbrvc1InputVO) {
        this.nrbrvc1InputVO = nrbrvc1InputVO;
    }

    public NRBRVC2InputVO getNrbrvc2InputVO() {
        return nrbrvc2InputVO;
    }

    public void setNrbrvc2InputVO(NRBRVC2InputVO nrbrvc2InputVO) {
        this.nrbrvc2InputVO = nrbrvc2InputVO;
    }

    public NRBRVC3InputVO getNrbrvc3InputVO() {
        return nrbrvc3InputVO;
    }

    public void setNrbrvc3InputVO(NRBRVC3InputVO nrbrvc3InputVO) {
        this.nrbrvc3InputVO = nrbrvc3InputVO;
    }

    public NJBRVA3InputVO getNjbrva3InputVO() {
        return njbrva3InputVO;
    }

    public void setNjbrva3InputVO(NJBRVA3InputVO njbrva3InputVO) {
        this.njbrva3InputVO = njbrva3InputVO;
    }

    public SDACTQ20InputVO getSdactq20InputVO() {
		return sdactq20InputVO;
	}

	public void setSdactq20InputVO(SDACTQ20InputVO sdactq20InputVO) {
		this.sdactq20InputVO = sdactq20InputVO;
	}

	public SDACTQ3InputVO getSdactq3InputVO() {
        return sdactq3InputVO;
    }

    public void setSdactq3InputVO(SDACTQ3InputVO sdactq3InputVO) {
        this.sdactq3InputVO = sdactq3InputVO;
    }

    public SDACTQ4InputVO getSdactq4InputVO() {
        return sdactq4InputVO;
    }

    public void setSdactq4InputVO(SDACTQ4InputVO sdactq4InputVO) {
        this.sdactq4InputVO = sdactq4InputVO;
    }

    public SDACTQ5InputVO getSdactq5InputVO() {
        return sdactq5InputVO;
    }

    public void setSdactq5InputVO(SDACTQ5InputVO sdactq5InputVO) {
        this.sdactq5InputVO = sdactq5InputVO;
    }

    public NFBRN1InputVO getNfbrn1InputVO() {
        return nfbrn1InputVO;
    }

    public void setNfbrn1InputVO(NFBRN1InputVO nfbrn1InputVO) {
        this.nfbrn1InputVO = nfbrn1InputVO;
    }

    public NR098NInputVO getNr098NInputVO() {
        return nr098NInputVO;
    }

    public void setNr098NInputVO(NR098NInputVO nr098NInputVO) {
        this.nr098NInputVO = nr098NInputVO;
    }

    public NFBRN2InputVO getNfbrn2InputVO() {
        return nfbrn2InputVO;
    }

    public void setNfbrn2InputVO(NFBRN2InputVO nfbrn2InputVO) {
        this.nfbrn2InputVO = nfbrn2InputVO;
    }

    public NFBRN3InputVO getNfbrn3InputVO() {
        return nfbrn3InputVO;
    }

    public void setNfbrn3InputVO(NFBRN3InputVO nfbrn3InputVO) {
        this.nfbrn3InputVO = nfbrn3InputVO;
    }

    public NFBRN5InputVO getNfbrn5InputVO() {
        return nfbrn5InputVO;
    }

    public void setNfbrn5InputVO(NFBRN5InputVO nfbrn5InputVO) {
        this.nfbrn5InputVO = nfbrn5InputVO;
    }

    public NFBRN9InputVO getNfbrn9InputVO() {
        return nfbrn9InputVO;
    }

    public void setNfbrn9InputVO(NFBRN9InputVO nfbrn9InputVO) {
        this.nfbrn9InputVO = nfbrn9InputVO;
    }

    public AFBRN9InputVO getAfbrn9InputVO() {
        return afbrn9InputVO;
    }

    public void setAfbrn9InputVO(AFBRN9InputVO afbrn9InputVO) {
        this.afbrn9InputVO = afbrn9InputVO;
    }

    public FC81InputVO getFc81InputVO() {
        return fc81InputVO;
    }

    public void setFc81InputVO(FC81InputVO fc81InputVO) {
        this.fc81InputVO = fc81InputVO;
    }

    public FP052650InputVO getFp052650InputVO() {
        return fp052650InputVO;
    }

    public void setFp052650InputVO(FP052650InputVO fp052650InputVO) {
        this.fp052650InputVO = fp052650InputVO;
    }

    public FC032671InputVO getFc032671InputVO() {
        return fc032671InputVO;
    }

    public void setFc032671InputVO(FC032671InputVO fc032671InputVO) {
        this.fc032671InputVO = fc032671InputVO;
    }

    public CCM7818InputVO getCcm7818InputVO() {
        return ccm7818InputVO;
    }

    public void setCcm7818InputVO(CCM7818InputVO ccm7818InputVO) {
        this.ccm7818InputVO = ccm7818InputVO;
    }

    public CM6220RInputVO getCm6220rInputVO() {
        return cm6220rInputVO;
    }

    public void setCm6220rInputVO(CM6220RInputVO cm6220rInputVO) {
        this.cm6220rInputVO = cm6220rInputVO;
    }

    public EB172656InputVO getEb172656InputVO() {
        return eb172656InputVO;
    }

    public void setEb172656InputVO(EB172656InputVO eb172656InputVO) {
        this.eb172656InputVO = eb172656InputVO;
    }

    public EB202650InputVO getEb202650InputVO() {
        return eb202650InputVO;
    }

    public void setEb202650InputVO(EB202650InputVO eb202650InputVO) {
        this.eb202650InputVO = eb202650InputVO;
    }

    public EB202674InputVO getEb202674InputVO() {
        return eb202674InputVO;
    }

    public void setEb202674InputVO(EB202674InputVO eb202674InputVO) {
        this.eb202674InputVO = eb202674InputVO;
    }

    public SD120140InputVO getSd120140InputVO() {
        return sd120140InputVO;
    }

    public void setSd120140InputVO(SD120140InputVO sd120140InputVO) {
        this.sd120140InputVO = sd120140InputVO;
    }

    public EB312201InputVO getEb312201InputVO() {
        return eb312201InputVO;
    }

    public void setEb312201InputVO(EB312201InputVO eb312201InputVO) {
        this.eb312201InputVO = eb312201InputVO;
    }

    public BKDCD003InputVO getBkdcd003InputVO() {
        return bkdcd003InputVO;
    }

    public EB382201InputVO getEb382201InputVO() {
        return eb382201InputVO;
    }

    public void setEb382201InputVO(EB382201InputVO eb382201InputVO) {
        this.eb382201InputVO = eb382201InputVO;
    }

    public EB12020002InputVO getEb12020002InputVO() {
        return eb12020002InputVO;
    }

    public void setEb12020002InputVO(EB12020002InputVO eb12020002InputVO) {
        this.eb12020002InputVO = eb12020002InputVO;
    }

    public NMVIPAInputVO getNmvipaInputVO() {
        return nmvipaInputVO;
    }

    public void setNmvipaInputVO(NMVIPAInputVO nmvipaInputVO) {
        this.nmvipaInputVO = nmvipaInputVO;
    }

    public NMVP3AInputVO getNmvp3aInputVO() {
        return nmvp3aInputVO;
    }

    public void setNmvp3aInputVO(NMVP3AInputVO nmvp3aInputVO) {
        this.nmvp3aInputVO = nmvp3aInputVO;
    }

    public FC032154InputVO getFc032154InputVO() {
        return fc032154InputVO;
    }

    public void setFc032154InputVO(FC032154InputVO fc032154InputVO) {
        this.fc032154InputVO = fc032154InputVO;
    }

    public SDPRC09AInputVO getSdprc09aInputVO() {
        return sdprc09aInputVO;
    }

    public void setSaprc09aInputVO(SDPRC09AInputVO sdprc09aInputVO) {
        this.sdprc09aInputVO = sdprc09aInputVO;
    }

    public CCM002InputVO getCcm002InputVO() {
        return ccm002InputVO;
    }

    public void setCcm002InputVO(CCM002InputVO ccm002InputVO) {
        this.ccm002InputVO = ccm002InputVO;
    }

    public void setSdprc09aInputVO(SDPRC09AInputVO sdprc09aInputVO) {
        this.sdprc09aInputVO = sdprc09aInputVO;
    }

    public CEW012RInputVO getCew012rInputVO() {
        return cew012rInputVO;
    }

    public void setCew012rInputVO(CEW012RInputVO cew012rInputVO) {
        this.cew012rInputVO = cew012rInputVO;
    }

    public TP552697InputVO getTp552697InputVO() {
        return tp552697InputVO;
    }

    public void setTp552697InputVO(TP552697InputVO tp552697InputVO) {
        this.tp552697InputVO = tp552697InputVO;
    }

    public NFEE001InputVO getNfee001InputVO() {
        return nfee001InputVO;
    }

    public void setNfee001InputVO(NFEE001InputVO nfee001InputVO) {
        this.nfee001InputVO = nfee001InputVO;
    }

    public VN067NInputVO getVn067nInputVO() {
        return vn067nInputVO;
    }

    public void setVn067nInputVO(VN067NInputVO vn067nInputVO) {
        this.vn067nInputVO = vn067nInputVO;
    }

    public EBPMNInputVO getEbpmnInputVO() {
        return ebpmnInputVO;
    }

    public void setEbpmnInputVO(EBPMNInputVO ebpmnInputVO) {
        this.ebpmnInputVO = ebpmnInputVO;
    }
    
    public EBPMN2InputVO getEbpmn2InputVO() {
        return ebpmn2InputVO;
    }

    public void setEbpmn2InputVO(EBPMN2InputVO ebpmn2InputVO) {
        this.ebpmn2InputVO = ebpmn2InputVO;
    }

    public FC032659InputVO getFc032659InputVO() {
        return fc032659InputVO;
    }

    public void setFc032659InputVO(FC032659InputVO fc032659InputVO) {
        this.fc032659InputVO = fc032659InputVO;
    }

    public NRBRQ01InputVO getNrbrq01InputVO() {
        return nrbrq01InputVO;
    }

    public void setNrbrq01InputVO(NRBRQ01InputVO nrbrq01InputVO) {
        this.nrbrq01InputVO = nrbrq01InputVO;
    }

    public EB372602InputVO getEb372602InputVO() {
        return eb372602InputVO;
    }

    public void setEb372602InputVO(EB372602InputVO eb372602InputVO) {
        this.eb372602InputVO = eb372602InputVO;
    }

    public NR070NInputVO getNr070nInputVO() {
        return nr070nInputVO;
    }

    public void setNr070nInputVO(NR070NInputVO nr070nInputVO) {
        this.nr070nInputVO = nr070nInputVO;
    }

    public NR080NInputVO getNr080nInputVO() {
        return nr080nInputVO;
    }

    public void setNr080nInputVO(NR080NInputVO nr080nInputVO) {
        this.nr080nInputVO = nr080nInputVO;
    }

    public NR074NInputVO getNr074nInputVO() {
        return nr074nInputVO;
    }

    public NB052650InputVO getNb052650InputVO() {
        return nb052650InputVO;
    }

    public void setNb052650InputVO(NB052650InputVO nb052650InputVO) {
        this.nb052650InputVO = nb052650InputVO;
    }

    public void setNr074nInputVO(NR074NInputVO nr074nInputVO) {
        this.nr074nInputVO = nr074nInputVO;
    }

    public SDACTQ8InputVO getSdactq8InputVO() {
        return sdactq8InputVO;
    }

    public void setSdactq8InputVO(SDACTQ8InputVO sdactq8InputVO) {
        this.sdactq8InputVO = sdactq8InputVO;
    }

    public NMP8YBInputVO getNmp8ybInputVO() {
        return nmp8ybInputVO;
    }

    public void setNmp8ybInputVO(NMP8YBInputVO nmp8ybInputVO) {
        this.nmp8ybInputVO = nmp8ybInputVO;
    }

    public NMI001InputVO getNmi001InputVO() {
		return nmi001InputVO;
	}

	public void setNmi001InputVO(NMI001InputVO nmi001InputVO) {
		this.nmi001InputVO = nmi001InputVO;
	}

	public NMI002InputVO getNmi002InputVO() {
        return nmi002InputVO;
    }

    public void setNmi002InputVO(NMI002InputVO nmi002InputVO) {
        this.nmi002InputVO = nmi002InputVO;
    }

    public NMI003InputVO getNmi003InputVO() {
		return nmi003InputVO;
	}

	public void setNmi003InputVO(NMI003InputVO nmi003InputVO) {
		this.nmi003InputVO = nmi003InputVO;
	}

	public NMVP4AInputVO getNmvp4aInputVO() {
        return nmvp4aInputVO;
    }

    public void setNmvp4aInputVO(NMVP4AInputVO nmvp4aInputVO) {
        this.nmvp4aInputVO = nmvp4aInputVO;
    }

    public NMVP5AInputVO getNmvp5aInputVO() {
        return nmvp5aInputVO;
    }

    public void setNmvp5aInputVO(NMVP5AInputVO nmvp5aInputVO) {
        this.nmvp5aInputVO = nmvp5aInputVO;
    }

    public EB032282InputVO getEb032282InputVO() {
        return eb032282InputVO;
    }

    public void setEb032282InputVO(EB032282InputVO eb032282InputVO) {
        this.eb032282InputVO = eb032282InputVO;
    }

    public GD320140InputVO getGd320140InputVO() {
        return gd320140InputVO;
    }

    public void setGd320140InputVO(GD320140InputVO gd320140InputVO) {
        this.gd320140InputVO = gd320140InputVO;
    }
	public EB032168InputVO getEb032168InputVO() {
		return eb032168InputVO;
	}

	public void setEb032168InputVO(EB032168InputVO eb032168InputVO) {
		this.eb032168InputVO = eb032168InputVO;
	}

	public HD00070000InputVO getHd00070000InputVO() {
		return hd00070000InputVO;
	}

	public ESBUtilInputVO setHd00070000InputVO(HD00070000InputVO hd00070000InputVO) {
		this.hd00070000InputVO = hd00070000InputVO;
		setSpec(new SpecHD00070000());
		return this;
	}
	
	public AML004InputVO getAml004InputVO() {
        return aml004InputVO;
    }

    public ESBUtilInputVO setAml004InputVO(AML004InputVO aml004InputVO) {
        this.aml004InputVO = aml004InputVO;
        return this;
    }

	public NJBRVC3InputVO getNjbrvc3InputVO() {
		return njbrvc3InputVO;
	}

	public ESBUtilInputVO setNjbrvc3InputVO(NJBRVC3InputVO njbrvc3InputVO) {
		this.njbrvc3InputVO = njbrvc3InputVO;
		setSpec(new SpecNJBRVC3());
		return this;
	}
	
    public MVC110001InputVO getMvc110001InputVO() {
		return mvc110001InputVO;
	}

	public void setMvc110001InputVO(MVC110001InputVO mvc110001InputVO) {
		this.mvc110001InputVO = mvc110001InputVO;
	}

	public MVC310001InputVO getMvc310001InputVO() {
		return mvc310001InputVO;
	}

	public void setMvc310001InputVO(MVC310001InputVO mvc310001InputVO) {
		this.mvc310001InputVO = mvc310001InputVO;
	}

	public MVC310002InputVO getMvc310002InputVO() {
		return mvc310002InputVO;
	}

	public void setMvc310002InputVO(MVC310002InputVO mvc310002InputVO) {
		this.mvc310002InputVO = mvc310002InputVO;
	}
	
    public NJWEEA60InputVO getNjweea60InputVO() {
		return njweea60InputVO;
	}

	public void setNjweea60InputVO(NJWEEA60InputVO njweea60InputVO) {
		this.njweea60InputVO = njweea60InputVO;
	}

	public CM061435CRInputVO getCm061435crInputVO() {
		return cm061435crInputVO;
	}

	public void setCm061435crInputVO(CM061435CRInputVO cm061435crInputVO) {
		this.cm061435crInputVO = cm061435crInputVO;
	}

	public WMSHACRInputVO getWmshacrInputVO() {
		return wmshacrInputVO;
	}

	public void setWmshacrInputVO(WMSHACRInputVO wmshacrInputVO) {
		this.wmshacrInputVO = wmshacrInputVO;
	}

	public NJBRVH3InputVO getNjbrvh3InputVO() {
		return njbrvh3InputVO;
	}

	public void setNjbrvh3InputVO(NJBRVH3InputVO njbrvh3InputVO) {
		this.njbrvh3InputVO = njbrvh3InputVO;
	}

	public WMSHAIAVO getWmshaiaInputVO() {
		return wmshaiaInputVO;
	}

	public void setWmshaiaInputVO(WMSHAIAVO wmshaiaInputVO) {
		this.wmshaiaInputVO = wmshaiaInputVO;
	}

	public NFBRNEInputVO getNfbrneInputVO() {
		return nfbrneInputVO;
	}

	public void setNfbrneInputVO(NFBRNEInputVO nfbrneInputVO) {
		this.nfbrneInputVO = nfbrneInputVO;
	}
	

	public EsbSpec getSpec() {
        return spec;
    }

    public void setSpec(EsbSpec spec) {
        this.spec = spec;
    }

	public VN084NInputVO getVn084nInputVO() {
		return vn084nInputVO;
	}

	public void setVn084nInputVO(VN084NInputVO vn084nInputVO) {
		this.vn084nInputVO = vn084nInputVO;
	}

	public VN084N1InputVO getVn084n1InputVO() {
		return vn084n1InputVO;
	}

	public void setVn084n1InputVO(VN084N1InputVO vn084n1InputVO) {
		this.vn084n1InputVO = vn084n1InputVO;
	}

	public UK084NInputVO getUk084nInputVO() {
		return uk084nInputVO;
	}

	public void setUk084nInputVO(UK084NInputVO uk084nInputVO) {
		this.uk084nInputVO = uk084nInputVO;
	}

	public AJW084InputVO getAjw084InputVO() {
		return ajw084InputVO;
	}

	public void setAjw084InputVO(AJW084InputVO ajw084InputVO) {
		this.ajw084InputVO = ajw084InputVO;
	}

	public NJW084InputVO getNjw084InputVO() {
		return njw084InputVO;
	}

	public void setNjw084InputVO(NJW084InputVO njw084InputVO) {
		this.njw084InputVO = njw084InputVO;
	}

	public SPWEBQ1InputVO getSpwebq1InputVO() {
		return spwebq1InputVO;
	}

	public void setSpwebq1InputVO(SPWEBQ1InputVO spwebq1InputVO) {
		this.spwebq1InputVO = spwebq1InputVO;
	}

	public CE6220RInputVO getCe6220rInputVO() {
		return ce6220rInputVO;
	}

	public void setCe6220rInputVO(CE6220RInputVO ce6220rInputVO) {
		this.ce6220rInputVO = ce6220rInputVO;
	}

	public NJWEEA70InputVO getNjweea70InputVO() {
		return njweea70InputVO;
	}

	public void setNjweea70InputVO(NJWEEA70InputVO njweea70InputVO) {
		this.njweea70InputVO = njweea70InputVO;
	}

	public NFBRNFInputVO getNfbrnfInputVO() {
		return nfbrnfInputVO;
	}

	public void setNfbrnfInputVO(NFBRNFInputVO nfbrnfInputVO) {
		this.nfbrnfInputVO = nfbrnfInputVO;
	}

	public NFBRNGInputVO getNfbrngInputVO() {
		return nfbrngInputVO;
	}

	public void setNfbrngInputVO(NFBRNGInputVO nfbrngInputVO) {
		this.nfbrngInputVO = nfbrngInputVO;
	}

	public NFBRNHInputVO getNfbrnhInputVO() {
		return nfbrnhInputVO;
	}

	public void setNfbrnhInputVO(NFBRNHInputVO nfbrnhInputVO) {
		this.nfbrnhInputVO = nfbrnhInputVO;
	}

	public NFBRNIInputVO getNfbrniInputVO() {
		return nfbrniInputVO;
	}

	public void setNfbrniInputVO(NFBRNIInputVO nfbrniInputVO) {
		this.nfbrniInputVO = nfbrniInputVO;
	}

	public AJBRVA2InputVO getAjbrva2InputVO() {
		return ajbrva2InputVO;
	}

	public void setAjbrva2InputVO(AJBRVA2InputVO ajbrva2InputVO) {
		this.ajbrva2InputVO = ajbrva2InputVO;
	}

	public AJBRVA3InputVO getAjbrva3InputVO() {
		return ajbrva3InputVO;
	}

	public void setAjbrva3InputVO(AJBRVA3InputVO ajbrva3InputVO) {
		this.ajbrva3InputVO = ajbrva3InputVO;
	}

	public AJBRVA9InputVO getAjbrva9InputVO() {
		return ajbrva9InputVO;
	}

	public void setAjbrva9InputVO(AJBRVA9InputVO ajbrva9InputVO) {
		this.ajbrva9InputVO = ajbrva9InputVO;
	}

	public AJBRVB9InputVO getAjbrvb9InputVO() {
		return ajbrvb9InputVO;
	}

	public void setAjbrvb9InputVO(AJBRVB9InputVO ajbrvb9InputVO) {
		this.ajbrvb9InputVO = ajbrvb9InputVO;
	}

	public AJBRVC9InputVO getAjbrvc9InputVO() {
		return ajbrvc9InputVO;
	}

	public void setAjbrvc9InputVO(AJBRVC9InputVO ajbrvc9InputVO) {
		this.ajbrvc9InputVO = ajbrvc9InputVO;
	}

	public AJBRVD9InputVO getAjbrvd9InputVO() {
		return ajbrvd9InputVO;
	}

	public void setAjbrvd9InputVO(AJBRVD9InputVO ajbrvd9InputVO) {
		this.ajbrvd9InputVO = ajbrvd9InputVO;
	}
    
}
