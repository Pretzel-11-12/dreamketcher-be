package pretzel.dreamketcherbe.common.logging;

import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.web.context.request.RequestContextHolder;

@RequiredArgsConstructor
public class QueryMethodInterceptor implements MethodInterceptor {

    private static final Set<String> PREPARED_STATEMENTS = Set.of(
        "execute", "executeQuery", "executeUpdate");

    private final QueryCounter queryCounter;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        if (isExecuteQuery(invocation) && isRequest()) {
            queryCounter.increaseQueryCount();
        }
        return invocation.proceed();
    }

    private boolean isExecuteQuery(MethodInvocation invocation) {
        return PREPARED_STATEMENTS.contains(invocation.getMethod().getName());
    }

    private boolean isRequest() {
        return Objects.nonNull(RequestContextHolder.getRequestAttributes());
    }
}
