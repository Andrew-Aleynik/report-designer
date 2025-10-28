import com.andrewaleynik.reportdesigner.reportdesigner.util.HibernateSessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseTest {

    @BeforeEach
    void setUp() {
        HibernateSessionFactory.getSessionFactory();
    }

    @AfterEach
    void tearDown() {
        HibernateSessionFactory.shutdown();
    }

    protected void assertPersisted(Object entity) {
        assertThat(entity).isNotNull();
        try {
            var idField = entity.getClass().getMethod("getId");
            Object id = idField.invoke(entity);
            assertThat(id).isNotNull();
        } catch (Exception e) {
        }
    }
}
