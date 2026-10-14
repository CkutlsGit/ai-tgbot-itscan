package ru.itscan.bot.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.itscan.bot.enums.UserState;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {


    public User(Long id, Integer countRequest) {
        this.id = id;
        this.countRequest = countRequest;
        this.userState = UserState.IDLE;
    }

    @Id
    private Long id;
    private Integer countRequest;

    @Enumerated(value = EnumType.STRING)
    private UserState userState;

    @Override
    public String toString() {
        return "Профиль пользователя - " + id + " \n Количество запросов: " + countRequest;
    }
}
