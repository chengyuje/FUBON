package com.systex.jbranch.ws.external.service.tx;

import com.systex.jbranch.fubon.bth.job.business_logic.RptResolver;
import com.systex.jbranch.fubon.commons.mail.MailConfig;
import com.systex.jbranch.fubon.commons.mail.MailService;
import com.systex.jbranch.fubon.commons.tx.tool.Box;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.ws.external.service.tx.action.TxAction;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/tx")
public class TxService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String AP_XML_CHARSET_UTF8 = "application/xml;charset=UTF-8";
    public static final String AP_TEXT_CHARSET_UTF8 = MediaType.TEXT_PLAIN + ";charset=utf-8";

    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(
            value = "test/{txId}",
            method = RequestMethod.GET
    )
    public String test(
            @PathVariable("txId") String txId,
            @RequestParam String xml
    ) throws JBranchException {
        System.out.println("txId: " + txId + ", xml: " + xml);
        return execute(txId, xml);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(
            value = "/{txId}",
            method = RequestMethod.POST,
            consumes = AP_XML_CHARSET_UTF8,  //指定處理 request 的 submit 的類型
            produces = AP_XML_CHARSET_UTF8   //返回的內容類型
    )
    public String execute(
            @PathVariable("txId") String txId,
            @RequestBody String xml
    ) throws JBranchException {
        logger.info("TxService => txId: {}, content: {}. ", txId, xml);
        TxVO txVO = new TxVO();
        try {
            // unmarshal xml input to java bean
            MailVO mailVO = Box.unmarshal(xml.replaceAll("TxBody", txId),
                    TxVO.class, new String[]{txId}).getMailVO();
            txVO.setMailVO(mailVO);
            mail(mailVO, fillDataInHTML(mailVO, getMailContentTemplate()));
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            txVO.setErrorMsg(e.getMessage());
        }

        try {
            return Box.marshal(txVO);
        } catch (JAXBException | IOException ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
        }
        return "系統發生錯誤，請洽系統管理員！";
    }

    private void mail(MailVO mailVO, String html) throws Exception {
        MailConfig cfg = new MailConfig();
        Map map = new HashMap();
        map.put(mailVO.getRECEIVER(), "");
        cfg.setMailToMap(map);
        cfg.setSubject("台北富邦銀行定存質借E-MAIL通知服務");
        cfg.setContent(html);
//        mail.setEncoding("BIG-5");
        Map imageMap = new HashMap();
        imageMap.put("<image1>", new File(DataManager.getRealPath(), "doc//OTH//WS//logo.JPG"));
        cfg.setImageMap(imageMap);
        PlatformContext.getBean(MailService.class).send(cfg);
    }

    private String fillDataInHTML(MailVO mailVO, String mailContentTemplate) {
        String template = mailContentTemplate.replace("$YYY", mailVO.getYYY());
        template = template.replace("$MM", mailVO.getMM());
        template = template.replace("$DD", mailVO.getDD());
        template = template.replace("$ACNO", mailVO.getACNO());
        System.out.println(template);
        return template;
    }

    private String getMailContentTemplate() throws IOException {
        try (StringWriter writer = new StringWriter()) {
            Path template = Paths.get(DataManager.getRealPath(), "doc//OTH//WS//formTest.html");
            IOUtils.copy(Files.newBufferedReader(template, RptResolver.getFileCharset(template)), writer);
            return writer.toString();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(
            value = "fubon",
            method = RequestMethod.GET,
            produces = AP_TEXT_CHARSET_UTF8
    )
    public String action(
            @RequestParam String action,
            @RequestParam(required = false) String custId
    ) {
        logger.info("TxService => action: {}, custId: {}. ", action, custId);
        String contentXml;

        try {
            TxAction txAction = PlatformContext.getBean(action + "TxAction", TxAction.class);
            contentXml = txAction.execute(custId);
        } catch (Exception e) {
            contentXml = ExceptionUtils.getFullStackTrace(e);
        }

        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "<soapenv:Body>\n" +
                "<soapenv:Result>\n" +
                "<action>" + action + "</action>\n" +
                contentXml +
                "<ns1:hostname xmlns:ns1=\"http://xml.apache.org/axis/\">TPEBNKFPQ5</ns1:hostname>\n" +
                "</detail>\n" +
                "</soapenv:Result>\n" +
                "</soapenv:Body>\n" +
                "</soapenv:Envelope>\n";
    }
}
