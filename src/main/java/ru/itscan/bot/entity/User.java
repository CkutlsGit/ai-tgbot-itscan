package ru.itscan.bot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    private Long id;
    private Integer countRequest;

    @Override
    public String toString() {
        return "Профиль пользователя - " + id + " \n Количество запросов: " + countRequest;
    }
}
