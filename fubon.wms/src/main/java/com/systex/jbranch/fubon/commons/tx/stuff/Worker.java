package com.systex.jbranch.fubon.commons.tx.stuff;

import com.systex.jbranch.fubon.commons.tx.traffic.Truck;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.systex.jbranch.platform.common.util.PlatformContext.getBean;

/**
 * 電文 Context
 */
@Component
@Scope("prototype")
public class Worker {
    /**
     * 電文抽象物件
     **/
    private Truck truck;
    /**
     * Esb Terminal
     **/
    public static final String ESB = "ESB";
    /**
     * Cbs Terminal
     **/
    public static final String CBS = "CBS";

    /**
     * 建立電文工作流
     **/
    public static Worker call() throws JBranchException {
        return getBean("worker", Worker.class);
    }

    /**
     * 設定電文目的地與何種電文交易
     *
     * @param terminal 電文送達端點名稱
     * @param itemId   電文交易名稱
     * @throws JBranchException
     */
    public Worker assign(String terminal, String itemId) throws Exception {
        truck = getBean(terminal, Truck.class);
        truck.initParameters(terminal, itemId);
        truck.configure();
        return this;
    }

    /**
     * 設定電文 RequestVO
     *
     * @param request
     * @return
     */
    public Worker setRequest(Object request) {
        truck.setRequestVO(request);
        return this;
    }

    /**
     * 發送電文
     *
     * @return
     * @throws Exception
     */
    public Worker work() throws Exception {
        truck.work();
        return this;
    }

    /**
     * 取得電文回送 ResponseVO
     *
     * @param response
     * @param <T>
     * @return
     */
    public <T> Worker getResponse(List<T> response) {
        for (Object vo : truck.getResponseVO())
            response.add((T) vo);
        return this;
    }
}
