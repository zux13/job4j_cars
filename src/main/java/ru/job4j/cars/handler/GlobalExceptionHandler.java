package ru.job4j.cars.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNotFound(NoSuchElementException e, Model model) {
        log.warn("Объект не найден: {}", e.getMessage());
        model.addAttribute("message", e.getMessage());
        return "errors/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralError(Exception e, Model model) {
        log.error("Непредвиденная ошибка", e);
        model.addAttribute("message", "Произошла внутренняя ошибка. Пожалуйста, попробуйте позже.");
        return "errors/500";
    }

}
