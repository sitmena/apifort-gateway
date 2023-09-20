package me.sitech.apifort.router.v1.gateway.processor;

import io.opentelemetry.api.trace.Span;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.exceptions.ApiFortInvalidEndpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.enterprise.context.ApplicationScoped;

import static me.sitech.apifort.constant.ApiFort.APIFORT_DOWNSTREAM_SERVICE_HEADER;

@Slf4j
@ApplicationScoped
public class GatewayExceptionHandlerProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        final Throwable ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
        log.error("Exception Handler ", ex);

        String traceId = Span.current().getSpanContext().getTraceId();

        if(ex instanceof ApiFortInvalidEndpoint || ex instanceof APIFortGeneralException){
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.BAD_REQUEST);
        }
        exchange.getIn().setHeader(ApiFort.APIFORT_TRACE_ID, traceId);
        exchange.getIn().removeHeader(APIFORT_DOWNSTREAM_SERVICE_HEADER);
    }
}
