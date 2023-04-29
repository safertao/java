package com.labs.safertao.memory;

import com.labs.safertao.entity.CryptResponse;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Component
public class InMemoryStorage
{
    private Map<String, CryptResponse> dataStorage = new HashMap<String, CryptResponse>();

    public synchronized void saveCryptResponse(CryptResponse response)
    {
        dataStorage.put(response.message(), response);
    }

    public CryptResponse getSavedCryptResponse(String key)
    {
        return dataStorage.get(key);
    }

    public Integer size()
    {
        return dataStorage.size();
    }

    public Collection<CryptResponse> getAllSavedCryptResponses()
    {
        return dataStorage.values();
    }

    public Map<String, CryptResponse> getDataStorage() { return dataStorage; }

}
