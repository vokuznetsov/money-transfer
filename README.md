## Требования
Java 8

## Сборка
Собрать jar файл из исходного кода и запустить его:
```bash
./gradlew clean build -x test && java -jar build/libs/money-transfer-1.0.jar
```

## Запуск
Запустить приложение из jar файла:
```bash
java -jar /path/to/jar/money-transfer-1.0.jar
```

## API
Для удобно оторажения доступных REST endpoints используется _Swagger_.
 
После запуска приложения API можно посмотреть по ссылке - `http://localhost:8080`

Посмотреть `swagger.json` можно по ссылке `http://localhost:8080/swagger.json`

## Описание
### Стек технологий
  * Guice
  * Jetty
  * Jersey
  * Swagger
  * Gradle