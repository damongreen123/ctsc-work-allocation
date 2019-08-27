package uk.gov.hmcts.reform.workallocation.mail;

import java.util.Date;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailUtil {

    private EmailUtil() {
        super();
    }

    /**
     * Utility method to send simple HTML email.
     *
     */
    public static void sendEmail(Session session, String toEmail, String subject, String body) {
        try {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            //  msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            //  msg.addHeader("format", "flowed");
            //  msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.addHeader("EventDesc", "Event Description Test");
            msg.addHeader("project", "Divorce");
            msg.addHeader("Service", "Divorce");

            msg.setReplyTo(InternetAddress.parse("ykiran2002@gmail.com", false));

            msg.setSubject(subject, "UTF-8");

            msg.setText(body, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            System.out.println("Message is ready");
            Transport.send(msg);

            System.out.println("EMail Sent Successfully!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
