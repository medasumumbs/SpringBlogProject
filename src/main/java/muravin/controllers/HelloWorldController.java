package muravin.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // Указываем Spring, что этот компонент является контроллером
@RequestMapping
public class HelloWorldController {
    @GetMapping("/home") // Принимаем GET-запрос по адресу /home
    @ResponseBody        // Указываем, что возвращаемое значение является ответом
    public String homePage() {
        return "<h1>Hello, world!</h1>"; // Ответ
    }
    public HelloWorldController() {
        super();
    }
}
