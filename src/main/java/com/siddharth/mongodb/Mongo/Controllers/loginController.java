package com.siddharth.mongodb.Mongo.Controllers;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.siddharth.mongodb.Mongo.Models.Login;
import com.siddharth.mongodb.Mongo.Models.LoginModel;
import com.siddharth.mongodb.Mongo.Models.ResponseModel;
import com.siddharth.mongodb.Mongo.Repos.LoginCred;
import com.siddharth.mongodb.Mongo.Repos.LoginRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;
import java.util.Random;
@RestController
public class loginController {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private LoginRepo loginRepo;
    @Autowired
    private LoginCred logincred;
    private final String secretKey = "lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ#";
    private final String algorithm = "AES";



    @GetMapping(value = "/getdata")
    public List<Login> getData(){
        return loginRepo.findAll();
    }

    @PostMapping(value = "/save")
    private Object insertdata(@RequestBody Login log) throws Exception {
        ResponseModel rm=new ResponseModel();
        try {
            Random random = new Random();
            long randomNumber = (long) (random.nextDouble() * Math.pow(10, 16));
            int randomInt = (int) (randomNumber % Integer.MAX_VALUE);
            log.setId(randomInt);
            String e=encrypt(log.getPassword().toString());
            log.setPassword(e);
            loginRepo.save(log);
            rm.setMessage("User created successfully");
            return log;
        } catch (Exception ex) {
             rm.setMessage(ex.getMessage().toString());
             return rm;
        }
    }
    @PostMapping(value = "/excludeemail/{email}")
    private List<Login> excludeEmail(@PathVariable String email){
        List<Login> data = loginRepo.findAll();
        List<Login> filteredData = new ArrayList<>();
        for (Login d : data) {
            if (!d.getEmail().equals(email)) {
                Login log=new Login();
                log.setId(d.getId());
                log.setUsername(d.getUsername());
                log.setEmail(d.getEmail());
                log.setPassword(d.getPassword());
                log.setToken(d.getToken());
                filteredData.add(log);
            }
        }
        return filteredData;
    }
    @PutMapping(value = "/update")
    private ResponseModel updateData(@RequestBody Login login){
        ResponseModel res=new ResponseModel();
       try{
           Login up=loginRepo.findByEmail(login.getEmail()).get();
           up.setUsername(login.getUsername());
           up.setEmail(login.getEmail());
           up.setPassword(encrypt(login.getPassword()));
           up.setToken(login.getToken());
           loginRepo.save(up);
           res.setMessage("Record Updated of "+login.getUsername());
           return res;
       }catch (Exception e) {
           if(e.getMessage().toString().contains("Write operation error on server ac-f7p16yd-shard-00-02.btuzfix.mongodb.net:27017. Write error: WriteError{code=11000, message='E11000 duplicate key error collection: 64255fb5315d3245005b14c4_WeeChat.Login index: email_unique_index dup key:"))
               res.setMessage("User Already Exist");
           return res;
       }
    }
    @DeleteMapping(value = "/delete/{id}")
    private ResponseModel deleteData(@PathVariable String id){
        ResponseModel res=new ResponseModel();
        try{
            loginRepo.deleteById(id);
            res.setMessage("Deleted success");
            return res;
        }catch (Exception e){
            res.setMessage(e.getMessage().toString());
            return res;
        }
    }
    @PostMapping(value = "/login")
    public Object getDataById(@RequestBody LoginModel loginmodel)  {
        ResponseModel res=new ResponseModel();
        if(logincred.findByEmail(loginmodel.getEmail())!=null){
            try {
                String dec=decrypt(logincred.findByEmail(loginmodel.getEmail()).get().getPassword().toString());
                String pass=loginmodel.getPassword().toString();
                if(pass.equals(dec)){
                    res.setMessage("Login Success");
                    return loginRepo.findByEmail(loginmodel.getEmail());
                }else{
                    res.setMessage("Wrong password");
                    return res;
                }
            } catch (Exception e) {
                res.setMessage("User does not exists");
                return res;
            }
        }else{//not null check
            res.setMessage("User does not exists");
            return res;
        }
    }
    @GetMapping(value = "/")
    public String welCome()
    {
        return "welcome mongoDB";
    }
    public String encrypt(String data) throws Exception {
        Key key = new SecretKeySpec(secretKey.getBytes(), algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    public String decrypt(String encryptedData) throws Exception {
        Key key = new SecretKeySpec(secretKey.getBytes(), algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}