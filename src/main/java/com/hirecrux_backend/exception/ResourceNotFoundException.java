package com.hirecrux_backend.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
        //call parent constructor runtimeexception.
        //only we want to give message donot give another name for dbt ->runtimeexception

    }
}
