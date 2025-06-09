package it.uniroma3.siwbooks.controller.validator;

import it.uniroma3.siwbooks.service.CredentialsService;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import it.uniroma3.siwbooks.models.Credentials;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
public class PasswordValidator implements Validator {

    @Autowired
    CredentialsService  credentialsService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Credentials.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Credentials credentials = (Credentials) target;
        if(!DigitInPassword(credentials.getPassword()) && !SpecialCharInPassword(credentials.getPassword())){
            errors.rejectValue("password", "password.notValid", "Password non valida, deve contenere almeno un carattere numerico e almeno un carattere speciale");
        }
    }
    private boolean DigitInPassword(String password){
        for(char c : password.toCharArray()){
            if(Character.isDigit(c))
                return true;
        }
        return false;
    }
    private boolean SpecialCharInPassword(String password) {
        Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^A-Za-z0-9]");
        Matcher m = SPECIAL_CHAR_PATTERN.matcher(password);
        return m.find();
    }
}
