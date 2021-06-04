package de.farue.autocut.repository;

import de.farue.autocut.domain.LaundryMachine;
import de.farue.autocut.domain.LaundryProgram;
import de.farue.autocut.domain.Tenant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LaundryProgram entity.
 */
@Repository
public interface LaundryProgramRepository extends JpaRepository<LaundryProgram, Long> {
    // NB: This query does not work with h2!
    // The query consists of two parts that do very similar things: Get the top 5 programs applicable to the provided
    // laundry machine based on their frequency and recency in the past 100 days. The first part gets the top 5
    // programs used by the provided tenants, the second part gets the top 5 programs by all users so new users will
    // also always be shown 5 suggestions.
    // The score for each history entry is calculated with a linear function that expresses the decreasing relevance
    // of an entry as it gets older: f(x) = -1/125 * x + 1. x is the age in days since now, so for a 0 day old entry
    // the score is 1, for a 100 day old entry the score is 0.2.
    // These scores are grouped by program and summed. The resulting value is ranked in descending order and we take
    // the top 5.
    @Query(
        nativeQuery = true,
        value = "select *" +
        " from (" +
        "          select wp.*" +
        "          from (select wmp.program_id, sum(1 + -1 / 125 * (100 - datediff(now(), h.using_date))) as priority" +
        "                from wash_history h" +
        "                         inner join wash_machine_program wmp on h.program_id = wmp.id" +
        "                where wmp.program_id in" +
        "                      (select wmp2.program_id from wash_machine_program wmp2 where wmp2.machine_id = :machineId)" +
        "                  and h.using_date between date_sub(now(), interval 100 day) and now()" +
        "                  and h.using_tenant_id in :tenantIds" +
        "                group by wmp.program_id" +
        "                order by priority desc" +
        "                limit 5) rated_progs_tenant" +
        "                   inner join wash_program wp on rated_progs_tenant.program_id = wp.id" +
        "          union" +
        "          select wp.*" +
        "          from (select wmp.program_id, sum(1 + -1 / 125 * (100 - datediff(now(), h.using_date))) as priority" +
        "                from wash_history h" +
        "                         inner join wash_machine_program wmp on h.program_id = wmp.id" +
        "                where wmp.program_id in" +
        "                      (select wmp2.program_id from wash_machine_program wmp2 where wmp2.machine_id = :machineId)" +
        "                  and h.using_date between date_sub(now(), interval 100 day) and now()" +
        "                group by wmp.program_id" +
        "                order by priority desc" +
        "                limit 5) rated_progs_all" +
        "                   inner join wash_program wp on rated_progs_all.program_id = wp.id" +
        "      ) rated_progs" +
        " limit 5"
    )
    List<LaundryProgram> findSuggestionsWithIds(Collection<Long> tenantIds, Long machineId);

    default List<LaundryProgram> findSuggestions(Collection<Tenant> tenants, LaundryMachine machine) {
        return findSuggestionsWithIds(tenants.stream().map(Tenant::getId).collect(Collectors.toList()), machine.getId());
    }
}
