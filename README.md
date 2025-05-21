# CMPE272Project
ReliefCircle Project Repository

---

## Backend Setup Instructions

### 1. Clone the Repository
```sh
git clone https://github.com/sonalilonkar1/CMPE272Project.git
cd CMPE272Project/backend
```

### 2. Verify Java and Maven Installation
Ensure that Java and Maven are properly installed by checking their versions:
```sh
java -version
mvn -version
```

### 3. Configure Environment Variables
Create a `.env` file in the root of the backend directory and add the following variables:
```env
# Database Configuration
DB_URL=jdbc:mysql://<host>:<port>/<dbname>?useSSL=false&createDatabaseIfNotExist=true
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

# Stripe Configuration
STRIPE_SECRET_KEY=your_stripe_secret

# Google OAuth Configuration
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# AWS SNS/S3 Configuration
sns.topic.arn.charity=your_aws_sns_charity_topic_arn
sns.topic.arn.updates=your_aws_sns_updates_topic_arn
aws.accessKeyId=your_aws_access_key_id
aws.secretKey=your_aws_secret_key
aws.region=us-east-2
aws.s3.bucket-name=your_s3_bucket_name

# JWT Configuration
jwt.secret=your_jwt_secret
jwt.expiration=86400000

```

**Note:**  
- All these variables are referenced in `src/main/resources/application.properties`.  
- Make sure to fill in all required values for your environment.

### 4. Configure application.properties
The backend uses src/main/resources/application.properties.
By default, it reads DB and secret values from environment variables (set in .env).
If you want, you can hardcode values in application.properties for local development.

### 5. Install Dependencies
To install dependencies, use the following command:
```sh
mvn clean install
```

### 6. Build and Run the Project
To build and run the project, use the following command:
```sh
mvn spring-boot:run

or 

java -jar target/backend-*.jar
```

### 7. Database
Make sure your MySQL database is running and accessible.
The app will auto-create tables if spring.jpa.hibernate.ddl-auto=update is set.