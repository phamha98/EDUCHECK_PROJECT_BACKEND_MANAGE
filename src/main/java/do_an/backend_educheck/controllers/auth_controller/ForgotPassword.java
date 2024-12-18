package do_an.backend_educheck.controllers.auth_controller;

import do_an.backend_educheck.controllers.ApiV1Controller;
import do_an.backend_educheck.dtos.auth_dto.ResetPasswordRequest;
import do_an.backend_educheck.models.user_entity.EWaitingR;
import do_an.backend_educheck.services.email_service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@ApiV1Controller
@RequestMapping(value = "/", produces = "application/json")
public class ForgotPassword {

    private final EmailService emailService;

    @PostMapping("forgot-password")
    public String sendMailForgotPassword(@RequestBody Map<String, String> requestBody) throws MessagingException {
        String email = requestBody.get("email");
        return emailService.sendEmail(email);
    }

    @GetMapping("reset-password")
    public String resetPassword(@RequestParam String e) {
        return emailService.resetPassword(e);
    }

    @GetMapping("get-all-ewaitingr")
    public List<EWaitingR> getAllEWaitingR() {
        return emailService.getAllEWaitingR();
    }

    @PostMapping("access-reset")
    public Boolean accessResetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest){
        return emailService.accessResetPassword(resetPasswordRequest);
    }

    @PostMapping("delete-forgotpass-ids")
    public Boolean deleteForgotPassWordIds(@RequestBody List<Long> ids){
        try{
            emailService.deleteUserEmailWithIds(ids);
            return true;
        }catch (Exception e){
            return false;
        }

    }

}
