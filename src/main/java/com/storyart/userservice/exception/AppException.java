package com.storyart.userservice.exception;


import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class AppException extends RuntimeException{
    public AppException(String message){
        super(message);
    }


    public AppException(String message, Throwable cause){
        super(message, cause);
    }

//todo; lam sao handle het exception o phia


}
