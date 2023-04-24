package com.siddharth.mongodb.Mongo.Controllers;
import com.siddharth.mongodb.Mongo.Models.Login;
import com.siddharth.mongodb.Mongo.Repos.LoginRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
public class loginController {
    @Autowired
    private LoginRepo loginRepo;
    @GetMapping(value = "/getdata")
    public List<Login> getData(){
        return loginRepo.findAll();
    }
    @PostMapping(value = "/save")
    private String insertdata(Login log){
        Random random = new Random();
        long randomNumber = (long) (random.nextDouble() * Math.pow(10, 16));
        int randomInt = (int) (randomNumber % Integer.MAX_VALUE);
        log.setId(randomInt);
        loginRepo.save(log);
        return "Data inseted";
    }
    @PutMapping(value = "/update/{id}")
    //helloWorld
    private String updateData(@PathVariable int id,@RequestBody Login login){
        Login up=loginRepo.findById(id).get();
        up.setUsername(login.getUsername());
        up.setEmail(login.getEmail());
        up.setPassword(login.getPassword());
        loginRepo.save(up);
        return "Record Updated of "+login.getUsername();
    }
    @DeleteMapping(value = "/delete/{id}")
    private String deleteData(@PathVariable int id){
        loginRepo.deleteById(id);
        return "deleted success";
    }
    @GetMapping(value = "/")
    public String welCome()
    {
        return "welcome mongoDB";
    }
}