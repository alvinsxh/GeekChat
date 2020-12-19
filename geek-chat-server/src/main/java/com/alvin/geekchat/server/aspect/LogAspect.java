package com.alvin.geekchat.server.aspect;

import com.alibaba.fastjson.JSON;
import com.alvin.geekchat.server.util.ContextUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {
    private static final Logger dalLogger = LoggerFactory.getLogger("dal");
    private static final Logger logger = LoggerFactory.getLogger("default");

    @Around("execution(public * com.alvin.geekchat.server.component..*.*(..))")
    public Object addComponentLog(ProceedingJoinPoint pjp) {
        long startTime = System.currentTimeMillis();
        String serviceName = pjp.getTarget().getClass().getSimpleName();
        String methodName = pjp.getSignature().getName();
        ContextUtil.entry();
        String traceId = ContextUtil.getTraceId();
        Object result = null;
        try {
            result = pjp.proceed();
        } catch (Exception e) {
            logger.error("service={}, method={}, traceId={}, exception={}", serviceName, methodName, traceId, e);
        } catch (Throwable t) {
            logger.error("service={}, method={}, traceId={}, throwable={}", serviceName, methodName, traceId, t);
        } finally {
            ContextUtil.exit();
            long cost = System.currentTimeMillis() - startTime;
            logger.info("service={}, method={}, traceId={}, cost={}", serviceName, methodName, traceId, cost);
        }
        return result;
    }

    @Around("execution(public * com.alvin.geekchat.server.dal.dao..*.*(..))")
    public Object addDalLog(ProceedingJoinPoint pjp) {
        long startTime = System.currentTimeMillis();
        String serviceName = pjp.getSignature().getDeclaringType().getSimpleName();
        String methodName = pjp.getSignature().getName();
        ContextUtil.entry();
        String traceId = ContextUtil.getTraceId();
        Object result = null;
        Object args = pjp.getArgs();
        try {
            result = pjp.proceed();
        } catch (Exception e) {
            dalLogger.error("service={}, method={}, traceId={}, exception={}", serviceName, methodName, traceId, e);
        } catch (Throwable t) {
            dalLogger.error("service={}, method={}, traceId={}, throwable={}", serviceName, methodName, traceId, t);
        } finally {
            ContextUtil.exit();
            long cost = System.currentTimeMillis() - startTime;
            dalLogger.info("service={}, method={}, traceId={}, args={}, result={}, cost={}", serviceName, methodName, traceId, JSON.toJSONString(args), JSON.toJSONString(result), cost);
        }
        return result;
    }
}
