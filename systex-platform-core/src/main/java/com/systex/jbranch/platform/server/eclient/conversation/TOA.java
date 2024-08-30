package com.systex.jbranch.platform.server.eclient.conversation;

import com.systex.jbranch.platform.common.communication.MessageProducer;
import com.systex.jbranch.platform.common.communication.MessageProducerIF;
import com.systex.jbranch.platform.common.communication.jms.*;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.server.conversation.MapToaIF;
import com.systex.jbranch.platform.server.conversation.ObjectToaIF;
import com.systex.jbranch.platform.server.conversation.ToaHelperIF;
import com.systex.jbranch.platform.server.conversation.ToaIF;
import com.systex.jbranch.platform.server.conversation.message.EnumMessageType;
import com.systex.jbranch.platform.server.conversation.message.EnumShowType;
import com.systex.jbranch.platform.server.conversation.message.EnumToaHeader;
import com.systex.jbranch.platform.server.conversation.message.MapToa;
import com.systex.jbranch.platform.server.eclient.conversation.broadcast.TOABroadcast;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TOA implements ToaHelperIF {
// ------------------------------ FIELDS ------------------------------

    private MapToa toaData = new MapToa();
    private String nextProc = "";
    private List<ToaIF> lstToaMsg = new ArrayList<ToaIF>();
    //private CommunicationIF communication;
    private UUID uuid = null;
    private Map<String, Object> ejDataMap = null;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ToaHelperIF ---------------------

    public MapToaIF createMapToa() {
        return new MapToa();
    }

    public ObjectToaIF createObjectToa() {
        //not supported yet.
        return null;
    }

    /**
     * 將指定資料傳送至 Client
     *
     * @param showType    訊息呈現方式
     * @param messageType 訊息種類
     * @param msgCode     訊息代號
     * @param msgData     訊息內容
     * @return
     * @throws
     * @see
     * @since 1.01
     */
    public void sendTOA(EnumShowType showType,
                        EnumMessageType messageType,
                        String msgCode,
                        String msgData) {
        sendTOA(showType, messageType, msgCode, msgData, false);
    }

    /**
     * 將指定資料傳送至 Client
     *
     * @param showType    訊息呈現方式
     * @param messageType 訊息種類
     * @param msgCode     訊息代號
     * @param msgData     訊息內容
     * @param bEndBreKet  是否為最後一筆
     * @return
     * @throws
     * @see
     * @since 1.01
     */
    public void sendTOA(EnumShowType showType,
                        EnumMessageType messageType,
                        String msgCode,
                        String msgData, boolean bEndBreKet) {
        TOAMsg toaMsg = new TOAMsg();
        toaMsg.setMsg(showType, messageType, msgCode, msgData);

        sendTOA(toaMsg, bEndBreKet);
    }

    /**
     * 將指定資料傳送至 Client
     *
     * @param toaMsg 回傳給 Client 的訊息、報表、螢幕資料..
     * @return
     * @throws
     * @see
     * @since 1.01
     */
    public void sendTOA(ToaIF toaMsg) {
        sendTOA(toaMsg, false);
    }

    /**
     * 將指定資料傳送至 Client
     *
     * @param toaMsg     回傳給 Client 的訊息、報表、螢幕資料..
     * @param bEndBreKet 是否為最後一筆
     * @return
     * @throws
     * @see
     * @since 1.01
     */
    @SuppressWarnings("unchecked")
    public void sendTOA(ToaIF toaMsg, boolean bEndBreKet) {
        if (toaMsg instanceof TOABroadcast) {
            sendToClient((TOABroadcast) toaMsg);
        }
        else {
            JSONObject obj = new JSONObject();

            /* 記 TOALog
                *
                */
            //記錄 ToaVO、並放至 DataManager 供 EJ 使用
            int nType = toaMsg.Headers().getInt(EnumToaHeader.OutputType);
            toaData.Headers().setInt(EnumToaHeader.OutputType, nType);
            toaData.Headers().setStr(EnumToaHeader.OutputData, toaMsg.toString());

            DataManager.getConversationVO(uuid).setToa(toaData);

            obj.put(EnumToaHeader.OutputType.toString(), nType);
            obj.put(EnumToaHeader.OutputData.toString(), toaMsg.toString());
            obj.put(EnumToaHeader.NextProc.toString(), this.nextProc);
            obj.put(EnumToaHeader.EndBracket.toString(), (bEndBreKet == true) ? "1" : "0");
            String toaJson = obj.toString();
            sendToClient(toaJson);
        }
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * @param toaMsg 將回傳給 Client 的訊息、報表、螢幕資料..暫存至緩衝區內
     * @return
     * @throws
     * @see
     * @since 1.01
     */
    public void addItem(ToaIF toaMsg) {
        lstToaMsg.add(toaMsg);
    }

    public ToaIF getToa() {
        return toaData;
    }

    /**
     * 將緩衝區內的資料逐一傳送至 Client
     *
     * @param
     * @return
     * @throws
     * @see
     * @since 1.01
     */
    public void sendTOA() {
        ToaIF toa = null;
        for (int i = 0; i < lstToaMsg.size(); i++) {
            toa = lstToaMsg.get(i);

            sendTOA(toa, ((lstToaMsg.size() - 1) == i));
        }
    }

    //
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * 實際呼叫傳送的進入點
     *
     * @param strToaData 實際傳送的資料內容
     */
    private void sendToClient(String strToaData) {
        MessageProducerIF socketHTTP = MessageProducer.getInstance(this.uuid);
        socketHTTP.send(strToaData);
    }

    /**
     * 實際呼叫傳送的進入點
     *
     * @param bcData 實際傳送的 broadcase 物件
     */
    private void sendToClient(TOABroadcast bcData) throws JmsException {
        int priority = 4;//訊息的優先權,預設是4
        DeliveryMode deliveryMode = DeliveryMode.PERSISTENT;//訊息傳輸模式(永續/非永續)

        JMSMessageProducerIF socketHTTP = JMSMessageProducer.getInstance(this.uuid);
        MessagePostProcessorIF processor = new BasicMessageProcessor(bcData.getBroadcastData());
        socketHTTP.send(bcData.toString(),
                deliveryMode, priority,
                bcData.getTimeOut(),
                processor);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * @param nextProc 連動交易的交易代號
     * @return
     * @throws
     * @see
     * @since 1.01
     */
    public void setNextProc(String nextProc) {
        this.nextProc = nextProc;
    }
}
