package book.manage;

import book.manage.entity.Book;
import book.manage.entity.Student;
import book.manage.sql.SQLUtil;
import lombok.extern.java.Log;
import org.apache.ibatis.io.Resources;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.LogManager;

@Log
public class Main {
    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in);){
            LogManager manager = LogManager.getLogManager();
            manager.readConfiguration(Resources.getResourceAsStream("logging.properties"));

            while(true) {
                System.out.println("===========================");
                System.out.println("1.录入学生信息");
                System.out.println("2.录入书籍信息");
                System.out.println("3.添加借阅信息");
                System.out.println("4.显示借阅信息");
                System.out.println("5.显示学生信息");
                System.out.println("6.显示书籍信息");
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
                        addBook(scanner);
                    case 3:
                        addBorrow(scanner);
                        break;
                    case 4:
                        showBorrow();
                        break;
                    case 5:
                        showStudent();
                        break;
                    case 6:
                        showBook();
                        break;
                    default:
                        return;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void showStudent() {
        SQLUtil.doSqlWork(bookMapper ->  {
            bookMapper.getStudentList().forEach(student -> {
                System.out.println(student.getSid() + " -> " + student.getName());
            });
        });
    }
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

    private static void addBorrow(Scanner scanner){
        System.out.println("输入书籍号：");
        String a = scanner.nextLine();
        int bid;
        try{
            bid = Integer.parseInt(a);
        }catch (NumberFormatException e){
            return;
        }
        System.out.println("输入学号：");
        String b = scanner.nextLine();
        int sid;
        try{
            sid = Integer.parseInt(b);
        }catch (NumberFormatException e){
            return;
        }
        //消费型函数接口
        SQLUtil.doSqlWork(bookMapper -> {
            bookMapper.addBorrow(sid,bid);
        });

    }



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
            if(i > 0) {
                System.out.println("书籍信息录入成功");
                log.info("新添加了一条书籍信息。" + book);
            }
            else System.out.println("书籍信息录入失败，请重试");
        } );
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
            if(i > 0) {
                System.out.println("学生信息录入成功");
                log.info("新添加了一条学生信息。" + student);
            }
            else System.out.println("学生信息录入失败，请重试");
        } );
    }
}
