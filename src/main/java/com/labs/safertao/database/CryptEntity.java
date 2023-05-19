package com.labs.safertao.database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "crypts")
@Component
public class CryptEntity
{
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "cryptsIdSeq", sequenceName = "crypts_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cryptsIdSeq")
    private Integer id;

    @Column(name = "mode")
    private char mode;

    @Column(name = "message")
    private String message;

    @Column(name = "answer")
    private String answer;

    public CryptEntity(Integer id, char mode, String message, String answer)
    {
        this.id = id;
        this.mode = mode;
        this.message = message;
        this.answer = answer;
    }

    public CryptEntity(char mode, String message, String answer)
    {
        this.mode = mode;
        this.message = message;
        this.answer = answer;
    }

    public CryptEntity() {}

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public char getMode()
    {
        return mode;
    }

    public void setMode(char mode)
    {
        this.mode = mode;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getAnswer()
    {
        return answer;
    }

    public void setAnswer(String answer)
    {
        this.answer = answer;
    }
}
