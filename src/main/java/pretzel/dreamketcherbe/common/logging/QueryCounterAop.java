package pretzel.dreamketcherbe.common.logging;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class QueryCounterAop {

    private final QueryCounter queryCounter;

    @Around("execution(* javax.sql.DataSource.getConnection(..))")
    public Object getConnection(ProceedingJoinPoint joinpoint)
        throws Throwable {
        Object connection = joinpoint.proceed();
        ProxyFactory proxyFactory = new ProxyFactory(connection);
        return proxyFactory.getProxy();
    }
}
