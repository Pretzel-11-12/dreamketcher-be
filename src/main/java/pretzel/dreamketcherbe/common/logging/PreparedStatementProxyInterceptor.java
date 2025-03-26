package pretzel.dreamketcherbe.common.logging;

import java.sql.PreparedStatement;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;

@RequiredArgsConstructor
public class PreparedStatementProxyInterceptor implements MethodInterceptor {

    private final QueryCounter queryCounter;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object result = invocation.proceed();

        if (result instanceof PreparedStatement preparedStatement) {
            ProxyFactory proxyFactory = new ProxyFactory(preparedStatement);
            proxyFactory.addAdvice(new QueryMethodInterceptor(queryCounter));
            return proxyFactory.getProxy();
        }

        return result;
    }
}
