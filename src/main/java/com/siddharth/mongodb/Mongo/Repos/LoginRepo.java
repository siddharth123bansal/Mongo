package com.siddharth.mongodb.Mongo.Repos;

import com.siddharth.mongodb.Mongo.Models.Login;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginRepo extends MongoRepository<Login,Integer> {
}
