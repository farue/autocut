package de.farue.autocut.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BroadcastMessageTextTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BroadcastMessageText.class);
        BroadcastMessageText broadcastMessageText1 = new BroadcastMessageText();
        broadcastMessageText1.setId(1L);
        BroadcastMessageText broadcastMessageText2 = new BroadcastMessageText();
        broadcastMessageText2.setId(broadcastMessageText1.getId());
        assertThat(broadcastMessageText1).isEqualTo(broadcastMessageText2);
        broadcastMessageText2.setId(2L);
        assertThat(broadcastMessageText1).isNotEqualTo(broadcastMessageText2);
        broadcastMessageText1.setId(null);
        assertThat(broadcastMessageText1).isNotEqualTo(broadcastMessageText2);
    }
}
