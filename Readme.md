# Inventory Management System

A Spring Boot-based inventory management system that provides REST APIs for managing SKUs (Stock Keeping Units) and their inventory levels.

## Prerequisites

- Java 21
- Maven
- MySQL Database
- Google Cloud SDK (for Cloud SQL connectivity)
- Docker (Optional, to push the code image to GCP and hoist it publicly)

## Setup

1. Clone the repository
2. Configure your database connection in `src/main/resources/application.properties`
3. Ensure you have the Google Cloud credentials file (`credentials.json`) in your project root
4. Build the project


## To run the application locally

1. mvn clean install
2. mvn spring-boot:run(Port can be configured in the application.properties file)


## To push the code image to GCP and hoist it publicly

1. mvn clean install(This will create a jar file in the target folder)
2. docker buildx build --platform linux/amd64 -t gcr.io/smart-impact-451102-s0/inventory-app .(This will build the image)
3. docker push gcr.io/smart-impact-451102-s0/inventory-app(This will push the image to GCP)
4. gcloud run deploy inventory-app \
    --image gcr.io/smart-impact-451102-s0/inventory-app \
    --platform managed \
    --region us-central1 \
    --allow-unauthenticated (This will deploy the image to GCP)
5. gcloud run services describe inventory-app --platform managed --region us-central1 | grep URL(This will get the public URL of the deployed service)




## APIs

The Apis are publicly exposed by hoisting in GCP as well.

The apis are as follows:

1. GET /api/inventory/skus/{name}

CURL Command for Public API:

curl --location 'https://inventory-app-325638869837.us-central1.run.app/api/inventory/skus/laptop' \
--header 'Accept: application/json'

This is a GET endpoint that searches for SKUs (Stock Keeping Units) by their name.

### Parameters
name (Path Variable): The search term to find SKUs by their name

### Response
Returns a ResponseEntity<List<SKU>> containing:
HTTP Status: 200 OK (if successful)
Body: List of SKU objects that match the search criteria

Example Usage

The above CURL command will return all SKUs where the name starts with "laptop", such as:

[
  {
    "skuId": 1,
    "skuName": "laptop-dell-xps",
    "availableQuantity": 10,
    "totalQuantity": 15
  },
  {
    "skuName": "laptop-hp-pavilion",
    "skuId": 2,
    "availableQuantity": 5,
    "totalQuantity": 8
  }
]



2. POST /api/inventory/addStock

CURL Command for Public API:

curl --location 'https://inventory-app-325638869837.us-central1.run.app/api/inventory/addStock?skuId=1&quantity=10' \
--header 'Accept: application/json'

This is a POST endpoint that adds stock to a specific SKU.

### Parameters
skuId (Query Parameter): The ID of the SKU to add stock to
quantity (Query Parameter): The quantity of stock to add

### Response
Returns a ResponseEntity<String> containing:
HTTP Status: 200 OK (if successful)
Body: "Stock added successfully"    

Example Usage

/api/inventory/addStock
This above CURL command will add 10 units of stock to the SKU with ID 1.



3. POST /api/inventory/allocate

CURL Command for Public API:

curl --location 'https://inventory-app-325638869837.us-central1.run.app/api/inventory/allocate?skuId=1&orderNumber=123456&quantity=5' \
--header 'Accept: application/json'

This is a POST endpoint that allocates stock to an order.

### Parameters
skuId (Query Parameter): The ID of the SKU to allocate stock to
orderNumber (Query Parameter): The order number to allocate stock to
quantity (Query Parameter): The quantity of stock to allocate

### Response
Returns a ResponseEntity<String> containing:
HTTP Status: 200 OK (if successful)
Body: "Stock allocated successfully"

Example Usage

This above CURL command will allocate 5 units of stock to the order with number 123456 for the SKU with ID 1.


4. GET /api/inventory/audit

CURL Command for Public API:

curl --location 'https://inventory-app-325638869837.us-central1.run.app/api/inventory/audit' \
--header 'Accept: application/json'

This is a GET endpoint that audits the inventory.

### Response
Returns a ResponseEntity<String> containing:
HTTP Status: 200 OK (if successful)
Body: "Inventory is in sync"

Example Usage

/api/inventory/audit
This above CURL command will audit the inventory and return a message indicating whether it is in sync or not.


## Sample Data Present in the Database

1. SKUs:

sku_id, sku_name, available_quantity, total_quantity
4, LAPTOP-DELL-XPS13, 0, 10
5, PHONE-IPHONE-14, 0, 0
6, TABLET-IPAD-PRO, 0, 0
7, WATCH-APPLE-S8, 0, 0
8, HEADPHONE-SONY-WH1000XM4, 0, 0
9, LAPTOP-DELL-INSPIRON15, 0, 0
10, LAPTOP-HP-PAVILION14, 0, 0
11, LAPTOP-LENOVO-THINKPAD, 0, 0
12, PHONE-SAMSUNG-S23, 0, 0
13, PHONE-GOOGLE-PIXEL7, 0, 0
14, PHONE-IPHONE-15, 0, 0
15, TABLET-SAMSUNG-TABS8, 0, 0
16, TABLET-LENOVO-P11, 0, 0
17, WATCH-SAMSUNG-GALAXY5, 0, 0
18, WATCH-GARMIN-FENIX7, 0, 0

2. Orders:

order_number, quantity, sku_id
1, 5, 4
2, 5, 4



