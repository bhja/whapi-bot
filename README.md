### Installation

### Requirement
    Java 17 or later
    Maven 3.3.2 or later

Configure the token as enviornment variable at runtime as illustrated below
WHAPI_TOKEN=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

![token](https://github.com/user-attachments/assets/98b624da-bf0e-474a-bce1-81384e103faa)



Start ngrok to listen to the requests that are being sent to the channel. The default port is 8080.
   
    ngrok http 8080

### Build the code 
mvn clean package 
### Configure whapi token
###### For MAC/Linux set the variable as shown 
    export WHAPI_TOKEN=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
###### For Windows
    set WHAPI_TOKEN=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

###### Jar Execution 
If the variable is set with one of the above enviornment option the jar can be executed as given below.

    java -jar ./whatsapp-bot-1.0.jar

It can also be passed during the jar execution as mentioned below.

    java -DWHAPI_TOKEN=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -jar target/whatsapp-bot-1.0.jar


Configure the ngrok https url with the application listener endpoint in this case <b>/webhook</b> as the URL in the whapi dashboard. 
 



 
