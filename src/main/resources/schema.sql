
CREATE TABLE CUSTOMER (
                          id VARCHAR(36) PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          email VARCHAR(255) NOT NULL,
                          annual_spend DECIMAL(15, 2),
                          last_purchase_date TIMESTAMP
);