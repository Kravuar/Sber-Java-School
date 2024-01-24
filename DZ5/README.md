### Задание и Выполнение

1. **Задача 1: Расчет факториала числа**
   Имплементировать следующий интерфейс в классе CalculatorImpl

   ```java
   public interface Calculator {
       /**
       * Расчет факториала числа.
       * @param number 
       */
       int calc(int number);
   }
   ```

   **Выполнение в** **net.kravuar.components.subjects.calculator**.
   В пакете `subjects` содержатся субъекты, над которыми будут применяться компоненты из других заданий.
   Здесь также находится **интерфейс и реализация** калькулятора из **задания 1**, размещенные в подпакете `calculator`.

2. **Задача 2: Вывод всех методов класса**
   Вывести на консоль все методы класса, включая все родительские методы (включая приватные)

   **Выполнение в** **net.kravuar.components.reflection.ReflectionUtils**.
   `printAllMethods`: выводит на консоль все методы класса, включая родительские, даже приватные.

3. **Задача 3: Вывод всех геттеров класса**
   Вывести все геттеры класса

   **Выполнение в** **net.kravuar.components.reflection.ReflectionUtils**.
   `printAllGetters`: выводит на консоль все геттеры класса.

4. **Задача 4: Проверка String констант**
   Проверить что все String константы имеют значение = их имени

   ```java
   public static final String MONDAY = "MONDAY";
   ```

   **Выполнение в** **net.kravuar.components.reflection.ReflectionUtils**.
   `validateStringEnumPattern`: проверяет, что все String константы имеют значение, равное их имени.

5. **Задача 5: Реализация кэширующего прокси**
   Реализуйте кэширующий прокси

   **Выполнение в** **net.kravuar.components.cache**
   Здесь находится аннотация `Cache`, которая помечает методы как кешируемые для прокси, и аннотация `CachedParameter`, которая указывает, какие параметры использовать при кешировании. Тут же находится класс `CachedInvocationHandler`, содержащий логику обработки аннотаций и простую реализацию кеша на Map.

6. **Задача 6: Создание аннотации Metric и PerformanceProxy**
   Создайте свою аннотацию `Metric` и реализуйте proxy-класс `PerformanceProxy`. В случае, если метод аннотирован `Metric`, время выполнения метода должно выводиться на консоль.

   Пример использования:

   ```java
   public interface Calculator {
       /**
       * Расчет факториала числа.
       * @param number 
       */
       @Metric
       int calc(int number);
   }
   ```

   ```java
   Calculator calculator = new PerformanceProxy(new Calculator()));
   System.out.println(calculator.calc(3));
   ```

   Ожидаемый вывод:

   ```
   Время работы метода: ххххх (в наносек)
   6
   ```

   **Выполнение в** **net.kravuar.components.metric**
   В пакете содержится маркерная аннотация `Metric` и статический прокси для интерфейса `Calculator`. При использовании этого прокси, если метод аннотирован `Metric`, время выполнения метода выводится на консоль.

7. **Задача 7: Реализация класса BeanUtils**
   Реализуйте класс `BeanUtils` согласно следующей документации:

   ```java
   public class BeanUtils {
        /**
        * Scans object "from" for all getters. If object "to"
        * contains correspondent setter, it will invoke it
        * to set property value for "to" which equals to the property
        * of "from".
        * <p/>
        * The type in setter should be compatible to the value returned
        * by getter (if not, no invocation performed).
        * Compatible means that parameter type in setter should
        * be the same or be superclass of the return type of the getter.
        * <p/>
        * The method takes care only about public methods.
        *
        * @param to   Object which properties will be set.
        * @param from Object which properties will be used to get values.
        */
        public static void assign(Object to, Object from) {... }
    }
   ```

   **Выполнение в** **net.kravuar.components.beans.BeanUtils**
   `BeanUtils` использует `ReflectionUtils` для нахождения геттеров и сеттеров. Метод `assign` сканирует объект "from" на наличие всех геттеров и, если объект "to" содержит соответствующий сеттер, вызывает его для установки значения свойства "to", равного свойству "from".

#### Демонстрация

Демонстрация работы нетестируемых компонентов находится в `net.kravuar.Main`.
На калькулятор, проверку строковых констант и `BeanUtils` написаны тесты.