package com.agsft.customer.Care.model;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
@Entity
@Table(name = "file_detail")
public class FileDetail {
    static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "name")
    String name;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDate createdAt;
    @Column(name = "failure_count")
    Integer failureCount;
    @Column(name = "success_count")
    Integer successCount;
    @Column(name = "status")
    String status;
    @Column(name = "path")
    String path;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,mappedBy = "fileDetail")
    List<FileInput> fileInputs;

}

