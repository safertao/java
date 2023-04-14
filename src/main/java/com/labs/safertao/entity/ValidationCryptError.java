package com.labs.safertao.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ValidationCryptError
{
    private List<String> errors = new ArrayList<>();
    private String status;

    public void addError(String error)
    {
        this.errors.add(error);
    }
    public void addErrors(List<String> errors)
    {
        this.errors.addAll(errors);
    }
    public void setStatus(String status)
    {
        this.status = status;
    }
    public List<String> getErrors()
    {
        return errors;
    }
    public String getStatus() { return status;}

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValidationCryptError that = (ValidationCryptError) o;

        if (!Objects.equals(errors, that.errors)) return false;
        return Objects.equals(status, that.status);
    }
}
