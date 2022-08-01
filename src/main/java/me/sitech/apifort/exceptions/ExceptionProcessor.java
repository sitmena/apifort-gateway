package me.sitech.apifort.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import me.sitech.apifort.constant.StatusCode;
import me.sitech.apifort.api.v1.portal.domain.response.ErrorResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.enterprise.context.ApplicationScoped;
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
        }else{

            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, StatusCode.BAD_REQUEST);
            exchange.getIn().setBody(new ErrorResponse(StatusCode.BAD_REQUEST,ex.getLocalizedMessage()));
        }

    }
}
