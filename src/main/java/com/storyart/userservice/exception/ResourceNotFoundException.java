package com.storyart.userservice.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    private String resouceName;
    private String fieldName;
    private Object fieldValue;


    public ResourceNotFoundException(String resouceName, String fieldName, Object fieldValue) {


        super(String.format("Không tìm thấy %s với %s '%s' ", resouceName, fieldName, fieldValue));


        this.resouceName = resouceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;


    }

}
