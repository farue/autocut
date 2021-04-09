package de.farue.autocut.service;

import de.farue.autocut.domain.GlobalSetting;
import de.farue.autocut.repository.GlobalSettingRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
public class GlobalSettingService {

    private final GlobalSettingRepository globalSettingRepository;
    private final ConversionService conversionService;

    @Autowired
    public GlobalSettingService(GlobalSettingRepository globalSettingRepository, ConversionService conversionService) {
        this.globalSettingRepository = globalSettingRepository;
        this.conversionService = conversionService;
    }

    @SneakyThrows(ClassNotFoundException.class)
    @SuppressWarnings("unchecked")
    public <T> T getValue(String key) {
        GlobalSetting globalSetting = globalSettingRepository.getByKey(key).orElseThrow(IllegalArgumentException::new);
        Class<?> type = Class.forName(globalSetting.getValueType());
        return (T) conversionService.convert(globalSetting.getValue(), type);
    }
}
