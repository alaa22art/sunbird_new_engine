package com.certacure.lis.interfaces.middleware.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.certacure.core.common.util.JSONUtil;
import com.certacure.lis.interfaces.middleware.core.observer.ObserverProtocol.MsgProcessed;

import akka.actor.AbstractActorWithStash;
import akka.event.EventStream;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scala.Option;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public abstract class FlowComponent<T extends ConfMsg> extends AbstractActorWithStash {

    protected final LoggingAdapter log;
    protected T conf;
    private EventStream eventStream;

    protected abstract PartialFunction<Object, BoxedUnit> getBehaviour();

    @SuppressWarnings("unchecked")
    protected FlowComponent() {
        this.eventStream= context().system().eventStream();
        log= Logging.getLogger(context().system(), this);

        receive(ReceiveBuilder
            .match(ConfMsg.class, conf -> {
                this.conf= (T) conf;
                init();
                context().become(getBehaviour());
                unstashAll();
            })
            .matchAny(__ -> stash())
            .build());
    }

    @Override
    public void preStart() {
        log.info(
            "\n=============================================Starting======================================================\n");
    }

    public void preRestart(Throwable reason, Optional<Object> message) {
        log.error(
            reason,
            "Restarting due to [{}] when processing [{}]",
            reason.getMessage(),
            message.isPresent() ? message.get() : "");
    }

    @Override
    public void aroundReceive(PartialFunction<Object, BoxedUnit> receive, Object msg) {
        long start= System.currentTimeMillis();
        if (getUnloggedTypes().contains(msg.getClass())) {
            super.aroundReceive(receive, msg);
        } else {
            try {
                super.aroundReceive(receive, msg);
                Map<String, String> map= new HashMap<>();
                map.put("sender", "Message processed. Sender: " + sender().toString());
                map.put("msg", ". Message body: " + msg.toString());
                map.put("machinePath", getContext().parent().toString());
                log.info(JSONUtil.convertMapToJSON(map));
                eventStream.publish(
                    new MsgProcessed(sender(), self(), msg, start, System.currentTimeMillis()));
            } catch (Throwable t) {
                log.error(
                    "Message processing failed. Sender: " + sender() + ". Message body: " + msg, t);
                eventStream.publish(
                    new MsgProcessed(sender(), self(), msg, start, System.currentTimeMillis(), t));
                throw t;

            }
        }
    }

    public Set<Class<?>> getUnloggedTypes() {
        return Collections.emptySet();
    }

    protected void init() throws Exception {}

    @Override
    public void preRestart(Throwable reason, Option<Object> message) {
        super.preRestart(reason, message);
        self().tell(conf, self());
    }
}