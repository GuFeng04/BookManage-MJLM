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
