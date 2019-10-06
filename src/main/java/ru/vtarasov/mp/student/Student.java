package ru.vtarasov.mp.student;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.persistence.annotations.UuidGenerator;

/**
 * @author vtarasov
 * @since 21.09.2019
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter @Setter
@Entity
@UuidGenerator(name="STUD_ID_GEN")
public class Student {
    @Id
    @GeneratedValue(generator = "STUD_ID_GEN")
    private String id;

    @NotEmpty
    private String name;

    @NotNull
    @Min(16)
    private Integer age;
}
