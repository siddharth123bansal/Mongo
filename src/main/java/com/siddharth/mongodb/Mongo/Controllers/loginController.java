package com.siddharth.mongodb.Mongo.Controllers;
import com.mongodb.DuplicateKeyException;
import com.siddharth.mongodb.Mongo.Models.Login;
import com.siddharth.mongodb.Mongo.Models.LoginModel;
import com.siddharth.mongodb.Mongo.Repos.LoginCred;
import com.siddharth.mongodb.Mongo.Repos.LoginRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;
import java.util.Random;

@RestController
public class loginController {
    @Autowired
    private LoginRepo loginRepo;
    @Autowired
    private LoginCred logincred;
    private final String secretKey = "lIlBxya5XVsmeDCoUl6vHhdIESMB6s==";
    private final String algorithm = "AES";

    @GetMapping(value = "/getdata")
    public List<Login> getData(){
        return loginRepo.findAll();
    }
    @PostMapping(value = "/save")
    private ResponseEntity<String> insertdata(Login log) throws Exception {
        try {
            Random random = new Random();
            long randomNumber = (long) (random.nextDouble() * Math.pow(10, 16));
            int randomInt = (int) (randomNumber % Integer.MAX_VALUE);
            log.setId(randomInt);
            String e=encrypt(log.getPassword().toString());
            log.setPassword(e);
            loginRepo.save(log);
            return ResponseEntity.ok("User created successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.valueOf(200))
                    .body(" User Already exists ");
        }
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
    @GetMapping(value = "/login")
    public String getDataById(@RequestBody LoginModel loginmodel)  {
        if(logincred.findByEmail(loginmodel.getEmail())!=null){
            try {
                String dec=decrypt(logincred.findByEmail(loginmodel.getEmail()).get().getPassword().toString());
                String pass=loginmodel.getPassword().toString();
                if(pass.equals(dec)){
                    return " login Success ";
                }else{

                    return " Wrong password ";
                }
            } catch (Exception e) {
                return "User does not exists";
            }

        }else{//not null check
            return "User does not exists";
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