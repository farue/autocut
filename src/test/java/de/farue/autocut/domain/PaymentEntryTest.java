package de.farue.autocut.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import de.farue.autocut.web.rest.TestUtil;

public class PaymentEntryTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentEntry.class);
        PaymentEntry paymentEntry1 = new PaymentEntry();
        paymentEntry1.setId(1L);
        PaymentEntry paymentEntry2 = new PaymentEntry();
        paymentEntry2.setId(paymentEntry1.getId());
        assertThat(paymentEntry1).isEqualTo(paymentEntry2);
        paymentEntry2.setId(2L);
        assertThat(paymentEntry1).isNotEqualTo(paymentEntry2);
        paymentEntry1.setId(null);
        assertThat(paymentEntry1).isNotEqualTo(paymentEntry2);
    }
}
