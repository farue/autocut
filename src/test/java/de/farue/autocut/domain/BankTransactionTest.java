package de.farue.autocut.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.farue.autocut.web.rest.TestUtil;

public class BankTransactionTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BankTransaction.class);
        BankTransaction bankTransaction1 = new BankTransaction();
        bankTransaction1.setId(1L);
        BankTransaction bankTransaction2 = new BankTransaction();
        bankTransaction2.setId(bankTransaction1.getId());
        assertThat(bankTransaction1).isEqualTo(bankTransaction2);
        bankTransaction2.setId(2L);
        assertThat(bankTransaction1).isNotEqualTo(bankTransaction2);
        bankTransaction1.setId(null);
        assertThat(bankTransaction1).isNotEqualTo(bankTransaction2);
    }
}
