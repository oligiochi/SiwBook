package it.uniroma3.siwbooks.controller.validator;

import it.uniroma3.siwbooks.models.Books;
import it.uniroma3.siwbooks.service.BookService;
import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookValidator implements Validator {

    @Autowired
    private BookService bookService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Books.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Books book = (Books) target;

        if(book.getTitle() != null && book.getReleaseDate() != null) {
            if(bookService.bookExistsByNameAndPublicationYear(book.getTitle(), book.getReleaseDate())){
                errors.reject("Book.duplicate");
            }
        }
    }
}
