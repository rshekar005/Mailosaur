import com.mailosaur.MailosaurClient;
import com.mailosaur.MailosaurException;
import com.mailosaur.models.Message;
import com.mailosaur.models.MessageSearchParams;
import com.mailosaur.models.SearchCriteria;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrowOTPTest {

    String apiKey = "7I1bEyhc8VmzQW3hThKhUQD2k0qppMl4";
    String serverId = "yvwrpc7q";
    String serverDomain = "yvwrpc7q.mailosaur.net";
    String from = "noreply@groww.in";

    public String getRandomEmail() {
        return "user" + System.currentTimeMillis() + "@" + serverDomain;
    }


    // Wait for a OTP
    public Message waitForEmail(String emailID, MailosaurClient mailosaur) {
        Wait<MailosaurClient> wait = new FluentWait<>(mailosaur).withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofSeconds(5))
                .ignoring(Exception.class);

        return wait.until(mailosaurClient1 -> {
            try {
                MessageSearchParams params = new MessageSearchParams();
                params.withServer(serverId);
                SearchCriteria criteria = new SearchCriteria();
                criteria.withSentTo(emailID);
                criteria.withSentFrom(from);
                Message message = mailosaur.messages().get(params, criteria);
                return message;
            } catch (MailosaurException | IOException e) {
                return null;
            }
        });

    }

    @Test
    public void test() throws MailosaurException, IOException {
        String toEmail = getRandomEmail();
        WebDriver driver = new ChromeDriver();
        driver.get("https://groww.in/");
        driver.findElement(By.xpath("//span[text()='Login/Register']")).click();
        driver.findElement(By.cssSelector("#login_email1")).sendKeys(toEmail);
        driver.findElement(By.cssSelector(".lils382ContinueBtn")).click();

        MailosaurClient mailosaur = new MailosaurClient(apiKey);

        Message message = waitForEmail(toEmail, mailosaur);

        String subject = message.subject();
        System.out.println("Subject is " + subject);
        Pattern pattern = Pattern.compile(".*([0-9]{6}).*");
        Matcher matcher = pattern.matcher(message.subject());
        matcher.find();
        String OTP = matcher.group(1);
        System.out.println("OTP is " + OTP);

        driver.findElement(By.cssSelector("#signup_otp1")).sendKeys(OTP);

    }
}
