package demo.hippo.email;

import org.apache.commons.mail2.core.EmailException;
import org.apache.commons.mail2.jakarta.HtmlEmail;

public class MailServiceImpl implements MailService {

    private static final String SMTP_HOST = "127.0.0.1";
    private static final int SMTP_PORT = 1025;

    private String cmsRoot;

    @Override
    public void sendMail(final String to, final String from, final String subject, final String html, final String text) throws EmailException {
        sendMail(new String[]{to}, from, subject, html, text);
    }

    @Override
    public void sendMail(final String[] to, final String from, final String subject, final String html, final String text) throws EmailException {
        final HtmlEmail email = new HtmlEmail();
        email.setHostName(SMTP_HOST);
        email.setSmtpPort(SMTP_PORT);
        email.addTo(to);
        email.setFrom(from);
        email.setSubject(subject);
        email.setHtmlMsg(html);
        email.setTextMsg(text);
        email.send();
    }

    public String getCmsRoot() {
        return cmsRoot;
    }

    @Override
    public void setCmsRoot(final String cmsRoot) {
        this.cmsRoot = cmsRoot;
    }
}
