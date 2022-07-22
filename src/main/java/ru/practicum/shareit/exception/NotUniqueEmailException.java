package ru.practicum.shareit.exception;

public class NotUniqueEmailException extends Exception {

    public NotUniqueEmailException() {
        super();
    }

    public NotUniqueEmailException(String message) {
        super(message);
    }

}
