package com.siddharth.mongodb.Mongo.Repos;

import com.siddharth.mongodb.Mongo.Models.Login;
import com.siddharth.mongodb.Mongo.Models.LoginModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoginRepo extends MongoRepository<Login,String> {
    Optional<Login> findByEmail(String email);
}
