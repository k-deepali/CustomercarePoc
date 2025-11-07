package com.agsft.customer.Care.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
@Entity
@Table(name = "file_input")
public class FileInput {
    static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "phone_number")
    String phoneNumber;
    @Column(name = "status")
    String status;
    @Column(name = "charges")
    Float charge;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDate createdAt;
    @ManyToOne
    @JoinColumn(name = "file_detail_id")
    FileDetail fileDetail;

    public FileInput(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
