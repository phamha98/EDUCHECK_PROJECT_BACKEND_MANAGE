package do_an.backend_educheck.services.email_service;

import do_an.backend_educheck.dtos.auth_dto.ResetPasswordRequest;
import do_an.backend_educheck.exceptions.ArgumentException;
import do_an.backend_educheck.exceptions.EditProfileException;
import do_an.backend_educheck.models.user_entity.EWaitingR;
import do_an.backend_educheck.models.user_entity.UserEntity;
import do_an.backend_educheck.repositories.IEWRRepo;
import do_an.backend_educheck.repositories.IUserRepo;
import do_an.backend_educheck.services.auth_service.TokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailService {
    final private String senderEmail = "ngocbeo3387@gmail.com";
    final private JavaMailSender mailSender;
    private final TokenProvider tokenProvider;
    private final IUserRepo iUserRepo;
    private final IEWRRepo iewrRepo;
    private final PasswordEncoder passwordEncoder;

    public String sendEmail(String to) throws MessagingException {
        final UserEntity userEntity = iUserRepo.findByEmail(to);
        if(userEntity==null){
            throw new EditProfileException("");
        }
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("QUÊN MẬT KHẨU KIỂM TRA TRỰC TUYẾN");

        String encodeEmail = tokenProvider.createJwtToken(to, "student");

        String htmlContent =
                "<!DOCTYPE html>" +
                        "<html lang=\"vi\">" +
                        "<head>" +
                        "    <meta charset=\"UTF-8\">" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                        "    <title>Yêu cầu lấy lại mật khẩu</title>" +
                        "    <style>" +
                        "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                        "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                        "        img { max-width: 200px; height: auto; margin-bottom: 20px; }" +
                        "        h1 { color: #2c3e50; font-size: 24px; margin-bottom: 20px; }" +
                        "        p { margin-bottom: 20px; font-weight: bold; color: #e74c3c; }" +
                        "        a { display: inline-block; background-color: #3498db; color: #ffffff; " +
                        "           padding: 10px 20px; text-decoration: none; border-radius: 5px; " +
                        "           font-size: 16px; transition: background-color 0.3s; }" +
                        "        a:hover { background-color: #2980b9; }" +
                        "    </style>" +
                        "</head>" +
                        "<body>" +
                        "    <div class=\"container\">" +
                        "        <img src=\"https://firebasestorage.googleapis.com/v0/b/travel-app-130de.appspot.com/o/avatar%2FLogo.png?alt=media&token=53b7ac61-f6ab-4f7b-9a17-d3d37b36c4db\" alt=\"logo\">" +
                        "        <h1>BẠN ĐÃ YÊU CẦU LẤY LẠI MẬT KHẨU</h1>" +
                        "        <p>KHÔNG ĐƯỢC CUNG CẤP ĐƯỜNG LINK BÊN DƯỚI TỚI NGƯỜI KHÁC!</p>" +
                        "        <a href=\"http://localhost:2002/reset-password/" + encodeEmail + "\">Nhấn vào đây để lấy lại mật khẩu</a>" +
                        "    </div>" +
                        "</body>" +
                        "</html>";

        message.setContent(htmlContent, "text/html; charset=utf-8");
        Optional<EWaitingR> isEmailExist = Optional.ofNullable(iewrRepo.findByEmail(to));
        if(!isEmailExist.isPresent()){
            EWaitingR eWaitingR = EWaitingR.builder()
                    .email(to)
                    .build();
            iewrRepo.save(eWaitingR);
        }
        mailSender.send(message);
        System.out.println("Mail sent successfully");
        return "success";
    }

    public String resetPassword(String e){
        try{
            Claims claims = tokenProvider.decodeJwt(e);
            final String email = claims.getSubject();
            final var isExistEmailCLient = iUserRepo.findByEmail(email);
            Optional<EWaitingR> isEmailExistEWR = Optional.ofNullable(iewrRepo.findByEmail(email));

            if(isExistEmailCLient == null ||isEmailExistEWR.isEmpty()){
                throw new ArgumentException("Cảnh báo!!");
            }

        }catch (ArgumentException argumentException){
            throw new ArgumentException("Cảnh báo!!");
        }

        return "success";
    }

    public Boolean accessResetPassword(ResetPasswordRequest resetPasswordRequest){
        try {
            Claims claims = tokenProvider.decodeJwt(resetPasswordRequest.getEmail());
            final String decodeEmail = claims.getSubject();
            final var isExistEmailClient = iUserRepo.findByEmail(decodeEmail);

            isExistEmailClient.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
            iewrRepo.deleteByEmail(decodeEmail);
            iUserRepo.save(isExistEmailClient);
            return true;
        }catch (Exception ex){
            System.out.println("EmailService=>>>>"+ex.getMessage());
            return false;
        }
    }

    public List<EWaitingR> getAllEWaitingR(){
        return iewrRepo.findAll();
    }


    public void deleteUserEmailWithIds(List<Long> ids) {
        iewrRepo.deleteUserEmailWithIds(ids);
    }

}
