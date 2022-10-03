package de.farue.autocut.service.mapper;

import de.farue.autocut.domain.Tenant;
import de.farue.autocut.service.dto.MemberDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface MemberMapper {
    @Mapping(target = "firstName", source = "tenant.firstName")
    @Mapping(target = "lastName", source = "tenant.lastName")
    @Mapping(target = "email", source = "tenant.user.email")
    @Mapping(target = "emailConfirmed", source = "tenant.user.activated")
    @Mapping(target = "memberVerified", source = "tenant.verified")
    MemberDTO fromTenant(Tenant tenant);
}
