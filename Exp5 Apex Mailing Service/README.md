# Experiment 5 - Mailing service using Apex (Salesforce)

## Aim
To implement a mailing service using the Apex programming language of Salesforce.

## Procedure
1. Open the Developer Console.
2. Create a new Apex class **EmailManagerApex** (File > New > Apex Class).
3. Replace the class body with the EmailManager code (uses `Messaging.SingleEmailMessage` and `Messaging.sendEmail`).
4. Save the class (File > Save).
5. Open **Debug > Open Execute Anonymous Window** and call `EmailManagerApex.sendMail(...)` with the recipient address, subject and body.
6. Execute with **Open Log** ticked and filter **Debug Only** to see `Email sent successfully`.
7. Check the inbox for the received email.

## Source code

**`src/classes/EmailManagerApex.cls`**

```java
public class EmailManagerApex {
    // Public method
    public static void sendMail(String address, String subject, String body) {
        // Create an email message object
        Messaging.SingleEmailMessage mail = new Messaging.SingleEmailMessage();
        String[] toAddresses = new String[] {address};
        mail.setToAddresses(toAddresses);
        mail.setSubject(subject);
        mail.setPlainTextBody(body);
        // Pass this email message to the built-in sendEmail method
        // of the Messaging class
        Messaging.SendEmailResult[] results = Messaging.sendEmail(
                                 new Messaging.SingleEmailMessage[] { mail });
        // Call a helper method to inspect the returned results
        inspectResults(results);
    }
    // Helper method
    private static Boolean inspectResults(Messaging.SendEmailResult[] results) {
        Boolean sendResult = true;
        // sendEmail returns an array of result objects.
        // Iterate through the list to inspect results.
        for (Messaging.SendEmailResult res : results) {
            if (res.isSuccess()) {
                System.debug('Email sent successfully');
            }
            else {
                sendResult = false;
                System.debug('The following errors occurred: ' + res.getErrors());
            }
        }
        return sendResult;
    }
}
```

**`scripts/apex/execute_anonymous.apex`**

```java
EmailManagerApex.sendMail(UserInfo.getUserEmail(), 'Cloud Computing Lab - Apex Mailing Service', 'Hello! This email was sent from Salesforce using the EmailManagerApex class written in Apex.');
```
## Result
The Apex mailing service sent the email successfully (`Email sent successfully` in the debug log).
