MODSEN EVENT API

You could choose 2 ways of starting api:

1. Install postgresql database and pgadmin on your PC. Create 'eventdb' database with credentials User: 'root' and
   Password: '123'

Run from console(terminal): 'mvn spring-boot:run' in root of this project. After that you could use swagger-api
documentation for check api endpoints. Swagger-path: http://localhost:8080/swagger-ui.html

For run tests use: 'mvn clean test'

2. Run from console(terminal): 'docker-compose up -d' in root of this project for lunch app in docker containers. You
   also could use swagger-api. Docker is not required to install postgresql and pgadmin and create eventdb!
   After launching containers you could use pgadmin in your favorite browser(http://localhost:5050) with credential
   email: 'artur@modsen.by' and password '123'. For stopping containers run:'docker-compose down'