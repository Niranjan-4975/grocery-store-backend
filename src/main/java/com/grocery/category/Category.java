package com.grocery.category;

import com.grocery.common.Status;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Data
@Getter
@Setter
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true)
	private String name;
	private String description;
	@Enumerated(EnumType.STRING)
	private Status status;
}
