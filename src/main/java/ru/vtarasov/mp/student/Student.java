package ru.vtarasov.mp.student;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author vtarasov
 * @since 21.09.2019
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter @Setter
public class Student {
    @Null
    private String id;

    @NotEmpty
    private String name;

    @NotNull
    @Min(16)
    private Integer age;
}
