package ibfbatch2miniproject.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ibfbatch2miniproject.backend.model.Login;
import ibfbatch2miniproject.backend.repository.SQLRepository;
import ibfbatch2miniproject.backend.utils.JWTUtils;

@Service
public class BackendService {

    @Autowired
    private SQLRepository sqlRepo; 

    @Autowired
    private JWTUtils utils;
    
    public String getLoginCreds(Login login) {
        Optional<Boolean> opt = sqlRepo.getLoginCreds(login); 
        if (opt.isEmpty())
            return "Invalid username"; 

        // wrong password 
        if (opt.get() == false)
            return "Invalid password";

        // correct credentials, send jwt
        return utils.generateToken(login);
    }
}
