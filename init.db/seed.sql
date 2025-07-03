CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE system_settings (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  setting_key VARCHAR(100) UNIQUE NOT NULL,
  setting_value TEXT,
  description TEXT,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  username VARCHAR(100),
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE fixed_blocked_extensions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  extension VARCHAR(20) NOT NULL UNIQUE,
  display_name VARCHAR(50),
  is_blocked BOOLEAN DEFAULT false,
  is_system BOOLEAN DEFAULT true, 
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE user_fixed_blocked_extensions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  extension_id UUID REFERENCES fixed_blocked_extensions(id) ON DELETE CASCADE,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW(),
  UNIQUE(user_id, extension_id)
);

CREATE TABLE user_custom_blocked_extensions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  extension VARCHAR(20) NOT NULL,
  created_at TIMESTAMP DEFAULT NOW(),
  UNIQUE(user_id, extension)
);

CREATE TABLE extension_activity_logs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES users(id) ON DELETE CASCADE NOT NULL,
  extension VARCHAR(20) NOT NULL,
  source_type VARCHAR(20) NOT NULL,
  action VARCHAR(20) NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);


INSERT INTO fixed_blocked_extensions (extension, display_name, is_blocked) VALUES
('bat', 'Batch File', false),
('cmd', 'Command Script', false),
('com', 'Command File', false),
('cpl', 'Control Panel', false),
('exe', 'Executable', false),
('scr', 'Screen Saver', false),
('js', 'JavaScript', false);


INSERT INTO system_settings (setting_key, setting_value, description) VALUES
('max_custom_extensions', '200', '사용자별 최대 커스텀 확장자 개수'),
('max_extension_length', '20', '확장자 최대 길이'),
('file_upload_enabled', 'true', '파일 업로드 기능 활성화 여부');



INSERT INTO users (email, password_hash, username)
VALUES (
    '1234@example.com',
    crypt('1234', gen_salt('bf')), 
    'testuser'
), (
    '2345@example.com',
    crypt('2345', gen_salt('bf')), 
    'testuser2'
), (
    '3456@example.com',
    crypt('3456', gen_salt('bf')), 
    'testuser3'
), (
    '4567@example.com',
    crypt('4567', gen_salt('bf')), 
    'testuser4'
), (
    '5678@example.com',
    crypt('5678', gen_salt('bf')), 
    'testuser5'
);


CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_user_custom_blocked_extensions_user_id ON user_custom_blocked_extensions(user_id);
CREATE INDEX idx_user_custom_blocked_extensions_extension ON user_custom_blocked_extensions(extension);
CREATE INDEX idx_extension_activity_logs_user_id ON extension_activity_logs(user_id);
CREATE INDEX idx_extension_activity_logs_extension ON extension_activity_logs(extension);


CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_fixed_blocked_extensions_updated_at 
    BEFORE UPDATE ON fixed_blocked_extensions 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_system_settings_updated_at 
    BEFORE UPDATE ON system_settings 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();