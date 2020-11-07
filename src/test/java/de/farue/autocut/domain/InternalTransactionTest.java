package de.farue.autocut.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.farue.autocut.web.rest.TestUtil;

public class InternalTransactionTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InternalTransaction.class);
        InternalTransaction internalTransaction1 = new InternalTransaction();
        internalTransaction1.setId(1L);
        InternalTransaction internalTransaction2 = new InternalTransaction();
        internalTransaction2.setId(internalTransaction1.getId());
        assertThat(internalTransaction1).isEqualTo(internalTransaction2);
        internalTransaction2.setId(2L);
        assertThat(internalTransaction1).isNotEqualTo(internalTransaction2);
        internalTransaction1.setId(null);
        assertThat(internalTransaction1).isNotEqualTo(internalTransaction2);
    }
}
