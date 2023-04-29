package com.labs.safertao.service;

import com.labs.safertao.entity.CryptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.labs.safertao.service.inter.ICryptService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CryptService implements ICryptService
{
    private CounterService counterService;

    @Autowired
    public CryptService(CounterService service)
    {
        counterService = service;
    }

    @Override
    public String cryptMessage(char mode, String message) throws RuntimeException
    {
        counterService.incrementCounter();
        counterService.incrementSynchronizedCounter();
        if(message.equals("yesterday")) throw new RuntimeException("yesterday is forbidden word to encrypt(decrypt)");
        if(message.equals("tomorrow")) return "";
        int encryptionNumber = 3;
        if(mode == 'd') encryptionNumber = -3;
        message = message.toLowerCase();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < message.length(); i++)
        {
            int tmp = message.charAt(i) - 97;
            tmp = (tmp + encryptionNumber + 26) % 26 + 97;
            result.append((char) tmp);
        }
        return result.toString();
    }

    public String minLengthString(List<CryptResponse> resultList)
    {
        String result = "";
        List<String> strings = new ArrayList<>();
        resultList.forEach(e -> strings.add(e.answer()));
        if (!strings.isEmpty())
        {
            result = strings.stream()
                    .min(Comparator.comparingInt(String::length))
                    .get();
        }
        return result;
    }

    public String maxLengthString(List<CryptResponse> resultList)
    {
        String result = "";
        List<String> strings = new ArrayList<>();
        resultList.forEach(e -> strings.add(e.answer()));
        if (!strings.isEmpty())
        {
            result = strings.stream()
                    .max(Comparator.comparingInt(String::length))
                    .get();
        }
        return result;
    }

    public Integer avgLength(List<CryptResponse> resultList)
    {
        final Integer[] sumLength = {0};
        resultList.forEach(e -> sumLength[0] += e.answer().length());
        return sumLength[0]/resultList.size();
    }
}