package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemStorageTest {

    @Autowired
    private ItemStorage itemRepository;
    @Autowired
    private UserStorage userStorage;

    @Test
    public void searchByNameAndDescription() {
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

        //сохраняем
        userStorage.save(user);
        itemRepository.save(item);

        //получаем
        List<Item> items = itemRepository.searchByNameAndDescription(text, pageable).getContent();

        //тестируем
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo(item.getName());
    }
}
