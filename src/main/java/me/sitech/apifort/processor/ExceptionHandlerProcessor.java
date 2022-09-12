package me.sitech.apifort.processor;

import com.networknt.schema.ValidationMessage;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.quarkus.logging.Log;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.domain.response.common.ErrorResponse;
import me.sitech.apifort.exceptions.APIFortNoDataFound;
import me.sitech.apifort.exceptions.APIFortPathNotFoundException;
import me.sitech.apifort.exceptions.APIFortSecurityException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ValidationException;
import org.apache.camel.component.jsonvalidator.JsonValidationException;
import org.apache.http.conn.HttpHostConnectException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.security.SignatureException;
import java.util.Set;

@Slf4j
@ApplicationScoped
public class ExceptionHandlerProcessor implements Processor {

//    @Inject
//    private Tracer tracer;

    @Override
    public void process(Exchange exchange) throws Exception {
        final Throwable ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
        String traceId ="";
//        Span consumeMessageSpan = tracer.spanBuilder("consumeMessage").startSpan();
//        if(consumeMessageSpan!=null)
//            traceId= consumeMessageSpan.getSpanContext().getSpanId();

        /*if (ex instanceof JsonValidationException) {
            Set<ValidationMessage> errors = ((JsonValidationException)ex).getErrors();
            for (ValidationMessage e : errors) {
                Log.error(e.getMessage());
            }
            return;
        }*/
        log.error(ex.getMessage());
        if (    ex instanceof APIFortSecurityException ||
                ex instanceof SignatureException ||
                ex instanceof MalformedJwtException ||
                ex instanceof ExpiredJwtException ||
                ex instanceof UnsupportedJwtException
                ) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.UNAUTHORIZED);
            exchange.getIn().setBody(new ErrorResponse(traceId, ApiFortStatusCode.UNAUTHORIZED_STRING));
        }else if(ex  instanceof HttpHostConnectException){
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.SERVICE_UNAVAILABLE);
            exchange.getIn().setBody(new ErrorResponse(traceId, ApiFortStatusCode.SERVICE_UNAVAILABLE_STRING));
        }

        else if(ex instanceof NoResultException){
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.BAD_REQUEST);
            exchange.getIn().setBody(new ErrorResponse(traceId, ex.getMessage()));
        }
        else if(ex instanceof APIFortNoDataFound){
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.NO_CONTENT);
            exchange.getIn().setBody(new ErrorResponse(traceId,ex.getLocalizedMessage()));
        }else if(ex instanceof APIFortPathNotFoundException){
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.BAD_REQUEST);
            exchange.getIn().setBody(new ErrorResponse(traceId,String.format("Invalid path %s", ex.getLocalizedMessage())));
        }
        else if(ex instanceof JsonValidationException){
            ((JsonValidationException) ex).getErrors().stream().forEach(item->{
               log.error(">>> Message {}",item.getMessage());
               log.error(">>> code {}",item.getCode());
               log.error(">>> type {}",item.getType());
               log.error(">>> path {}",item.getPath());
               log.error(">>> schema  {}",item.getSchemaPath());
               log.error(">>> details {}",item.getDetails());
               log.error(">>> args {}",item.getArguments());
            });
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.BAD_REQUEST);
            exchange.getIn().setBody(new ErrorResponse(traceId,((JsonValidationException) ex).getErrors().toString()));
        }
        else{
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.BAD_REQUEST);
            exchange.getIn().setBody(new ErrorResponse(traceId,ex.getLocalizedMessage()));
        }

    }
}
