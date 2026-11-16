package com.grocery.product;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grocery.category.Category;
import com.grocery.common.Status;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Data
@Setter
@Getter
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String name;
	@Column(columnDefinition = "TEXT")
	private String description;
	@Column(nullable = false)
	private double price;
	@Column(nullable = false)
	private Integer stock;
	private String imageUrl;
    @Column(nullable = false)
    private boolean isFeatured = false;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;
    @Transient
    private boolean isWishlisted;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Category category;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	@PrePersist
	public void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		status = Status.ACTIVE;
        this.isFeatured = false;
	}
	@PreUpdate
	public void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
