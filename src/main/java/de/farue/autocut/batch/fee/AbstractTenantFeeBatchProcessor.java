package de.farue.autocut.batch.fee;

import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.accounting.BookingTemplate;
import java.time.LocalDate;
import java.util.List;
import org.springframework.batch.item.ItemProcessor;

public abstract class AbstractTenantFeeBatchProcessor implements ItemProcessor<Lease, List<BookingTemplate>> {

    public static final String ISSUER = "TenantFeeService";

    protected final LeaseService leaseService;
    protected final TenantFeeServiceQualifierDataMapper serviceQualifierDataMapper;

    public AbstractTenantFeeBatchProcessor(LeaseService leaseService, TenantFeeServiceQualifierDataMapper serviceQualifierDataMapper) {
        this.leaseService = leaseService;
        this.serviceQualifierDataMapper = serviceQualifierDataMapper;
    }

    @Override
    public List<BookingTemplate> process(Lease lease) throws Exception {
        TransactionBook transactionBook = leaseService.getCashTransactionBook(lease);
        return doProcess(lease, transactionBook);
    }

    protected abstract List<BookingTemplate> doProcess(Lease lease, TransactionBook transactionBook) throws Exception;

    protected String createDescription(LocalDate chargeDate) {
        return "" + chargeDate.getMonthValue() + "/" + chargeDate.getYear();
    }

    protected String createServiceQualifierData(LocalDate chargeDate, boolean discount) {
        TenantFeeServiceQualifierData serviceQualifierData = new TenantFeeServiceQualifierData();
        serviceQualifierData.setChargeDate(chargeDate);
        serviceQualifierData.setDiscount(discount);
        return serviceQualifierDataMapper.map(serviceQualifierData);
    }
}
