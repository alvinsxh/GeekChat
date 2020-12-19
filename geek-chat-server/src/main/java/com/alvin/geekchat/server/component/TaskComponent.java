package com.alvin.geekchat.server.component;

import com.alvin.geekchat.server.model.OnlineUser;
import com.alvin.geekchat.server.util.ContextUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Component
public class TaskComponent {
    private ExecutorService pool = Executors.newWorkStealingPool();

    public void submitRequestHandleTask(Consumer<OnlineUser> handler, OnlineUser onlineUser) {
        String traceId = ContextUtil.getTraceId();
        pool.submit(() -> {
            ContextUtil.setTraceId(traceId);
            handler.accept(onlineUser);
        });
    }

    @PreDestroy
    public void destroy() {
        pool.shutdown();
    }
}
