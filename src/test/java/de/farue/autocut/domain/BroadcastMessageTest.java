package de.farue.autocut.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BroadcastMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BroadcastMessage.class);
        BroadcastMessage broadcastMessage1 = new BroadcastMessage();
        broadcastMessage1.setId(1L);
        BroadcastMessage broadcastMessage2 = new BroadcastMessage();
        broadcastMessage2.setId(broadcastMessage1.getId());
        assertThat(broadcastMessage1).isEqualTo(broadcastMessage2);
        broadcastMessage2.setId(2L);
        assertThat(broadcastMessage1).isNotEqualTo(broadcastMessage2);
        broadcastMessage1.setId(null);
        assertThat(broadcastMessage1).isNotEqualTo(broadcastMessage2);
    }
}
