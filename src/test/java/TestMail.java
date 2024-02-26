import com.mailosaur.MailosaurClient;
import com.mailosaur.MailosaurException;
import com.mailosaur.models.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Document: https://mailosaur.com/docs/languages/java
public class TestMail {

    @Test
    public void test() throws MailosaurException, IOException {
        String apiKey = "7I1bEyhc8VmzQW3hThKhUQD2k0qppMl4";
        String serverId = "yvwrpc7q";
        String serverDomain = "yvwrpc7q.mailosaur.net";

        MailosaurClient mailosaur = new MailosaurClient(apiKey);

        MessageSearchParams params = new MessageSearchParams();
        params.withServer(serverId);

        SearchCriteria criteria = new SearchCriteria();
        criteria.withSentTo("anything@" + serverDomain);

        Message message = mailosaur.messages().get(params, criteria);

        System.out.println(message.subject());
        System.out.println(message.to().get(0).email());
        System.out.println(message.from().get(0).email());
        System.out.println(message.cc());

        System.out.println("************  BODY  **************");
        System.out.println(message.text().body());

        Assert.assertTrue(message.text().body().contains("Thank you for choosing Livpure Smart"));

        // How many links?
        System.out.println("************  Links  **************");
        System.out.println(message.html().links().size()); // 2

        Link firstLink = message.html().links().get(0);
        System.out.println(firstLink.text()); // "Google Search"
        System.out.println(firstLink.href()); // "https://www.google.com/"



        System.out.println("************  ATTACHMENTS  **************");
        System.out.println(message.attachments().size()); // 2

        Attachment firstAttachment = message.attachments().get(0);
        System.out.println(firstAttachment.fileName()); // "contract.pdf"
        System.out.println(firstAttachment.contentType()); // "application/pdf"

        // Here the file will get download in the project directory
        byte[] file = mailosaur.files().getAttachment(firstAttachment.id());
        Files.write(Paths.get(firstAttachment.fileName()), file);


        System.out.println("*****************  CODES *********************");
       /* // How many codes?
        System.out.println(message.text().codes().size()); // 2

        Code firstCode = message.text().codes().get(0);
        System.out.println(firstCode.value()); // "456812"*/

        Pattern pattern = Pattern.compile(".*([0-9]{6}).*");
        Matcher matcher = pattern.matcher(message.text().body());
        matcher.find();

        System.out.println(matcher.group(1)); // "243546"



        Assert.assertNotNull(message);
        Assert.assertEquals("Fwd: Livpure Smart RO Assurance Certificate", message.subject());
    }
}
