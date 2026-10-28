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

## Шаг 1. Запустить контейнеры

Запустите Docker Desktop и дождитесь состояния Engine running. В IntelliJ IDEA откройте окно Services через View → Tool Windows → Services. Если Docker подключён, найдите в нём конфигурацию Docker Compose для файла docker-compose.yml и выполните Run / Up. При первом запуске дождитесь загрузки образов и готовности контейнеров.

Проверьте в окне Services, что запущены четыре сервиса: mysql, postgres, gate-simulator и selenium-chrome. При необходимости откройте логи сервиса. Порты на локальном компьютере: MySQL — 13306, PostgreSQL — 15432, банковский симулятор — 9999, Selenium Grid — 4444.

Если Docker Compose не отображается в Services, установите/включите Docker-плагин IntelliJ IDEA и подключите Docker Desktop в Settings → Build, Execution, Deployment → Docker. Когда запустить Compose из IDE невозможно, используйте встроенный терминал IntelliJ IDEA как запасной вариант (PowerShell): перейдите в корень проекта и выполните команду:

    docker compose up -d --build

Проверить контейнеры можно командой docker compose ps; остановить — docker compose down.

## Шаг 2. Запустить тестируемое приложение из IntelliJ IDEA

Создайте конфигурацию запуска JAR:

1. Откройте Run → Edit Configurations… и нажмите +.
2. Выберите JAR Application.
3. Укажите путь к JAR: $PROJECT_DIR$\artifacts\aqa-shop.jar.
4. В поле Program arguments вставьте параметры для выбранной базы данных из примеров ниже.
5. Выберите JRE на базе Java 11 или новее и сохраните конфигурацию.
6. Нажмите Run. Оставьте приложение запущенным, пока выполняются тесты.

### Приложение с MySQL

    --server.port=8080 --spring.datasource.url=jdbc:mysql://localhost:13306/app --spring.datasource.username=app --spring.datasource.password=pass --spring.payment-gate.url=http://localhost:9999/payment --spring.credit-gate.url=http://localhost:9999/credit

### Приложение с PostgreSQL

Перед переключением остановите работающую конфигурацию приложения кнопкой Stop, затем запустите её с такими Program arguments:

    --server.port=8080 --spring.datasource.url=jdbc:postgresql://localhost:15432/app --spring.datasource.username=app --spring.datasource.password=pass --spring.payment-gate.url=http://localhost:9999/payment --spring.credit-gate.url=http://localhost:9999/credit

Убедитесь, что приложение открывается по адресу http://localhost:8080.

## Шаг 3. Запустить автотесты из IntelliJ IDEA

Тесты запускаются через Gradle, который IDEA использует через Gradle Wrapper:

1. Откройте окно Gradle: View → Tool Windows → Gradle.
2. Найдите проект Diplom → Tasks → verification → test.
3. Для чистого прогона сначала выполните задачу clean из Tasks → build, затем test из Tasks → verification.
4. Убедитесь, что приложение из шага 2 и контейнеры из шага 1 продолжают работать.

Для MySQL дополнительные настройки тестов не нужны: проект использует адрес jdbc:mysql://localhost:13306/app по умолчанию. Чтобы запустить тесты с PostgreSQL, создайте Gradle Run Configuration: выберите Gradle-проект Diplom, укажите задачи clean test, а в Arguments добавьте:

    --info -Ddb.url=jdbc:postgresql://localhost:15432/app

Запустите эту конфигурацию кнопкой Run. Отчёт Gradle появится в build\reports\tests\test\index.html, результаты Allure — в build\allure-results.

Тесты выполняют проверки отдельно для дебетовой формы и формы покупки в кредит. Всего в проекте описано 46 сценариев: четыре проверки результата покупки и 42 проверки форм — по 21 для каждого способа покупки.

## Шаг 4. Открыть Allure-отчёт

В окне Gradle откройте раздел Tasks → other, выберите задачу allureServe и запустите её. После сборки Gradle откроет интерактивный Allure-отчёт в браузере.

## Остановка

1. Остановите приложение кнопкой Stop рядом с его конфигурацией запуска в IntelliJ IDEA.
2. В окне Services IntelliJ IDEA остановите конфигурацию Docker Compose командой Stop / Down.
3. Если остановка Compose из IDE недоступна, выполните в терминале IntelliJ IDEA из корня проекта команду docker compose down.

Контейнеры используют Docker-тома для хранения данных баз. Обычная команда docker compose down контейнеры останавливает, но тома не удаляет.
