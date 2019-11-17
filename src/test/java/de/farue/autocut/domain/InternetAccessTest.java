package de.farue.autocut.domain;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InternetAccessTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InternetAccess.class);
        InternetAccess internetAccess1 = new InternetAccess();
        internetAccess1.setId(1L);
        InternetAccess internetAccess2 = new InternetAccess();
        internetAccess2.setId(internetAccess1.getId());
        assertThat(internetAccess1).isEqualTo(internetAccess2);
        internetAccess2.setId(2L);
        assertThat(internetAccess1).isNotEqualTo(internetAccess2);
        internetAccess1.setId(null);
        assertThat(internetAccess1).isNotEqualTo(internetAccess2);
    }
}
