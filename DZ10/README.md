### Задание и Выполнение

#### Распараллелить расчёт факториалов из файла
Дан файл содержащий несколько случайных натуральных чисел от 1 до 50.
Необходимо написать многопоточное приложение, которое параллельно рассчитает 
и выведет в консоль факториал для каждого числа из файла.

###### ** Реализация **
Открываем файл с помощью `FileReader`, затем, с помощью `Scanner` разбиваем входной поток на токены,
используя указанный разделитель, трансформируя их в `int`. Добавляем новую задачу вычисления
факториала в `newThreadPerTaskExecutor` (использование такого пула накладывает лишние
затраты на постоянное создание новых потоков) и запускаем.