package model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")

public class User {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int id;

	    @Column(unique = true, nullable = false)
	    private String email;

	    @Column(name = "user_password", nullable = false)
	    private String user_password;

	    @Column(unique = true)
	    private int accountNumber;

	    @Column
	    private double balance;
	    
	    public User() {}

		public User(int id, String email, String user_password, int accountNumber, double balance) {
			super();
			this.id = id;
			this.email = email;
			this.user_password = user_password;
			this.accountNumber = accountNumber;
			this.balance = balance;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPassword() {
			return user_password;
		}

		public void setPassword(String user_password) {
			this.user_password = user_password;
		}

		public int getAccountNumber() {
			return accountNumber;
		}

		public void setAccountNumber(int accountNumber) {
			this.accountNumber = accountNumber;
		}

		public double getBalance() {
			return balance;
		}

		public void setBalance(double balance) {
			this.balance = balance;
		}

}
