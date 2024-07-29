package co.spribe.currency.repository;

import co.spribe.currency.model.entity.Currency;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CurrencyRepository extends CrudRepository<Currency, String> {

    @Override
    @Modifying
    @Query("DELETE FROM Currency")
    void deleteAll();

    Optional<Currency> findByCode(String code);

    @Override
    List<Currency> findAll();

    @Query("SELECT c.code FROM Currency c")
    Set<String> findAllCodes();

    boolean existsByCode(String code);
}
