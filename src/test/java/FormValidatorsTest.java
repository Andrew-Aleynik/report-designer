import com.andrewaleynik.reportdesigner.reportdesigner.util.FormValidators;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FormValidatorsTest {

    @Test
    void isBlankOrTooShort_WithNull_ReturnsTrue() {
        assertThat(FormValidators.isBlankOrTooShort(null, 3)).isTrue();
    }

    @Test
    void isBlankOrTooShort_WithShortValue_ReturnsTrue() {
        assertThat(FormValidators.isBlankOrTooShort("ab", 3)).isTrue();
    }

    @Test
    void isBlankOrTooShort_WithValidValue_ReturnsFalse() {
        assertThat(FormValidators.isBlankOrTooShort("abc", 3)).isFalse();
    }

    @Test
    void isOptionalNonNegativeLong_WithEmptyValue_ReturnsTrue() {
        assertThat(FormValidators.isOptionalNonNegativeLong("")).isTrue();
    }

    @Test
    void isOptionalNonNegativeLong_WithNegativeValue_ReturnsFalse() {
        assertThat(FormValidators.isOptionalNonNegativeLong("-1")).isFalse();
    }

    @Test
    void isOptionalNonNegativeLong_WithInvalidValue_ReturnsFalse() {
        assertThat(FormValidators.isOptionalNonNegativeLong("abc")).isFalse();
    }

    @Test
    void isOptionalNonNegativeBigDecimal_WithValidValue_ReturnsTrue() {
        assertThat(FormValidators.isOptionalNonNegativeBigDecimal("10.5")).isTrue();
    }

    @Test
    void isOptionalNonNegativeBigDecimal_WithNegativeValue_ReturnsFalse() {
        assertThat(FormValidators.isOptionalNonNegativeBigDecimal("-0.01")).isFalse();
    }
}
