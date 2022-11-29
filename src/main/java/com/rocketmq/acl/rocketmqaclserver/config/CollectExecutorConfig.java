
package com.rocketmq.acl.rocketmqaclserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
@ConfigurationProperties(prefix = "threadpool.config")
@Data
public class CollectExecutorConfig {
    private int coreSize = 20;
    private int maxSize = 20;
    private long keepAliveTime = 3000L;
    private int queueSize = 1000;

    @Bean(name = "collectExecutor")
    public ExecutorService collectExecutor(CollectExecutorConfig collectExecutorConfig) {
        ExecutorService collectExecutor = new ThreadPoolExecutor(
            collectExecutorConfig.getCoreSize(),
            collectExecutorConfig.getMaxSize(),
            collectExecutorConfig.getKeepAliveTime(),
            TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>(collectExecutorConfig.getQueueSize()),
            new ThreadFactory() {
                private final AtomicLong threadIndex = new AtomicLong(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "collectTopicThread_" + this.threadIndex.incrementAndGet());
                }
            },
            new ThreadPoolExecutor.DiscardOldestPolicy()
        );
        return collectExecutor;
    }
}
