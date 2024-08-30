package com.systex.jbranch.ws.external.service.domain.high_assets;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.ws.external.service.domain.ErrorVO;
import com.systex.jbranch.ws.external.service.domain.RequestVO;
import com.systex.jbranch.ws.external.service.domain.ResponseVO;
import com.systex.jbranch.ws.external.service.domain.high_assets.wmsha001.WMSHA001InputVO;
import com.systex.jbranch.ws.external.service.domain.high_assets.wmsha001.WMSHA001OutputVO;
import com.systex.jbranch.ws.external.service.domain.high_assets.wmsha001.WMSHA001Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@RestController
@RequestMapping("/api/wmsha")
public class HighAssetsController {

    @RequestMapping(method = RequestMethod.POST, value = "/wmsha001")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public ResponseVO<WMSHA001OutputVO> wmsha001(@RequestBody RequestVO<WMSHA001InputVO> requestVO) throws Exception {
        ResponseVO<WMSHA001OutputVO> returnVO = new ResponseVO<>();

        try {
            WMSHA001Service service = PlatformContext.getBean(WMSHA001Service.class);
            WMSHA001OutputVO outputVO = service.search(requestVO.getBody());
            returnVO.setBody(outputVO);
        } catch (JBranchException e) {
            ErrorVO errorVO = new ErrorVO();
            errorVO.setEMSGID("X9999");
            errorVO.setEMSGTXT(e.getMessage());
            returnVO.setError(errorVO);
        }
        return returnVO;
    }
}
