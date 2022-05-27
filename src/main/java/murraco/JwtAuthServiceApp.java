package murraco;

import lombok.RequiredArgsConstructor;
import murraco.model.AppUser;
import murraco.model.AppUserRole;
import murraco.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
@RequiredArgsConstructor
public class JwtAuthServiceApp implements CommandLineRunner {

    final UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(JwtAuthServiceApp.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Override
    public void run(String... args) throws Exception {
        AppUser appUser = new AppUser();
        appUser.setAppUserRoles(Arrays.asList(AppUserRole.ROLE_ADMIN));
        appUser.setUsername("admin");
        appUser.setPassword("admin");
        appUser.setInstituteId("admin");
        userService.signup(appUser);
    }
}
