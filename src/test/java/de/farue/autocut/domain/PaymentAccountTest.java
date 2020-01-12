package de.farue.autocut.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import de.farue.autocut.web.rest.TestUtil;

public class PaymentAccountTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentAccount.class);
        PaymentAccount paymentAccount1 = new PaymentAccount();
        paymentAccount1.setId(1L);
        PaymentAccount paymentAccount2 = new PaymentAccount();
        paymentAccount2.setId(paymentAccount1.getId());
        assertThat(paymentAccount1).isEqualTo(paymentAccount2);
        paymentAccount2.setId(2L);
        assertThat(paymentAccount1).isNotEqualTo(paymentAccount2);
        paymentAccount1.setId(null);
        assertThat(paymentAccount1).isNotEqualTo(paymentAccount2);
    }
}
