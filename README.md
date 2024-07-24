# Задание #1

## Запуск

Использовалась 22 версия JDK.

1. Установить зависимости приложения

2. Собрать .jar файл с приложением

```sh
mvn package
```

3. .jar файл находится в папке target с именем bundler-1.0-SNAPSHOT-jar-with-dependencies.jar. Для запуска необходим бинарник java поддерживающий class file version 66.

4. Аргументы коммандной строки:
   3.1. Первый аргумент - путь к папке с файлами для обработки
   3.2. Второй аргумент - путь к файлу для сохранения результата

```sh
java -jar ./target/bundler-1.0-SNAPSHOT-jar-with-dependencies.jar ./examples ./result.txt
```
