package com.example.demo.record;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public record UserEntity(@Id @GeneratedValue Integer id, @Column(length = 255) String login) {
}
