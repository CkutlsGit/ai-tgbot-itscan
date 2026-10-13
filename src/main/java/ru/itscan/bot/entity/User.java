package ru.itscan.bot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    private Long id;
    private Integer countRequest;

    @Override
    public String toString() {
        return "Профиль пользователя - " + id + " \n Количество запросов: " + countRequest;
    }
}
