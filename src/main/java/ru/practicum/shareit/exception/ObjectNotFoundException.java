package ru.practicum.shareit.exception;

public class ObjectNotFoundException extends Exception {

    private final String nameObject;
    private final Number idObject;

    public ObjectNotFoundException(String nameObject, Number idObject) {
        this.nameObject = nameObject;
        this.idObject = idObject;
    }

    public ObjectNotFoundException(String nameObject) {
        this.nameObject = nameObject;
        this.idObject = null;
    }

    public String getNameObject() {
        return nameObject;
    }

    public Number getIdObject() {
        return idObject;
    }

}
