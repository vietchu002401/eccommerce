package com.vti.ecommerce.exception;

public class ServerErrorException extends RuntimeException{
    public ServerErrorException(String message){
        super(message);
    }
}
