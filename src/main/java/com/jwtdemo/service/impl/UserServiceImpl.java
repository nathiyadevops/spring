package com.jwtdemo.service.impl;

import com.jwtdemo.model.User;
import com.jwtdemo.repo.UserRepo;
import com.jwtdemo.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JavaMailSender mailSender;


    @Override
    public void sendVerificationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "mnsh.pv1@gmail.com";
        String senderName = "Mounish";
        String subject = "Verify your registration";
        String content = "Dear [[name]],<br>" +
                "Please click the link below to verify your registration:<br>" +
                "<h3><a href=\"[[url]]\" target=\"_self\">VERIFY</a></h3>" +
                "Thank you,<br>" +
                "Mounish";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress,senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]",user.getFirstName());
        String verifyUrl = "http://locahost:8080/v1/auth/verify?code="+user.getVerificationCode();
        content = content.replace("[[url]]",verifyUrl);
        helper.setText(content,true);
        mailSender.send(message);
        System.out.println("mail sent");
    }

    @Override
    public boolean verify(String code) {
        User user = userRepo.findByVerificationCode(code);
        if (user == null || !user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepo.save(user);
            return true;
        }
    }
}
