package br.com.solbank.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class ConcurrencyConfig {

    @Bean(name = "virtualExecutor")
    public Executor virtualExecutor(){
        // barato para I/O (cada tarefa = 1 vt)
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean(name = "fixedExecutor")
    public Executor fixedExecutor() {
        // pool tradicional (platform threads)
        int n = Math.max(2, Runtime.getRuntime().availableProcessors());
        return Executors.newFixedThreadPool(n);
    }
}
