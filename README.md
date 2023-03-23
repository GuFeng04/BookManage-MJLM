# **图书管理系统（基于Mybatis+JUL+Lombook+Maven）**

## 项目需求

- 在线录入学生信息和书籍信息
- 查询书籍信息列表
- 查询学生信息列表
- 查询借阅信息列表
- 完整的日志系统

## 开发环境

IDEA+Navicat

语言：java

数据库：Mysql

## 一、创建数据库

打开Navicat，连接本地mysql，创建数据库book_manage，修改字符集utf8，否则不支持中文。

![image-20230322185712256](C:\Users\古峰\AppData\Roaming\Typora\typora-user-images\image-20230322185712256.png)

新建三张表student，book和Borrow：

student表：

![image-20230322190104253](C:\Users\古峰\AppData\Roaming\Typora\typora-user-images\image-20230322190104253.png)

book表：

![image-20230322205351082](C:\Users\古峰\AppData\Roaming\Typora\typora-user-images\image-20230322205351082.png)

borrow表：

![image-20230322190128571](C:\Users\古峰\AppData\Roaming\Typora\typora-user-images\image-20230322190128571.png)

添加borrow表的外键：

![image-20230322190145672](C:\Users\古峰\AppData\Roaming\Typora\typora-user-images\image-20230322190145672.png)

添加borrow表的索引：

![image-20230322190204745](C:\Users\古峰\AppData\Roaming\Typora\typora-user-images\image-20230322190204745.png)

添加book表的触发器：

```sql
DELETE FROM borrow WHERE bid = old.bid
```

![image-20230322190229030](C:\Users\古峰\AppData\Roaming\Typora\typora-user-images\image-20230322190229030.png)

添加student表的触发器：

![image-20230322190326098](C:\Users\古峰\AppData\Roaming\Typora\typora-user-images\image-20230322190326098.png)

```sql
DELETE FROM borrow WHERE sid = old.sid
```

## 二、创建项目

使用IDEA，利用Maven创建项目：

![image-20230322190522604](C:\Users\古峰\AppData\Roaming\Typora\typora-user-images\image-20230322190522604.png)

连接数据库，并测试连接：

<img src="C:\Users\古峰\AppData\Roaming\Typora\typora-user-images\image-20230322190622295.png" alt="image-20230322190622295"  />

打开pom.xml，将<maven.compiler.source>和<maven.compiler.target>修改版本为8，使用jdk8进行编译，以免不必要的问题，并添加依赖：

```xml
<dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.22</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.8.1</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.27</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.7</version>
        </dependency>

    </dependencies>
```

如果无法添加，可以考虑换将Maven换成阿里源，具体方式谷歌。

在book.manage包中，创建主类Main.java。

### 配置mybatis

在resources中新建Mybatits_config.xml文件，配置mybatis，使用jdbc连接mysql，url为上面连接数据库的url，账号密码为数据库的账号密码。有多个数据库的情况下，要在url后面加上数据库的名字。否则会报错：SQLException: No database selected 

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/book_manage"/>
                <property name="username" value="root"/>
                <property name="password" value="123456"/>
            </dataSource>
        </environment>
    </environments>
</configuration>
```

在book.manage包下新建Mapper包，并新建BookMapper接口，在Mybatis_config.xml中添加Mappers。添加位置为environment后面。

```xml
<mappers>
     <mapper class="book.manage.mapper.BookMapper"/>
</mappers>
```

### 测试连接

定义实体类Student：

在book.manage下新建entity包，并新建Student类和book类。

```java
package book.manage.entity;

import lombok.Data;

@Data
public class Student {
    int sid;
    final String name;
    final String sex;
    final int grade;

}
```

```java
package book.manage.entity;

import lombok.Data;

@Data
public class Book {
    int bid;
    final String title;
    final String desc;
    final Double price;
}

```

在BookMapper中加入一个添加学生信息的功能：

```java
package book.manage.mapper;

import book.manage.entity.Student;
import org.apache.ibatis.annotations.Insert;

public interface BookMapper {

    @Insert("insert into student(name, sex, grade) values(#{name}, #{sex}, #{grade}) ")
    int addStudent(Student student);

}
```

在Main中，

```java
package book.manage;

import book.manage.entity.Student;
import book.manage.mapper.BookMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("Mybatits_config.xml"));
        try(SqlSession sqlSession = factory.openSession(true)){
            BookMapper bookMapper = sqlSession.getMapper(BookMapper.class);
            System.out.printf("", bookMapper.addStudent(new Student("小明", "男", 2019)));
        }
    }
}
```

运行测试，打开Navicat，可以发现数据被成功的添加。测试完成，在Navicat中删除这条数据。

编写book，在BookMapper中添加：

desc是个关键字，要加上``

```java
@Insert("insert into book(title, `desc`, price) values(#{title}, #{desc}, #{price}) ")
int addBook(Book book);
```



编写一个工具类：

在book.manage包下新建一个sql包，并新建SQLUtil工具类：

```java
package book.manage.sql;

import book.manage.mapper.BookMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.util.function.Consumer;

public class SQLUtil {

    private SQLUtil(){}
    private static  SqlSessionFactory factory;

    static {
        try {
            factory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("Mybatits_config.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void doSqlWork(Consumer<BookMapper> consumer){
        try(SqlSession sqlSession = factory.openSession(true)){
            BookMapper bookMapper = sqlSession.getMapper(BookMapper.class);
            consumer.accept(bookMapper);
        }
    }
}

```

### 第一个需求

在线录入学生信息和书籍信息

编写Main

```java
package book.manage;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        try(Scanner scanner = new Scanner(System.in);){
            while(true) {
                System.out.println("===========================");
                System.out.println("1.录入学生信息");
                System.out.println("2.录入书籍信息");
                System.out.println("输入你想要执行的操作(输入其它任意数字退出)");
                System.out.println("===========================");
                int input = scanner.nextInt();
                switch (input){
                    case 1:
                        break;
                    case 2:
                        break;
                    default:
                        return;
                }
            }
        }
    }
}
```

测试发现，输入字母就会异常，所以加入一段判断:

```java
int input;
try{
    input = scanner.nextInt();
}catch (Exception e){
    return;
}
switch (input){
    case 1:
        break;
    case 2:
        break;
    default:
        return;	
```

这样，运行就没问题了。

#### 录入学生信息

在Main中：

```java
package book.manage;

import book.manage.entity.Student;
import book.manage.sql.SQLUtil;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        try(Scanner scanner = new Scanner(System.in);){
            while(true) {
                System.out.println("===========================");
                System.out.println("1.录入学生信息");
                System.out.println("2.录入书籍信息");
                System.out.println("输入你想要执行的操作(输入其它任意数字退出)");
                System.out.println("===========================");
                int input;
                try{
                    input = scanner.nextInt();
                }catch (Exception e){
                    return;
                }
                scanner.nextLine();
                switch (input){
                    case 1:
                        addStudent(scanner);
                        break;
                    case 2:
                        break;
                    default:
                        return;
                }
            }
        }
    }
    private static  void addStudent(Scanner scanner){
        System.out.println("请输入学生名字：");
        String name = scanner.nextLine();
        System.out.println("请输入学生性别（男/女）");
        String sex = scanner.nextLine();
        System.out.println("请输入学生年级");
        String grade = scanner.nextLine();
        int gradeInt;
        try{
            gradeInt = Integer.parseInt(grade);
        }catch (NumberFormatException e){
            return;
        }
        Student student = new Student(name ,sex ,gradeInt);
        SQLUtil.doSqlWork(bookMapper -> {
            int i =  bookMapper.addStudent(student);
            if(i > 0) System.out.println("学生信息录入成功");
            else System.out.println("学生信息录入失败，请重试");
        } );

    }
}
```

测试一下，发现可以插入。

![image-20230322203931034](C:\Users\古峰\AppData\Roaming\Typora\typora-user-images\image-20230322203931034.png)

![image-20230322203944090](C:\Users\古峰\AppData\Roaming\Typora\typora-user-images\image-20230322203944090.png)

#### 录入书籍信息

```java
private static  void addBook(Scanner scanner){
        System.out.println("请输入书籍标题：");
        String title = scanner.nextLine();
        System.out.println("请输入书籍介绍：");
        String desc = scanner.nextLine();
        System.out.println("请输入书籍价格：");
        String price = scanner.nextLine();
        double priceDouble;
        try{
            priceDouble = Double.parseDouble(price);
        }catch (NumberFormatException e){
            return;
        }
        Book book = new Book(title ,desc ,priceDouble);
        SQLUtil.doSqlWork(bookMapper -> {
            int i =  bookMapper.addBook(book);
            if(i > 0) System.out.println("书籍信息录入成功");
            else System.out.println("书籍信息录入失败，请重试");
        } );
    }
```

然后在case2：中加入

```java
case 2:
    addBook(scanner);
```

运行测试一下，可以插入数据。

![image-20230322205304146](C:\Users\古峰\AppData\Roaming\Typora\typora-user-images\image-20230322205304146.png)

### 完整的日志系统

在resources下新建logging.properties，并进行配置，按住Ctrl点击FileHandler，可以看见支持的配置选项。

```properties
# RootLogger 默认文件打印
handlers= java.util.logging.FileHandler
# RootLogger 的默认的日志级别
.level= INFO

#文件打印目录
java.util.logging.FileHandler.pattern= console.log
#文件格式
java.util.logging.FileHandler.formatter= java.util.logging.SimpleFormatter
#改成追加填写
java.util.logging.FileHandler.append= true
```

在Mybatis_config.xml文件中，关闭mybatis的日志打印，在enviroments前面加上：

```xml
<settings>
    <setting name="logImpl" value="NO_LOGGING"/>
</settings>
```

然后在Main方法上，写上@log注解，并输入日志：

```java
LogManager manager = LogManager.getLogManager();
manager.readConfiguration(Resources.getResourceAsStream("logging.properties"));
```

在addStudent中，在书籍信息如如成功这个if条件中，加上

```java
log.info("新添加了一条学生信息。" + student);
```

book同理，然后测试，发现多了一个console.log文件，可以正常输出日志了。

![image-20230322212153462](C:\Users\古峰\AppData\Roaming\Typora\typora-user-images\image-20230322212153462.png)

### 查询书籍信息列表

创建实体类borrow：

```java
package book.manage.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Borrow {
    int id;
    //映射
    Student student;
    Book book;

    public Borrow( Student student, Book book) {
        this.student = student;
        this.book = book;
    }

}
```

在mapper中，加入borrow的select。

在Mapper中，Result的作用是映射查询结果集到实体类属性。

```java
//Result注解中，column作为参数传递到select中进行查询的参数
//property表示实体类中定义的对象的名称
@Results({
    @Result(column = "id", property = "id", id =true),
    @Result(column = "sid", property = "student", one = @One(select = "getStudentBySid")),
    @Result(column = "bid", property = "book", one = @One(select = "getBookByBid")),
})
@Select("select * from borrow")
List<Borrow> getBorrowList();

@Select("select * from student where sid = #{sid}")
Student getStudentBySid(int  sid);
@Select("select * from book where bid = #{bid}")
Book getBookByBid(int  bid);
```

编写测试类com.test.MainTest:

简单说一下test1，通过SQLUtil封装的SqlSessionFactory,直接生成sqlSession，doSqlWork帮我们做好mapper映射。

```java
package com.test;

import book.manage.sql.SQLUtil;
import org.junit.jupiter.api.Test;

public class MainTest {

    @Test
    public void test1(){
        SQLUtil.doSqlWork(bookMapper -> {
            bookMapper.getBorrowList().forEach(System.out::println);
        });
    }
}
```

测试通过：

![image-20230323192737200](https://gitee.com/gufengspace/github_img/raw/master/img/image-20230323192737200.png)

在main中编写showBorrow:

```java
private  static  void showBorrow(){
    SQLUtil.doSqlWork(bookMapper ->  {
        bookMapper.getBorrowList().forEach(borrow -> {
            System.out.println(borrow.getStudent().getName() + " -> " + borrow.getBook().getTitle());
        });
    });
}
```

以上便完成了查询借阅列表。

### 查询学生和书籍

在mapper中编写getStudentList和getBookList

```java
@Select("select * from student")
List<Student> getStudentList();
@Select("select * from book")
List<Book> getBookList();
```

编写测试类：

```java
@Test
public void test2(){
    SQLUtil.doSqlWork(bookMapper -> {
        bookMapper.getStudentList().forEach(System.out::println);
    });
}

@Test
public void test3(){
    SQLUtil.doSqlWork(bookMapper -> {
        bookMapper.getBookList().forEach(System.out::println);
    });
}
```

可以发现测试用例都正常通过。

最后加上两个方法即可。

```java
private static void showBook() {
    SQLUtil.doSqlWork(bookMapper ->  {
        bookMapper.getBookList().forEach(book -> {
            System.out.println(book.getBid() + " -> " + book.getTitle());
        });
    });
}

private  static  void showBorrow(){
    SQLUtil.doSqlWork(bookMapper ->  {
        bookMapper.getBorrowList().forEach(borrow -> {
            System.out.println(borrow.getStudent().getName() + " -> " + borrow.getBook().getTitle());
        });
    });
}
```

## 打包

在pom配置文件中，加上打包的插件，因为JUnit 5不支持，要加上JUnit 5的。

```xml
<artifactId>maven-assembly-plugin</artifactId>
    <version>3.1.0</version>
    <configuration>
        <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
        <archive>
            <manifest>
                <addClasspath>true</addClasspath>
                <mainClass>book.manage.Main</mainClass>
            </manifest>
        </archive>
    </configuration>
    <executions>
        <execution>
            <id>make-assembly</id>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
        </execution>
    </executions>
</plugin>
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <!-- JUnit 5 requires Surefire version 2.22.0 or higher -->
    <version>2.22.0</version>
</plugin>
```

配置好后，点击右侧的maven中的生命周期中的package打包。

然后便可以运行了。

![image-20230323201134345](https://gitee.com/gufengspace/github_img/raw/master/img/image-20230323201134345.png)