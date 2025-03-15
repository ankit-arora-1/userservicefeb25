package com.scaler.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaler.userservice.dtos.SendEmailDto;
import com.scaler.userservice.models.Token;
import com.scaler.userservice.models.User;
import com.scaler.userservice.repositories.TokenRepository;
import com.scaler.userservice.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;

    public UserServiceImpl(UserRepository userRepository,
                           TokenRepository tokenRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           KafkaTemplate kafkaTemplate) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Token login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()) {
            // throw an exception for wrong credentials
            return null;
        }

        User user = userOptional.get();
        if(!bCryptPasswordEncoder
                .matches(password, user.getHashedPassword())) {
            // throw an exception for wrong credentials
            return null;
        }

        Token token = createToken(user);

        return tokenRepository.save(token);
    }

    @Override
    public User signUp(String name, String email, String password) {
        Optional<User> userOptional = userRepository
                .findByEmail(email);

        if(userOptional.isPresent()) {
            // throw an exception telling user already exists
            return null;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);

        SendEmailDto sendEmailDto = new SendEmailDto();
        sendEmailDto.setFrom("arora.ankit7@gmail.com");
        sendEmailDto.setTo(user.getEmail());
        sendEmailDto.setSubject("Welcome");
        sendEmailDto.setBody("Welcome Welcome Welcome");

        String sendEmailDtoString = null;
        try {
            sendEmailDtoString = objectMapper
                    .writeValueAsString(sendEmailDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        kafkaTemplate.send("sendEmail", sendEmailDtoString);

        return savedUser;
    }

    @Override
    public User validateToken(String tokenValue) {
        System.out.println("Calling this server");
        Optional<Token> tokenOptional = tokenRepository
                .findByValueAndDeletedAndExpiryGreaterThan(tokenValue,
                       false,
                        new Date());

        if(tokenOptional.isEmpty()) {
            // throw an exception
            return null;
        }

        return tokenOptional.get().getUser();
    }

    @Override
    public void logout(String tokenValue) {
        Optional<Token> tokenOptional = tokenRepository
                .findByValueAndDeleted(tokenValue, false);

        if(tokenOptional.isEmpty()) {
            // throw an exception
            return;
        }

        Token token = tokenOptional.get();
        token.setDeleted(true);
        tokenRepository.save(token);
    }

    private Token createToken(User user) {
        Token token = new Token();
        token.setUser(user);
        token.setValue(RandomStringUtils.randomAlphanumeric(128));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date date = calendar.getTime();

        token.setExpiry(date);
        token.setDeleted(false);

        return token;
    }
}
