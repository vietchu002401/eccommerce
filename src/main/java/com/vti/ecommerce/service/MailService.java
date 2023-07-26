package com.vti.ecommerce.service;

import com.vti.ecommerce.dto.OrderDTO;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

@Service
public class MailService {

    @Value("${email-password}")
    private String emailPassword;

    public void sendmail(String email, OrderDTO orderDTO) throws AddressException, MessagingException, IOException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "25");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("viet.nguyenquoc2@vti.com.vn", emailPassword);
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("viet.nguyenquoc2@vti.com.vn", "Shop Wibu"));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        msg.setSubject("Thanks for trusting");
        msg.setContent("<p><strong>Your order information here: </strong> <a href='https://animevietsub.moe/phim/suki-na-ko-ga-megane-wo-wasureta-a4927/tap-01-92280.html'>Client's link</a></p>" +
            "</br><p><strong>Total price: </strong>" + orderDTO.getTotalPrice() + " VND.</p></br>" +
            "<p><strong>Payment type: </strong>" + orderDTO.getUserPayment().getProvider() + " </p></br>" +
            "<p><strong>Estimated delivery day: </strong>" + orderDTO.getCreatedDate() + " </p>", "text/html");
        msg.setSentDate(new Date());
        Transport.send(msg);
    }
}
