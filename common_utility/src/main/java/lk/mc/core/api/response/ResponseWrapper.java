/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.mc.core.api.response;

import lk.mc.core.constants.ApplicationConstants;

import java.io.Serializable;

/**
 * Common standard to return an API response.
 * Use this class in all requests
 *
 * @author vihangawicks
 * @since 11/25/21
 * MC-lms
 */
public class ResponseWrapper<T> implements Serializable {

    private String message;
    private T data;

    public ResponseWrapper() {
    }

    public ResponseWrapper(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public ResponseWrapper<T> responseOk(T data) {
        message = ApplicationConstants.RESPONSE_OK;
        this.data = data;
        return this;
    }

    public ResponseWrapper<T> responseOk(String msg, T data) {
        message = msg;
        this.data = data;
        return this;
    }

    public ResponseWrapper<T> responseFail(T data) {
        message = (String) data;
        this.data = data;
        return this;
    }

    public ResponseWrapper<T> responseFail(String msg, T data) {
        message = msg;
        this.data = data;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseWrapper{"
                + "message='" + message + '\''
                + ", data=" + data
                + '}';
    }
}
