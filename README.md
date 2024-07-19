# Fetch API Assesment

## Vignesh Manikandan

### GitHub repository link:

https://github.com/Vigneshmanikandan98/fetch-api-assessment

### Languages and Technologies used

1. Java
2. Spring Boot
3. Maven
4. Docker

### Steps to run the project server locally using docker.

1. Install Docker Desktop.
2. Download the zip file from the repository and extract.
3. From the folder path open terminal and run this command "docker build -t fetchapi ." to create an image.
4. Once the image has been created run this command to run the image as container "docker run -p 8080:8080 fetchapi", the command runs the container on port 8080.

The app is implemented with basic spring security authentication to the endpoints.
username: admin
password: password

### Steps to test in Postman

1. Make sure the Spring Boot is running and go to postman and create a new workspace for API Testing.
2. Once you have created a workspace click on "new" from the side tab and click "HTTP" to test the http request.
3. Select POST request and paste the URL in the tab http://localhost:8080/receipts/process.
4. In the body tab give the input receipt
   {
   "retailer": "Target",
   "purchaseDate": "2022-01-01",
   "purchaseTime": "13:01",
   "items": [
   {
   "shortDescription": "Mountain Dew 12PK",
   "price": "6.49"
   },{
   "shortDescription": "Emils Cheese Pizza",
   "price": "12.25"
   },{
   "shortDescription": "Knorr Creamy Chicken",
   "price": "1.26"
   },{
   "shortDescription": "Doritos Nacho Cheese",
   "price": "3.35"
   },{
   "shortDescription": " Klarbrunn 12-PK 12 FL OZ ",
   "price": "12.00"
   }
   ],
   "total": "35.35"
   }
5. Go to Authorization tab and select "Basic Auth" and give the above credentials.
6. Now click on send and you will be able to see the server response with ID on the bottom.
7. Now select a new GET request with the URL http://localhost:8080/receipts/{id}/points, give the id from the POST request's response.
8. The response will give the points for that particular receipt.
