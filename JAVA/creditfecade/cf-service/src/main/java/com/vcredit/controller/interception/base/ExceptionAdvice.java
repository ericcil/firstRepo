package com.vcredit.controller.interception.base;

import com.alibaba.fastjson.JSON;
import com.vcredit.config.exception.BusinessException;
import com.vcredit.config.exception.ExceptionCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 异常统一处理类
 * 考虑到大多项目不是restful风格的接口，不采用{@link ResponseEntityExceptionHandler} 提供的默认异常处理
 * @Author chenyubin
 * @Date 2018/7/11
 */
@ControllerAdvice
@Slf4j
public class ExceptionAdvice /*extends ResponseEntityExceptionHandler*/ {

    private static final String ERROR_CODE_PARAM_VALID = ExceptionCodeEnum.PARAM_VALID.getErrorCode();
    private static final String ERROR_CODE_BASE = ExceptionCodeEnum.DEFAULT_CODE.getErrorCode();


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultBody> handleCustomException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        String url = request.getRequestURI();

        if(ex instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException validEx = (MethodArgumentNotValidException)ex;
            return new ResponseEntity( paramValidExceptionHandle(url,validEx) ,HttpStatus.OK);
        }
        if(ex instanceof HttpMessageNotReadableException){
            log.error("url==={},错误的入参",url);
            return new ResponseEntity( exceptionCodeForma(ExceptionCodeEnum.BAD_REQUEST_FORMAT) ,HttpStatus.OK);
        }
        if(ex instanceof BusinessException){
            BusinessException be = (BusinessException)ex;
            log.error("url==={},发生业务异常:code={},msg={}",url,be.getErrorCode(),be.getErrorMsg());
            return new ResponseEntity( buinessExceptionForma(be) ,HttpStatus.OK);
        }
        log.error("url==={},request error,info:",ex);
        ResultBody body = new ResultBody().error( ERROR_CODE_BASE ,"系统异常");
        return new ResponseEntity(body,HttpStatus.OK);
    }


    /**
     * 处理Hibernate Validator抛出的异常
     * @param validEx
     * @return
     */
    private ResultBody paramValidExceptionHandle(String url,MethodArgumentNotValidException validEx){
        BindingResult exResult = validEx.getBindingResult();
        String msg = "参数校验未通过";
        if( exResult.getErrorCount() > 0 ){
            ObjectError objError = exResult.getAllErrors().get(0);
            if( !StringUtils.isEmpty(objError.getDefaultMessage()) ){
                msg = objError.getDefaultMessage();
            }
        }
        log.error("url==={},参数校验未通过：{}",url,msg);
        return new ResultBody().error(ERROR_CODE_PARAM_VALID,msg);
    }


    private ResultBody exceptionCodeForma(ExceptionCodeEnum ece){
        return new ResultBody().error(ece.getErrorCode(),ece.getErrorMsg());
    }

    private ResultBody buinessExceptionForma(BusinessException be){
        return new ResultBody().error(be.getErrorCode(),be.getErrorMsg());
    }
}