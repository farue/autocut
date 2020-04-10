package de.farue.autocut.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import de.farue.autocut.web.rest.TestUtil;

public class GlobalSettingTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GlobalSetting.class);
        GlobalSetting globalSetting1 = new GlobalSetting();
        globalSetting1.setId(1L);
        GlobalSetting globalSetting2 = new GlobalSetting();
        globalSetting2.setId(globalSetting1.getId());
        assertThat(globalSetting1).isEqualTo(globalSetting2);
        globalSetting2.setId(2L);
        assertThat(globalSetting1).isNotEqualTo(globalSetting2);
        globalSetting1.setId(null);
        assertThat(globalSetting1).isNotEqualTo(globalSetting2);
    }
}
