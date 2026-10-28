package com.moptra.invoice.comman.exception;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GenericException extends RuntimeException{
    private String errorMessage;
    private int statusCode;

    public GenericException(int statusCode, String errorMessage) {
        super(errorMessage);
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }}
