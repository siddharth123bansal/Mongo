package com.siddharth.mongodb.Mongo.Repos;

import com.siddharth.mongodb.Mongo.Models.LoginModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LoginCred extends MongoRepository<LoginModel,String> {
    Optional<LoginModel> findByEmail(String email);}
