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
    /**使用Lambda先传入一个Consumer函数式,函数式会给一个bookmMapper进行消费，
    *这样可以直接进行消费
    */
    public static void doSqlWork(Consumer<BookMapper> consumer){
        try(SqlSession sqlSession = factory.openSession(true)){
            BookMapper bookMapper = sqlSession.getMapper(BookMapper.class);
            consumer.accept(bookMapper);
        }
    }
}
