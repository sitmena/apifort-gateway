package me.sitech.integration.exception;

import io.opentelemetry.api.trace.Span;
import lombok.extern.slf4j.Slf4j;
import me.sitech.integration.domain.response.errors.ErrorRes;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.enterprise.context.ApplicationScoped;
import java.security.SignatureException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@ApplicationScoped
public class IntegrationExceptionHandler implements Processor {

    Pattern pattern = Pattern.compile("\\((.*?)\\)");
    @Override
    public void process(Exchange exchange) throws Exception {
        final Throwable ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
        String traceId = Span.current().getSpanContext().getTraceId();
        log.debug(ex.getClass().getName());
        log.error("Exception Handler:", ex);
        if (ex instanceof SignatureException) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 401);
            exchange.getIn().setBody(new ErrorRes(traceId, "Unauthorized"));
        } else if (ex instanceof io.grpc.StatusRuntimeException) {
            String msg = ex.getMessage();
            Matcher matcher = pattern.matcher(msg);
            if (matcher.find()) {
                String err = matcher.group(1);
                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, Integer.parseInt(err.split(",")[0].split("=")[1]));
                exchange.getIn().setBody(new ErrorRes(traceId, err.split(",")[1].split("=")[1]));
            } else {
                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
                exchange.getIn().setBody(new ErrorRes(traceId, msg));
            }
        }
    }
}
