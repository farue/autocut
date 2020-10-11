package de.farue.autocut.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import de.farue.autocut.web.rest.TestUtil;

public class TransactionBookTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionBook.class);
        TransactionBook transactionBook1 = new TransactionBook();
        transactionBook1.setId(1L);
        TransactionBook transactionBook2 = new TransactionBook();
        transactionBook2.setId(transactionBook1.getId());
        assertThat(transactionBook1).isEqualTo(transactionBook2);
        transactionBook2.setId(2L);
        assertThat(transactionBook1).isNotEqualTo(transactionBook2);
        transactionBook1.setId(null);
        assertThat(transactionBook1).isNotEqualTo(transactionBook2);
    }
}
