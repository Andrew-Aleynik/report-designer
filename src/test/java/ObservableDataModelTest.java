import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ObservableDataModel;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ObservableDataModelTest {

    private static final class TestDataModel extends ObservableDataModel {
        void bump() {
            fireChanged();
        }
    }

    @Test
    void onChange_WithRunnable_IsInvokedOnFireChanged() {
        TestDataModel model = new TestDataModel();
        AtomicInteger calls = new AtomicInteger();

        model.onChange(calls::incrementAndGet);
        model.bump();
        model.bump();

        assertThat(calls.get()).isEqualTo(2);
    }

    @Test
    void onChange_WithConsumer_ReceivesSourceModel() {
        TestDataModel model = new TestDataModel();
        AtomicInteger calls = new AtomicInteger();

        model.onChange(source -> {
            assertThat(source).isSameAs(model);
            calls.incrementAndGet();
        });
        model.bump();

        assertThat(calls.get()).isEqualTo(1);
    }

    @Test
    void removeChangeListener_StopsNotifications() {
        TestDataModel model = new TestDataModel();
        AtomicInteger calls = new AtomicInteger();
        java.util.function.Consumer<ObservableDataModel> listener = source -> calls.incrementAndGet();

        model.onChange(listener);
        model.bump();
        model.removeChangeListener(listener);
        model.bump();

        assertThat(calls.get()).isEqualTo(1);
    }
}
