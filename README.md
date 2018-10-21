# spring-oauth-jpa-demo
It is a demo project to configure OAuth2 authorization server with JWT token using Spring Boot 2, JPA, MySQL database and Gradle

### Configuration
1.  To configure this project you can generate you keystore file using following command :
    ```sh
    keytool -genkeypair -alias jwt -keyalg RSA -keypass password -keystore jwt.jks -storepass password
    ```
    ![picture alt](https://github.com/snj07/spring-oauth-jpa-demo/blob/master/images/generate_key.png "Token generate")
    
    **Note:** Windows user you can find keytool in JDK directory.
    It will generate jwt.jks which contains the Public and Private keys.

2.  It is recommended to migrate above generated key to **PKCS12**. Execute the following command to generate PKCS12
    ```sh
    keytool -importkeystore -srckeystore jwt.jks -destkeystore jwt.jks -deststoretype pkcs12
    ```
3.  Generate public key using following command
    ```sh
    keytool -list -rfc --keystore jwt.jks | openssl x509 -inform pem -pubkey
    ```
    You will get public key on console after executing the above command.
4.  Copy from (including) **-----BEGIN PUBLIC KEY-----** to **-----END PUBLIC KEY-----** and save it in file public.key file. 
    It will be used in resource server.
    
    
    
### Import project in IntelliJ IDEA

Import the project in IntelliJ IDEA by using following steps
1.  Start IntelliJ IDEA 
2.  Click on Open(if no project is opened) or go to file -> open
3.  Select build.gradle file in the cloned repository and click on Open as Project
4.  After build completion, Run **Application.java**
5.  Uncomment following code in Application.java to insert admin user for the first time
    ```java
    @Bean
    public CommandLineRunner setupDefaultUser(UserService service) {
        return args -> {
            service.save(new User(1,
                    "admin",
                    "password",
                    Arrays.asList(new Role(1,"ROLE_ADMIN"), new Role(2, "ROLE_EMPLOYEE")),//roles
                    true
            ));
        };
    }
    ```
    
### Test Project
1. You can use Postman to test the app. Enter client(client id) and secret(client secret) for basic authentication.
   ![picture alt](https://github.com/snj07/spring-oauth-jpa-demo/blob/master/images/token1.png "Postman configuration")


2. Enter username and password to generate token. Use **password** instead of **client_credentials** for grant_type parameter to test throught password
![picture alt](https://github.com/snj07/spring-oauth-jpa-demo/blob/master/images/token2.png "Postman configuration")



## License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
