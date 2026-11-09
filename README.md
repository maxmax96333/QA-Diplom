# Дипломный проект: автоматизация проверки покупки тура

Проект содержит UI-тесты учебного приложения «Путешествие дня» (aqa-shop.jar). Проверяются покупка тура дебетовой картой и в кредит, ответы банковского симулятора и сохранение результата в базе данных. Для каждого способа покупки предусмотрены отдельные проверки успешной операции, отказа банка и валидации полей. Тесты можно запускать с MySQL или PostgreSQL.

## Документация

- [План автоматизации и тестовые сценарии](docs/Plan.md)

## Инструменты

- Java 11 и Gradle Wrapper;
- JUnit 5, Selenide и Allure;
- Selenium Grid с Chrome в Docker-контейнере;
- MySQL и PostgreSQL;
- банковский симулятор из каталога gate-simulator.

Браузер Chrome устанавливать на компьютер не нужно: тесты подключаются к удалённому браузеру через Selenium Grid по адресу http://localhost:4444. Chrome запускается в контейнере без окна VNC/noVNC.

## Требования

1. Windows 10/11.
2. IntelliJ IDEA Java 11.
3. Docker Desktop с запущенным Docker Engine.

## Запуск автотестов

1. Запустите Docker Desktop

2. IntelliJ IDEA.

3. В терминале IDEA выполните docker compose down, затем docker compose up -d --build.

4. Дождитесь запуска контейнеров: mysql, postgres, gate-simulator.

5. Запустите приложение aqa-shop.jar через конфигурацию JAR Application с параметрами для MySQL или PostgreSQL.
Команда для приложения java -jar ./artifacts/aqa-shop.jar

6. В терминале IDEA выполните ./gradlew clean test для запуска тестов.

7. Для просмотра отчёта Allure выполните ./gradlew allureServe.

8. Для остановки приложения нажмите Stop, для остановки контейнеров выполните docker compose down.