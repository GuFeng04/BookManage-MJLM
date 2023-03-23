package book.manage.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实体类Book
 */
@Data
@NoArgsConstructor
public class Book {
    int bid;
    String title;
    String desc;
    Double price;

    public Book(String title, String desc, Double price) {
        this.title = title;
        this.desc = desc;
        this.price = price;
    }
}
