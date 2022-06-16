package com.ski.tournament.service;

import com.ski.tournament.config.UserRole;
import com.ski.tournament.model.AppUser;
import com.ski.tournament.model.Person;
import com.ski.tournament.model.Unit;
import com.ski.tournament.repository.PersonRepository;
import com.vaadin.flow.component.dialog.Dialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.artur.helpers.CrudService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

@Service
public class PersonService extends CrudService<Person, Integer> implements UserDetailsService {

    private final PersonRepository personRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Autowired
    public PersonService(PersonRepository personRepository, JavaMailSender mailSender) {
        this.personRepository = personRepository;
        this.mailSender = mailSender;
    }

    public List<Person> getPersons() {
        List<Person> personList = personRepository.findAll();
        return personList;
    }

    @Transactional
    public HashMap<String,Object> editAccount(HashMap<String,Object> data, Person currentUser) {
        if(data.containsKey("USERNAME")) currentUser.setUsername((String) data.get("USERNAME"));
        if(data.containsKey("NEWPASSWORD")) currentUser.setPassword(encoder.encode((String) data.get("NEWPASSWORD")));
        if(data.containsKey("GENDER")) currentUser.setGender((String) data.get("GENDER"));
        if(data.containsKey("FIRSTNAME")) currentUser.setFirstName((String) data.get("FIRSTNAME"));
        if(data.containsKey("LASTNAME")) currentUser.setLastName((String) data.get("LASTNAME"));
        if(data.containsKey("UNIT")) currentUser.setUnit((Unit) data.get("UNIT"));
        if(data.containsKey("BIRTHDATE")) currentUser.setDateOfBirth((LocalDate) data.get("BIRTHDATE"));
        if(data.containsKey("PHONE")) currentUser.setPhone((String) data.get("PHONE"));

        Person editedPerson = personRepository.save(currentUser);
        HashMap<String,Object> result = new HashMap<>();
        result.put("OK",editedPerson==null ? false : true);
        result.put("DETAILS", editedPerson==null ? "Wystąpił błąd. Nie udało ci się zmodyfikować danych konta" :
                "Udało ci się zmodyfikować dane konta. Po zamknięciu tego konta zostaniesz wylogowany");
        return result;
    }

    public class AuthException extends Exception {

    }
    @Override
    protected JpaRepository<Person, Integer> getRepository() {
        return personRepository;
    }

    @Transactional(readOnly=true)
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        Person person = personRepository.findByUsername(username);
        if (person == null) {
            throw new UsernameNotFoundException("Nie znaleziono użytkownika o nazwie '" + username + "'");
        }
        return new AppUser(person);
    }

    public static Person getCurrentLoggedUser()  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) return ((AppUser) authentication.getPrincipal()).getPerson();
        return null;
    }

    public static String getCurrentLoggedUseFirstNameAndLastName()  {
        Person currentUser = getCurrentLoggedUser();
        return currentUser != null ? currentUser.getFirstName() + " " + currentUser.getLastName() : "Unknown User";
    }

    public void register(String username, String password1, String gender,Unit unit, String firstName, String lastName, LocalDate birthDate, String phone) {
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setUsername(username);
        person.setPassword(encoder.encode(password1));
        person.setGender(gender);
        person.setUnit(unit);
        person.setDateOfBirth(birthDate);
        person.setPhone(phone);
        person.setAuthorities(new TreeSet<UserRole>(Arrays.asList(UserRole.ROLE_User)));
        person.generateActivationCode();
        this.getRepository().save(person);
        String text = "http://localhost:8080/activate?code=" + person.getActivationCode();

        //Tymczasowo wyłączono aktywację poprzez email.
        // Jeżeli chcesz ją włączyć, odkomentuj poniższy kod i zmodyfikuj implementację lub stwórz własną, oraz usuń pojawianie się dialogu z kodem aktywacyjnym

//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("noreply@example.com");
//        message.setSubject("Confirmation email");
//        message.setText(text);
//        message.setTo(username);
//        mailSender.send(message);

        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Aktywacja");
        dialog.add(text);
        dialog.open();

    }

    public boolean checkIfExistsPersonByUsername(String usernamen){
        Integer result = personRepository.checkIfExistsPersonByUsername(usernamen);
        if(result!=0) return true;
        return false;
    }

    @Transactional
    public void activate(String activationCode) throws AuthException {
        Person person = personRepository.findByActivationCode(activationCode);
        if (person != null) {
            person.setEnabled(true);

        } else {
            throw new AuthException();
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void get() {


        Person admin = personRepository.findByUsername("PiotrKowalski@pk.edu.pl");
        Person staff = personRepository.findByUsername("TomaszWisniewski@pk.edu.pl");
        Person user = personRepository.findByUsername("WojciechSzymanski@pk.edu.pl");

        admin.setAuthorities(new TreeSet<UserRole>(Arrays.asList(UserRole.ROLE_Admin,UserRole.ROLE_Staff,UserRole.ROLE_User)));
        admin.setPassword(encoder.encode(admin.getPassword()));
        staff.setAuthorities(new TreeSet<UserRole>(Arrays.asList(UserRole.ROLE_Staff,UserRole.ROLE_User)));
        staff.setPassword(encoder.encode(staff.getPassword()));
        user.setAuthorities(new TreeSet<UserRole>(Arrays.asList(UserRole.ROLE_User)));
        user.setPassword(encoder.encode(user.getPassword()));

    }


}
