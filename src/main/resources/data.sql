-- ============================================================
-- Trade Risk Monitoring & Analytics Platform
-- Seed Data — Realistic Mock Data for Development/Testing
-- ============================================================

-- ==================== APP USERS (Authentication) ====================
-- Passwords are BCrypt-hashed. All passwords = "password123"
INSERT INTO app_users (id, username, password, role, enabled, created_at, updated_at)
VALUES
    ('a0000001-0000-0000-0000-000000000001', 'admin',
     '$2b$10$ZYOgLS3HiwhfitP0zPbImu3Azm1Kou8pKryDXOLL/o95rGT9BBegu',
     'ROLE_ADMIN', true, NOW(), NOW()),
    ('a0000001-0000-0000-0000-000000000002', 'trader_john',
     '$2b$10$ZYOgLS3HiwhfitP0zPbImu3Azm1Kou8pKryDXOLL/o95rGT9BBegu',
     'ROLE_TRADER', true, NOW(), NOW()),
    ('a0000001-0000-0000-0000-000000000003', 'risk_manager',
     '$2b$10$ZYOgLS3HiwhfitP0zPbImu3Azm1Kou8pKryDXOLL/o95rGT9BBegu',
     'ROLE_RISK_MANAGER', true, NOW(), NOW());

-- ==================== TRADERS ====================
INSERT INTO traders (id, employee_id, name, desk, role, risk_limit, active, created_at, updated_at)
VALUES
    ('b0000001-0000-0000-0000-000000000001', 'EMP001', 'John Mitchell',
     'EQUITY', 'SENIOR_TRADER', 5000000.0000, true, NOW(), NOW()),
    ('b0000001-0000-0000-0000-000000000002', 'EMP002', 'Sarah Chen',
     'EQUITY', 'DESK_HEAD', 10000000.0000, true, NOW(), NOW()),
    ('b0000001-0000-0000-0000-000000000003', 'EMP003', 'Raj Patel',
     'DERIVATIVES', 'SENIOR_TRADER', 8000000.0000, true, NOW(), NOW()),
    ('b0000001-0000-0000-0000-000000000004', 'EMP004', 'Emily Watson',
     'FIXED_INCOME', 'JUNIOR_TRADER', 2000000.0000, true, NOW(), NOW()),
    ('b0000001-0000-0000-0000-000000000005', 'EMP005', 'Michael Tanaka',
     'FX', 'SENIOR_TRADER', 7000000.0000, true, NOW(), NOW()),
    ('b0000001-0000-0000-0000-000000000006', 'EMP006', 'Lisa Park',
     'DERIVATIVES', 'DESK_HEAD', 15000000.0000, true, NOW(), NOW()),
    ('b0000001-0000-0000-0000-000000000007', 'EMP007', 'David Rosenberg',
     'COMMODITIES', 'SENIOR_TRADER', 6000000.0000, true, NOW(), NOW());

-- ==================== TRADES ====================
-- John Mitchell — diversified equity portfolio
INSERT INTO trades (id, trader_id, asset_symbol, asset_type, trade_type, instrument_type,
                    quantity, entry_price, current_price, trade_status, counterparty,
                    trade_date, settlement_date, created_at, updated_at)
VALUES
    ('c0000001-0000-0000-0000-000000000001', 'b0000001-0000-0000-0000-000000000001',
     'AAPL', 'EQUITY', 'BUY', 'STOCK', 500.0000, 175.5000, 178.5000, 'OPEN',
     'Goldman Sachs', NOW() - INTERVAL '5' DAY, CURRENT_DATE + 2, NOW(), NOW()),
    ('c0000001-0000-0000-0000-000000000002', 'b0000001-0000-0000-0000-000000000001',
     'MSFT', 'EQUITY', 'BUY', 'STOCK', 300.0000, 370.0000, 378.2500, 'OPEN',
     'JPMorgan Chase', NOW() - INTERVAL '3' DAY, CURRENT_DATE + 2, NOW(), NOW()),
    ('c0000001-0000-0000-0000-000000000003', 'b0000001-0000-0000-0000-000000000001',
     'TSLA', 'EQUITY', 'SELL', 'STOCK', 200.0000, 255.0000, 248.5000, 'OPEN',
     'Morgan Stanley', NOW() - INTERVAL '2' DAY, CURRENT_DATE + 2, NOW(), NOW()),
    ('c0000001-0000-0000-0000-000000000004', 'b0000001-0000-0000-0000-000000000001',
     'GOOGL', 'EQUITY', 'BUY', 'STOCK', 400.0000, 138.0000, 141.8000, 'OPEN',
     'Citadel Securities', NOW() - INTERVAL '4' DAY, CURRENT_DATE + 2, NOW(), NOW());

-- Sarah Chen — large positions
INSERT INTO trades (id, trader_id, asset_symbol, asset_type, trade_type, instrument_type,
                    quantity, entry_price, current_price, trade_status, counterparty,
                    trade_date, settlement_date, created_at, updated_at)
VALUES
    ('c0000001-0000-0000-0000-000000000005', 'b0000001-0000-0000-0000-000000000002',
     'NVDA', 'EQUITY', 'BUY', 'STOCK', 800.0000, 850.0000, 875.3000, 'OPEN',
     'Barclays', NOW() - INTERVAL '7' DAY, CURRENT_DATE + 2, NOW(), NOW()),
    ('c0000001-0000-0000-0000-000000000006', 'b0000001-0000-0000-0000-000000000002',
     'META', 'EQUITY', 'BUY', 'STOCK', 600.0000, 495.0000, 505.7500, 'OPEN',
     'Deutsche Bank', NOW() - INTERVAL '6' DAY, CURRENT_DATE + 2, NOW(), NOW()),
    ('c0000001-0000-0000-0000-000000000007', 'b0000001-0000-0000-0000-000000000002',
     'AMZN', 'EQUITY', 'BUY', 'STOCK', 450.0000, 180.0000, 185.6000, 'OPEN',
     'UBS', NOW() - INTERVAL '4' DAY, CURRENT_DATE + 2, NOW(), NOW()),
    ('c0000001-0000-0000-0000-000000000008', 'b0000001-0000-0000-0000-000000000002',
     'AAPL', 'EQUITY', 'SELL', 'STOCK', 300.0000, 180.0000, 178.5000, 'OPEN',
     'Nomura', NOW() - INTERVAL '1' DAY, CURRENT_DATE + 2, NOW(), NOW());

-- Raj Patel — derivatives (futures)
INSERT INTO trades (id, trader_id, asset_symbol, asset_type, trade_type, instrument_type,
                    quantity, entry_price, current_price, trade_status, counterparty,
                    trade_date, settlement_date, created_at, updated_at)
VALUES
    ('c0000001-0000-0000-0000-000000000009', 'b0000001-0000-0000-0000-000000000003',
     'AAPL', 'DERIVATIVE', 'BUY', 'FUTURE', 100.0000, 178.0000, 178.5000, 'OPEN',
     'CME Group', NOW() - INTERVAL '3' DAY, CURRENT_DATE + 1, NOW(), NOW()),
    ('c0000001-0000-0000-0000-000000000010', 'b0000001-0000-0000-0000-000000000003',
     'NVDA', 'DERIVATIVE', 'BUY', 'FUTURE', 50.0000, 860.0000, 875.3000, 'OPEN',
     'ICE', NOW() - INTERVAL '2' DAY, CURRENT_DATE + 1, NOW(), NOW()),
    ('c0000001-0000-0000-0000-000000000011', 'b0000001-0000-0000-0000-000000000003',
     'TSLA', 'DERIVATIVE', 'SELL', 'FUTURE', 150.0000, 260.0000, 248.5000, 'OPEN',
     'CBOE', NOW() - INTERVAL '5' DAY, CURRENT_DATE + 1, NOW(), NOW());

-- Emily Watson — smaller fixed income positions
INSERT INTO trades (id, trader_id, asset_symbol, asset_type, trade_type, instrument_type,
                    quantity, entry_price, current_price, trade_status, counterparty,
                    trade_date, settlement_date, created_at, updated_at)
VALUES
    ('c0000001-0000-0000-0000-000000000012', 'b0000001-0000-0000-0000-000000000004',
     'BAC', 'EQUITY', 'BUY', 'STOCK', 1000.0000, 34.5000, 35.2000, 'OPEN',
     'Wells Fargo', NOW() - INTERVAL '6' DAY, CURRENT_DATE + 2, NOW(), NOW()),
    ('c0000001-0000-0000-0000-000000000013', 'b0000001-0000-0000-0000-000000000004',
     'JPM', 'EQUITY', 'BUY', 'STOCK', 200.0000, 190.0000, 196.4000, 'OPEN',
     'Bank of America', NOW() - INTERVAL '4' DAY, CURRENT_DATE + 2, NOW(), NOW()),
    ('c0000001-0000-0000-0000-000000000014', 'b0000001-0000-0000-0000-000000000004',
     'GS', 'EQUITY', 'BUY', 'STOCK', 100.0000, 450.0000, 458.7500, 'OPEN',
     'Citigroup', NOW() - INTERVAL '3' DAY, CURRENT_DATE + 2, NOW(), NOW());

-- Michael Tanaka — FX desk
INSERT INTO trades (id, trader_id, asset_symbol, asset_type, trade_type, instrument_type,
                    quantity, entry_price, current_price, trade_status, counterparty,
                    trade_date, settlement_date, created_at, updated_at)
VALUES
    ('c0000001-0000-0000-0000-000000000015', 'b0000001-0000-0000-0000-000000000005',
     'JPM', 'EQUITY', 'BUY', 'STOCK', 500.0000, 192.0000, 196.4000, 'OPEN',
     'HSBC', NOW() - INTERVAL '5' DAY, CURRENT_DATE + 2, NOW(), NOW()),
    ('c0000001-0000-0000-0000-000000000016', 'b0000001-0000-0000-0000-000000000005',
     'GS', 'EQUITY', 'BUY', 'STOCK', 300.0000, 455.0000, 458.7500, 'OPEN',
     'BNP Paribas', NOW() - INTERVAL '3' DAY, CURRENT_DATE + 2, NOW(), NOW()),
    ('c0000001-0000-0000-0000-000000000017', 'b0000001-0000-0000-0000-000000000005',
     'MSFT', 'EQUITY', 'SELL', 'STOCK', 250.0000, 382.0000, 378.2500, 'OPEN',
     'SocGen', NOW() - INTERVAL '2' DAY, CURRENT_DATE + 2, NOW(), NOW());

-- Closed trades for realized PnL
INSERT INTO trades (id, trader_id, asset_symbol, asset_type, trade_type, instrument_type,
                    quantity, entry_price, current_price, trade_status, counterparty,
                    trade_date, settlement_date, exit_price, closed_at, created_at, updated_at)
VALUES
    ('c0000001-0000-0000-0000-000000000018', 'b0000001-0000-0000-0000-000000000001',
     'AMZN', 'EQUITY', 'BUY', 'STOCK', 200.0000, 170.0000, 185.6000, 'CLOSED',
     'Jefferies', NOW() - INTERVAL '10' DAY, CURRENT_DATE - 8, 185.6000,
     NOW() - INTERVAL '8' DAY, NOW(), NOW()),
    ('c0000001-0000-0000-0000-000000000019', 'b0000001-0000-0000-0000-000000000002',
     'TSLA', 'EQUITY', 'BUY', 'STOCK', 150.0000, 220.0000, 248.5000, 'CLOSED',
     'RBC Capital', NOW() - INTERVAL '15' DAY, CURRENT_DATE - 13, 248.5000,
     NOW() - INTERVAL '13' DAY, NOW(), NOW());

-- ==================== POSITIONS ====================
INSERT INTO positions (id, trader_id, asset_symbol, instrument_type, net_quantity,
                       average_price, current_price, market_value, unrealized_pnl,
                       last_updated, created_at, updated_at)
VALUES
    ('d0000001-0000-0000-0000-000000000001', 'b0000001-0000-0000-0000-000000000001',
     'AAPL', 'STOCK', 500.0000, 175.5000, 178.5000, 89250.0000, 1500.0000, NOW(), NOW(), NOW()),
    ('d0000001-0000-0000-0000-000000000002', 'b0000001-0000-0000-0000-000000000001',
     'MSFT', 'STOCK', 300.0000, 370.0000, 378.2500, 113475.0000, 2475.0000, NOW(), NOW(), NOW()),
    ('d0000001-0000-0000-0000-000000000003', 'b0000001-0000-0000-0000-000000000001',
     'TSLA', 'STOCK', -200.0000, 255.0000, 248.5000, 49700.0000, 1300.0000, NOW(), NOW(), NOW()),
    ('d0000001-0000-0000-0000-000000000004', 'b0000001-0000-0000-0000-000000000001',
     'GOOGL', 'STOCK', 400.0000, 138.0000, 141.8000, 56720.0000, 1520.0000, NOW(), NOW(), NOW()),
    ('d0000001-0000-0000-0000-000000000005', 'b0000001-0000-0000-0000-000000000002',
     'NVDA', 'STOCK', 800.0000, 850.0000, 875.3000, 700240.0000, 20240.0000, NOW(), NOW(), NOW()),
    ('d0000001-0000-0000-0000-000000000006', 'b0000001-0000-0000-0000-000000000002',
     'META', 'STOCK', 600.0000, 495.0000, 505.7500, 303450.0000, 6450.0000, NOW(), NOW(), NOW());

-- ==================== RISK METRICS ====================
INSERT INTO risk_metrics (id, trader_id, total_exposure, total_pnl, var_estimate,
                          breach_status, last_calculated, created_at, updated_at)
VALUES
    ('e0000001-0000-0000-0000-000000000001', 'b0000001-0000-0000-0000-000000000001',
     309145.0000, 6795.0000, 10168.4000, false, NOW(), NOW(), NOW()),
    ('e0000001-0000-0000-0000-000000000002', 'b0000001-0000-0000-0000-000000000002',
     1087200.0000, 26690.0000, 35770.3200, false, NOW(), NOW(), NOW()),
    ('e0000001-0000-0000-0000-000000000003', 'b0000001-0000-0000-0000-000000000003',
     98275.0000, 2475.0000, 3233.2500, false, NOW(), NOW(), NOW()),
    ('e0000001-0000-0000-0000-000000000004', 'b0000001-0000-0000-0000-000000000004',
     120555.0000, 2795.0000, 3966.2500, false, NOW(), NOW(), NOW());

-- ==================== PNL HISTORY ====================
INSERT INTO pnl_history (id, trader_id, date, daily_pnl, cumulative_pnl, created_at, updated_at)
VALUES
    ('f0000001-0000-0000-0000-000000000001', 'b0000001-0000-0000-0000-000000000001',
     CURRENT_DATE - 7, 1200.0000, 1200.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000002', 'b0000001-0000-0000-0000-000000000001',
     CURRENT_DATE - 6, -800.0000, 400.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000003', 'b0000001-0000-0000-0000-000000000001',
     CURRENT_DATE - 5, 2100.0000, 2500.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000004', 'b0000001-0000-0000-0000-000000000001',
     CURRENT_DATE - 4, 950.0000, 3450.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000005', 'b0000001-0000-0000-0000-000000000001',
     CURRENT_DATE - 3, -350.0000, 3100.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000006', 'b0000001-0000-0000-0000-000000000001',
     CURRENT_DATE - 2, 1800.0000, 4900.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000007', 'b0000001-0000-0000-0000-000000000001',
     CURRENT_DATE - 1, 1895.0000, 6795.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000008', 'b0000001-0000-0000-0000-000000000002',
     CURRENT_DATE - 7, 5400.0000, 5400.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000009', 'b0000001-0000-0000-0000-000000000002',
     CURRENT_DATE - 6, 3200.0000, 8600.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000010', 'b0000001-0000-0000-0000-000000000002',
     CURRENT_DATE - 5, -2100.0000, 6500.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000011', 'b0000001-0000-0000-0000-000000000002',
     CURRENT_DATE - 4, 7800.0000, 14300.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000012', 'b0000001-0000-0000-0000-000000000002',
     CURRENT_DATE - 3, 4100.0000, 18400.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000013', 'b0000001-0000-0000-0000-000000000002',
     CURRENT_DATE - 2, -1200.0000, 17200.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000014', 'b0000001-0000-0000-0000-000000000002',
     CURRENT_DATE - 1, 9490.0000, 26690.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000015', 'b0000001-0000-0000-0000-000000000003',
     CURRENT_DATE - 5, 800.0000, 800.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000016', 'b0000001-0000-0000-0000-000000000003',
     CURRENT_DATE - 4, 1200.0000, 2000.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000017', 'b0000001-0000-0000-0000-000000000003',
     CURRENT_DATE - 3, -500.0000, 1500.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000018', 'b0000001-0000-0000-0000-000000000003',
     CURRENT_DATE - 2, 600.0000, 2100.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000019', 'b0000001-0000-0000-0000-000000000003',
     CURRENT_DATE - 1, 375.0000, 2475.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000020', 'b0000001-0000-0000-0000-000000000004',
     CURRENT_DATE - 3, 900.0000, 900.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000021', 'b0000001-0000-0000-0000-000000000004',
     CURRENT_DATE - 2, 1100.0000, 2000.0000, NOW(), NOW()),
    ('f0000001-0000-0000-0000-000000000022', 'b0000001-0000-0000-0000-000000000004',
     CURRENT_DATE - 1, 795.0000, 2795.0000, NOW(), NOW());

-- ==================== RISK ALERTS ====================
-- Using valid hex UUID format (no 'g' prefix)
INSERT INTO risk_alerts (id, trader_id, alert_type, severity, message, acknowledged, created_at, updated_at)
VALUES
    ('aa000001-0000-0000-0000-000000000001', 'b0000001-0000-0000-0000-000000000002',
     'LARGE_EXPOSURE', 'HIGH',
     'EXPOSURE WARNING: Sarah Chen approaching risk limit. Exposure: $1,087,200 / Limit: $10,000,000 (10.87% utilization).',
     false, NOW() - INTERVAL '2' DAY, NOW()),
    ('aa000001-0000-0000-0000-000000000002', 'b0000001-0000-0000-0000-000000000003',
     'VAR_BREACH', 'MEDIUM',
     'VAR ALERT: Raj Patel VaR estimate ($3,233) exceeds threshold. Total exposure: $98,275.',
     false, NOW() - INTERVAL '1' DAY, NOW()),
    ('aa000001-0000-0000-0000-000000000003', 'b0000001-0000-0000-0000-000000000001',
     'PNL_ANOMALY', 'LOW',
     'PnL Anomaly: John Mitchell had unusually large daily PnL swing of $2,100.',
     true, NOW() - INTERVAL '5' DAY, NOW());
