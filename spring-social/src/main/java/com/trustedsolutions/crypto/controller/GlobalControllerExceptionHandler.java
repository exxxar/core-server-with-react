package com.trustedsolutions.crypto.controller;

import com.core.cryptolib.TelegramLoggerService;
import java.util.EnumMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    MessageSource messageSource;

    @Value("${app.title}")
    private String appTitle;

    @Value("${telegram.logger-channel}")
    private String telegramChannel;

    @Value("${telegram.logger-token}")
    private String telegramToken;

    TelegramLoggerService logger;

    public GlobalControllerExceptionHandler() {
        this.logger = new TelegramLoggerService( 
                telegramChannel, 
                telegramToken, 
                appTitle,
                GlobalControllerExceptionHandler.class.getName(),
                true);
            
    }

    public JSONObject errorPrepareFactory(HttpStatus status, String messageKey, Object[] objs) {
        JSONObject message = new JSONObject();
        message.put("detail", messageSource.getMessage(messageKey,
                objs,
                LocaleContextHolder.getLocale()));
        message.put("title", status.name());
        message.put("code", status.value());

        logger.info(message.toJSONString());

        return message;
    }

    @Override
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        JSONObject message = this.errorPrepareFactory(status, "http.status.code.405", null);

        return new ResponseEntity<>(message, status);
    }

    @Override
    public ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        JSONObject message = this.errorPrepareFactory(status, "http.status.code.415", null);

        return new ResponseEntity<>(message, status);
    }

    @Override
    public ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        JSONObject message = this.errorPrepareFactory(status, "http.status.code.415", null);

        return new ResponseEntity<>(message, status);
    }

    @Override
    public ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        JSONObject message = this.errorPrepareFactory(status, "http.status.code.400", null);

        return new ResponseEntity<>(message, status);
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        JSONObject message = this.errorPrepareFactory(status, "http.status.code.501", null);

        return new ResponseEntity<>(message, status);
    }

    @Override
    public ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        JSONObject message = this.errorPrepareFactory(status, "http.status.code.500", null);

        return new ResponseEntity<>(message, status);
    }

    @Override
    public ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        JSONObject message = this.errorPrepareFactory(status, "http.status.code.400", null);

        return new ResponseEntity<>(message, status);
    }

    @ExceptionHandler(RequestRejectedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleRequestRejectedException(final HttpServletRequest request, final RequestRejectedException ex) {
        JSONObject message = this.errorPrepareFactory(HttpStatus.INTERNAL_SERVER_ERROR, "http.status.code.500", null);

        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Generates a Server Error page.
     *
     * @param ex An exception.
     * @return The tile definition name for the page.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleException(final Exception ex) {
        JSONObject message = this.errorPrepareFactory(HttpStatus.INTERNAL_SERVER_ERROR, "http.status.code.500", null);

        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        JSONObject message = this.errorPrepareFactory(status, "http.status.code.405", null);

        return new ResponseEntity<>(message, status);
    }

    @Override
    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        Object responseBody = null;

        HttpStatus st = status;

        if (request.getHeader("X-API-VERSION") == null
                && (status == HttpStatus.UNSUPPORTED_MEDIA_TYPE
                || status == HttpStatus.NOT_FOUND)) {
            st = HttpStatus.GONE;

            responseBody = this.errorPrepareFactory(st, "api.error.version", null);
        }

        if (request.getHeader("X-API-VERSION") != null
                && (status == HttpStatus.UNSUPPORTED_MEDIA_TYPE
                || status == HttpStatus.NOT_FOUND)) {
            st = HttpStatus.GONE;

            responseBody = this.errorPrepareFactory(st, "api.error.version.param",
                    new Object[]{request.getHeader("X-API-VERSION")});

        }
//
//        if (status == HttpStatus.NOT_FOUND) {
//            st = HttpStatus.NOT_FOUND;
//
//            responseBody = this.errorPrepareFactory(st, "http.status.code.404", null);
//        }

        return new ResponseEntity<>(responseBody, st);
    }

    @ExceptionHandler({NoSuchMessageException.class, IllegalArgumentException.class, NullPointerException.class})
    public ResponseEntity<Object> systemExceptionHandling(Exception ex) {

        Object responseBody = this.errorPrepareFactory(HttpStatus.INTERNAL_SERVER_ERROR, "http.status.code.500", null);

        return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }
//

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handlerResposneException(ResponseStatusException ex) {

        Map<HttpStatus, String> map = new EnumMap<>(HttpStatus.class);
        map.put(HttpStatus.BAD_REQUEST, "http.status.code.400");
        map.put(HttpStatus.UNAUTHORIZED, "http.status.code.401");
        map.put(HttpStatus.FORBIDDEN, "http.status.code.403");
        map.put(HttpStatus.NOT_FOUND, "http.status.code.404");
        map.put(HttpStatus.NO_CONTENT, "http.status.code.404");
        map.put(HttpStatus.METHOD_NOT_ALLOWED, "http.status.code.405");
        map.put(HttpStatus.GONE, "http.status.code.410");
        map.put(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "http.status.code.415");
        map.put(HttpStatus.LOCKED, "http.status.code.423");
        map.put(HttpStatus.INTERNAL_SERVER_ERROR, "http.status.code.500");
        map.put(HttpStatus.NOT_IMPLEMENTED, "http.status.code.501");
        map.put(HttpStatus.SERVICE_UNAVAILABLE, "http.status.code.503");

        Object responseBody = this.errorPrepareFactory(ex.getStatus(), map.get(ex.getStatus()), null);

        return new ResponseEntity<>(responseBody, ex.getStatus());
    }

}
