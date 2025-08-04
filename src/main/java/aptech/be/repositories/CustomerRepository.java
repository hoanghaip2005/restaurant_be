package aptech.be.repositories;

import aptech.be.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import aptech.be.models.enums.CustomerType;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    
    List<Customer> findByType(CustomerType type);
    
    List<Customer> findByNewsletterSubscribed(boolean subscribed);
}
