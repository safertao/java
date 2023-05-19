package com.labs.safertao.entity;

public class AsyncEntity
{
    private String message;
    private long id;

    public AsyncEntity(String message, long id)
    {
        this.message = message;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
