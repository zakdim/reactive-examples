package guru.springframework.reactiveexamples;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Créé par dmitri le 2022-07-04.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonCommand {

    private String firstName;
    private String lastName;

    public PersonCommand(Person person) {
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
    }

    public String sayMyName(){
        return "My Name is " + firstName + " " + lastName + ".";
    }
}
