package br.ifsp.ctrltcc;

import br.ifsp.ctrltcc.model.Role;
import br.ifsp.ctrltcc.model.User;
import br.ifsp.ctrltcc.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class CtrlTCCApplication {

    public static void main(String[] args) {
        SpringApplication.run(CtrlTCCApplication.class, args);
    }

    @Bean
    CommandLineRunner seed(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new User("admin@ifsp.edu.br", encoder.encode("admin"), "Administrator", Role.ADMIN));
                repo.save(new User("teacher@ifsp.edu.br",  encoder.encode("teacher"),  "Teacher",  Role.TEACHER));
                repo.save(new User("student@aluno.ifsp.edu.br",  encoder.encode("student"),  "Student",  Role.STUDENT));
                System.out.println(">>> Seed: admin@ifsp.edu.br / admin");
            }
        };
    }
}
