package aptech.be.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "customerdetails")
public class CustomerDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String phoneNumber;
    private String address;
    private String imageUrl;
    private String point;
    private String voucher;
    private Boolean newsletterSubscribed; // Đăng ký nhận newsletter
    private String customerLevel; // REGULAR, VIP, PREMIUM

    @OneToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Boolean getNewsletterSubscribed() {
        return newsletterSubscribed;
    }

    public void setNewsletterSubscribed(Boolean newsletterSubscribed) {
        this.newsletterSubscribed = newsletterSubscribed;
    }

    public String getCustomerLevel() {
        return customerLevel;
    }

    public void setCustomerLevel(String customerLevel) {
        this.customerLevel = customerLevel;
    }
}
