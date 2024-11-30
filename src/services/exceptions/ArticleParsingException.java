package services.exceptions;

public class ArticleParsingException extends RuntimeException {
    public ArticleParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
