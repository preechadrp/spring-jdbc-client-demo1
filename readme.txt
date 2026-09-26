== spring boot demo jdbc api เบื้องต้น (เวอร์ชัน JdbcClient) ===
- clone มาจาก spring-jdbc-api-demo1 แล้วเปลี่ยน CustOrderRepository จาก JdbcTemplate เป็น JdbcClient
  JdbcClient (Spring 6.1+): jdbcClient.sql(SQL).param("name", value).query(rowMapper).list()/optional()/single()
  - ใช้ชื่อ parameter (:orderId) แทน ?
  - findById คืน Optional แทนการ throw EmptyResultDataAccessException
  - batch update ยังต้องใช้ JdbcTemplate

- relax binding  (การ map ชื่อระหว่าง java กับ config/environment varible/database table field)

  camel case = companyTaxId    เริ่มด้วยตัวพิมพ์เล็กและแบ่งคำด้วยตัวใหญ่  (ใช้ตั้งชื่อตัวแปร)
  kebab case = company-tax-id  ขีดกลาง     (map กับ application.properties/yml)
  snake case = company_tax_id  ใช้ขีดล่าง     (map ชื่อตัวแปรไปยังชื่อฟิลด์ใน table ของ database)
  pascal case = CompanyTaxId   เริ่มด้วยตัวพิมพ์ใหญ่และแบ่งคำด้วยตัวใหญ่  (ใช้ตั้งชื่อ class)
  upper snake case = COMPANY_TAX_ID  ใช้สร้างตัวแปรใน environment varible ใน docker-compose.yml
  
- การเริ่มสร้าง project ด้วย springboot
  1. https://start.spring.io/
  2. eclipse plugins (ติดตั้ง springboot tool) 
  3. spring tools  (https://spring.io/tools#eclipse เป็นของ vmware ที่เอา eclipse+springboot plugins พร้อมใช้งาน)
     
- lombok plugins for eclipse

- Spring Boot Bean Annotations เมื่อใส่ที่ class แล้วระบบจะสร้างเป็น spring bean
  @Component
  @Service
  @Controller 
  @RestController
  @Repository
  ฯลฯ
    
- เริ่ม api
  controller  (get,post)
  service
  validate
  logback
  global exception handler
  component

- การอ่านค่าจาก config หรือ application.properties
  
  แบบใช้ @Value("${property.name}")  // property.name = value
  private String propertyName;  
  
  แบบใช้ @ConfigurationProperties(prefix = "property")  // property.name = value
  
  ลำดับการอ่านค่า config
    -D > Environment Varible > application.properties/.yml > (./config > ./ > src/main/resources)
    ตัวอย่าง
    1. application.properties เช่น abc.companyTaxId=mycomp 
    2. Environment Varible เช่น ABC_COMPANY_TAX_ID=mycomp
    3. -D เช่น java -Dabc.companyTaxId=mycomp -jar myapp.jar
    
- การเชื่อม database ด้วย spring jdbc api
  model  (@ToString, @Accessors(chain = true))
  repository
  dto
  dao
  jsonNode/Jackson
  @Bean/CommandLineRunner
  @Transactional
  
- junit
  @SpringBootTest  

- schedule
  แบบ fixedRate
  แบบ fixedDelay
  แบบ cron
  
- spring security  //TODO
  jwt  (jason web token)
  nimbus library  (รองรับการทำงาน jwt,jws)

== หลักการ == 
HTTP Request  //Client Request
   ↓
Controller,RestController   //HTTP
   ↓
Service (@Transactional)    //business logic ควรใช้ @Transactional(rollbackFor = Exception.class) เพื่อให้ rollbak ทุกกรณี
   ↓
Repository (JdbcTemplate)   //database access
   ↓
DataSource (แต่ในตัวอย่างนี้ใช้ HikariDataSource โดยตรง)
   ↓
HikariCP
   ↓
Database

