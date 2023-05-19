package com.labs.safertao.database;

import com.labs.safertao.entity.CryptResponse;
import com.labs.safertao.entity.InputPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataBaseService
{
    private CryptJpaRepository repository;
    private long nextId;

    @Autowired
    public DataBaseService(CryptJpaRepository repository)
    {
        this.repository = repository;
        nextId = repository.count() + 1;
    }

    public DataBaseService(){}

    public void saveCrypts(List<CryptResponse> crypts)
    {
        crypts.forEach(this::saveCrypt);
    }

    public void saveCrypt(CryptResponse crypt)
    {
        CryptEntity entity = new CryptEntity(crypt.mode(), crypt.message(), crypt.answer());
        repository.save(entity);
    }

    public List<CryptEntity> getAllCrypts()
    {
        return repository.findAll();
    }

    public CryptEntity getCryptEntity(InputPair input)
    {
        for(CryptEntity entity:getAllCrypts())
        {
            if(entity.getInputPair().equals(input))
                return entity;
        }
        return null;
    }

    public long size(){
        return repository.count();
    }

    public CryptEntity getById(long id)
    {
        for(CryptEntity entity:getAllCrypts())
        {
            if(entity.getId() == id) return entity;
        }
        return null;
    }

    public synchronized long getNextId()
    {
        nextId++;
        return nextId - 1;
    }
//
//    // сохранить по индексу
//    public void save(Numbers numbers, ResultPair resultPair, long id){
//        DbEntity entity = new DbEntity(numbers, resultPair);
//        entity.setId((int)id);
//        repository.save(entity);
//    }
}