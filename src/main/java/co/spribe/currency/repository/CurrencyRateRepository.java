package co.spribe.currency.repository;

import co.spribe.currency.model.entity.CurrencyRate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRateRepository extends CrudRepository<CurrencyRate, Long> {

    @Override
    @Modifying
    @Query("DELETE FROM CurrencyRate")
    void deleteAll();
}
