package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@DataJpaTest
public class ItemStorageTest {

    @Autowired
    private TestEntityManager tem;

    @Autowired
    private ItemStorage repository;

    @Test
    public void test() {
        //подготовка данных
        User user = new User(); //владелец
        user.setId(1L);
        user.setName("Max");
        user.setEmail("maxiTrue@gmail.ru");
        Item item = new Item(); //вещь
        item.setName("добро");
        item.setDescription("что - то доброе");
        item.setRequest(null);
        item.setAvailable(Boolean.TRUE);
        item.setOwner(user);
        String text = "добро"; //текст поиска
        Pageable pageable = PageRequest.of(0, 5);

        tem.persist(item);

        List<Item> items = repository.searchByNameAndDescription(text, pageable).getContent();
    }
}
