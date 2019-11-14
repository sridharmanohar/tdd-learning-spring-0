## User Story
As a user of the API, I should be able to see a list of all metro cities and I should be able to propose a new city for being promoted as a metro. And, every other use of this api, should see the list of cities that are already under proposal for being promoted to a metro.

## Use Cases, Tasks and TestCases
a. Ability to see a list of all approved metros.
    a0. show all existing metros when user hits "/metro"
        a0.0. verify that when a user hits "/metro" he gets a status 200.**DONE**
        a0.1. verify that when a user hits "/metro" he gets a json response.**DONE**
        a0.2. verify the count of metros returned when user hits "/metro".**DONE**
        a0.3. verify if you happen to see 'chennai' in the list of metros returned when user hits "/metro"
        a0.4. verify service layer returns a list of metros when it calls the necessary repository method.**DONE**
b. Ability to propose a new metro.
c. Ability to check the list of all proposed metros.

## Running a single test class and/or only a certain methods of a test class with mvn test:
+ mvn -Dtest=<test_class_name> test : this will run only the mentioned test class and all of it's test methods.
+ mvn -Dtest=<test_class_name>#<test_method_1>+<test_method_2> : this will run only the 2 methods of that particular test class and nothing else.

## Issue after adding spring-boot-starter-data-jpa and postgresql
+ Was getting the below error while booting the application and also while running the test with mvn test.
+ java.lang.IllegalStateException: Failed to load ApplicationContext
Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaConfiguration': Unsatisfied dependency expressed through constructor parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'dataSource' defined in class path resource [org/springframework/boot/autoconfigure/jdbc/DataSourceConfiguration$Hikari.class]: Bean instantiation via factory method failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [com.zaxxer.hikari.HikariDataSource]: Factory method 'dataSource' threw exception; nested exception is org.springframework.boot.autoconfigure.jdbc.DataSourceProperties$DataSourceBeanCreationException: Failed to determine a suitable driver class
+ **Reason:** Well, I din't create an application.properties file in both src/main/resources and src/test/resources.
+ Created it with following:  
    spring.datasource.url=jdbc:postgresql://localhost:5432/Temp  
    spring.datasource.username=postgres  
    spring.datasource.password=postgre  

## NullPointerException for Unit Test of Service Layer
+ Service layer has a dependency on Repository so I decided to mock the repository instance in test and create the dependency using constructor injection as follows:
  @MockBean
  private MetroRepository metroRepository;

  private MetroService metroService = new MetroService(metroRepository);

+ There were no compile-time issues but when I went into test method and called the service.<method_name>, it started throw nullpointer because Service layer instance was not getting created in time.
+ I was not properly creating the Service Layer instance in the test.
+ Moved the Service layer instance creation into a setUp() w/ @BeforeEach:
  @MockBean
  private MetroRepository metroRepository;
  
  private MetroService metroService;
  
  @BeforeEach
  public void setUp() {
    metroService = new MetroService(metroRepository);
  }
+ And, that's it, this resolved the issue.




