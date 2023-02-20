package me.sitech.integration.exception;

import io.opentelemetry.api.trace.Span;
import lombok.extern.slf4j.Slf4j;
import me.sitech.integration.domain.response.errors.ErrorRes;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import javax.enterprise.context.ApplicationScoped;
import java.security.SignatureException;

@Slf4j
@ApplicationScoped
public class IntegrationExceptionHandler implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        final Throwable ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
        String traceId = Span.current().getSpanContext().getTraceId();
        log.debug(ex.getClass().getName());
        log.error("Exception Handler:",ex);
        if (ex instanceof SignatureException ) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 401);
            exchange.getIn().setBody(new ErrorRes(traceId, "Unauthorized"));
        }
        else if( ex instanceof io.grpc.StatusRuntimeException){
            String msg = ex.getMessage();
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
            exchange.getIn().setBody(new ErrorRes(traceId, msg));
        }
        else {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
            exchange.getIn().setBody(new ErrorRes(traceId,ex.getLocalizedMessage()));
        }
    }
}
