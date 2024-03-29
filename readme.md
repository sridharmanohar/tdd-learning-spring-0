## User Story
As a user of the API, I should be able to see a list of all metro cities and I should be able to propose a new city for being promoted as a metro. And, every other use of this api, should see the list of cities that are already under proposal for being promoted to a metro.

## Use Cases, Tasks and TestCases
a. Ability to see a list of all approved metros.
    a0. show all existing metros when user hits "/metro"
        a0.0. verify that when a user hits "/metro" he gets a status 200.**DONE**
        a0.1. verify that when a user hits "/metro" he gets a json response.**DONE**
        a0.2. verify the count of metros returned when user hits "/metro".**DONE**
        a0.3. verify if you happen to see 'chennai' in the list of metros returned when user hits "/metro"**DONE**
        a0.4. verify service layer returns a list of metros when it calls the necessary repository method.**DONE**
b. Ability to propose a new metro.
c. Ability to check the list of all proposed metros.

## Running a single test class and/or only a certain methods of a test class with mvn test:
+ mvn -Dtest=<test_class_name> test : this will run only the mentioned test class and all of it's test methods.
+ mvn -Dtest=<test_class_name>#<test_method_1>+<test_method_2> : this will run only the 2 methods of that particular test class and nothing else.

## Mandatory Naming convention to be followed while creating test class to be executed by maven's surefire plugin:
+ This is good, has all the details: https://maven.apache.org/surefire/maven-surefire-plugin/examples/inclusion-exclusion.html
+ So, basically, either start the Test class name with Test or end with Test(s).

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

## Changing all bean configurations to be managed by Spring Container by using @Autowired:
+ It was getting really difficult to test through and through just by using constructor injection (i.e. w/o using @Autowired), lot of errors. So, i've switched to using @Autowired annotation.

## java.lang.IllegalArgumentException: Not a managed type: class com.tdd.spring.Metro
+ Was getting this because the Repository requires the Metro model but this Model currently is a dummy and does not have anything in it and not annotated at all.
+ public class Metro {}

## org.hibernate.AnnotationException: No identifier specified for entity: com.tdd.spring.Metro
+ After I annotated the entity/model with @Entity annotation, it then started thowing the above because I haven't specified any @Id annot. on any of the attributes of this class.
+ @Entity
public class Metro {}

## org.springframework.data.mapping.PropertyReferenceException: No property findMetros found for type Metro!
+ Started getting this after I annot. one of the fields in the Metro entity with @Id:
@Entity
public class Metro {
  @Id
  private Integer id;
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }
}
+ And, this error because the Repository is asking for some way to know what and how to fetch findMetros:
@Repository
public interface MetroRepository extends org.springframework.data.repository.Repository<Metro, Integer>{
  public Set<String> findMetros(); 
}
+ So, for now, I'm chaning this to findAll(), which is a default method in JpaRepository, which extends Repository, so it'll know. And, of course, I also have to change this method references in other test classes also to findAll() (from findMetros()), or I could have probably used a native query or a jpa query, I guess.

## nested exception is org.springframework.dao.InvalidDataAccessResourceUsageException: could not extract ResultSet;
+ This, probably because I haven't mentioned any schema details and also the table does not exist in real.
+ So, make sure you mention correct database details, username and pwd in application.properties that is inside src/test/resources.
+ And, now go ahead and create a table in postgres (since that is the db of choice for this application).
+ And, then also create an @Table annot. on the entity and provide the name of the table with which this entity needs to be mapped.

## Fire-up your postgres sql db:
+ Search for pgadmin4 (the client) which has been installed during postgres installation and just click on servers, a password promter pops up, enter the password and you'll get connected.
+ The other way, I guess, is using psql. Search for psql, a cmdline utility opens.

## Table creation in postgres:
+ During my last project (springboot-rest-redis-postgre), I've noticed there is some issue using pgamdin4 (the client for postgres) for table creation etc, some space/blank chars are getting embedded, I guess, so it's better to use psql (the cmd line client for postgres).
+ Search for psql, click on it, a command line utility opens-up. It'll ask for the following, you don't have to etnter all details, if you alreadu find them to be right, so just press enter, otherwise enter the details as desired:
Server [localhost]:      
Database [postgres]: Temp
Port [5432]: 
Username [postgres]: 
Password for user postgres: (I've entered the pwd here, you just can't see it!)
psql.bin (10.7)
+ create table metros (
id integer PRIMARY KEY,
name text not null
);
+ select * from pg_tables where tablename='metros';

## To find description of a table in psql:
+ \d <table_name> 
OR
+ \d+ <table_name>
OR
+ select column_name, is_nullable, data_type, domain_name from information_schema.columns where table_name = 'metros';

## Insert data into table:
+ don't use double-quotes for string values, it doesn't work in postgres.
+ Insert Scripts:
insert into metros (id, name) values (1, 'Hyderabad');
insert into metros (id, name) values (2, 'Chennai');
insert into metros (id, name) values (3, 'Bengaluru');
insert into metros (id, name) values (4, 'Kolkata');
insert into metros (id, name) values (5, 'Mumbai');

## To clear psql screen:
+ \! clear

##  org.hibernate.InstantiationException: No default constructor for entity:  : com.tdd.spring.Metro:
+ Well, this is because requires a default constructor to be physically present.
+ No matter how many other constructors you have in your entity, make sure you also have a default one. A bit silly though. It should assume one already exists, IDK why 
it doesn't!

## To Launch the application:
+ Just run the main class (which is annoatated with @SpringBootApplication) and has a main which calls SpringApplication.run, as a java application.
+ Spring will launch the tomcat instance and you can then access the app from browser and since this is a REST API you can access this from postman as well.
+ go to browser and hit : localhost:8080/metro

## It seems, field injection is criminal:
+ When you use the @Autowired annotation on a field like:
@RestController
public class MetroController {

  @Autowired
  private MetroService metroService;
}
+ You are essentially asking spring to use field injection to inject dependencies into the application.
+ A couple of articles below explains why Field Injection is a bad design decision:
https://www.vojtechruzicka.com/field-dependency-injection-considered-harmful/
https://www.javacodegeeks.com/2019/02/field-setter-constructor-injection.html
+ Even the spring guide (section Using @Autowired) talks primarily about using @Autowired for injecting dependencies using constructors.
+ So, I refactored my test and application code to make use of constructor injection instead of field injection by making changes similar to this:
  private final MetroRepository metroRepository;

  @Autowired
  public MetroService(MetroRepository metroRepository) {
    this.metroRepository = metroRepository;
  }
+ Tested all, working just fine.

## Alter table in postgresql to add a column:
+ I want to add a column by name 'status' to store the status of metro.
+ Query:
alter table metros add status varchar;
+ To verify:
\d metros
+ And, update all existing rows with status = 'confirmed':
update metros set status = 'confirmed';
select * from metros;

## Creating a Sequence for id column of the table:
+ I want to perform save ops on Metro object for which I need id to be autogenerated, so would have to first create a sequence at the db level and the annotate the column at the entity with the appropriate annotation.
+ create sequence metro_id_seq owned by metros.id;

## PostgreSQL - Hibernate Sequence
+ GenerationType.AUTO is not working w/ postgresql and hibernate.    
+ Generation.IDENTITY also not working.  
+ So, use the following:  
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="metro_id_seq")  
   @SequenceGenerator(name="metro_id_seq", sequenceName="metro_id_seq", allocationSize=1)  
+ Ofcourse, you still have to create this sequence in the db for it to work
+ Now, let's create a sequence in postgresql Temp db:
    CREATE SEQUENCE metro_id_seq OWNED BY metros.id;
+ As of now I dont know how to check what sequence is attached with a table's column, I've tried the below query but it doesn't show anything: 
SELECT table_name, column_name, column_default from information_schema.columns where table_name='metros';    
+ But if you want to check all sequences that have been created, use the below query. It still does not tell you with which table/column is this sequence associated with.
select * from information_schema.sequences;

## validating JSON Response from the RestController:
+ Most of the times we get a json response from RestController but to validate it in the test unit it is not straightforward.
+ For instance, if you are getting your MockHttpServletResponse as below in your unit test code:
MockHttpServletResponse:
           Status = 200
    Error message = null
          Headers = [Content-Type:"application/json"]
     Content type = application/json
             Body = [{"id":3,"name":"Bengaluru","status":"confirmed"},{"id":4,"name":"Kolkata","status":"confirmed"},{"id":1,"name":"Hyderabad","status":"confirmed"},{"id":2,"name":"Chennai","status":"confirmed"},{"id":46,"name":"Delhi","status":"proposed"},{"id":5,"name":"Mumbai","status":"confirmed"}]
    Forwarded URL = null
   Redirected URL = null
          Cookies = []

+ And if you want to validate whether a certain element is part of the body, one way is to pull the entire thing as a string and do a string compare or a substring search but the better way is to use Jackson's ObjectMapper API.
+ When you do an andReturn() on your MockMvc instance then you get a MvcResult instance, use this to fetch the response as content string:
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.post("/proposeMetro/{metroName}", proposedMetro))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    String contentString = result.getResponse().getContentAsString();
+ Now, use the above contentstring and supply it to the ObjectMapper along with the class that would match with the elements in json response:
    java.util.List<Metro> metroList = this.objectMapper.readValue(contentString,
        new TypeReference<java.util.List<Metro>>() {
        });
+ That's it, now you have a List of Metro objects, do whatever validation you want to do on this list of objects.
+ This article is a great resource on various things that we can using Jackson's ObjectMapper i.e. - how to serialize Java objects into JSON and deserialize JSON string into Java objects
https://www.baeldung.com/jackson-object-mapper-tutorial

## Testing from REST Client (POSTMAN):
+ Boot up the application by running the main class i.e. class containg the annot. @SpringBootApplication and calling a SpringApplication.run method.
+ Open up your postman and do a GET for http://localhost:8080/metro, you must see the response in json array.
+ Now, let's do a POST:
    http://localhost:8080/proposeMetro/Delhi
    This must return you the list of all existing metros along with this one, but this should have a status of 'proposed' and everything else the status of 'conformed'.
+ Now, if you POST the same metro again, you should get a BAD REQUEST (400) as the status code and the body should contain the already existing metro name and it's status:
    http://localhost:8080/proposeMetro/Delhi    
+ And, if you POST a metro which already exists as a confirmed metro in the db then you should again get a 400 BAD REQUEST as a status code and the body should contain the metro name and it's status:
    http://localhost:8080/proposeMetro/Hyderabad    
----------------------------------------------------------------------------------------------------------------------------
## ROUGH Notes

