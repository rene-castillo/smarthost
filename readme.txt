The Coding Challenge

README

Call bash script run.sh to run the REST server application SmartHost.

Run the tests using Maven, type "mvn test"

To test the REST web service, open this URL in your browser:
http://localhost:8080/booking?premium=2&economy=3&customers=13,14,21,21,100

It should return this JSON object:
{"premiumRooms":2,"economyRooms":3,"totalRevenue":169}