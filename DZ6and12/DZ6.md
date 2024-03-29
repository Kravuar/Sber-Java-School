### Задание и Выполнение

#### Вопросы для самоконтроля:

1. **Что означает аннотация @Before?**
    Аннотированный метод будет вызван перед каждым тестом, в JUnit 5 - `@BeforeEach`    

2. **Как в тесте проверить, что метод должен бросить исключение?**
    `@Test(expected=SomeException.class)`, в JUnit 5 - `assertThrows`

3. **Что такое mock? Spy?**
    mock - заглушка, имитирующая реальные объекты, позволяющая отслеживать взаимодействие с ними, определять их поведение.
    spy - обёртка над реальными объектами, позволяющаяя отслеживать взаимодействие с ними, частично переопределять поведение объектов.

4. **Для чего применяется статический метод Mockito.verify?**
    для проверки, был ли вызван определенный метод на mock/spy с определенными аргументами, кол-во вызовов.

#### Реализация своего итератора массива объектов:

Реализуйте свой итератор массива объектов. Напишите тесты для проверки его работоспособности. Оформите сборку кода через maven.

###### **Выполнение в модуле iterator**

#### Проектирование соц. сети:

Спроектировать дизайн соц. сети. В данном задании интересует разбитие приложения на модули, взаимодействие интерфейсов, а не реализация конкретных классов.

Соц. сети обычно предлагают большой набор сервисов: поиск/добавление друзей, просмотр профилей,  загрузка и просмотр фото, общение через чат или стены, рекомендации, подарки и куча других сервисов. Все эти сервисы должны находится в отдельных модулях и иметь связи между собой.
Ваша задача создать maven проект, создать модули для каждого сервиса, прописать зависимости одних модулей от других. В каждом модуле должны быть интерфейсы и доменная модель данного сервиса + в некоторых модулях нужна примерная реализация интерфейсов, где показано как используются интерфейсы других модулей.
Написать юнит тесты к классам из данного задания (с помощью junit5 + mockito)

###### **Выполнение в модуле social**
Попробовал гексагональную архитектуру (ports & adapters).
Использование портов позволило полностью отделить бизнес логику от инфраструктуры,
а также избавиться от зависимостей от других модулей.

**В доменном подмодуле** каждого модуля содержатся пакеты:
* model - доменные объекты модуля
* ports:
  * in - use-case'ы данного модуля 
  * out - SPI данного модуля
* а также фасады реализующие use-case'ы модуля

Также в доменных подмодулях содержатся тесты бизнес-логики.

**В инфраструктурном подмодуле** каждого модуля содержится набросок
определения адаптеров для портов соответствующего доменного модуля (межмодульные зависимости) с использованием контейнера инверсии управления
(самого контейнера нет, т.к. это только набросок).

Реализованно 3 модуля: 
* accounts - содержит use-case аутентификации и регистрации
* friends - содержит use-case'ы управление списком друзей (отправить запрос, принять, отклонить, отменить, заблокировать), а также use-case'ы получения списка друзей, отправленных запросов (от кого, к кому)
* messages - содержит use-case'ы отправки, редактирования сообщений, получения списка сообщений (по отправителю и получателю).