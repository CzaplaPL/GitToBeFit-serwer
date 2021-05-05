package pl.umk.mat.git2befit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import pl.umk.mat.git2befit.user.model.entity.User;
import pl.umk.mat.git2befit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@SpringBootTest(classes = Git2befitApplication.class)
public class SpringBootJPAIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testowanko() {
        userRepository.save(new User("kacper@kacper.pl", "paswordzik"));
        List<User> all = userRepository.findAll();
        System.out.println(all);

        assertThat(all.size(), equalTo(1));
    }

    @Test
    public void testowanko2() {
        userRepository.save(new User("kacper@kacper.pl", "paswordzik"));
        List<User> all = userRepository.findAll();
        System.out.println(all);
        assertThat(all.size(), equalTo(1));
    }
}