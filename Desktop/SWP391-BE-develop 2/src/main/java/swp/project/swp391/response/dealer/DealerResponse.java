package swp.project.swp391.response.dealer;

import lombok.Data;

@Data
public class DealerResponse {

    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private Boolean isActive;
    private String region;

    public DealerResponse(Long id, String name, String address, String phoneNumber, String email, Boolean isActive, String region) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.isActive = isActive;
        this.region = region;
    }
}
