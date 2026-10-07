# Experiment 4 - Simple application using Apex (Salesforce)

## Aim
To develop a simple custom application using the Apex programming language on the Salesforce cloud platform.

## Procedure
1. Log in to the Salesforce Developer org.
2. Open the **Developer Console** from the Setup (gear) menu.
3. Create a new Apex class: **File > New > Apex Class** (`HelloWorldApex`).
4. Write the `sayHello()` method that prints `WELCOME TO APEX PROGRAMMING` to the debug log and **File > Save**.
5. Open **Debug > Open Execute Anonymous Window**, enter `HelloWorldApex.sayHello();`, tick **Open Log** and click **Execute**.
6. In the log, tick **Debug Only** to view the output.

## Source code

**`src/classes/HelloWorldApex.cls`**

```java
public class HelloWorldApex {
    public static void sayHello() {
        System.debug('WELCOME TO APEX PROGRAMMING');
    }
}
```

**`scripts/apex/execute_anonymous.apex`**

```java
HelloWorldApex.sayHello();
```
## Result
The Apex class executed successfully and the message `WELCOME TO APEX PROGRAMMING` was displayed in the debug log.
