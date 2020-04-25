package de.farue.autocut.service;

import de.farue.autocut.AutocutApp;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = AutocutApp.class)
@Transactional
class WashingServiceIT {

}
