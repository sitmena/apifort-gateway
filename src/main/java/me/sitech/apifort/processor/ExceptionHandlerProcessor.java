package me.sitech.apifort.processor;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.domain.response.common.ErrorResponse;
import me.sitech.apifort.exceptions.APIFortNoDataFound;
import me.sitech.apifort.exceptions.APIFortPathNotFoundException;
import me.sitech.apifort.exceptions.APIFortSecurityException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.conn.HttpHostConnectException;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.NoResultException;
import java.security.SignatureException;

@ApplicationScoped
public class ExceptionHandlerProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        final Throwable ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
        ex.printStackTrace();


        if (    ex instanceof APIFortSecurityException ||
                ex instanceof SignatureException ||
                ex instanceof MalformedJwtException ||
                ex instanceof ExpiredJwtException ||
                ex instanceof UnsupportedJwtException
                ) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.UNAUTHORIZED);
            exchange.getIn().setBody(new ErrorResponse(ApiFortStatusCode.UNAUTHORIZED, ApiFortStatusCode.UNAUTHORIZED_STRING));
        }else if(ex  instanceof HttpHostConnectException){
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.SERVICE_UNAVAILABLE);
            exchange.getIn().setBody(new ErrorResponse(ApiFortStatusCode.SERVICE_UNAVAILABLE, ApiFortStatusCode.SERVICE_UNAVAILABLE_STRING));
        }

        else if(ex instanceof NoResultException){
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.BAD_REQUEST);
            exchange.getIn().setBody(new ErrorResponse(ApiFortStatusCode.BAD_REQUEST, ex.getMessage()));
        }
        else if(ex instanceof APIFortNoDataFound){
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.NO_CONTENT);
            exchange.getIn().setBody(new ErrorResponse(ApiFortStatusCode.NO_CONTENT,ex.getLocalizedMessage()));
        }else if(ex instanceof APIFortPathNotFoundException){
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.BAD_REQUEST);
            exchange.getIn().setBody(new ErrorResponse(ApiFortStatusCode.BAD_REQUEST,String.format("Invalid path %s", ex.getLocalizedMessage())));
        }
        else{
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.BAD_REQUEST);
            exchange.getIn().setBody(new ErrorResponse(ApiFortStatusCode.BAD_REQUEST,ex.getLocalizedMessage()));
        }

    }
}
