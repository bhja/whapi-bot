### Installation

### Requirement
    Java 17 or later
    Maven 3.3.2 or later

Configure the token as enviornment variable at runtime as illustrated below
WHAPI_TOKEN=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"

Start ngrok to listen to the requests that are being sent to the channel. The default port is 8080 
ngrok http 8080

### Build the code 
mvn clean package 
### Configure whapi token
###### For MAC/Linux set the variable as shown 
    export WHAPI_TOKEN=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
###### For Windows
    set WHAPI_TOKEN=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

To execute the jar as shown below if variable is set as shown above. 
    java -jar ./whatsapp-bot-1.0.jar
The token can also be passed during the jar execution as shown below.

    java -DWHAPI_TOKEN=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -jar target/whatsapp-bot-1.0.jar

Configure the webhook url in the whapi dashboard to listen to the messages and start testing.



 
