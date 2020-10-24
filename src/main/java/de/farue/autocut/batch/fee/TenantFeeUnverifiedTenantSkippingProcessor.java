package de.farue.autocut.batch.fee;

import org.springframework.batch.item.ItemProcessor;

import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;

public class TenantFeeUnverifiedTenantSkippingProcessor implements ItemProcessor<Lease, Lease> {

    @Override
    public Lease process(Lease lease) {
        boolean verifiedTenants = lease.getTenants().stream().anyMatch(Tenant::isVerified);
        return verifiedTenants ? lease : null;
    }
}
