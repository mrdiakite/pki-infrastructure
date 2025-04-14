-- Table pour stocker les certificats
CREATE TABLE IF NOT EXISTS certificate (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    certificate_data VARCHAR(255) NOT NULL
);

-- Table pour stocker les paires de clés
CREATE TABLE IF NOT EXISTS key_pair_entity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    private_key VARCHAR(255) NOT NULL,
    public_key VARCHAR(255) NOT NULL
);

-- Table pour stocker les signatures
CREATE TABLE IF NOT EXISTS signature_entity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data VARCHAR(255) NOT NULL,
    public_key VARCHAR(255) NOT NULL,
    signature VARCHAR(255) NOT NULL
);