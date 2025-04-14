-- Insérer des certificats de test
INSERT INTO certificate (certificate_data) VALUES
('Certificate 1 Data'),
('Certificate 2 Data'),
('Certificate 3 Data');

-- Insérer des paires de clés de test
INSERT INTO key_pair_entity (private_key, public_key) VALUES
('Private Key 1', 'Public Key 1'),
('Private Key 2', 'Public Key 2'),
('Private Key 3', 'Public Key 3');

-- Insérer des signatures de test
INSERT INTO signature_entity (data, public_key, signature) VALUES
('Data 1', 'Public Key 1', 'Signature 1'),
('Data 2', 'Public Key 2', 'Signature 2'),
('Data 3', 'Public Key 3', 'Signature 3');