# MBank scraper
Simple app fetching bank accounts data (number, balance) and linked saving accounts.  

## Building and running the app

### Using script

To build and run the app execute `buildAndRun.sh` and follow instructions.
You may change port of the wire mock server (default 8666) adding port as an argument of the script:\
`./buildAndRun.sh <port>`


### Using commands
Build with command:  
`mvn clean install`

You may change default port of the wire mock server as well using below command:\
`mvn clean install -DMockServerPort=<port>`

Run with below command and follow instructions.  
`java -jar path-to-jar-file`
