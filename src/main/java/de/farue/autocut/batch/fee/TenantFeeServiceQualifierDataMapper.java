package de.farue.autocut.batch.fee;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class TenantFeeServiceQualifierDataMapper {

    private static final String DATE_GROUP = "date";
    private static final String DISCOUNT_GROUP = "discount";

    private static final String SERVICE_QUALIFIER_REGEX =
        "(?<" + DATE_GROUP + ">\\d{4}-\\d{2}-\\d{2});(?<" + DISCOUNT_GROUP + ">(?:true|false))";
    private static final Pattern SERVICE_QUALIFIER_PATTERN = Pattern.compile(SERVICE_QUALIFIER_REGEX);

    public TenantFeeServiceQualifierData map(String serviceQualifier) {
        Matcher matcher = SERVICE_QUALIFIER_PATTERN.matcher(serviceQualifier);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Malformed service qualifier: " + serviceQualifier);
        }

        LocalDate date = LocalDate.parse(matcher.group(DATE_GROUP));
        boolean discount = Boolean.parseBoolean(matcher.group(DISCOUNT_GROUP));
        TenantFeeServiceQualifierData data = new TenantFeeServiceQualifierData();
        data.setChargeDate(date);
        data.setDiscount(discount);
        return data;
    }

    public String map(TenantFeeServiceQualifierData serviceQualifierData) {
        return serviceQualifierData.getChargeDate().toString() + ";" + serviceQualifierData.isDiscount();
    }
}
