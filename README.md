##### Installation

##### Requirement
Java 17 or later
Maven 3.3.2 or later

Configure the token as enviornment variable at runtime as illustrated below
WHAPI_TOKEN=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"

Start ngrok to listen to the requests that are being sent to the channel. The default port is 8080 
ngrok http 8080

## Build the code 
mvn clean package 
For MAC/Linux set the variable as shown 
export WHAPI_TOKEN=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
For Windows
SET WHAPI_TOKEN=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
Run the code through the IDE or execute the jar as shown below. 
java -jar ./whatsapp-bot-1.0.jar

The Token can also be passed during the jar execution as shown below
java -Dwhapi.token=H36AASGagp9CE3u7cFBB2GodKKYVa28J -jar target/whatsapp-bot-1.0.jar





 
