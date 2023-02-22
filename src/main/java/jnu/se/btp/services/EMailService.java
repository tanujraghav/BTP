package jnu.se.btp.services;

import jnu.se.btp.entities.ResourceFileEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class EMailService {

    private final JavaMailSender javaMailSender;

    public EMailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void send(String[] to, String subject, String message) {
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom("BTech Project - tanujraghav <btp@tanujraghav.me>");
            mimeMessageHelper.setSubject(subject);

            mimeMessageHelper.setText(message, true);
        });
    }

    public void deleteNotification(@NotNull ResourceFileEntity resourceFileEntity) {

        final String message = "<html><style>*{box-sizing:border-box}</style><body style=margin:0;font-family:sans-serif;font-size:1rem;font-weight:400;line-height:1.5><div style=width:100%;padding-right:.75rem;padding-left:.75rem;margin-right:auto;margin-left:auto><div style='position:relative;display:flex;flex-direction:column;min-width:0;word-wrap:break-word;background-clip:border-box;border:2px solid rgba(0,0,0,.125);border-radius:.25rem;flex:1 1 auto;padding:1rem 1rem;background-color:#f8f9fa;margin:1.5rem'><div style=width:100%;padding-right:.75rem;padding-left:.75rem;margin-right:auto;margin-left:auto>Hey Admin,<p><div style='position:relative;padding:1rem 1rem;margin-bottom:1rem;border:1px solid transparent;border-radius:.25rem;color:#842029;background-color:#f8d7da;border-color:#f5c2c7'>The following resource has been <i>deleted</i></div><table><tr><th>ID<th> : <td>" + resourceFileEntity.getId() + "<tr><th>Title<th> : <td>" + resourceFileEntity.getTitle() + "<tr><th>Author<th> : <td>" + resourceFileEntity.getAuthor() + "<tr><th>Year<th> : <td>" + resourceFileEntity.getYear() + "<tr><th>Semester<th> : <td>" + resourceFileEntity.getSemester() + "<tr><th>Field<th> : <td>" + resourceFileEntity.getField() + "<tr><th>Extension<th> : <td>" + resourceFileEntity.getExtension() + "</table></div></div></div>";

        send(new String[]{"tanuj81_soe@jnu.ac.in"},
                "Resource Deleted",
                message);
    }

    public void OTPMail(Integer OTP, String to) {

        final String message = "<html><style>*{box-sizing:border-box}</style><body style=margin:0;font-family:sans-serif;font-size:1rem;font-weight:400;line-height:1.5><div style=width:100%;padding-right:.75rem;padding-left:.75rem;margin-right:auto;margin-left:auto><div style='position:relative;display:flex;flex-direction:column;min-width:0;word-wrap:break-word;background-clip:border-box;border:2px solid rgba(0,0,0,.125);border-radius:.25rem;flex:1 1 auto;padding:1rem 1rem;background-color:#f8f9fa;margin:1.5rem'><div style=width:100%;padding-right:.75rem;padding-left:.75rem;margin-right:auto;margin-left:auto>Hey Buddy,<p style=margin-top:1rem;margin-bottom:1rem>Here is your One Time Password for contributing to <a href=https://btp.tanujraghav.me style=text-decoration:none;color:#1d1f5a;font-weight:700 target=_blank>B.Tech. Project</a> :<h2 style='text-align:center;margin-bottom:1.5rem;font-weight:500;line-height:1.2;font-size:calc(1.325rem + .9vw)'>" + OTP + "</h2><div style='position:relative;padding:1rem 1rem;margin-bottom:1rem;border:1px solid transparent;border-radius:.25rem;color:#664d03;background-color:#fff3cd;border-color:#ffecb5;font-family:monospace'>The OTP is only valid for <b>7 minutes</b>!</div>Cheers,<br>The Team</div></div></div>";

        send(new String[]{to},
                "OTP for BTP",
                message);
    }

}
