package com.systex.jbranch.fubon.commons.esb.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.systex.jbranch.fubon.commons.esb.vo.MVC110001.MVC110001OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.MVC310001.MVC310001OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.MVC310002.MVC310002OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.afbrn8.AFBRN8OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.afee011.AFEE011OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrva2.AJBRVA2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrva3.AJBRVA3OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrva9.AJBRVA9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrvb9.AJBRVB9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrvc9.AJBRVC9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrvd9.AJBRVD9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajw084.AJW084OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.aml004.AML004OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.bkdcd003.BKDCD003OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ccm002.CCM002OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ccm7818.CCM7818OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ce6200r.CE6220ROutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.cew012r.CEW012ROutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.clm032151.CLM032151OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.cm061435cr.CM061435CROutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.cm6220r.CM6220ROutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb032168.EB032168OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb032282.EB032282OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb12020002.EB12020002OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb172656.EB172656OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb202650.EB202650OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb202674.EB202674OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb312201.EB312201OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb372602.EB372602OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb382201.EB382201OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ebpmn.EBPMN2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ebpmn.EBPMNOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032151.FC032151OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032154.FC032154OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032275.FC032275OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032659.FC032659OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032671.FC032671OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032675.FC032675OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc81.FC81OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032151.FP032151OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032671.FP032671OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032675.FP032675OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp052650.FP052650OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.gd320140.GD320140OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.hd00070000.HD00070000OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nb052650.NB052650OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn1.NFBRN1OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn2.NFBRN2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn3.NFBRN3OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn4.NFBRN4OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn5.NFBRN5OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn6.NFBRN6OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn7.NFBRN7OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn8.NFBRN8OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn9.NFBRN9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrne.NFBRNEOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrnf.NFBRNFOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrng.NFBRNGOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrnh.NFBRNHOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrni.NFBRNIOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee001.NFEE001OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee002.NFEE002OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee011.NFEE011OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee012.NFEE012OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee086.NFEE086OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfei001.NFEI001OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfei002.NFEI002OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfei003.NFEI003OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfvipa.NFVIPAOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrva1.NJBRVA1OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrva2.NJBRVA2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrva3.NJBRVA3OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrva9.NJBRVA9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvb1.NJBRVB1OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvb9.NJBRVB9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc1.NJBRVC1OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc2.NJBRVC2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc3.NJBRVC3OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc9.NJBRVC9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvd9.NJBRVD9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvh3.NJBRVH3OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvx1.NJBRVX1OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvx2.NJBRVX2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvx3.NJBRVX3OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njchklc2.NJCHKLC2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njw084.NJW084OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njweea60.NJWEEA60OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njweea70.NJWEEA70OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nkne01.NKNE01OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmi001.NMI001OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmi002.NMI002OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmi003.NMI003OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmp8yb.NMP8YBOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvipa.NMVIPAOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp3a.NMVP3AOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp4a.NMVP4AOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp5a.NMVP5AOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp6a.NMVP6AOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp7a.NMVP7AOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp8a.NMVP8AOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp9a.NMVP9AOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr070n.NR070NOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr074n.NR074NOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr080n.NR080NOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr096n.NR096NOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr097n.NR097NOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr098n.NR098NOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrq01.NRBRQ01OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrva1.NRBRVA1OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrva2.NRBRVA2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrva3.NRBRVA3OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrva9.NRBRVA9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc1.NRBRVC1OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc2.NRBRVC2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc3.NRBRVC3OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc4.NRBRVC4OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sc120100.SC120100OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sd120140.SD120140OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq20.SDACTQ20OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq3.SDACTQ3OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq4.SDACTQ4OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq5.SDACTQ5OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq8.SDACTQ8OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdprc09a.SDPRC09AOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.spwebq1.SPWEBQ1OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.tp032675.TP032675OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.tp552697.TP552697OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.uk084n.UK084NOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.vip032153.VIP032153OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.vn067n.VN067NOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.vn084n.VN084NOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.vn084n1.VN084N1OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.vn085n.VN085NOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.vr032675.VR032675OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032154.WMS032154OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032275.WMS032275OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032675.WMS032675OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms552697.WMS552697OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wmshacr.WMSHACROutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wmshaia.WMSHAIAVO;

/**
 * Created by SebastianWu on 2016/9/6.
 */
@XmlRootElement(name = "Tx") 
@XmlAccessorType(XmlAccessType.FIELD)
public class ESBUtilOutputVO {

	@XmlAttribute
	private String txid;
	@XmlAttribute
	private String encoding;
	@XmlAttribute
	private String fontremapping;

	@XmlElement(name = "TxHead")
	private TxHeadVO txHeadVO;

	@XmlElement(name = "TxBody")
	private ESBErrorVO esbErrorVO;

	@XmlElement(name = "FC032675")
	private FC032675OutputVO fc032675OutputVO;

	@XmlElement(name = "FC032659")
	private FC032659OutputVO fc032659OutputVO;

	@XmlElement(name = "TP032675")
	private TP032675OutputVO tp032675OutputVO;
	@XmlElement(name = "AFEE011")
	private AFEE011OutputVO afee011OutputVO;
	@XmlElement(name = "NFEE011")
	private NFEE011OutputVO nfee011OutputVO;
	@XmlElement(name = "FC032275")
	private FC032275OutputVO fc032275OutputVO;
	@XmlElement(name = "NFEE012")
	private NFEE012OutputVO nfee012OutputVO;
	@XmlElement(name = "NKNE01")
	private NKNE01OutputVO nkne01OutputVO;
	@XmlElement(name = "NKNET1")
	private NKNE01OutputVO nknet1OutputVO; //海外股/ETF金錢信託是否為首購 ，電文格式同NKNE01
	@XmlElement(name = "NRBRVA3")
	private NRBRVA3OutputVO nrbrva3OutputVO;
	@XmlElement(name = "NRBRVA9")
	private NRBRVA9OutputVO nrbrva9OutputVO;
	@XmlElement(name = "NRBRVT9")
	private NRBRVA9OutputVO nrbrvt9OutputVO; //海外股/ETF金錢信託理專鍵機股票交易，電文格式同NRBRVA9
	@XmlElement(name = "NFVIPA")
	private NFVIPAOutputVO nfvipaOutputVO;
	@XmlElement(name = "NRBRVA1")
	private NRBRVA1OutputVO nrbrva1OutputVO;
	@XmlElement(name = "NRBRVA2")
	private NRBRVA2OutputVO nrbrva2OutputVO;
	@XmlElement(name = "NR096N")
	private NR096NOutputVO nr096NOutputVO;
	@XmlElement(name = "NJBRVA9")
	private NJBRVA9OutputVO njbrva9OutputVO;
	@XmlElement(name = "NJBRVB9")
	private NJBRVB9OutputVO njbrvb9OutputVO;
	@XmlElement(name = "NJBRVB1")
	private NJBRVB1OutputVO njbrvb1OutputVO;
	@XmlElement(name = "NJBRVC1")
	private NJBRVC1OutputVO njbrvc1OutputVO;
	@XmlElement(name = "NJBRVC2")
	private NJBRVC2OutputVO njbrvc2OutputVO;
	@XmlElement(name = "AJBRVC1")
	private NJBRVC1OutputVO ajbrvc1OutputVO;
	@XmlElement(name = "AJBRVC2")
	private NJBRVC2OutputVO ajbrvc2OutputVO;
	@XmlElement(name = "NJBRVC9")
	private NJBRVC9OutputVO njbrvc9OutputVO;
	@XmlElement(name = "NJBRVD9")
	private NJBRVD9OutputVO njbrvd9OutputVO;
	
	//add by James 2017/07/13
	@XmlElement(name = "AJBRVB1")
	private NJBRVB1OutputVO ajbrvb1OutputVO;
	
	@XmlElement(name = "NJBRVA1")
	private NJBRVA1OutputVO njbrva1OutputVO;
	@XmlElement(name = "NJBRVA2")
	private NJBRVA2OutputVO njbrva2OutputVO;
	@XmlElement(name = "NRBRVC1")
	private NRBRVC1OutputVO nrbrvc1OutputVO;
	@XmlElement(name = "NRBRVC2")
	private NRBRVC2OutputVO nrbrvc2OutputVO;
	@XmlElement(name = "NRBRVC3")
	private NRBRVC3OutputVO nrbrvc3OutputVO;
	@XmlElement(name = "NRBRVC4")
	private NRBRVC4OutputVO nrbrvc4OutputVO;
	@XmlElement(name = "NJBRVA3")
	private NJBRVA3OutputVO njbrva3OutputVO;
	@XmlElement(name = "SDACTQ20")
	private SDACTQ20OutputVO sdactq20OutputVO;
	@XmlElement(name = "SDACTQ3")
	private SDACTQ3OutputVO sdactq3OutputVO;
	@XmlElement(name = "SDACTQ4")
	private SDACTQ4OutputVO sdactq4OutputVO;
	@XmlElement(name = "SDACTQ5")
	private SDACTQ5OutputVO sdactq5OutputVO;
	@XmlElement(name = "NFBRN1")
	private NFBRN1OutputVO nfbrn1OutputVO;
	@XmlElement(name = "NFBRN7")
	private NFBRN7OutputVO nfbrn7OutputVO;
	@XmlElement(name = "AFBRN7")
	private NFBRN7OutputVO afbrn7OutputVO;
	@XmlElement(name = "NFEE002")
	private NFEE002OutputVO nfee002OutputVO;
	@XmlElement(name = "AFEE002")
	private NFEE002OutputVO afee002OutputVO;
	@XmlElement(name = "VN085N")
	private VN085NOutputVO vn085nOutputVO;
	@XmlElement(name = "NFBRN6")
	private NFBRN6OutputVO nfbrn6OutputVO;
	@XmlElement(name = "NFBRN8")
	private NFBRN8OutputVO nfbrn8OutputVO;
	@XmlElement(name = "AFBRN8")
	private AFBRN8OutputVO afbrn8OutputVO;
	@XmlElement(name = "FC81")
	private FC81OutputVO fc81OutputVO;
	@XmlElement(name = "FP052650")
	private FP052650OutputVO fp052650OutputVO;
	@XmlElement(name = "NB052650")
	private NB052650OutputVO nb052650OutputVO;
	@XmlElement(name = "NFEE086")
	private NFEE086OutputVO nfee086OutputVO;
	@XmlElement(name = "NFBRN4")
	private NFBRN4OutputVO nfbrn4OutputVO;
	@XmlElement(name = "FC032151")
	private FC032151OutputVO fc032151OutputVO;
	@XmlElement(name = "NR097N")
	private NR097NOutputVO nr097NOutputVO;
	@XmlElement(name = "NR098N")
	private NR098NOutputVO nr098NOutputVO;
	@XmlElement(name = "NFBRN2")
	private NFBRN2OutputVO nfbrn2OutputVO;
	@XmlElement(name = "NFBRN3")
	private NFBRN3OutputVO nfbrn3OutputVO;
	@XmlElement(name = "NFBRN5")
	private NFBRN5OutputVO nfbrn5OutputVO;
	@XmlElement(name = "NFBRN9")
	private NFBRN9OutputVO nfbrn9OutputVO;
	
	//add by James 2017/06/09
	@XmlElement(name = "AFBRN9")
	private NFBRN9OutputVO afbrn9OutputVO;

	// add by walalala 2016/12/06
	@XmlElement(name = "FC032671")
	private FC032671OutputVO fc032671OutputVO;

	@XmlElement(name = "CCM7818")
	private CCM7818OutputVO ccm7818OutputVO;

	@XmlElement(name = "CM6220R")
	private CM6220ROutputVO cm6220rOutputVO;

	@XmlElement(name = "EB172656")
	private EB172656OutputVO eb172656OutputVO;

	@XmlElement(name = "EB202650")
	private EB202650OutputVO eb202650OutputVO;

	@XmlElement(name = "EB202674")
	private EB202674OutputVO eb202674OutputVO;

	@XmlElement(name = "SD120140")
	private SD120140OutputVO sd120140OutputVO;

	@XmlElement(name = "GD320140")
	private GD320140OutputVO gd320140OutputVO;

	@XmlElement(name = "BKDCD003")
	private BKDCD003OutputVO bkdcd003OutputVO;

	@XmlElement(name = "EB312201")
	private EB312201OutputVO eb312201OutputVO;

	@XmlElement(name = "EB382201")
	private EB382201OutputVO eb382201OutputVO;

	@XmlElement(name = "EB12020002")
	private EB12020002OutputVO eb12020002OutputVO;

	@XmlElement(name = "NMVIPA")
	private NMVIPAOutputVO nmvipaOutputVO;

	@XmlElement(name = "NMVP3A")
	private NMVP3AOutputVO nmvp3aOutputVO;

	@XmlElement(name = "FC032154")
	private FC032154OutputVO fc032154OutputVO;

	// SI帳戶總覽
	@XmlElement(name = "SDPRC09A")
	private SDPRC09AOutputVO sdprc09aOutputVO;

	@XmlElement(name = "CCM002")
	private CCM002OutputVO ccm002OutputVO;

	@XmlElement(name = "SC120100")
	private SC120100OutputVO sc120100OutputVO;

	@XmlElement(name = "NFEE001")
	private NFEE001OutputVO nfee001OutputVO;

	@XmlElement(name = "NJCHKLC2")
	private NJCHKLC2OutputVO njchklc20OutputVO;

	@XmlElement(name = "VN067N")
	private VN067NOutputVO vn067nOutputVO;
	
	@XmlElement(name = "EBPMN")
	private EBPMNOutputVO ebpmnOutputVO;
	@XmlElement(name = "EBPMN2")
	private EBPMN2OutputVO ebpmn2OutputVO;
	@XmlElement(name = "EB372602")
	private EB372602OutputVO eb372602OutputVO;

	// add by Stella 2017/01/05
	@XmlElement(name = "CEW012R")
	private CEW012ROutputVO cew012rOutputVO;

	@XmlElement(name = "TP552697")
	private TP552697OutputVO tp552697OutputVO;
	
	@XmlElement(name = "NRBRQ01")
	private NRBRQ01OutputVO nrbrq01OutputVO;
	
    @XmlElement(name = "NR070N")// 海外ETF/股票可用餘額查詢
    private NR070NOutputVO nr070nOutputVO;
    
    @XmlElement(name = "NR080N")// 股票委託明細
    private NR080NOutputVO nr080nOutputVO;
    @XmlElement(name = "NR074N")// 即時成交交易資訊
    private NR074NOutputVO nr074nOutputVO;
    
    @XmlElement(name = "SDACTQ8")// 結構型商品推介同意註記
    private SDACTQ8OutputVO sdactq8OutputVO;
 
    @XmlElement(name = "NMP8YB")// 奈米投庫存查詢
    private NMP8YBOutputVO nmp8ybOutputVO;
    
    @XmlElement(name = "NMI001")// 奈米投個人風險屬性、卡片標籤查詢
    private NMI001OutputVO nmi001OutputVO;
    
    @XmlElement(name = "NMI002")// 奈米投庫存查詢
    private NMI002OutputVO nmi002OutputVO;
    
    @XmlElement(name = "NMI003")// 奈米投契約交易紀錄查詢
    private NMI003OutputVO nmi003OutputVO;

    @XmlElement(name = "NMVP4A")// 金錢信託_台外幣活存明細查詢
    private NMVP4AOutputVO nmvp4aOutputVO;
  
  	@XmlElement(name = "NMVP5A")// 金錢信託_台外幣定存明細查詢
  	private NMVP5AOutputVO nmvp5aOutputVO;
    
    @XmlElement(name = "EB032282")// 解除學歷待確認註記
    private EB032282OutputVO eb032282OutputVO;
    
    @XmlElement(name = "NMVP7A") // 金錢信託_首購檢核
    private NMVP7AOutputVO nmvp7aOutputVO;
    
    @XmlElement(name = "NMVP6A") // 金錢信託_客戶統編查詢契約
    private NMVP6AOutputVO nmvp6aOutputVO;
    
    @XmlElement(name = "NMVP8A") // 金錢信託_查詢關係人-管理運用人
    private NMVP8AOutputVO nmvp8aOutputVO;
    
    @XmlElement(name = "NMVP9A") // 金錢信託_ETF查詢客戶庫存
    private NMVP9AOutputVO nmvp9aOutputVO;
    
    @XmlElement(name = "NJBRVX1") // 債券手續費議價-金錢信託
    private NJBRVX1OutputVO njbrvx1OutputVO;
    
    @XmlElement(name = "NJBRVX2") // 債券申購交易-金錢信託
    private NJBRVX2OutputVO njbrvx2OutputVO;
    
    @XmlElement(name = "NJBRVX3") // 債券贖回庫存查詢-金錢信託
    private NJBRVX3OutputVO njbrvx3OutputVO;
    
    @XmlElement(name = "NFBRX1") // 基金申購交易-金錢信託
    private NFBRN1OutputVO nfbrx1OutputVO;
    
    @XmlElement(name = "NFBRX7") // 基金申購手續費交易-金錢信託
    private NFBRN7OutputVO nfbrx7OutputVO;
    
    @XmlElement(name = "NFBRX9") // 基金庫存查詢-金錢信託
    private NFBRN9OutputVO nfbrx9OutputVO;
    
    // add by SamTu 2018.02.13~
    @XmlElement(name = "FP032671")
	private FP032671OutputVO fp032671OutputVO;
    @XmlElement(name = "WMS032275")
	private WMS032275OutputVO wms032275OutputVO;
    @XmlElement(name = "CLM032151")
   	private CLM032151OutputVO clm032151OutputVO;
    @XmlElement(name = "FP032675")
	private FP032675OutputVO fp032675OutputVO;
    @XmlElement(name = "VR032675")
	private VR032675OutputVO vr032675OutputVO;
    @XmlElement(name = "WMS032675")
   	private WMS032675OutputVO wms032675OutputVO;
    @XmlElement(name = "FP032151")
   	private FP032151OutputVO fp032151OutputVO;
    @XmlElement(name = "WMS552697")
   	private WMS552697OutputVO wms552697OutputVO;
    @XmlElement(name = "WMS032154")
   	private WMS032154OutputVO wms032154OutputVO;
    @XmlElement(name = "VIP032153")
   	private VIP032153OutputVO vip032153OutputVO;

    @XmlElement(name = "NFEI002")
   	private NFEI002OutputVO nfei002OutputVO;
    @XmlElement(name = "NFEI001")
   	private NFEI001OutputVO nfei001OutputVO;
    @XmlElement(name = "NFEI003")
   	private NFEI003OutputVO nfei003OutputVO;
	@XmlElement(name = "AML004")
	private AML004OutputVO aml004OutputVO;
	@XmlElement(name = "HD00070000")
	private HD00070000OutputVO hd00070000OutputVO;
	@XmlElement(name = "AFEE003")
	private VN085NOutputVO afee003OutputVO;
	@XmlElement(name = "AFBRN1")
	private NFBRN1OutputVO afbrn1OutputVO;
	@XmlElement(name = "AFBRN2")
	private NFBRN2OutputVO afbrn2OutputVO;
	@XmlElement(name = "AFBRN3")
	private NFBRN3OutputVO afbrn3OutputVO;
	@XmlElement(name = "AFBRN5")
	private NFBRN5OutputVO afbrn5OutputVO;
	@XmlElement(name = "AFBRN6")
	private NFBRN6OutputVO afbrn6OutputVO;
	@XmlElement(name = "NJBRVC3")
	private NJBRVC3OutputVO njbrvc3OutputVO;
	@XmlElement(name = "AJBRVC3")
	private NJBRVC3OutputVO ajbrvc3OutputVO;

	@XmlElement(name = "MVC110001")
	private MVC110001OutputVO mvc110001OutputVO;
	@XmlElement(name = "MVC310001")
	private MVC310001OutputVO mvc310001OutputVO;
	@XmlElement(name = "MVC310002")
	private MVC310002OutputVO mvc310002OutputVO;

	@XmlElement(name = "NJWEEA60")
	private NJWEEA60OutputVO njweea60OutputVO;
	
	@XmlElement(name = "CM061435CR")
	private CM061435CROutputVO cm061435crOutputVO;
	@XmlElement(name = "WMSHACR")
	private WMSHACROutputVO wmshacrOutputVO;
	@XmlElement(name = "NJBRVH3")
	private NJBRVH3OutputVO njbrvh3OutputVO;
	@XmlElement(name = "WMSHAD001")
	private NJBRVH3OutputVO wmshad001OutputVO; //上下行同NJBRVH3
	@XmlElement(name = "WMSHAD003")
	private NJBRVH3OutputVO wmshad003OutputVO; //上下行同NJBRVH3
	@XmlElement(name = "AJBRVH3")
	private NJBRVH3OutputVO ajbrvh3OutputVO; //上下行同NJBRVH3
	@XmlElement(name = "WMSHAD005")
	private NJBRVH3OutputVO wmshad005OutputVO; //上下行同NJBRVH3
	@XmlElement(name = "NFBRND")
	private NJBRVH3OutputVO nfbrndOutputVO; //上下行同NJBRVH3
	@XmlElement(name = "AFBRND")
	private NJBRVH3OutputVO afbrndOutputVO; //上下行同NJBRVH3
	@XmlElement(name = "NFBRNE")
	private NFBRNEOutputVO nfbrneOutputVO; //國外私募基金贖回修正單位數交易電文(DBU)
	@XmlElement(name = "AFBRNE")
	private NFBRNEOutputVO afbrneOutputVO; //國外私募基金贖回修正單位數交易電文(OBU格式同NFBRNE)
	@XmlElement(name = "WMSHAIA")
	private WMSHAIAVO wmshaiaOutputVO; //高資產客戶投組適配承作，取得風險檢核資訊
	@XmlElement(name = "NFBRNF")
	private NFBRNFOutputVO nfbrnfOutputVO; //動態鎖利基金申購交易電文(DBU)
	@XmlElement(name = "AFBRNF")
	private NFBRNFOutputVO afbrnfOutputVO; //動態鎖利基金申購交易電文(OBU格式同NFBRNF)
	@XmlElement(name = "NFBRNG")
	private NFBRNGOutputVO nfbrngOutputVO; //動態鎖利母基金加碼交易電文(DBU)
	@XmlElement(name = "AFBRNG")
	private NFBRNGOutputVO afbrngOutputVO; //動態鎖利母基金加碼交易電文(OBU格式同NFBRNG)
	@XmlElement(name = "NFBRNH")
	private NFBRNHOutputVO nfbrnhOutputVO; //動態鎖利贖回交易電文(DBU)
	@XmlElement(name = "AFBRNH")
	private NFBRNHOutputVO afbrnhOutputVO; //動態鎖利贖回交易電文(OBU格式同NFBRNH)
	@XmlElement(name = "NFBRNI")
	private NFBRNIOutputVO nfbrniOutputVO; //動態鎖利事件變更交易電文(DBU)
	@XmlElement(name = "AFBRNI")
	private NFBRNIOutputVO afbrniOutputVO; //動態鎖利事件變更交易電文(OBU格式同NFBRNI)
	//#1913
	@XmlElement(name = "VN084N")
	private VN084NOutputVO vn084nOutputVO; 
	@XmlElement(name = "VN084N1")
	private VN084N1OutputVO vn084n1OutputVO;
	@XmlElement(name = "UK084N")
	private UK084NOutputVO uk084nOutputvo;
	@XmlElement(name = "AJW084")
	private AJW084OutputVO ajw084OutputVO;
	@XmlElement(name = "NJW084")
	private NJW084OutputVO njw084OutputVO;
	@XmlElement(name = "SPWEBQ1")
	private SPWEBQ1OutputVO spwebq1OutputVO;
	
	//#1976
	@XmlElement(name = "CE6220R")
	private CE6220ROutputVO ce6220rOutputVO;
	
	// #2056：WMS-CR-20210923-02_新增海外債預約交易功能
	@XmlElement(name = "NJWEEA70")
	private NJWEEA70OutputVO njweea70OutputVO;
	
	// #：WMS-CR-20240311-01_海外債券及SN，OBU申購及贖回套表功能
	@XmlElement(name = "AJBRVA2")
    private AJBRVA2OutputVO ajbrva2OutputVO; 	// 理專鍵機債券手續費議價OBU
    @XmlElement(name = "AJBRVA3") 
    private AJBRVA3OutputVO ajbrva3OutputVO; 	// 理專鍵機債券首購查詢OBU
    @XmlElement(name = "AJBRVA9") 
    private AJBRVA9OutputVO ajbrva9OutputVO; 	// 理專鍵機債券申購交易OBU
    @XmlElement(name = "AJBRVB9") 
    private AJBRVB9OutputVO ajbrvb9OutputVO; 	// 理專鍵機債券贖回交易OBU
    @XmlElement(name = "AJBRVC9") 
    private AJBRVC9OutputVO ajbrvc9OutputVO; 	// 理專鍵機債券長效單申購交易OBU
    @XmlElement(name = "AJBRVD9") 
    private AJBRVD9OutputVO ajbrvd9OutputVO; 	// 理專鍵機債券長效單贖回交易OBU
	
	public NMVP7AOutputVO getNmvp7aOutputVO() {
		return nmvp7aOutputVO;
	}

	public void setNmvp7aOutputVO(NMVP7AOutputVO nmvp7aOutputVO) {
		this.nmvp7aOutputVO = nmvp7aOutputVO;
	}

	public NMVP6AOutputVO getNmvp6aOutputVO() {
		return nmvp6aOutputVO;
	}

	public void setNmvp6aOutputVO(NMVP6AOutputVO nmvp6aOutputVO) {
		this.nmvp6aOutputVO = nmvp6aOutputVO;
	}

	public NMVP8AOutputVO getNmvp8aOutputVO() {
		return nmvp8aOutputVO;
	}

	public void setNmvp8aOutputVO(NMVP8AOutputVO nmvp8aOutputVO) {
		this.nmvp8aOutputVO = nmvp8aOutputVO;
	}

	public NMVP9AOutputVO getNmvp9aOutputVO() {
		return nmvp9aOutputVO;
	}

	public void setNmvp9aOutputVO(NMVP9AOutputVO nmvp9aOutputVO) {
		this.nmvp9aOutputVO = nmvp9aOutputVO;
	}

	public NJBRVX1OutputVO getNjbrvx1OutputVO() {
		return njbrvx1OutputVO;
	}

	public void setNjbrvx1OutputVO(NJBRVX1OutputVO njbrvx1OutputVO) {
		this.njbrvx1OutputVO = njbrvx1OutputVO;
	}

	public NJBRVX2OutputVO getNjbrvx2OutputVO() {
		return njbrvx2OutputVO;
	}

	public void setNjbrvx2OutputVO(NJBRVX2OutputVO njbrvx2OutputVO) {
		this.njbrvx2OutputVO = njbrvx2OutputVO;
	}

	public NJBRVX3OutputVO getNjbrvx3OutputVO() {
		return njbrvx3OutputVO;
	}

	public void setNjbrvx3OutputVO(NJBRVX3OutputVO njbrvx3OutputVO) {
		this.njbrvx3OutputVO = njbrvx3OutputVO;
	}


    @XmlElement(name = "EB032168") // 洗錢防制電文 (AML & Precheck)
    private EB032168OutputVO eb032168OutputVO;
    

	public NFBRN1OutputVO getNfbrx1OutputVO() {
		return nfbrx1OutputVO;
	}

	public void setNfbrx1OutputVO(NFBRN1OutputVO nfbrx1OutputVO) {
		this.nfbrx1OutputVO = nfbrx1OutputVO;
	}

	public NFBRN7OutputVO getNfbrx7OutputVO() {
		return nfbrx7OutputVO;
	}

	public void setNfbrx7OutputVO(NFBRN7OutputVO nfbrx7OutputVO) {
		this.nfbrx7OutputVO = nfbrx7OutputVO;
	}

	public NFBRN9OutputVO getNfbrx9OutputVO() {
		return nfbrx9OutputVO;
	}

	public void setNfbrx9OutputVO(NFBRN9OutputVO nfbrx9OutputVO) {
		this.nfbrx9OutputVO = nfbrx9OutputVO;
	}

	public NFEI003OutputVO getNfei003OutputVO() {
		return nfei003OutputVO;
	}

	public void setNfei003OutputVO(NFEI003OutputVO nfei003OutputVO) {
		this.nfei003OutputVO = nfei003OutputVO;
	}

	public NFEI001OutputVO getNfei001OutputVO() {
		return nfei001OutputVO;
	}

	public void setNfei001OutputVO(NFEI001OutputVO nfei001OutputVO) {
		this.nfei001OutputVO = nfei001OutputVO;
	}

	public NFEI002OutputVO getNfei002OutputVO() {
		return nfei002OutputVO;
	}

	public void setNfei002OutputVO(NFEI002OutputVO nfei002OutputVO) {
		this.nfei002OutputVO = nfei002OutputVO;
	}

	public VIP032153OutputVO getVip032153OutputVO() {
		return vip032153OutputVO;
	}

	public void setVip032153OutputVO(VIP032153OutputVO vip032153OutputVO) {
		this.vip032153OutputVO = vip032153OutputVO;
	}

	public WMS552697OutputVO getWms552697OutputVO() {
		return wms552697OutputVO;
	}

	public void setWms552697OutputVO(WMS552697OutputVO wms552697OutputVO) {
		this.wms552697OutputVO = wms552697OutputVO;
	}

	public WMS032154OutputVO getWms032154OutputVO() {
		return wms032154OutputVO;
	}

	public void setWms032154OutputVO(WMS032154OutputVO wms032154OutputVO) {
		this.wms032154OutputVO = wms032154OutputVO;
	}

	public FP032151OutputVO getFp032151OutputVO() {
		return fp032151OutputVO;
	}

	public void setFp032151OutputVO(FP032151OutputVO fp032151OutputVO) {
		this.fp032151OutputVO = fp032151OutputVO;
	}

	public WMS032675OutputVO getWms032675OutputVO() {
		return wms032675OutputVO;
	}

	public void setWms032675OutputVO(WMS032675OutputVO wms032675OutputVO) {
		this.wms032675OutputVO = wms032675OutputVO;
	}

	public CLM032151OutputVO getClm032151OutputVO() {
		return clm032151OutputVO;
	}

	public void setClm032151OutputVO(CLM032151OutputVO clm032151OutputVO) {
		this.clm032151OutputVO = clm032151OutputVO;
	}

	public FP032675OutputVO getFp032675OutputVO() {
		return fp032675OutputVO;
	}

	public void setFp032675OutputVO(FP032675OutputVO fp032675OutputVO) {
		this.fp032675OutputVO = fp032675OutputVO;
	}

	public VR032675OutputVO getVr032675OutputVO() {
		return vr032675OutputVO;
	}

	public void setVr032675OutputVO(VR032675OutputVO vr032675OutputVO) {
		this.vr032675OutputVO = vr032675OutputVO;
	}

	public WMS032275OutputVO getWms032275OutputVO() {
		return wms032275OutputVO;
	}

	public void setWms032275OutputVO(WMS032275OutputVO wms032275OutputVO) {
		this.wms032275OutputVO = wms032275OutputVO;
	}

	public FP032671OutputVO getFp032671OutputVO() {
		return fp032671OutputVO;
	}

	public void setFp032671OutputVO(FP032671OutputVO fp032671OutputVO) {
		this.fp032671OutputVO = fp032671OutputVO;
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

	public void setTxHeadVO(TxHeadVO txHeadVO) {
		this.txHeadVO = txHeadVO;
	}

	public ESBErrorVO getEsbErrorVO() {
		return esbErrorVO;
	}

	public void setEsbErrorVO(ESBErrorVO esbErrorVO) {
		this.esbErrorVO = esbErrorVO;
	}

	public FC032675OutputVO getFc032675OutputVO() {
		return fc032675OutputVO;
	}

	public void setFc032675OutputVO(FC032675OutputVO fc032675OutputVO) {
		this.fc032675OutputVO = fc032675OutputVO;
	}

	public FC032659OutputVO getFc032659OutputVO() {
		return fc032659OutputVO;
	}

	public void setFc032659OutputVO(FC032659OutputVO fc032659OutputVO) {
		this.fc032659OutputVO = fc032659OutputVO;
	}

	public TP032675OutputVO getTp032675OutputVO() {
		return tp032675OutputVO;
	}

	public void setTp032675OutputVO(TP032675OutputVO tp032675OutputVO) {
		this.tp032675OutputVO = tp032675OutputVO;
	}

	public AFEE011OutputVO getAfee011OutputVO() {
		return afee011OutputVO;
	}

	public void setAfee011OutputVO(AFEE011OutputVO afee011OutputVO) {
		this.afee011OutputVO = afee011OutputVO;
	}

	public NFEE011OutputVO getNfee011OutputVO() {
		return nfee011OutputVO;
	}

	public void setNfee011OutputVO(NFEE011OutputVO nfee011OutputVO) {
		this.nfee011OutputVO = nfee011OutputVO;
	}

	public FC032275OutputVO getFc032275OutputVO() {
		return fc032275OutputVO;
	}

	public void setFc032275OutputVO(FC032275OutputVO fc032275OutputVO) {
		this.fc032275OutputVO = fc032275OutputVO;
	}

	public NFEE012OutputVO getNfee012OutputVO() {
		return nfee012OutputVO;
	}

	public void setNfee012OutputVO(NFEE012OutputVO nfee012OutputVO) {
		this.nfee012OutputVO = nfee012OutputVO;
	}

	public NKNE01OutputVO getNkne01OutputVO() {
		return nkne01OutputVO;
	}

	public void setNkne01OutputVO(NKNE01OutputVO nkne01OutputVO) {
		this.nkne01OutputVO = nkne01OutputVO;
	}

	public NKNE01OutputVO getNknet1OutputVO() {
		return nknet1OutputVO;
	}

	public void setNknet1OutputVO(NKNE01OutputVO nknet1OutputVO) {
		this.nknet1OutputVO = nknet1OutputVO;
	}

	public NRBRVA3OutputVO getNrbrva3OutputVO() {
		return nrbrva3OutputVO;
	}

	public void setNrbrva3OutputVO(NRBRVA3OutputVO nrbrva3OutputVO) {
		this.nrbrva3OutputVO = nrbrva3OutputVO;
	}

	public NRBRVA9OutputVO getNrbrva9OutputVO() {
		return nrbrva9OutputVO;
	}

	public void setNrbrva9OutputVO(NRBRVA9OutputVO nrbrva9OutputVO) {
		this.nrbrva9OutputVO = nrbrva9OutputVO;
	}

	public NRBRVA9OutputVO getNrbrvt9OutputVO() {
		return nrbrvt9OutputVO;
	}

	public void setNrbrvt9OutputVO(NRBRVA9OutputVO nrbrvt9OutputVO) {
		this.nrbrvt9OutputVO = nrbrvt9OutputVO;
	}

	public NFVIPAOutputVO getNfvipaOutputVO() {
		return nfvipaOutputVO;
	}

	public void setNfvipaOutputVO(NFVIPAOutputVO nfvipaOutputVO) {
		this.nfvipaOutputVO = nfvipaOutputVO;
	}

	public NRBRVA1OutputVO getNrbrva1OutputVO() {
		return nrbrva1OutputVO;
	}

	public void setNrbrva1OutputVO(NRBRVA1OutputVO nrbrva1OutputVO) {
		this.nrbrva1OutputVO = nrbrva1OutputVO;
	}

	public NRBRVA2OutputVO getNrbrva2OutputVO() {
		return nrbrva2OutputVO;
	}

	public void setNrbrva2OutputVO(NRBRVA2OutputVO nrbrva2OutputVO) {
		this.nrbrva2OutputVO = nrbrva2OutputVO;
	}

	public NR096NOutputVO getNr096NOutputVO() {
		return nr096NOutputVO;
	}

	public void setNr096NOutputVO(NR096NOutputVO nr096nOutputVO) {
		nr096NOutputVO = nr096nOutputVO;
	}

	public NJBRVA9OutputVO getNjbrva9OutputVO() {
		return njbrva9OutputVO;
	}

	public void setNjbrva9OutputVO(NJBRVA9OutputVO njbrva9OutputVO) {
		this.njbrva9OutputVO = njbrva9OutputVO;
	}

	public NJBRVB9OutputVO getNjbrvb9OutputVO() {
		return njbrvb9OutputVO;
	}

	public void setNjbrvb9OutputVO(NJBRVB9OutputVO njbrvb9OutputVO) {
		this.njbrvb9OutputVO = njbrvb9OutputVO;
	}

	public NJBRVB1OutputVO getNjbrvb1OutputVO() {
		return njbrvb1OutputVO;
	}

	public void setNjbrvb1OutputVO(NJBRVB1OutputVO njbrvb1OutputVO) {
		this.njbrvb1OutputVO = njbrvb1OutputVO;
	}
	
	public NJBRVC1OutputVO getNjbrvc1OutputVO() {
		return njbrvc1OutputVO;
	}

	public void setNjbrvc1OutputVO(NJBRVC1OutputVO njbrvc1OutputVO) {
		this.njbrvc1OutputVO = njbrvc1OutputVO;
	}

	public NJBRVC2OutputVO getNjbrvc2OutputVO() {
		return njbrvc2OutputVO;
	}

	public void setNjbrvc2OutputVO(NJBRVC2OutputVO njbrvc2OutputVO) {
		this.njbrvc2OutputVO = njbrvc2OutputVO;
	}

	public NJBRVC1OutputVO getAjbrvc1OutputVO() {
		return ajbrvc1OutputVO;
	}

	public void setAjbrvc1OutputVO(NJBRVC1OutputVO ajbrvc1OutputVO) {
		this.ajbrvc1OutputVO = ajbrvc1OutputVO;
	}

	public NJBRVC2OutputVO getAjbrvc2OutputVO() {
		return ajbrvc2OutputVO;
	}

	public void setAjbrvc2OutputVO(NJBRVC2OutputVO ajbrvc2OutputVO) {
		this.ajbrvc2OutputVO = ajbrvc2OutputVO;
	}

	public NJBRVC9OutputVO getNjbrvc9OutputVO() {
		return njbrvc9OutputVO;
	}

	public void setNjbrvc9OutputVO(NJBRVC9OutputVO njbrvc9OutputVO) {
		this.njbrvc9OutputVO = njbrvc9OutputVO;
	}

	public NJBRVD9OutputVO getNjbrvd9OutputVO() {
		return njbrvd9OutputVO;
	}

	public void setNjbrvd9OutputVO(NJBRVD9OutputVO njbrvd9OutputVO) {
		this.njbrvd9OutputVO = njbrvd9OutputVO;
	}

	public NJBRVB1OutputVO getAjbrvb1OutputVO() {
		return ajbrvb1OutputVO;
	}

	public void setAjbrvb1OutputVO(NJBRVB1OutputVO ajbrvb1OutputVO) {
		this.ajbrvb1OutputVO = ajbrvb1OutputVO;
	}

	public NJBRVA1OutputVO getNjbrva1OutputVO() {
		return njbrva1OutputVO;
	}

	public void setNjbrva1OutputVO(NJBRVA1OutputVO njbrva1OutputVO) {
		this.njbrva1OutputVO = njbrva1OutputVO;
	}

	public NJBRVA2OutputVO getNjbrva2OutputVO() {
		return njbrva2OutputVO;
	}

	public void setNjbrva2OutputVO(NJBRVA2OutputVO njbrva2OutputVO) {
		this.njbrva2OutputVO = njbrva2OutputVO;
	}

	public NRBRVC1OutputVO getNrbrvc1OutputVO() {
		return nrbrvc1OutputVO;
	}

	public void setNrbrvc1OutputVO(NRBRVC1OutputVO nrbrvc1OutputVO) {
		this.nrbrvc1OutputVO = nrbrvc1OutputVO;
	}

	public NRBRVC2OutputVO getNrbrvc2OutputVO() {
		return nrbrvc2OutputVO;
	}

	public void setNrbrvc2OutputVO(NRBRVC2OutputVO nrbrvc2OutputVO) {
		this.nrbrvc2OutputVO = nrbrvc2OutputVO;
	}

	public NRBRVC3OutputVO getNrbrvc3OutputVO() {
		return nrbrvc3OutputVO;
	}

	public void setNrbrvc3OutputVO(NRBRVC3OutputVO nrbrvc3OutputVO) {
		this.nrbrvc3OutputVO = nrbrvc3OutputVO;
	}

	public NRBRVC4OutputVO getNrbrvc4OutputVO() {
		return nrbrvc4OutputVO;
	}

	public void setNrbrvc4OutputVO(NRBRVC4OutputVO nrbrvc4OutputVO) {
		this.nrbrvc4OutputVO = nrbrvc4OutputVO;
	}

	public NJBRVA3OutputVO getNjbrva3OutputVO() {
		return njbrva3OutputVO;
	}

	public void setNjbrva3OutputVO(NJBRVA3OutputVO njbrva3OutputVO) {
		this.njbrva3OutputVO = njbrva3OutputVO;
	}

	public SDACTQ20OutputVO getSdactq20OutputVO() {
		return sdactq20OutputVO;
	}

	public void setSdactq20OutputVO(SDACTQ20OutputVO sdactq20OutputVO) {
		this.sdactq20OutputVO = sdactq20OutputVO;
	}

	public SDACTQ3OutputVO getSdactq3OutputVO() {
		return sdactq3OutputVO;
	}

	public void setSdactq3OutputVO(SDACTQ3OutputVO sdactq3OutputVO) {
		this.sdactq3OutputVO = sdactq3OutputVO;
	}

	public SDACTQ4OutputVO getSdactq4OutputVO() {
		return sdactq4OutputVO;
	}

	public void setSdactq4OutputVO(SDACTQ4OutputVO sdactq4OutputVO) {
		this.sdactq4OutputVO = sdactq4OutputVO;
	}

	public SDACTQ5OutputVO getSdactq5OutputVO() {
		return sdactq5OutputVO;
	}

	public void setSdactq5OutputVO(SDACTQ5OutputVO sdactq5OutputVO) {
		this.sdactq5OutputVO = sdactq5OutputVO;
	}

	public NFBRN1OutputVO getNfbrn1OutputVO() {
		return nfbrn1OutputVO;
	}

	public void setNfbrn1OutputVO(NFBRN1OutputVO nfbrn1OutputVO) {
		this.nfbrn1OutputVO = nfbrn1OutputVO;
	}

	public NFBRN7OutputVO getNfbrn7OutputVO() {
		return nfbrn7OutputVO;
	}

	public void setNfbrn7OutputVO(NFBRN7OutputVO nfbrn7OutputVO) {
		this.nfbrn7OutputVO = nfbrn7OutputVO;
	}

	public NFEE002OutputVO getNfee002OutputVO() {
		return nfee002OutputVO;
	}

	public void setNfee002OutputVO(NFEE002OutputVO nfee002OutputVO) {
		this.nfee002OutputVO = nfee002OutputVO;
	}

	public VN085NOutputVO getVn085nOutputVO() {
		return vn085nOutputVO;
	}

	public void setVn085nOutputVO(VN085NOutputVO vn085nOutputVO) {
		this.vn085nOutputVO = vn085nOutputVO;
	}

	public NFBRN6OutputVO getNfbrn6OutputVO() {
		return nfbrn6OutputVO;
	}

	public void setNfbrn6OutputVO(NFBRN6OutputVO nfbrn6OutputVO) {
		this.nfbrn6OutputVO = nfbrn6OutputVO;
	}

	public NFBRN8OutputVO getNfbrn8OutputVO() {
		return nfbrn8OutputVO;
	}

	public void setNfbrn8OutputVO(NFBRN8OutputVO nfbrn8OutputVO) {
		this.nfbrn8OutputVO = nfbrn8OutputVO;
	}

	public AFBRN8OutputVO getAfbrn8OutputVO() {
		return afbrn8OutputVO;
	}

	public void setAfbrn8OutputVO(AFBRN8OutputVO afbrn8OutputVO) {
		this.afbrn8OutputVO = afbrn8OutputVO;
	}

	public FC81OutputVO getFc81OutputVO() {
		return fc81OutputVO;
	}

	public void setFc81OutputVO(FC81OutputVO fc81OutputVO) {
		this.fc81OutputVO = fc81OutputVO;
	}

	public FP052650OutputVO getFp052650OutputVO() {
		return fp052650OutputVO;
	}

	public void setFp052650OutputVO(FP052650OutputVO fp052650OutputVO) {
		this.fp052650OutputVO = fp052650OutputVO;
	}

	public NFEE086OutputVO getNfee086OutputVO() {
		return nfee086OutputVO;
	}

	public void setNfee086OutputVO(NFEE086OutputVO nfee086OutputVO) {
		this.nfee086OutputVO = nfee086OutputVO;
	}

	public NFBRN4OutputVO getNfbrn4OutputVO() {
		return nfbrn4OutputVO;
	}

	public void setNfbrn4OutputVO(NFBRN4OutputVO nfbrn4OutputVO) {
		this.nfbrn4OutputVO = nfbrn4OutputVO;
	}

	public FC032151OutputVO getFc032151OutputVO() {
		return fc032151OutputVO;
	}

	public void setFc032151OutputVO(FC032151OutputVO fc032151OutputVO) {
		this.fc032151OutputVO = fc032151OutputVO;
	}

	public NR097NOutputVO getNr097NOutputVO() {
		return nr097NOutputVO;
	}

	public void setNr097NOutputVO(NR097NOutputVO nr097nOutputVO) {
		nr097NOutputVO = nr097nOutputVO;
	}

	public NR098NOutputVO getNr098NOutputVO() {
		return nr098NOutputVO;
	}

	public void setNr098NOutputVO(NR098NOutputVO nr098nOutputVO) {
		nr098NOutputVO = nr098nOutputVO;
	}

	public NFBRN2OutputVO getNfbrn2OutputVO() {
		return nfbrn2OutputVO;
	}

	public void setNfbrn2OutputVO(NFBRN2OutputVO nfbrn2OutputVO) {
		this.nfbrn2OutputVO = nfbrn2OutputVO;
	}

	public NFBRN3OutputVO getNfbrn3OutputVO() {
		return nfbrn3OutputVO;
	}

	public void setNfbrn3OutputVO(NFBRN3OutputVO nfbrn3OutputVO) {
		this.nfbrn3OutputVO = nfbrn3OutputVO;
	}

	public NFBRN5OutputVO getNfbrn5OutputVO() {
		return nfbrn5OutputVO;
	}

	public void setNfbrn5OutputVO(NFBRN5OutputVO nfbrn5OutputVO) {
		this.nfbrn5OutputVO = nfbrn5OutputVO;
	}

	public NFBRN9OutputVO getNfbrn9OutputVO() {
		return nfbrn9OutputVO;
	}

	public void setNfbrn9OutputVO(NFBRN9OutputVO nfbrn9OutputVO) {
		this.nfbrn9OutputVO = nfbrn9OutputVO;
	}	

	public NFBRN9OutputVO getAfbrn9OutputVO() {
		return afbrn9OutputVO;
	}

	public void setAfbrn9OutputVO(NFBRN9OutputVO afbrn9OutputVO) {
		this.afbrn9OutputVO = afbrn9OutputVO;
	}

	public FC032671OutputVO getFc032671OutputVO() {
		return fc032671OutputVO;
	}

	public void setFc032671OutputVO(FC032671OutputVO fc032671OutputVO) {
		this.fc032671OutputVO = fc032671OutputVO;
	}

	public CCM7818OutputVO getCcm7818OutputVO() {
		return ccm7818OutputVO;
	}

	public void setCcm7818OutputVO(CCM7818OutputVO ccm7818OutputVO) {
		this.ccm7818OutputVO = ccm7818OutputVO;
	}

	public CM6220ROutputVO getCm6220rOutputVO() {
		return cm6220rOutputVO;
	}

	public void setCm6220rOutputVO(CM6220ROutputVO cm6220rOutputVO) {
		this.cm6220rOutputVO = cm6220rOutputVO;
	}

	public EB172656OutputVO getEb172656OutputVO() {
		return eb172656OutputVO;
	}

	public void setEb172656OutputVO(EB172656OutputVO eb172656OutputVO) {
		this.eb172656OutputVO = eb172656OutputVO;
	}

	public EB202650OutputVO getEb202650OutputVO() {
		return eb202650OutputVO;
	}

	public void setEb202650OutputVO(EB202650OutputVO eb202650OutputVO) {
		this.eb202650OutputVO = eb202650OutputVO;
	}

	public EB202674OutputVO getEb202674OutputVO() {
		return eb202674OutputVO;
	}

	public void setEb202674OutputVO(EB202674OutputVO eb202674OutputVO) {
		this.eb202674OutputVO = eb202674OutputVO;
	}

	public SD120140OutputVO getSd120140OutputVO() {
		return sd120140OutputVO;
	}

	public void setSd120140OutputVO(SD120140OutputVO sd120140OutputVO) {
		this.sd120140OutputVO = sd120140OutputVO;
	}

	public BKDCD003OutputVO getBkdcd003OutputVO() {
		return bkdcd003OutputVO;
	}

	public void setBkdcd003OutputVO(BKDCD003OutputVO bkdcd003OutputVO) {
		this.bkdcd003OutputVO = bkdcd003OutputVO;
	}

	public EB312201OutputVO getEb312201OutputVO() {
		return eb312201OutputVO;
	}

	public void setEb312201OutputVO(EB312201OutputVO eb312201OutputVO) {
		this.eb312201OutputVO = eb312201OutputVO;
	}

	public EB382201OutputVO getEb382201OutputVO() {
		return eb382201OutputVO;
	}

	public void setEb382201OutputVO(EB382201OutputVO eb382201OutputVO) {
		this.eb382201OutputVO = eb382201OutputVO;
	}

	public EB12020002OutputVO getEb12020002OutputVO() {
		return eb12020002OutputVO;
	}

	public void setEb12020002OutputVO(EB12020002OutputVO eb12020002OutputVO) {
		this.eb12020002OutputVO = eb12020002OutputVO;
	}

	public NMVIPAOutputVO getNmvipaOutputVO() {
		return nmvipaOutputVO;
	}

	public void setNmvipaOutputVO(NMVIPAOutputVO nmvipaOutputVO) {
		this.nmvipaOutputVO = nmvipaOutputVO;
	}

	public NMVP3AOutputVO getNmvp3aOutputVO() {
		return nmvp3aOutputVO;
	}

	public void setNmvp3aOutputVO(NMVP3AOutputVO nmvp3aOutputVO) {
		this.nmvp3aOutputVO = nmvp3aOutputVO;
	}

	public FC032154OutputVO getFc032154OutputVO() {
		return fc032154OutputVO;
	}

	public void setFc032154OutputVO(FC032154OutputVO fc032154OutputVO) {
		this.fc032154OutputVO = fc032154OutputVO;
	}

	public SDPRC09AOutputVO getSdprc09aOutputVO() {
		return sdprc09aOutputVO;
	}

	public void setSdprc09aOutputVO(SDPRC09AOutputVO sdprc09aOutputVO) {
		this.sdprc09aOutputVO = sdprc09aOutputVO;
	}

	public CCM002OutputVO getCcm002OutputVO() {
		return ccm002OutputVO;
	}

	public void setCcm002OutputVO(CCM002OutputVO ccm002OutputVO) {
		this.ccm002OutputVO = ccm002OutputVO;
	}

	public SC120100OutputVO getSc120100OutputVO() {
		return sc120100OutputVO;
	}

	public void setSc120100OutputVO(SC120100OutputVO sc120100OutputVO) {
		this.sc120100OutputVO = sc120100OutputVO;
	}

	public NFEE001OutputVO getNfee001OutputVO() {
		return nfee001OutputVO;
	}

	public void setNfee001OutputVO(NFEE001OutputVO nfee001OutputVO) {
		this.nfee001OutputVO = nfee001OutputVO;
	}

	public NJCHKLC2OutputVO getNjchklc20OutputVO() {
		return njchklc20OutputVO;
	}

	public void setNjchklc20OutputVO(NJCHKLC2OutputVO njchklc20OutputVO) {
		this.njchklc20OutputVO = njchklc20OutputVO;
	}

	public VN067NOutputVO getVn067nOutputVO() {
		return vn067nOutputVO;
	}

	public void setVn067nOutputVO(VN067NOutputVO vn067nOutputVO) {
		this.vn067nOutputVO = vn067nOutputVO;
	}

	public EBPMNOutputVO getEbpmnOutputVO() {
		return ebpmnOutputVO;
	}

	public void setEbpmnOutputVO(EBPMNOutputVO ebpmnOutputVO) {
		this.ebpmnOutputVO = ebpmnOutputVO;
	}
	
	public EBPMN2OutputVO getEbpmn2OutputVO() {
		return ebpmn2OutputVO;
	}

	public void setEbpmnOutputVO(EBPMN2OutputVO ebpmn2OutputVO) {
		this.ebpmn2OutputVO = ebpmn2OutputVO;
	}

	public CEW012ROutputVO getCew012rOutputVO() {
		return cew012rOutputVO;
	}

	public void setCew012rOutputVO(CEW012ROutputVO cew012rOutputVO) {
		this.cew012rOutputVO = cew012rOutputVO;
	}

	public TP552697OutputVO getTp552697OutputVO() {
		return tp552697OutputVO;
	}

	public void setTp552697OutputVO(TP552697OutputVO tp552697OutputVO) {
		this.tp552697OutputVO = tp552697OutputVO;
	}

	public NRBRQ01OutputVO getNrbrq01OutputVO() {
		return nrbrq01OutputVO;
	}

	public void setNrbrq01OutputVO(NRBRQ01OutputVO nrbrq01OutputVO) {
		this.nrbrq01OutputVO = nrbrq01OutputVO;
	}
	
	public EB372602OutputVO getEb372602OutputVO() {
		return eb372602OutputVO;
	}

	public void setEb372602OutputVO(EB372602OutputVO eb372602OutputVO) {
		this.eb372602OutputVO = eb372602OutputVO;
	}
	public NR070NOutputVO getNr070nOutputVO() {
		return nr070nOutputVO;
	}

	public void setNr070nOutputVO(NR070NOutputVO nr070nOutputVO) {
		this.nr070nOutputVO = nr070nOutputVO;
	}

	public NR080NOutputVO getNr080nOutputVO() {
		return nr080nOutputVO;
	}

	public void setNr080nOutputVO(NR080NOutputVO nr080nOutputVO) {
		this.nr080nOutputVO = nr080nOutputVO;
	}

	public NR074NOutputVO getNr074nOutputVO() {
		return nr074nOutputVO;
	}

	public void setNr074nOutputVO(NR074NOutputVO nr074nOutputVO) {
		this.nr074nOutputVO = nr074nOutputVO;
	}

	public GD320140OutputVO getGd320140OutputVO() {
		return gd320140OutputVO;
	}

	public SDACTQ8OutputVO getSdactq8OutputVO() {
		return sdactq8OutputVO;
	}

	public void setSdactq8OutputVO(SDACTQ8OutputVO sdactq8OutputVO) {
		this.sdactq8OutputVO = sdactq8OutputVO;
	}

	public NMP8YBOutputVO getNmp8ybOutputVO() {
		return nmp8ybOutputVO;
	}

	public void setNmp8ybOutputVO(NMP8YBOutputVO nmp8ybOutputVO) {
		this.nmp8ybOutputVO = nmp8ybOutputVO;
	}

	public NMI001OutputVO getNmi001OutputVO() {
		return nmi001OutputVO;
	}

	public void setNmi001OutputVO(NMI001OutputVO nmi001OutputVO) {
		this.nmi001OutputVO = nmi001OutputVO;
	}

	public NMI002OutputVO getNmi002OutputVO() {
		return nmi002OutputVO;
	}

	public void setNmi002OutputVO(NMI002OutputVO nmi002OutputVO) {
		this.nmi002OutputVO = nmi002OutputVO;
	}

	public NMI003OutputVO getNmi003OutputVO() {
		return nmi003OutputVO;
	}

	public void setNmi003OutputVO(NMI003OutputVO nmi003OutputVO) {
		this.nmi003OutputVO = nmi003OutputVO;
	}

	public NMVP4AOutputVO getNmvp4aOutputVO() {
		return nmvp4aOutputVO;
	}

	public void setNmvp4aOutputVO(NMVP4AOutputVO nmvp4aOutputVO) {
		this.nmvp4aOutputVO = nmvp4aOutputVO;
	}

	public NMVP5AOutputVO getNmvp5aOutputVO() {
		return nmvp5aOutputVO;
	}	

	public void setNmvp5aOutputVO(NMVP5AOutputVO nmvp5aOutputVO) {
		this.nmvp5aOutputVO = nmvp5aOutputVO;
	}

	public EB032282OutputVO getEb032282OutputVO() {
		return eb032282OutputVO;
	}

	public void setEb032282OutputVO(EB032282OutputVO eb032282OutputVO) {
		this.eb032282OutputVO = eb032282OutputVO;
	}

	public EB032168OutputVO getEb032168OutputVO() {
		return eb032168OutputVO;
	}

	public void setEb032168OutputVO(EB032168OutputVO eb032168OutputVO) {
		this.eb032168OutputVO = eb032168OutputVO;
	}
	
	public void setGd320140OutputVO(GD320140OutputVO gd320140OutputVO) {
		this.gd320140OutputVO = gd320140OutputVO;
	}

	public NB052650OutputVO getNb052650OutputVO() {
		return nb052650OutputVO;
	}

	public void setNb052650OutputVO(NB052650OutputVO nb052650OutputVO) {
		this.nb052650OutputVO = nb052650OutputVO;
	}


	public AML004OutputVO getAml004OutputVO() {
		return aml004OutputVO;
	}

	public void setAml004OutputVO(AML004OutputVO aml004OutputVO) {
		this.aml004OutputVO = aml004OutputVO;
	}

	public HD00070000OutputVO getHd00070000OutputVO() {
		return hd00070000OutputVO;
	}

	public void setHd00070000OutputVO(HD00070000OutputVO hd00070000OutputVO) {
		this.hd00070000OutputVO = hd00070000OutputVO;
	}

	public VN085NOutputVO getAfee003OutputVO() {
		return afee003OutputVO;
	}

	public void setAfee003OutputVO(VN085NOutputVO afee003OutputVO) {
		this.afee003OutputVO = afee003OutputVO;
	}

	public NFBRN1OutputVO getAfbrn1OutputVO() {
		return afbrn1OutputVO;
	}

	public void setAfbrn1OutputVO(NFBRN1OutputVO afbrn1OutputVO) {
		this.afbrn1OutputVO = afbrn1OutputVO;
	}

	public NFBRN2OutputVO getAfbrn2OutputVO() {
		return afbrn2OutputVO;
	}

	public void setAfbrn2OutputVO(NFBRN2OutputVO afbrn2OutputVO) {
		this.afbrn2OutputVO = afbrn2OutputVO;
	}

	public NFBRN3OutputVO getAfbrn3OutputVO() {
		return afbrn3OutputVO;
	}

	public void setAfbrn3OutputVO(NFBRN3OutputVO afbrn3OutputVO) {
		this.afbrn3OutputVO = afbrn3OutputVO;
	}

	public NFBRN5OutputVO getAfbrn5OutputVO() {
		return afbrn5OutputVO;
	}

	public void setAfbrn5OutputVO(NFBRN5OutputVO afbrn5OutputVO) {
		this.afbrn5OutputVO = afbrn5OutputVO;
	}

	public NFBRN6OutputVO getAfbrn6OutputVO() {
		return afbrn6OutputVO;
	}

	public void setAfbrn6OutputVO(NFBRN6OutputVO afbrn6OutputVO) {
		this.afbrn6OutputVO = afbrn6OutputVO;
	}

	public NFBRN7OutputVO getAfbrn7OutputVO() {
		return afbrn7OutputVO;
	}

	public void setAfbrn7OutputVO(NFBRN7OutputVO afbrn7OutputVO) {
		this.afbrn7OutputVO = afbrn7OutputVO;
	}

	public NFEE002OutputVO getAfee002OutputVO() {
		return afee002OutputVO;
	}

	public void setAfee002OutputVO(NFEE002OutputVO afee002OutputVO) {
		this.afee002OutputVO = afee002OutputVO;
	}

	public NJBRVC3OutputVO getNjbrvc3OutputVO() {
		return njbrvc3OutputVO;
	}

	public void setNjbrvc3OutputVO(NJBRVC3OutputVO njbrvc3OutputVO) {
		this.njbrvc3OutputVO = njbrvc3OutputVO;
	}

	public NJBRVC3OutputVO getAjbrvc3OutputVO() {
		return ajbrvc3OutputVO;
	}

	public void setAjbrvc3OutputVO(NJBRVC3OutputVO ajbrvc3OutputVO) {
		this.ajbrvc3OutputVO = ajbrvc3OutputVO;
	}

	public MVC110001OutputVO getMvc110001OutputVO() {
		return mvc110001OutputVO;
	}

	public void setMvc110001OutputVO(MVC110001OutputVO mvc110001OutputVO) {
		this.mvc110001OutputVO = mvc110001OutputVO;
	}

	public MVC310001OutputVO getMvc310001OutputVO() {
		return mvc310001OutputVO;
	}

	public void setMvc310001OutputVO(MVC310001OutputVO mvc310001OutputVO) {
		this.mvc310001OutputVO = mvc310001OutputVO;
	}

	public MVC310002OutputVO getMvc310002OutputVO() {
		return mvc310002OutputVO;
	}

	public void setMvc310002OutputVO(MVC310002OutputVO mvc310002OutputVO) {
		this.mvc310002OutputVO = mvc310002OutputVO;
	}

	public NJWEEA60OutputVO getNjweea60OutputVO() {
		return njweea60OutputVO;
	}

	public void setNjweea60OutputVO(NJWEEA60OutputVO njweea60OutputVO) {
		this.njweea60OutputVO = njweea60OutputVO;
	}

	public CM061435CROutputVO getCm061435crOutputVO() {
		return cm061435crOutputVO;
	}

	public void setCm061435crOutputVO(CM061435CROutputVO cm061435crOutputVO) {
		this.cm061435crOutputVO = cm061435crOutputVO;
	}

	public WMSHACROutputVO getWmshacrOutputVO() {
		return wmshacrOutputVO;
	}

	public void setWmshacrOutputVO(WMSHACROutputVO wmshacrOutputVO) {
		this.wmshacrOutputVO = wmshacrOutputVO;
	}

	public NJBRVH3OutputVO getNjbrvh3OutputVO() {
		return njbrvh3OutputVO;
	}

	public void setNjbrvh3OutputVO(NJBRVH3OutputVO njbrvh3OutputVO) {
		this.njbrvh3OutputVO = njbrvh3OutputVO;
	}

	public NJBRVH3OutputVO getWmshad001OutputVO() {
		return wmshad001OutputVO;
	}

	public void setWmshad001OutputVO(NJBRVH3OutputVO wmshad001OutputVO) {
		this.wmshad001OutputVO = wmshad001OutputVO;
	}

	public NJBRVH3OutputVO getWmshad003OutputVO() {
		return wmshad003OutputVO;
	}

	public void setWmshad003OutputVO(NJBRVH3OutputVO wmshad003OutputVO) {
		this.wmshad003OutputVO = wmshad003OutputVO;
	}

	public NJBRVH3OutputVO getAjbrvh3OutputVO() {
		return ajbrvh3OutputVO;
	}

	public void setAjbrvh3OutputVO(NJBRVH3OutputVO ajbrvh3OutputVO) {
		this.ajbrvh3OutputVO = ajbrvh3OutputVO;
	}

	public NJBRVH3OutputVO getWmshad005OutputVO() {
		return wmshad005OutputVO;
	}

	public void setWmshad005OutputVO(NJBRVH3OutputVO wmshad005OutputVO) {
		this.wmshad005OutputVO = wmshad005OutputVO;
	}

	public NJBRVH3OutputVO getNfbrndOutputVO() {
		return nfbrndOutputVO;
	}

	public void setNfbrndOutputVO(NJBRVH3OutputVO nfbrndOutputVO) {
		this.nfbrndOutputVO = nfbrndOutputVO;
	}

	public NJBRVH3OutputVO getAfbrndOutputVO() {
		return afbrndOutputVO;
	}

	public void setAfbrndOutputVO(NJBRVH3OutputVO afbrndOutputVO) {
		this.afbrndOutputVO = afbrndOutputVO;
	}

	public NFBRNEOutputVO getNfbrneOutputVO() {
		return nfbrneOutputVO;
	}

	public void setNfbrneOutputVO(NFBRNEOutputVO nfbrneOutputVO) {
		this.nfbrneOutputVO = nfbrneOutputVO;
	}

	public NFBRNEOutputVO getAfbrneOutputVO() {
		return afbrneOutputVO;
	}

	public void setAfbrneOutputVO(NFBRNEOutputVO afbrneOutputVO) {
		this.afbrneOutputVO = afbrneOutputVO;
	}

	public WMSHAIAVO getWmshaiaOutputVO() {
		return wmshaiaOutputVO;
	}

	public void setWmshaiaOutputVO(WMSHAIAVO wmshaiaOutputVO) {
		this.wmshaiaOutputVO = wmshaiaOutputVO;
	}

	public VN084NOutputVO getVn084nOutputVO() {
		return vn084nOutputVO;
	}

	public void setVn084nOutputVO(VN084NOutputVO vn084nOutputVO) {
		this.vn084nOutputVO = vn084nOutputVO;
	}

	public VN084N1OutputVO getVn084n1OutputVO() {
		return vn084n1OutputVO;
	}

	public void setVn084n1OutputVO(VN084N1OutputVO vn084n1OutputVO) {
		this.vn084n1OutputVO = vn084n1OutputVO;
	}

	public UK084NOutputVO getUk084nOutputvo() {
		return uk084nOutputvo;
	}

	public void setUk084nOutputvo(UK084NOutputVO uk084nOutputvo) {
		this.uk084nOutputvo = uk084nOutputvo;
	}

	public AJW084OutputVO getAjw084OutputVO() {
		return ajw084OutputVO;
	}

	public void setAjw084OutputVO(AJW084OutputVO ajw084OutputVO) {
		this.ajw084OutputVO = ajw084OutputVO;
	}

	public NJW084OutputVO getNjw084OutputVO() {
		return njw084OutputVO;
	}

	public void setNjw084OutputVO(NJW084OutputVO njw084OutputVO) {
		this.njw084OutputVO = njw084OutputVO;
	}

	public SPWEBQ1OutputVO getSpwebq1OutputVO() {
		return spwebq1OutputVO;
	}

	public void setSpwebq1OutputVO(SPWEBQ1OutputVO spwebq1OutputVO) {
		this.spwebq1OutputVO = spwebq1OutputVO;
	}

	public CE6220ROutputVO getCe6220rOutputVO() {
		return ce6220rOutputVO;
	}

	public void setCe6220rOutputVO(CE6220ROutputVO ce6220rOutputVO) {
		this.ce6220rOutputVO = ce6220rOutputVO;
	}

	public NJWEEA70OutputVO getNjweea70OutputVO() {
		return njweea70OutputVO;
	}

	public void setNjweea70OutputVO(NJWEEA70OutputVO njweea70OutputVO) {
		this.njweea70OutputVO = njweea70OutputVO;
	}

	public NFBRNFOutputVO getNfbrnfOutputVO() {
		return nfbrnfOutputVO;
	}

	public void setNfbrnfOutputVO(NFBRNFOutputVO nfbrnfOutputVO) {
		this.nfbrnfOutputVO = nfbrnfOutputVO;
	}

	public NFBRNFOutputVO getAfbrnfOutputVO() {
		return afbrnfOutputVO;
	}

	public void setAfbrnfOutputVO(NFBRNFOutputVO afbrnfOutputVO) {
		this.afbrnfOutputVO = afbrnfOutputVO;
	}

	public NFBRNGOutputVO getNfbrngOutputVO() {
		return nfbrngOutputVO;
	}

	public void setNfbrngOutputVO(NFBRNGOutputVO nfbrngOutputVO) {
		this.nfbrngOutputVO = nfbrngOutputVO;
	}

	public NFBRNGOutputVO getAfbrngOutputVO() {
		return afbrngOutputVO;
	}

	public void setAfbrngOutputVO(NFBRNGOutputVO afbrngOutputVO) {
		this.afbrngOutputVO = afbrngOutputVO;
	}

	public NFBRNHOutputVO getNfbrnhOutputVO() {
		return nfbrnhOutputVO;
	}

	public void setNfbrnhOutputVO(NFBRNHOutputVO nfbrnhOutputVO) {
		this.nfbrnhOutputVO = nfbrnhOutputVO;
	}

	public NFBRNHOutputVO getAfbrnhOutputVO() {
		return afbrnhOutputVO;
	}

	public void setAfbrnhOutputVO(NFBRNHOutputVO afbrnhOutputVO) {
		this.afbrnhOutputVO = afbrnhOutputVO;
	}

	public NFBRNIOutputVO getNfbrniOutputVO() {
		return nfbrniOutputVO;
	}

	public void setNfbrniOutputVO(NFBRNIOutputVO nfbrniOutputVO) {
		this.nfbrniOutputVO = nfbrniOutputVO;
	}

	public NFBRNIOutputVO getAfbrniOutputVO() {
		return afbrniOutputVO;
	}

	public void setAfbrniOutputVO(NFBRNIOutputVO afbrniOutputVO) {
		this.afbrniOutputVO = afbrniOutputVO;
	}

	public AJBRVA2OutputVO getAjbrva2OutputVO() {
		return ajbrva2OutputVO;
	}

	public void setAjbrva2OutputVO(AJBRVA2OutputVO ajbrva2OutputVO) {
		this.ajbrva2OutputVO = ajbrva2OutputVO;
	}

	public AJBRVA3OutputVO getAjbrva3OutputVO() {
		return ajbrva3OutputVO;
	}

	public void setAjbrva3OutputVO(AJBRVA3OutputVO ajbrva3OutputVO) {
		this.ajbrva3OutputVO = ajbrva3OutputVO;
	}

	public AJBRVA9OutputVO getAjbrva9OutputVO() {
		return ajbrva9OutputVO;
	}

	public void setAjbrva9OutputVO(AJBRVA9OutputVO ajbrva9OutputVO) {
		this.ajbrva9OutputVO = ajbrva9OutputVO;
	}

	public AJBRVB9OutputVO getAjbrvb9OutputVO() {
		return ajbrvb9OutputVO;
	}

	public void setAjbrvb9OutputVO(AJBRVB9OutputVO ajbrvb9OutputVO) {
		this.ajbrvb9OutputVO = ajbrvb9OutputVO;
	}

	public AJBRVC9OutputVO getAjbrvc9OutputVO() {
		return ajbrvc9OutputVO;
	}

	public void setAjbrvc9OutputVO(AJBRVC9OutputVO ajbrvc9OutputVO) {
		this.ajbrvc9OutputVO = ajbrvc9OutputVO;
	}

	public AJBRVD9OutputVO getAjbrvd9OutputVO() {
		return ajbrvd9OutputVO;
	}

	public void setAjbrvd9OutputVO(AJBRVD9OutputVO ajbrvd9OutputVO) {
		this.ajbrvd9OutputVO = ajbrvd9OutputVO;
	}

	public void setEbpmn2OutputVO(EBPMN2OutputVO ebpmn2OutputVO) {
		this.ebpmn2OutputVO = ebpmn2OutputVO;
	}

}
