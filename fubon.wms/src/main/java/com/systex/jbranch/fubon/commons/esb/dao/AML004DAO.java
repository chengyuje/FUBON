package com.systex.jbranch.fubon.commons.esb.dao;

import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.aml004.AML004InputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Esb;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.AML004;

/**
 * 防洗錢註記（AML）
 */
@Repository
public class AML004DAO {
    public List<ESBUtilOutputVO> search(String custID) throws Exception {
        List<ESBUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.ESB, AML004)
                .setRequest(Esb.createRequestVO()
                        .setModule("AML004DAO.search")
                        .setTxHeadVO(Esb.createTxHeadVO()
                                .setHTLID("2004115"))
                        .setAml004InputVO(new AML004InputVO(custID)))
                .work()
                .getResponse(list);
        return list;
    }
}
