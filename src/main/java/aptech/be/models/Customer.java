package aptech.be.models;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import aptech.be.models.enums.CustomerType;

@Entity
@Table(name = "customers")
public class Customer {
    
    public Customer() {
        this.type = CustomerType.REGULAR;
        this.newsletterSubscribed = false;
    }
    
    public Customer(Long id, String fullName, String provider, String email, String password, String role, CustomerType type, boolean newsletterSubscribed, CustomerDetail customerDetail) {
        this.id = id;
        this.fullName = fullName;
        this.provider = provider;
        this.email = email;
        this.password = password;
        this.role = role;
        this.type = type;
        this.newsletterSubscribed = newsletterSubscribed;
        this.customerDetail = customerDetail;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @PostPersist
    @PostLoad
    public void initializeDefaults() {
        if (this.type == null) {
            this.type = CustomerType.REGULAR;
        }
        // Không cần thiết lập newsletterSubscribed vì boolean luôn có giá trị mặc định là false
    }
    private String fullName;
    private String provider;
    private String email;
    private String password;
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type")
    private CustomerType type = CustomerType.REGULAR;

    @Column(name = "newsletter_subscribed", nullable = false, columnDefinition = "boolean default false")
    private boolean newsletterSubscribed;

    @OneToOne(mappedBy = "customer")
    @JsonManagedReference
    private CustomerDetail customerDetail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CustomerType getType() {
        return type;
    }

    public void setType(CustomerType type) {
        this.type = type;
    }

    public boolean isNewsletterSubscribed() {
        return newsletterSubscribed;
    }

    public void setNewsletterSubscribed(boolean newsletterSubscribed) {
        this.newsletterSubscribed = newsletterSubscribed;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CustomerDetail getCustomerDetail() {
        return customerDetail;
    }

    public void setCustomerDetail(CustomerDetail customerDetail) {
        this.customerDetail = customerDetail;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}