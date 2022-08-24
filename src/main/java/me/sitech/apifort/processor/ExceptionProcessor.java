package me.sitech.apifort.processor;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import me.sitech.apifort.constant.StatusCode;
import me.sitech.apifort.domain.response.common.ErrorResponse;
import me.sitech.apifort.exceptions.APIFortNotfoundException;
import me.sitech.apifort.exceptions.APIFortSecurityException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.conn.HttpHostConnectException;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.NoResultException;
import java.security.SignatureException;

@ApplicationScoped
public class ExceptionProcessor implements Processor {
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
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, StatusCode.UNAUTHORIZED);
            exchange.getIn().setBody(new ErrorResponse(StatusCode.UNAUTHORIZED,StatusCode.UNAUTHORIZED_STRING));
        }else if(ex  instanceof HttpHostConnectException){
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, StatusCode.SERVICE_UNAVAILABLE);
            exchange.getIn().setBody(new ErrorResponse(StatusCode.SERVICE_UNAVAILABLE,StatusCode.SERVICE_UNAVAILABLE_STRING));
        }else if(ex instanceof NoResultException){
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, StatusCode.BAD_REQUEST);
            exchange.getIn().setBody(new ErrorResponse(StatusCode.BAD_REQUEST, ex.getMessage()));
        }
        else if(ex instanceof APIFortNotfoundException){
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, StatusCode.NO_CONTENT);
            exchange.getIn().setBody(new ErrorResponse(StatusCode.BAD_REQUEST,ex.getLocalizedMessage()));
        }
        else{
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, StatusCode.BAD_REQUEST);
            exchange.getIn().setBody(new ErrorResponse(StatusCode.BAD_REQUEST,ex.getLocalizedMessage()));
        }

    }
}
