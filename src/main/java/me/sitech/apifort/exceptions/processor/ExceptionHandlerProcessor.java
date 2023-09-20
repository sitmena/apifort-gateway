package me.sitech.apifort.exceptions.processor;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.opentelemetry.api.trace.Span;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.domain.response.common.ErrorRes;
import me.sitech.apifort.exceptions.APIFortNoDataFound;
import me.sitech.apifort.exceptions.APIFortPathNotFoundException;
import me.sitech.apifort.exceptions.APIFortSecurityException;
import me.sitech.apifort.exceptions.ApiFortEntityException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.jsonvalidator.JsonValidationException;
import org.apache.http.conn.HttpHostConnectException;
import org.hibernate.exception.ConstraintViolationException;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.NoResultException;
import java.security.SignatureException;
import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@ApplicationScoped
public class ExceptionHandlerProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        final Throwable ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
        String traceId = Span.current().getSpanContext().getTraceId();
        log.error("Exception Handler:", ex);
        if (ex instanceof APIFortSecurityException ||
                ex instanceof SignatureException ||
                ex instanceof MalformedJwtException ||
                ex instanceof ExpiredJwtException ||
                ex instanceof UnsupportedJwtException
        ) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.UNAUTHORIZED);
            exchange.getIn().setBody(new ErrorRes(traceId, ApiFortStatusCode.UNAUTHORIZED_STRING));
            return;
        }

        if (ex instanceof HttpHostConnectException) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.SERVICE_UNAVAILABLE);
            exchange.getIn().setBody(new ErrorRes(traceId, ApiFortStatusCode.SERVICE_UNAVAILABLE_STRING));
            return;
        }
        if (ex instanceof ApiFortEntityException ||
                ex instanceof NoResultException) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.BAD_REQUEST);
            exchange.getIn().setBody(new ErrorRes(traceId, ex.getMessage()));
            return;
        }

        if (ex instanceof APIFortNoDataFound) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.NO_CONTENT);
            exchange.getIn().setBody(new ErrorRes(traceId, ex.getLocalizedMessage()));
            return;
        }
        if (ex instanceof APIFortPathNotFoundException) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.BAD_REQUEST);
            exchange.getIn().setBody(new ErrorRes(traceId, String.format("Invalid path %s", ex.getLocalizedMessage())));
            return;
        }
        if (ex instanceof JsonValidationException exception) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.BAD_REQUEST);
            exchange.getIn().setBody(new ErrorRes(traceId, exception.getErrors().toString()));
            return;
        }
        if (ex instanceof javax.transaction.RollbackException e) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.BAD_REQUEST);
            if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause() instanceof ConstraintViolationException) {
                exchange.getIn().setBody(new ErrorRes(traceId, "Records already exists"));
                return;
            }
            exchange.getIn().setBody(new ErrorRes(traceId, e.getMessage()));
            return;
        }
        if (ex.getCause()!=null && ex.getCause().getCause() instanceof SQLIntegrityConstraintViolationException) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.BAD_REQUEST);
            exchange.getIn().setBody(new ErrorRes(traceId, "Duplicated record"));
            return;
        }
        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.BAD_REQUEST);
        exchange.getIn().setBody(new ErrorRes(traceId, ex.getLocalizedMessage()));


    }
}
