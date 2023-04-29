package com.labs.safertao.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ValidationCryptError
{
    private List<String> errors = new ArrayList<>();

    public void addError(String error)
    {
        this.errors.add(error);
    }
    public void addErrors(List<String> errors)
    {
        this.errors.addAll(errors);
    }
    public List<String> getErrors()
    {
        return errors;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValidationCryptError that = (ValidationCryptError) o;

        return Objects.equals(errors, that.errors);
    }
}