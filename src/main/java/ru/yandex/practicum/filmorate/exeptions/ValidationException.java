package ru.yandex.practicum.filmorate.exeptions;

import org.slf4j.Logger;

public class ValidationException extends RuntimeException{

    public ValidationException(Logger log, final String message) {
        super(message);
        log.error(message);
    }

}
