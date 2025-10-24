package se196411.booking_ticket.dto;

    // import lombok ...

    public class SignupRequest {
        private String username;
        private String email;
        private String password;
        private String phone; // Thêm dòng này

        // Getters and Setters for all fields, including phone
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        // Thêm getter và setter cho phone
        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }