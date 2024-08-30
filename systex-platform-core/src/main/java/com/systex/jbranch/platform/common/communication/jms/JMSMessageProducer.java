package com.systex.jbranch.platform.common.communication.jms;

import com.systex.jbranch.platform.common.communication.debug.DebuggerIF;
import com.systex.jbranch.platform.common.communication.jms.vo.JmsVoIF;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import javax.jms.*;


public final class JMSMessageProducer implements JMSMessageProducerIF {
// ------------------------------ FIELDS ------------------------------

    public static final String CONFIG_FILENAME = getConfigFileName();
    private static ApplicationContext context = new FileSystemXmlApplicationContext(CONFIG_FILENAME);
    private static Logger logger = LoggerFactory.getLogger(JMSMessageProducer.class);
    private static final String DEFAULT_CONFIG_FILENAME = "c:\\jbranch\\config\\system-jms.xml";

    private static final String FILE_SEPARATOR = System.getProperties().getProperty("file.separator");
    private UUID uuid = null;
    private MessageConverterIF converter = null;
    private DebuggerIF debugger = null;
    private JmsVoIF vo = null;
    private ConnectionFactory connectionFactory = null;
    private Connection conn = null;
    private Session session = null;
    private MessageProducer sender = null;
    private Destination destination = null;
    private final Object finalizerGuardian = new Object() {
        protected void finalize() throws Throwable {
            close();
        }
    };
    //目前連線狀態
    private volatile boolean isConnected = false;

// -------------------------- STATIC METHODS --------------------------

    private static String getConfigFileName() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(DataManager.getSystem().getRoot());
            sb.append(FILE_SEPARATOR);
            sb.append(DataManager.getSystem().getPath().get("config"));
            sb.append(FILE_SEPARATOR);
            sb.append(DataManager.getSystem().getConfigFileName().get("jms"));
            return sb.toString();
        }
        catch (Exception err) {
            logger.error(err.getMessage(), err);
            return DEFAULT_CONFIG_FILENAME;
        }
    }

    public static JMSMessageProducerIF getInstance(UUID uuid) {
        try {
            return new JMSMessageProducer(uuid);
        }
        catch (Exception err) {
            throw JmsUtil.convertJmsAccessException(err);
        }
    }

// --------------------------- CONSTRUCTORS ---------------------------

    private JMSMessageProducer(UUID uuid) throws JmsException {
        try {
            this.uuid = uuid;

            this.connectionFactory = (ConnectionFactory) context.getBean("multiUseConnectionFactory");
            this.vo = (JmsVoIF) context.getBean("jmsProducerVO");
            this.converter = (MessageConverterIF) context.getBean("DefaultMessageConverter");
            this.debugger = (DebuggerIF) context.getBean("DefaultDebugger");

            this.connect();
        }
        catch (Exception err) {
            throw JmsUtil.convertJmsAccessException(err);
        }
    }

    private void connect() throws JmsException {
        try {
            conn = connectionFactory.createConnection(vo.getUserName(), vo.getPassword());
            conn.setExceptionListener(this);
            conn.start();
            session = conn.createSession((vo.getAcknowledgeMode() == Session.SESSION_TRANSACTED), vo.getAcknowledgeMode());

            destination = createDestination(vo.getDestinationName());

            sender = session.createProducer(destination);

            sender.setDeliveryMode(vo.getDeliveryMode());
            sender.setDisableMessageID(vo.getDisableMessageID());
            sender.setDisableMessageTimestamp(vo.getDisableMessageTimestamp());
            sender.setPriority(vo.getPriority());
            sender.setTimeToLive(vo.getTimeToLive());

            isConnected = true;
        }
        catch (Exception err) {
            throw JmsUtil.convertJmsAccessException(err);
        }
    }

    public Destination createDestination(String name) throws JmsException {
        try {
            return session.createQueue(name);
        }
        catch (Exception err) {
        }
        try {
            return session.createTopic(name);
        }
        catch (Exception err) {
            throw JmsUtil.convertJmsAccessException(err);
        }
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ExceptionListener ---------------------

    public void onException(JMSException ex) {
        isConnected = false;

        debugger.writeLine("onException:%d%n", this.hashCode());

        int times = vo.getReconnectTimes() + 1;
        while (times > 0) {
            times--;
            try {
                debugger.writeLine("connect:%d%n", this.hashCode());
                this.connect();
                break;
            }
            catch (Exception err) {
            }
        }
        debugger.writeLine("failed:%d%n", this.hashCode());
    }

// --------------------- Interface JMSMessageProducerIF ---------------------

    public void commit() throws JmsException {
        try {
            session.commit();
        }
        catch (Exception err) {
            throw JmsUtil.convertJmsAccessException(err);
        }
    }

    public void recover() throws JmsException {
        try {
            session.recover();
        }
        catch (Exception err) {
            throw JmsUtil.convertJmsAccessException(err);
        }
    }

    public void rollback() throws JmsException {
        try {
            session.rollback();
        }
        catch (Exception err) {
            throw JmsUtil.convertJmsAccessException(err);
        }
    }

    public void close() {
        try {
            sender.close();
        }
        catch (Exception err) {
        }
        try {
            session.close();
        }
        catch (Exception err) {
        }
        try {
            conn.close();
        }
        catch (Exception err) {
        }

        conn = null;
        session = null;
        sender = null;
        destination = null;
    }

    public void send(Destination destination, Object message,
                     DeliveryMode deliveryMode, int priority, long timeToLive,
                     MessagePostProcessorIF postProcessor) throws JmsException {
        try {
            int timeout = vo.getTimeout();
            long time = System.currentTimeMillis();

            debugger.writeLine("before send:%d", this.hashCode());
            while (!isConnected && System.currentTimeMillis() - time < timeout) {
            }
            sender.send(destination, postProcessor.postProcessMessage(converter.toMessage(message, session)), deliveryMode.intValue(), priority, timeToLive);
            debugger.writeLine("after send:%d", this.hashCode());
        }
        catch (Exception err) {
            throw JmsUtil.convertJmsAccessException(err);
        }
    }

    public void send(Destination destination, Object message,
                     MessagePostProcessorIF postProcessor) throws JmsException {
        this.send(destination, message, DeliveryMode.valueOf(vo.getDeliveryMode()), vo.getPriority(), vo.getTimeToLive(), postProcessor);
    }

    public void send(Object message, DeliveryMode deliveryMode, int priority,
                     long timeToLive, MessagePostProcessorIF postProcessor) throws JmsException {
        try {
            send(destination, message, deliveryMode, priority, timeToLive, postProcessor);
        }
        catch (Exception err) {
            throw JmsUtil.convertJmsAccessException(err);
        }
    }

    public void send(Object message, MessagePostProcessorIF postProcessor) throws JmsException {
        try {
            send(destination, message, postProcessor);
        }
        catch (Exception err) {
            throw JmsUtil.convertJmsAccessException(err);
        }
    }

    public void send(Destination destination, Object message,
                     DeliveryMode deliveryMode, int priority, long timeToLive) throws JmsException {
        try {
            send(destination, message, deliveryMode,
                    priority, timeToLive, new MessagePostProcessorIF() {
                        public Message postProcessMessage(Message message) {
                            return message;
                        }
                    });
        }
        catch (Exception err) {
            throw JmsUtil.convertJmsAccessException(err);
        }
    }

    public void send(Destination destination, Object message) throws JmsException {
        try {
            send(destination, message, new MessagePostProcessorIF() {
                public Message postProcessMessage(Message message) {
                    return message;
                }
            });
        }
        catch (Exception err) {
            throw JmsUtil.convertJmsAccessException(err);
        }
    }

    public void send(Object message, DeliveryMode deliveryMode, int priority,
                     long timeToLive) throws JmsException {
        try {
            send(message, deliveryMode, priority, timeToLive,
                    new MessagePostProcessorIF() {
                        public Message postProcessMessage(Message message) {
                            return message;
                        }
                    });
        }
        catch (Exception err) {
            throw JmsUtil.convertJmsAccessException(err);
        }
    }

    public void send(Object message) throws JmsException {
        try {
            send(destination, message,
                    new MessagePostProcessorIF() {
                        public Message postProcessMessage(Message message) {
                            return message;
                        }
                    });
        }
        catch (Exception err) {
            throw JmsUtil.convertJmsAccessException(err);
        }
    }
}
