package br.com.solbank.common.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.*;
import org.springframework.stereotype.Component;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//TODO perguntar para o GPT o que esta fazendo com esse aspecto
@Aspect
@Component
public class LogExecAspect {
    private static final Logger log = LoggerFactory.getLogger(LogExecAspect.class);

    @Around("@annotation(logExec)")
    public Object around(ProceedingJoinPoint pjp, LogExec logExec) throws Throwable {
        String corr = UUID.randomUUID().toString();
        MDC.put("corrId", corr);
        long t0 = System.nanoTime();
        String method = pjp.getSignature().toShortString();
        try {
            log.info("Start method={} tag={}", method, logExec.value());
            Object ret = pjp.proceed();
            long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0);
            log.info("End method={} elapsedMs={}", method, ms);
            return ret;
        } catch (Exception e){
            long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0);
            log.error("Error method={} elapsedMs={} ex={}", method, ms, e.toString());
            throw e;
        } finally {
            MDC.clear();
        }
    }

}

