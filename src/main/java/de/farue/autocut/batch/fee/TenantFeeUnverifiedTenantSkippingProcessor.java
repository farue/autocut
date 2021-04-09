package de.farue.autocut.batch.fee;

import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import org.springframework.batch.item.ItemProcessor;

public class TenantFeeUnverifiedTenantSkippingProcessor implements ItemProcessor<Lease, Lease> {

    @Override
    public Lease process(Lease lease) {
        boolean verifiedTenants = lease.getTenants().stream().anyMatch(Tenant::getVerified);
        return verifiedTenants ? lease : null;
    }
}
