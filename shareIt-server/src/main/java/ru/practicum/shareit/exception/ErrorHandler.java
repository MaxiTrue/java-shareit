package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ValidException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validException(final Exception e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundException(final ObjectNotFoundException e) {
        if (e.getIdObject() != null && e.getNameObject() != null) {
            return new ErrorResponse(String.format(
                    "Объект %s с  id - (%d) не найден",
                    e.getNameObject(),
                    e.getIdObject()));
        }

        if (e.getIdObject() == null) {
            return new ErrorResponse(String.format("Объект %s не найден", e.getNameObject()));
        }

        return new ErrorResponse(e.getMessage());
    }

}
