package com.systex.jbranch.fubon.commons.tx.traffic;

import com.systex.jbranch.fubon.commons.cbs.service_platform.axis.*;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilInputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CbsSpec;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.TxHeadVO;
import com.systex.jbranch.platform.common.errHandle.APException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.systex.jbranch.fubon.commons.tx.tool.Box.*;
import static org.apache.commons.lang.StringUtils.defaultString;
import static org.apache.commons.lang.StringUtils.isBlank;

@Component("CBS")
@Scope("prototype")
public class Cbs extends Truck {
    /**
     * RequestVO
     **/
    private CBSUtilInputVO request;
    /**
     * ResponseVO
     **/
    private List<CBSUtilOutputVO> response;

    /**
     * Web Service Objects
     **/
    private SoapService service;
    private SoapServiceServiceHeader header;
    private SoapServiceServiceBody body;
    private SoapServiceServiceError error;


    @Override
    public void configure() throws Exception {
        response = new ArrayList();

        createWebService();
        configureServiceHeader();
        configureServiceBody();
    }

    /**
     * 配置 Web Service Body 的參數
     **/
    private void configureServiceBody() {
        body.setDataType("XML");
        body.setShouldRender(true);
    }

    /**
     * 配置 Web Service Header 的參數
     **/
    private void configureServiceHeader() {
        Date date = new Date();
        header.setHSYDAY(new SimpleDateFormat("yyyyMMdd").format(date));
        header.setHSYTIME(new SimpleDateFormat("hhmmss").format(date));
        header.setSPName(journal.getId());
        header.setHWSID(journal.getId());
        header.setTxID(journal.getItemId());
        header.setHSTANO(journal.getSeq());
        header.setHTLID("99940119");
        header.setHRETRN(" ");
        header.setEncoding("UTF-8");
        header.setUUID(getUUID());
    }

    /** UUID 規格：HSYDAY(8) + HSTANO(7) + TxID(6) + WMSR(4) + 9001 **/
    private String getUUID() {
        return header.getHSYDAY() + header.getHSTANO() + header.getTxID() + header.getHWSID() + "9001";
    }

    /**
     * 建立 Web Service 相關物件
     **/
    private void createWebService() {
        header = new SoapServiceServiceHeader();
        body = new SoapServiceServiceBody();
        error = new SoapServiceServiceError();

        service = new SoapService();
        service.setServiceHeader(header);
        service.setServiceBody(body);
        service.setServiceError(error);
    }

    @Override
    public void setRequestVO(Object object) {
        request = (CBSUtilInputVO) object;

        journal.setPickUpId(request.getPickUpId());
        journal.setModule(defaultString(request.getModule(), "not Offer"));

        TxHeadVO head = request.getTxHeadVO();
        head.setTellerNumber(header.getHTLID());
        head.setTransactionCode(header.getTxID());
        head.setUUID(header.getUUID()); // 統一用制定的 UUID 格式， 原先加上後綴 + head.getUUIDSuffix() 先取消
    }

    @Override
    protected void send() throws Exception {
        /** 準備上行電文 **/
        prepareOnMsg(marshal(request));
        /** 準備並且發送電文 **/
        SoapServiceHolder parameter = new SoapServiceHolder(service);
        departure().operation(parameter);
        /** 處理下行電文 **/
        processOffMsg(parameter.value);
    }

    /**
     * 取得發送電文的 Soap 物件
     **/
    private ReceiverSoap departure() throws MalformedURLException, ServiceException {
        return new TCSSOAPEMFListenerLocator().getSOAPListener(new URL(journal.getUrl()));
    }

    @Override
    public List getResponseVO() {
        return response;
    }

    /**
     * Cbs 的電文 RequestVO
     **/
    public static CBSUtilInputVO createRequestVO() {
        return new CBSUtilInputVO();
    }

    /**
     * Cbs 的電文 Request HeaderVO
     **/
    public static TxHeadVO createTxHeadVO() {
        return new TxHeadVO();
    }

    /**
     * 準備上行電文
     **/
    private void prepareOnMsg(String onMsg) {
        body.setTxnString(onMsg);
        recordOnMsg(onMsg);
    }

    /**
     * 處理下行電文
     *
     * @param result 上送電文後，回傳的結果
     */
    private void processOffMsg(SoapService result) throws Exception {
        CbsSpec spec = configureIfExistsSpec(result);

        String offMsg = result.getServiceBody().getTxnString();
        recordOffMsg(xmlFormat(offMsg));

        SoapServiceServiceError error = result.getServiceError();
        if (isBlank(error.getErrorCode())) {
            normalProcess(offMsg, spec);
        } else {
            errorProcess(error, spec);
        }
    }

    /** 部分電文有特殊規格，如果有則配置並返回規格。 **/
    private CbsSpec configureIfExistsSpec(SoapService result) {
        CbsSpec spec = request.getSpec();
        if (null != spec) {
            spec.setRequest(request);
            spec.setTxUpObject(service);
            spec.setTxDownObject(result);
        }
        return spec;
    }

    /**
     * 沒有錯誤訊息的正常處理流程
     * @param offMsg 下行電文 xml
     * @param spec 部分電文有特殊規格
     * @throws Exception
     */
    private void normalProcess(String offMsg, CbsSpec spec) throws Exception {
        String pickUpTxName = "CBS" + request.getPickUpId();
        CBSUtilOutputVO outputVO = unmarshal(offMsg.replaceAll("TxBody", pickUpTxName),
                CBSUtilOutputVO.class, new String[]{"TxHead", pickUpTxName});

        if (null != spec) {
            spec.setTxData(outputVO);
            spec.process();
            response.add(spec.getCustomTxData() != null? spec.getCustomTxData(): spec.getTxData());
            if (spec.isMultiple()) {
                header.setHRETRN("C");
                send();
            }
        } else {
            response.add(outputVO);
        }
    }

    /**
     * 有錯誤訊息的錯誤處理流程
     * @param error 電文回傳錯誤碼
     * @param spec 部分電文有特殊規格
     * @throws Exception
     */
    private void errorProcess(SoapServiceServiceError error, CbsSpec spec) throws Exception {
        if (null != spec) {
            spec.setErrorCode(error.getErrorCode());
            spec.process();
            if (null != spec.getCustomTxData()) {
                response.add(spec.getCustomTxData());
            }
        }

        if (null == spec || !spec.isHasCustomErrorProcess())
            throw new APException(
                    String.format("TX_LOG： SEQ: %s, HTXTID: %s, ErrorCode: %s, ErrorMessage: %s ",
                            journal.getSeq(), header.getTxID() + "-" + request.getPickUpId(), error.getErrorCode(), error.getErrorMessage()));
    }
}
