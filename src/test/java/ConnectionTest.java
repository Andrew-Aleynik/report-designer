import com.andrewaleynik.reportdesigner.reportdesigner.util.HibernateSessionFactory;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionTest {

    @Test
    void testDirectH2Connection() {
        System.out.println("=== Тестируем прямое подключение к H2 ===");

        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "")) {
            assertNotNull(conn);
            assertFalse(conn.isClosed());
            System.out.println("✓ Прямое подключение к H2 успешно");
        } catch (Exception e) {
            fail("Не удалось подключиться к H2: " + e.getMessage());
        }
    }

    @Test
    void testHibernateConnection() {
        System.out.println("=== Тестируем Hibernate подключение ===");

        try {
            var sessionFactory = HibernateSessionFactory.getSessionFactory();
            assertNotNull(sessionFactory);
            assertFalse(sessionFactory.isClosed());

            var session = sessionFactory.openSession();
            assertNotNull(session);
            assertTrue(session.isOpen());

            session.close();
            System.out.println("✓ Hibernate подключение успешно");

        } catch (Exception e) {
            fail("Не удалось создать SessionFactory: " + e.getMessage());
        } finally {
            HibernateSessionFactory.shutdown();
        }
    }
}