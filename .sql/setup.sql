/*
Laptop Website Database
Generated from LaptopWebsite_RDS.docx
Target DBMS: Microsoft SQL Server

Notes:
- The RDS lists both bs_Reviews and bs_Review. bs_Reviews is implemented as the main table.
- A synonym dbo.bs_Review is created to alias dbo.bs_Reviews for compatibility.
- Passwords should be stored as hashed values in the application layer.
*/

IF DB_ID(N'LaptopWebsiteDB') IS NULL
BEGIN
    CREATE DATABASE LaptopWebsiteDB;
END
GO

USE LaptopWebsiteDB;
GO

/* =========================
   1) Master / Reference Tables
   ========================= */

IF OBJECT_ID(N'dbo.bs_Categories', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_Categories (
        category_id      INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_Categories PRIMARY KEY,
        category_name    NVARCHAR(150) NOT NULL,
        description      NVARCHAR(MAX) NULL,
        status           NVARCHAR(20) NOT NULL CONSTRAINT DF_bs_Categories_status DEFAULT ('Active'),
        created_at       DATETIME2(0) NOT NULL CONSTRAINT DF_bs_Categories_created_at DEFAULT SYSUTCDATETIME(),
        updated_at       DATETIME2(0) NULL,
        CONSTRAINT UQ_bs_Categories_category_name UNIQUE (category_name),
        CONSTRAINT CK_bs_Categories_status CHECK (status IN ('Active', 'Inactive', 'Hidden'))
    );
END
GO

IF OBJECT_ID(N'dbo.bs_Brands', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_Brands (
        brand_id      INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_Brands PRIMARY KEY,
        brand_name    NVARCHAR(150) NOT NULL,
        logo          NVARCHAR(500) NULL,
        created_at    DATETIME2(0) NOT NULL CONSTRAINT DF_bs_Brands_created_at DEFAULT SYSUTCDATETIME(),
        updated_at    DATETIME2(0) NULL,
        CONSTRAINT UQ_bs_Brands_brand_name UNIQUE (brand_name)
    );
END
GO

IF OBJECT_ID(N'dbo.bs_Roles', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_Roles (
        role_id      INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_Roles PRIMARY KEY,
        role_name    NVARCHAR(50) NOT NULL,
        CONSTRAINT UQ_bs_Roles_role_name UNIQUE (role_name)
    );

    INSERT INTO dbo.bs_Roles ([role_name])
        VALUES ('Admin'),
                ('User'),
                ('Staff'),
                ('Guest');
END
GO

/* =========================
   2) Account Tables
   ========================= */

IF OBJECT_ID(N'dbo.bs_user', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_user (
        user_id        INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_user PRIMARY KEY,
        role_id        INT NOT NULL,
        full_name      NVARCHAR(150) NOT NULL,
        email          NVARCHAR(255) NOT NULL,
        phone          NVARCHAR(20) NULL,
        password       NVARCHAR(255) NULL, -- store hashed password
        isverified     BIT NOT NULL DEFAULT 0,
        status         NVARCHAR(20) NOT NULL CONSTRAINT DF_bs_user_status DEFAULT ('Active'),
        created_at     DATETIME2(0) NOT NULL CONSTRAINT DF_bs_user_created_at DEFAULT SYSUTCDATETIME(),
        updated_at     DATETIME2(0) NULL,
        CONSTRAINT UQ_bs_user_email UNIQUE (email),
        CONSTRAINT CK_bs_user_status CHECK (status IN ('Active', 'Blocked', 'Inactive', 'Pending'))
    );

    INSERT INTO dbo.bs_user ([role_id], [full_name], [email], [phone], [password], [isverified], [status], [created_at], [updated_at])
        VALUES (1, 'Administrator', 'administrator@example.com', '012345678', 'pbkdf2$65536$4u0S7QtsuN6xRdP/ibP+NQ==$AZzekFD608x0d6OS0AZUAIPyhysmAz+xjH8kPgtBkpY=', 1, 'Active', SYSUTCDATETIME(), SYSUTCDATETIME()); 
        /* Password is 123456 */
END
GO

IF OBJECT_ID(N'dbo.bs_Addresses', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_Addresses (
        address_id       INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_Addresses PRIMARY KEY,
        user_id          INT NOT NULL,
        home_address    NVARCHAR(150) NOT NULL,
        phone            NVARCHAR(20) NOT NULL,
        province         NVARCHAR(100) NOT NULL,
        ward             NVARCHAR(100) NOT NULL,
        is_default       BIT NOT NULL CONSTRAINT DF_bs_Addresses_is_default DEFAULT (0),
        created_at       DATETIME2(0) NOT NULL CONSTRAINT DF_bs_Addresses_created_at DEFAULT SYSUTCDATETIME()
    );
END
GO

CREATE OR ALTER TRIGGER trg_bs_Addresses_SetNewDefault
	ON dbo.bs_Addresses
	AFTER INSERT, UPDATE
AS
BEGIN
    -- Prevents extra result sets from interfering with SELECT statements
    SET NOCOUNT ON;

    -- Only run if an incoming or updated row has is_default = 1
    IF EXISTS (SELECT 1 FROM inserted WHERE is_default = 1)
    BEGIN
        -- Clear the 'is_default' flag for all OTHER addresses belonging to this user
        UPDATE a
			SET a.is_default = 0
			FROM dbo.bs_Addresses a
			WHERE a.user_id IN 
				(SELECT user_id FROM inserted WHERE is_default = 1) 
					AND (a.is_default = 1)
					AND a.address_id NOT IN (SELECT MAX(address_id) FROM inserted WHERE is_default = 1  GROUP BY user_id);
    END
END
GO

/* =========================
   3) Product Catalog Tables
   ========================= */

IF OBJECT_ID(N'dbo.bs_Products', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_Products (
        product_id      INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_Products PRIMARY KEY,
        category_id     INT NOT NULL,
        brand_id        INT NOT NULL,
        sku             NVARCHAR(80) NOT NULL,
        product_name    NVARCHAR(200) NOT NULL,
        description     NVARCHAR(MAX) NULL,
        price           DECIMAL(18,2) NOT NULL,
        stock           INT NOT NULL CONSTRAINT DF_bs_Products_stock DEFAULT (0),
        thumbnail       NVARCHAR(500) NULL,
        status          NVARCHAR(20) NOT NULL CONSTRAINT DF_bs_Products_status DEFAULT ('Active'),
        created_at      DATETIME2(0) NOT NULL CONSTRAINT DF_bs_Products_created_at DEFAULT SYSUTCDATETIME(),
        updated_at      DATETIME2(0) NULL,
        CONSTRAINT UQ_bs_Products_sku UNIQUE (sku),
        CONSTRAINT CK_bs_Products_price CHECK (price > 0),
        CONSTRAINT CK_bs_Products_stock CHECK (stock >= 0),
        CONSTRAINT CK_bs_Products_status CHECK (status IN ('Active', 'Out of Stock', 'Inactive', 'Hidden'))
    );
END
GO

IF OBJECT_ID(N'dbo.bs_ProductImages', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_ProductImages (
        image_id      INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_ProductImages PRIMARY KEY,
        product_id    INT NOT NULL,
        image_url     NVARCHAR(500) NOT NULL,
        is_primary    BIT NOT NULL CONSTRAINT DF_bs_ProductImages_is_primary DEFAULT (0),
        sort_order    INT NOT NULL CONSTRAINT DF_bs_ProductImages_sort_order DEFAULT (1)
    );
END
GO

IF OBJECT_ID(N'dbo.bs_ProductSpecifications', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_ProductSpecifications (
        spec_id      INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_ProductSpecifications PRIMARY KEY,
        product_id   INT NOT NULL,
        spec_key     NVARCHAR(80) NOT NULL,
        spec_label   NVARCHAR(120) NOT NULL,
        spec_value   NVARCHAR(500) NOT NULL,
        sort_order   INT NOT NULL CONSTRAINT DF_bs_ProductSpecifications_sort_order DEFAULT (0),
        created_at   DATETIME2(0) NOT NULL CONSTRAINT DF_bs_ProductSpecifications_created_at DEFAULT SYSUTCDATETIME(),
        updated_at   DATETIME2(0) NULL,
        CONSTRAINT UQ_bs_ProductSpecifications_product_key UNIQUE (product_id, spec_key)
    );
END
GO

/* =========================
   4) Cart Tables
   ========================= */

IF OBJECT_ID(N'dbo.bs_Cart', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_Cart (
        cart_id      INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_Cart PRIMARY KEY,
        user_id      INT NOT NULL,
        created_at   DATETIME2(0) NOT NULL CONSTRAINT DF_bs_Cart_created_at DEFAULT SYSUTCDATETIME(),
        updated_at   DATETIME2(0) NULL,
        CONSTRAINT UQ_bs_Cart_user UNIQUE (user_id)
    );
END
GO

IF OBJECT_ID(N'dbo.bs_CartItems', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_CartItems (
        cart_item_id   INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_CartItems PRIMARY KEY,
        cart_id        INT NOT NULL,
        product_id     INT NOT NULL,
        quantity       INT NOT NULL,
        unit_price     DECIMAL(18,2) NOT NULL,
        subtotal       AS (CONVERT(DECIMAL(18,2), quantity * unit_price)) PERSISTED,
        created_at     DATETIME2(0) NOT NULL CONSTRAINT DF_bs_CartItems_created_at DEFAULT SYSUTCDATETIME(),
        updated_at     DATETIME2(0) NULL,
        CONSTRAINT UQ_bs_CartItems_cart_product UNIQUE (cart_id, product_id),
        CONSTRAINT CK_bs_CartItems_quantity CHECK (quantity >= 1),
        CONSTRAINT CK_bs_CartItems_unit_price CHECK (unit_price > 0)
    );
END
GO

/* =========================
   5) Order / Payment Tables
   ========================= */

IF OBJECT_ID(N'dbo.bs_Vouchers', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_Vouchers (
        voucher_id         INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_Vouchers PRIMARY KEY,
        code               NVARCHAR(50) NOT NULL,
        discount_percent   DECIMAL(5,2) NOT NULL,
        quantity           INT NOT NULL CONSTRAINT DF_bs_Vouchers_quantity DEFAULT (0),
        expired_date       DATE NOT NULL,
        status             NVARCHAR(20) NOT NULL CONSTRAINT DF_bs_Vouchers_status DEFAULT ('Active'),
        created_at         DATETIME2(0) NOT NULL CONSTRAINT DF_bs_Vouchers_created_at DEFAULT SYSUTCDATETIME(),
        updated_at         DATETIME2(0) NULL,
        CONSTRAINT UQ_bs_Vouchers_code UNIQUE (code),
        CONSTRAINT CK_bs_Vouchers_discount CHECK (discount_percent > 0 AND discount_percent <= 100),
        CONSTRAINT CK_bs_Vouchers_quantity CHECK (quantity >= 0),
        CONSTRAINT CK_bs_Vouchers_status CHECK (status IN ('Active', 'Inactive', 'Expired'))
    );
END
GO

IF OBJECT_ID(N'dbo.bs_Orders', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_Orders (
        order_id           INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_Orders PRIMARY KEY,
        user_id            INT NOT NULL,
        voucher_id         INT NULL,
        total_amount       DECIMAL(18,2) NOT NULL,
        shipping_fee       DECIMAL(18,2) NOT NULL CONSTRAINT DF_bs_Orders_shipping_fee DEFAULT (0),
        discount_amount    DECIMAL(18,2) NOT NULL CONSTRAINT DF_bs_Orders_discount_amount DEFAULT (0),
        payment_method     NVARCHAR(30) NOT NULL,
        order_status       NVARCHAR(20) NOT NULL CONSTRAINT DF_bs_Orders_order_status DEFAULT ('Pending'),
        note               NVARCHAR(500) NULL,
        phone              NVARCHAR(20) NULL,
        address_info       NVARCHAR(1000) NOT NULL,
        created_at         DATETIME2(0) NOT NULL CONSTRAINT DF_bs_Orders_created_at DEFAULT SYSUTCDATETIME(),
        updated_at         DATETIME2(0) NULL,
        CONSTRAINT CK_bs_Orders_total_amount CHECK (total_amount >= 0),
        CONSTRAINT CK_bs_Orders_shipping_fee CHECK (shipping_fee >= 0),
        CONSTRAINT CK_bs_Orders_discount_amount CHECK (discount_amount >= 0),
        CONSTRAINT CK_bs_Orders_payment_method CHECK (payment_method IN ('COD', 'VNPay', 'MoMo', 'Bank Transfer', 'Other')),
        CONSTRAINT CK_bs_Orders_order_status CHECK (order_status IN ('Pending', 'Confirmed', 'Shipping', 'Delivered', 'Cancelled', 'Payment Failed'))
    );
END
GO

IF OBJECT_ID(N'dbo.bs_OrderDetails', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_OrderDetails (
        order_detail_id   INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_OrderDetails PRIMARY KEY,
        order_id          INT NOT NULL,
        product_id        INT NOT NULL,
        quantity          INT NOT NULL,
        unit_price        DECIMAL(18,2) NOT NULL,
        subtotal          AS (CONVERT(DECIMAL(18,2), quantity * unit_price)) PERSISTED,
        created_at        DATETIME2(0) NOT NULL CONSTRAINT DF_bs_OrderDetails_created_at DEFAULT SYSUTCDATETIME(),
        CONSTRAINT CK_bs_OrderDetails_quantity CHECK (quantity >= 1),
        CONSTRAINT CK_bs_OrderDetails_unit_price CHECK (unit_price > 0)
    );
END
GO

IF OBJECT_ID(N'dbo.bs_Payments', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_Payments (
        payment_id        INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_Payments PRIMARY KEY,
        order_id          INT NOT NULL,
        payment_method    NVARCHAR(30) NOT NULL,
        payment_status    NVARCHAR(20) NOT NULL CONSTRAINT DF_bs_Payments_payment_status DEFAULT ('Pending'),
        transaction_no    NVARCHAR(100) NULL,
        paid_at           DATETIME2(0) NULL,
        created_at        DATETIME2(0) NOT NULL CONSTRAINT DF_bs_Payments_created_at DEFAULT SYSUTCDATETIME(),
        updated_at        DATETIME2(0) NULL,
        CONSTRAINT UQ_bs_Payments_order UNIQUE (order_id),
        CONSTRAINT CK_bs_Payments_payment_method CHECK (payment_method IN ('COD', 'VNPay', 'MoMo', 'Bank Transfer', 'Other')),
        CONSTRAINT CK_bs_Payments_payment_status CHECK (payment_status IN ('Pending', 'Paid', 'Failed', 'Refunded'))
    );
END
GO

/* =========================
   6) Review Tables
   ========================= */

IF OBJECT_ID(N'dbo.bs_Reviews', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_Reviews (
        review_id      INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_Reviews PRIMARY KEY,
        user_id        INT NOT NULL,
        order_id       INT NULL,
        product_id     INT NOT NULL,
        rating         INT NOT NULL,
        comment        NVARCHAR(1000) NULL,
        status         NVARCHAR(20) NOT NULL CONSTRAINT DF_bs_Reviews_status DEFAULT ('Visible'),
        moderated_by   INT NULL,
        moderated_at   DATETIME2(0) NULL,
        created_at     DATETIME2(0) NOT NULL CONSTRAINT DF_bs_Reviews_created_at DEFAULT SYSUTCDATETIME(),
        updated_at     DATETIME2(0) NULL,
        CONSTRAINT UQ_bs_Reviews_user_order_product UNIQUE (user_id, order_id, product_id),
        CONSTRAINT CK_bs_Reviews_rating CHECK (rating BETWEEN 1 AND 5),
        CONSTRAINT CK_bs_Reviews_status CHECK (status IN ('Visible', 'Hidden'))
    );
END
GO

IF OBJECT_ID(N'dbo.bs_AdminAuditLogs', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_AdminAuditLogs (
        audit_id       BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_AdminAuditLogs PRIMARY KEY,
        admin_id       INT NOT NULL,
        action         NVARCHAR(80) NOT NULL,
        entity_type    NVARCHAR(80) NOT NULL,
        entity_id      NVARCHAR(80) NULL,
        details        NVARCHAR(1000) NULL,
        created_at     DATETIME2(0) NOT NULL CONSTRAINT DF_bs_AdminAuditLogs_created_at DEFAULT SYSUTCDATETIME()
    );
END
GO

IF OBJECT_ID(N'dbo.bs_StockReceipts', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_StockReceipts (
        receipt_id    BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_StockReceipts PRIMARY KEY,
        product_id    INT NOT NULL,
        quantity      INT NOT NULL,
        previous_stock INT NOT NULL,
        resulting_stock INT NOT NULL,
        note          NVARCHAR(500) NULL,
        admin_id      INT NOT NULL,
        created_at    DATETIME2(0) NOT NULL CONSTRAINT DF_bs_StockReceipts_created_at DEFAULT SYSUTCDATETIME(),
        CONSTRAINT CK_bs_StockReceipts_quantity CHECK (quantity > 0),
        CONSTRAINT FK_bs_StockReceipts_product FOREIGN KEY (product_id) REFERENCES dbo.bs_Products(product_id),
        CONSTRAINT FK_bs_StockReceipts_admin FOREIGN KEY (admin_id) REFERENCES dbo.bs_user(user_id)
    );
END
GO

/* Compatibility alias for the duplicate RDS name */
IF NOT EXISTS (SELECT 1 FROM sys.synonyms WHERE name = N'bs_Review' AND schema_id = SCHEMA_ID(N'dbo'))
BEGIN
    CREATE SYNONYM dbo.bs_Review FOR dbo.bs_Reviews;
END
GO

/* =========================
   Foreign Keys
   ========================= */

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_user_bs_Roles')
    ALTER TABLE dbo.bs_user
    ADD CONSTRAINT FK_bs_user_bs_Roles
    FOREIGN KEY (role_id) REFERENCES dbo.bs_Roles(role_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_Addresses_bs_user')
    ALTER TABLE dbo.bs_Addresses
    ADD CONSTRAINT FK_bs_Addresses_bs_user
    FOREIGN KEY (user_id) REFERENCES dbo.bs_user(user_id) ON DELETE CASCADE;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_Products_bs_Categories')
    ALTER TABLE dbo.bs_Products
    ADD CONSTRAINT FK_bs_Products_bs_Categories
    FOREIGN KEY (category_id) REFERENCES dbo.bs_Categories(category_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_Products_bs_Brands')
    ALTER TABLE dbo.bs_Products
    ADD CONSTRAINT FK_bs_Products_bs_Brands
    FOREIGN KEY (brand_id) REFERENCES dbo.bs_Brands(brand_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_ProductImages_bs_Products')
    ALTER TABLE dbo.bs_ProductImages
    ADD CONSTRAINT FK_bs_ProductImages_bs_Products
    FOREIGN KEY (product_id) REFERENCES dbo.bs_Products(product_id) ON DELETE CASCADE;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_ProductSpecifications_bs_Products')
    ALTER TABLE dbo.bs_ProductSpecifications
    ADD CONSTRAINT FK_bs_ProductSpecifications_bs_Products
    FOREIGN KEY (product_id) REFERENCES dbo.bs_Products(product_id) ON DELETE CASCADE;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_Cart_bs_user')
    ALTER TABLE dbo.bs_Cart
    ADD CONSTRAINT FK_bs_Cart_bs_user
    FOREIGN KEY (user_id) REFERENCES dbo.bs_user(user_id) ON DELETE CASCADE;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_CartItems_bs_Cart')
    ALTER TABLE dbo.bs_CartItems
    ADD CONSTRAINT FK_bs_CartItems_bs_Cart
    FOREIGN KEY (cart_id) REFERENCES dbo.bs_Cart(cart_id) ON DELETE CASCADE;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_CartItems_bs_Products')
    ALTER TABLE dbo.bs_CartItems
    ADD CONSTRAINT FK_bs_CartItems_bs_Products
    FOREIGN KEY (product_id) REFERENCES dbo.bs_Products(product_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_Orders_bs_user')
    ALTER TABLE dbo.bs_Orders
    ADD CONSTRAINT FK_bs_Orders_bs_user
    FOREIGN KEY (user_id) REFERENCES dbo.bs_user(user_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_Orders_bs_Vouchers')
    ALTER TABLE dbo.bs_Orders
    ADD CONSTRAINT FK_bs_Orders_bs_Vouchers
    FOREIGN KEY (voucher_id) REFERENCES dbo.bs_Vouchers(voucher_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_OrderDetails_bs_Orders')
    ALTER TABLE dbo.bs_OrderDetails
    ADD CONSTRAINT FK_bs_OrderDetails_bs_Orders
    FOREIGN KEY (order_id) REFERENCES dbo.bs_Orders(order_id) ON DELETE CASCADE;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_OrderDetails_bs_Products')
    ALTER TABLE dbo.bs_OrderDetails
    ADD CONSTRAINT FK_bs_OrderDetails_bs_Products
    FOREIGN KEY (product_id) REFERENCES dbo.bs_Products(product_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_Payments_bs_Orders')
    ALTER TABLE dbo.bs_Payments
    ADD CONSTRAINT FK_bs_Payments_bs_Orders
    FOREIGN KEY (order_id) REFERENCES dbo.bs_Orders(order_id) ON DELETE CASCADE;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_Reviews_bs_user')
    ALTER TABLE dbo.bs_Reviews
    ADD CONSTRAINT FK_bs_Reviews_bs_user
    FOREIGN KEY (user_id) REFERENCES dbo.bs_user(user_id) ON DELETE CASCADE;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_Reviews_bs_Orders')
    ALTER TABLE dbo.bs_Reviews
    ADD CONSTRAINT FK_bs_Reviews_bs_Orders
    FOREIGN KEY (order_id) REFERENCES dbo.bs_Orders(order_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_Reviews_bs_Products')
    ALTER TABLE dbo.bs_Reviews
    ADD CONSTRAINT FK_bs_Reviews_bs_Products
    FOREIGN KEY (product_id) REFERENCES dbo.bs_Products(product_id) ON DELETE CASCADE;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_Reviews_moderated_by')
    ALTER TABLE dbo.bs_Reviews
    ADD CONSTRAINT FK_bs_Reviews_moderated_by
    FOREIGN KEY (moderated_by) REFERENCES dbo.bs_user(user_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_AdminAuditLogs_user')
    ALTER TABLE dbo.bs_AdminAuditLogs
    ADD CONSTRAINT FK_bs_AdminAuditLogs_user
    FOREIGN KEY (admin_id) REFERENCES dbo.bs_user(user_id);
GO

/* =========================
   Helpful Indexes
   ========================= */

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_bs_Products_name' AND object_id = OBJECT_ID(N'dbo.bs_Products'))
    CREATE INDEX IX_bs_Products_name ON dbo.bs_Products(product_name);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_bs_Orders_user_created' AND object_id = OBJECT_ID(N'dbo.bs_Orders'))
    CREATE INDEX IX_bs_Orders_user_created ON dbo.bs_Orders(user_id, created_at DESC);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_bs_OrderDetails_order' AND object_id = OBJECT_ID(N'dbo.bs_OrderDetails'))
    CREATE INDEX IX_bs_OrderDetails_order ON dbo.bs_OrderDetails(order_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_bs_Orders_status_created' AND object_id = OBJECT_ID(N'dbo.bs_Orders'))
    CREATE INDEX IX_bs_Orders_status_created ON dbo.bs_Orders(order_status, created_at DESC);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_bs_Reviews_status_created' AND object_id = OBJECT_ID(N'dbo.bs_Reviews'))
    CREATE INDEX IX_bs_Reviews_status_created ON dbo.bs_Reviews(status, created_at DESC);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_bs_AdminAuditLogs_created_at' AND object_id = OBJECT_ID(N'dbo.bs_AdminAuditLogs'))
    CREATE INDEX IX_bs_AdminAuditLogs_created_at ON dbo.bs_AdminAuditLogs(created_at DESC);
GO

PRINT 'LaptopWebsiteDB schema created successfully.';
GO

/* =========================
   FixNavbar-UpdateDB-UpdateProductBackend - Part 1
   ========================= */
/*
    01_create_catalog_support_tables.sql
    Purpose: create category navigation/filter support tables used by the product catalog.

    Safety notes:
    - The script runs inside a transaction. If an error happens, SQL Server rolls back.

    Run this before 02_seed_repository_data.sql.
*/

SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    IF OBJECT_ID(N'dbo.bs_CategoryMenuGroups', N'U') IS NULL
    BEGIN
        CREATE TABLE dbo.bs_CategoryMenuGroups (
            menu_group_id INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_CategoryMenuGroups PRIMARY KEY,
            category_id INT NOT NULL,
            group_title NVARCHAR(120) NOT NULL,
            sort_order INT NOT NULL,
            created_at DATETIME2(0) NOT NULL CONSTRAINT DF_bs_CategoryMenuGroups_created_at DEFAULT (SYSDATETIME()),
            updated_at DATETIME2(0) NULL
        );
    END;

    IF OBJECT_ID(N'dbo.bs_CategoryMenuOptions', N'U') IS NULL
    BEGIN
        CREATE TABLE dbo.bs_CategoryMenuOptions (
            menu_option_id INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_CategoryMenuOptions PRIMARY KEY,
            menu_group_id INT NOT NULL,
            option_value NVARCHAR(250) NOT NULL,
            sort_order INT NOT NULL,
            created_at DATETIME2(0) NOT NULL CONSTRAINT DF_bs_CategoryMenuOptions_created_at DEFAULT (SYSDATETIME()),
            updated_at DATETIME2(0) NULL
        );
    END;

    IF OBJECT_ID(N'dbo.bs_CategoryFilters', N'U') IS NULL
    BEGIN
        CREATE TABLE dbo.bs_CategoryFilters (
            category_filter_id INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_CategoryFilters PRIMARY KEY,
            category_id INT NOT NULL,
            filter_key NVARCHAR(80) NOT NULL,
            filter_label NVARCHAR(120) NOT NULL,
            sort_order INT NOT NULL,
            created_at DATETIME2(0) NOT NULL CONSTRAINT DF_bs_CategoryFilters_created_at DEFAULT (SYSDATETIME()),
            updated_at DATETIME2(0) NULL
        );
    END;

    IF OBJECT_ID(N'dbo.bs_CategoryFilterOptions', N'U') IS NULL
    BEGIN
        CREATE TABLE dbo.bs_CategoryFilterOptions (
            filter_option_id INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_CategoryFilterOptions PRIMARY KEY,
            category_filter_id INT NOT NULL,
            option_value NVARCHAR(250) NOT NULL,
            sort_order INT NOT NULL,
            created_at DATETIME2(0) NOT NULL CONSTRAINT DF_bs_CategoryFilterOptions_created_at DEFAULT (SYSDATETIME()),
            updated_at DATETIME2(0) NULL
        );
    END;

    IF OBJECT_ID(N'dbo.bs_Categories', N'U') IS NOT NULL
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_CategoryMenuGroups_bs_Categories')
            ALTER TABLE dbo.bs_CategoryMenuGroups WITH CHECK ADD CONSTRAINT FK_bs_CategoryMenuGroups_bs_Categories
                FOREIGN KEY(category_id) REFERENCES dbo.bs_Categories(category_id) ON DELETE CASCADE;

        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_CategoryFilters_bs_Categories')
            ALTER TABLE dbo.bs_CategoryFilters WITH CHECK ADD CONSTRAINT FK_bs_CategoryFilters_bs_Categories
                FOREIGN KEY(category_id) REFERENCES dbo.bs_Categories(category_id) ON DELETE CASCADE;
    END;

    IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_CategoryMenuOptions_bs_CategoryMenuGroups')
    BEGIN
        ALTER TABLE dbo.bs_CategoryMenuOptions WITH CHECK ADD CONSTRAINT FK_bs_CategoryMenuOptions_bs_CategoryMenuGroups
            FOREIGN KEY(menu_group_id) REFERENCES dbo.bs_CategoryMenuGroups(menu_group_id) ON DELETE CASCADE;
    END;

    IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_CategoryFilterOptions_bs_CategoryFilters')
    BEGIN
        ALTER TABLE dbo.bs_CategoryFilterOptions WITH CHECK ADD CONSTRAINT FK_bs_CategoryFilterOptions_bs_CategoryFilters
            FOREIGN KEY(category_filter_id) REFERENCES dbo.bs_CategoryFilters(category_filter_id) ON DELETE CASCADE;
    END;

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;

    THROW;
END CATCH;

/* =========================
   FixNavbar-UpdateDB-UpdateProductBackend - Part 2
   ========================= */
   /*
    02_seed_repository_data.sql
    Purpose: seed SQL Server with all data currently hardcoded in
    CategoryRepository.java and ProductRepository.java.

    Run after 01_create_catalog_support_tables.sql.
    The script is idempotent for category, brand, product, image, and product spec rows.
*/

SET XACT_ABORT ON;
BEGIN TRANSACTION;

DECLARE @Categories TABLE (category_name NVARCHAR(120) NOT NULL, description NVARCHAR(500) NOT NULL, sort_order INT NOT NULL);
INSERT INTO @Categories (category_name, description, sort_order) VALUES
    (N'Laptops', N'Portable computers for business, gaming, students, and workstation users.', 1),
    (N'Mouse', N'Computer mice for gaming, office, wireless, and ergonomic use.', 2),
    (N'Keyboards', N'Mechanical and wireless keyboards for gaming, office, and compact setups.', 3),
    (N'Monitors', N'Displays for gaming, design, office, and ultrawide workspaces.', 4),
    (N'SSD', N'Solid-state drives for boot drives, gaming, creators, and storage upgrades.', 5),
    (N'RAM', N'Memory kits for laptop upgrades, gaming PCs, workstations, and RGB builds.', 6),
    (N'CPU', N'Desktop processors for gaming, office, streaming, and workstation computers.', 7),
    (N'GPU', N'Graphics cards for gaming, creator workloads, and AI work.', 8),
    (N'PC Case', N'Computer cases for compact, airflow, showcase, and silent builds.', 9),
    (N'Mainboard', N'Motherboards for Intel and AMD desktop builds.', 10),
    (N'PC Fan', N'Case and radiator fans for silent, RGB, and airflow-focused cooling.', 11);

MERGE dbo.bs_Categories AS target
USING @Categories AS source
    ON target.category_name = source.category_name
WHEN MATCHED THEN
    UPDATE SET description = source.description, status = 'Active', updated_at = SYSDATETIME()
WHEN NOT MATCHED THEN
    INSERT (category_name, description, status, created_at, updated_at)
    VALUES (source.category_name, source.description, 'Active', SYSDATETIME(), SYSDATETIME());

DECLARE @Brands TABLE (brand_name NVARCHAR(120) NOT NULL);
INSERT INTO @Brands (brand_name) VALUES
    (N'Dell'),
    (N'ASUS'),
    (N'Lenovo'),
    (N'HP'),
    (N'Acer'),
    (N'Logitech'),
    (N'Razer'),
    (N'SteelSeries'),
    (N'Corsair'),
    (N'Keychron'),
    (N'Akko'),
    (N'LG'),
    (N'Samsung'),
    (N'AOC'),
    (N'WD'),
    (N'Kingston'),
    (N'Crucial'),
    (N'Seagate'),
    (N'G.Skill'),
    (N'TeamGroup'),
    (N'Intel'),
    (N'AMD'),
    (N'MSI'),
    (N'Gigabyte'),
    (N'Sapphire'),
    (N'Zotac'),
    (N'NZXT'),
    (N'Lian Li'),
    (N'Cooler Master'),
    (N'DeepCool'),
    (N'ASRock'),
    (N'Biostar'),
    (N'Noctua'),
    (N'Arctic'),
    (N'Apple'),
    (N'Microsoft Surface'),
    (N'Glorious'),
    (N'Pulsar'),
    (N'Zowie'),
    (N'Ducky'),
    (N'Leopold'),
    (N'BenQ'),
    (N'Lexar'),
    (N'ADATA'),
    (N'Patriot'),
    (N'PNY'),
    (N'PowerColor'),
    (N'Galax'),
    (N'Fractal Design'),
    (N'Phanteks'),
    (N'Thermaltake');

MERGE dbo.bs_Brands AS target
USING @Brands AS source
    ON target.brand_name = source.brand_name
WHEN MATCHED THEN
    UPDATE SET updated_at = SYSDATETIME()
WHEN NOT MATCHED THEN
    INSERT (brand_name, logo, created_at, updated_at)
    VALUES (source.brand_name, NULL, SYSDATETIME(), SYSDATETIME());

DECLARE @Products TABLE (category_name NVARCHAR(120), brand_name NVARCHAR(120), sku NVARCHAR(80), product_name NVARCHAR(250), description NVARCHAR(MAX), price DECIMAL(18,2), stock INT, thumbnail NVARCHAR(1000), status NVARCHAR(50));
INSERT INTO @Products (category_name, brand_name, sku, product_name, description, price, stock, thumbnail, status) VALUES
    (N'Laptops', N'Dell', N'LAPTOPS-0001', N'Dell XPS 13 Plus', N'Dell XPS 13 Plus is a top seller laptops option for business users.', 32990000, 6, N'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Laptops', N'ASUS', N'LAPTOPS-0002', N'ASUS ROG Zephyrus G14', N'ASUS ROG Zephyrus G14 is a gaming pick laptops option for gaming users.', 42990000, 7, N'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Laptops', N'Lenovo', N'LAPTOPS-0003', N'Lenovo ThinkPad P16', N'Lenovo ThinkPad P16 is a workstation laptops option for workstation users.', 46990000, 8, N'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Laptops', N'HP', N'LAPTOPS-0004', N'HP Pavilion 15', N'HP Pavilion 15 is a best value laptops option for student users.', 16990000, 9, N'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Laptops', N'Acer', N'LAPTOPS-0005', N'Acer Swift Go 14', N'Acer Swift Go 14 is a oled value laptops option for student users.', 21990000, 10, N'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Mouse', N'Logitech', N'MOUSE-0006', N'Logitech G Pro X Superlight 2', N'Logitech G Pro X Superlight 2 is a esports mouse option for gaming users.', 3290000, 11, N'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Mouse', N'Razer', N'MOUSE-0007', N'Razer DeathAdder V3 Pro', N'Razer DeathAdder V3 Pro is a ergonomic mouse option for gaming users.', 3490000, 12, N'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Mouse', N'SteelSeries', N'MOUSE-0008', N'SteelSeries Aerox 5 Wireless', N'SteelSeries Aerox 5 Wireless is a lightweight mouse option for wireless users.', 2490000, 13, N'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Mouse', N'Corsair', N'MOUSE-0009', N'Corsair Katar Elite Wireless', N'Corsair Katar Elite Wireless is a compact mouse option for office users.', 1590000, 0, N'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Mouse', N'ASUS', N'MOUSE-0010', N'ASUS ROG Keris AimPoint', N'ASUS ROG Keris AimPoint is a fps ready mouse option for gaming users.', 2190000, 15, N'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Keyboards', N'Keychron', N'KEYBOARDS-0011', N'Keychron K2 Pro', N'Keychron K2 Pro is a wireless keyboards option for office users.', 2490000, 16, N'https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Keyboards', N'Logitech', N'KEYBOARDS-0012', N'Logitech G Pro X TKL', N'Logitech G Pro X TKL is a tournament keyboards option for gaming users.', 3290000, 5, N'https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Keyboards', N'Razer', N'KEYBOARDS-0013', N'Razer Huntsman Mini', N'Razer Huntsman Mini is a compact keyboards option for gaming users.', 2590000, 6, N'https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Keyboards', N'Akko', N'KEYBOARDS-0014', N'Akko 5075B Plus', N'Akko 5075B Plus is a hot-swap keyboards option for compact users.', 1990000, 7, N'https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Keyboards', N'Corsair', N'KEYBOARDS-0015', N'Corsair K70 RGB Pro', N'Corsair K70 RGB Pro is a full-size keyboards option for gaming users.', 3490000, 8, N'https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Monitors', N'LG', N'MONITORS-0016', N'LG UltraGear 27GP850', N'LG UltraGear 27GP850 is a qhd gaming monitors option for gaming users.', 7290000, 9, N'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Monitors', N'Samsung', N'MONITORS-0017', N'Samsung Odyssey G5 32', N'Samsung Odyssey G5 32 is a curved monitors option for gaming users.', 6790000, 10, N'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Monitors', N'Dell', N'MONITORS-0018', N'Dell UltraSharp U2723QE', N'Dell UltraSharp U2723QE is a creator monitors option for design users.', 12990000, 0, N'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Monitors', N'ASUS', N'MONITORS-0019', N'ASUS ProArt PA278QV', N'ASUS ProArt PA278QV is a color work monitors option for design users.', 6990000, 12, N'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Monitors', N'AOC', N'MONITORS-0020', N'AOC 24G2SP', N'AOC 24G2SP is a budget gaming monitors option for gaming users.', 3990000, 13, N'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'SSD', N'Samsung', N'SSD-0021', N'Samsung 990 Pro 1TB', N'Samsung 990 Pro 1TB is a pcie 4.0 ssd option for gaming users.', 2890000, 14, N'https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'SSD', N'WD', N'SSD-0022', N'WD Black SN850X 2TB', N'WD Black SN850X 2TB is a high speed ssd option for creator users.', 4590000, 15, N'https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'SSD', N'Kingston', N'SSD-0023', N'Kingston NV2 1TB', N'Kingston NV2 1TB is a budget nvme ssd option for boot drive users.', 1490000, 16, N'https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'SSD', N'Crucial', N'SSD-0024', N'Crucial MX500 1TB', N'Crucial MX500 1TB is a sata reliable ssd option for boot drive users.', 1690000, 5, N'https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'SSD', N'Seagate', N'SSD-0025', N'Seagate FireCuda 540 2TB', N'Seagate FireCuda 540 2TB is a pcie 5.0 ssd option for creator users.', 6290000, 6, N'https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'RAM', N'Corsair', N'RAM-0026', N'Corsair Vengeance DDR5 32GB', N'Corsair Vengeance DDR5 32GB is a ddr5 kit ram option for gaming pc users.', 2990000, 7, N'https://images.unsplash.com/photo-1562976540-1502c2145186?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'RAM', N'Kingston', N'RAM-0027', N'Kingston Fury Beast 16GB', N'Kingston Fury Beast 16GB is a gaming value ram option for gaming pc users.', 1290000, 0, N'https://images.unsplash.com/photo-1562976540-1502c2145186?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'RAM', N'G.Skill', N'RAM-0028', N'G.Skill Trident Z5 RGB 32GB', N'G.Skill Trident Z5 RGB 32GB is a rgb build ram option for rgb build users.', 3490000, 9, N'https://images.unsplash.com/photo-1562976540-1502c2145186?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'RAM', N'Crucial', N'RAM-0029', N'Crucial Laptop DDR4 16GB', N'Crucial Laptop DDR4 16GB is a laptop upgrade ram option for laptop upgrade users.', 990000, 10, N'https://images.unsplash.com/photo-1562976540-1502c2145186?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'RAM', N'TeamGroup', N'RAM-0030', N'TeamGroup Elite DDR5 16GB', N'TeamGroup Elite DDR5 16GB is a ddr5 value ram option for laptop upgrade users.', 1490000, 11, N'https://images.unsplash.com/photo-1562976540-1502c2145186?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'CPU', N'Intel', N'CPU-0031', N'Intel Core i5-14600K', N'Intel Core i5-14600K is a gaming value cpu option for gaming users.', 7890000, 12, N'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'CPU', N'Intel', N'CPU-0032', N'Intel Core i7-14700K', N'Intel Core i7-14700K is a creator pick cpu option for streaming users.', 11290000, 13, N'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'CPU', N'AMD', N'CPU-0033', N'AMD Ryzen 5 7600', N'AMD Ryzen 5 7600 is a am5 value cpu option for office users.', 4990000, 14, N'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'CPU', N'AMD', N'CPU-0034', N'AMD Ryzen 7 7800X3D', N'AMD Ryzen 7 7800X3D is a gaming king cpu option for gaming users.', 9690000, 15, N'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'CPU', N'AMD', N'CPU-0035', N'AMD Ryzen 9 7950X', N'AMD Ryzen 9 7950X is a workstation cpu option for workstation users.', 13990000, 16, N'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'GPU', N'ASUS', N'GPU-0036', N'ASUS Dual RTX 4060 OC', N'ASUS Dual RTX 4060 OC is a 1080p gpu option for 1080p gaming users.', 7990000, 0, N'https://images.unsplash.com/photo-1591488320449-011701bb6704?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'GPU', N'MSI', N'GPU-0037', N'MSI RTX 4070 Super Gaming X', N'MSI RTX 4070 Super Gaming X is a 1440p gpu option for 1440p gaming users.', 17990000, 6, N'https://images.unsplash.com/photo-1591488320449-011701bb6704?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'GPU', N'Gigabyte', N'GPU-0038', N'Gigabyte RTX 4080 Super Aero', N'Gigabyte RTX 4080 Super Aero is a 4k gpu option for 4k gaming users.', 31990000, 7, N'https://images.unsplash.com/photo-1591488320449-011701bb6704?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'GPU', N'Sapphire', N'GPU-0039', N'Sapphire Pulse RX 7800 XT', N'Sapphire Pulse RX 7800 XT is a radeon value gpu option for 1440p gaming users.', 13990000, 8, N'https://images.unsplash.com/photo-1591488320449-011701bb6704?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'GPU', N'Zotac', N'GPU-0040', N'Zotac RTX 4090 Trinity', N'Zotac RTX 4090 Trinity is a ai work gpu option for ai work users.', 46990000, 9, N'https://images.unsplash.com/photo-1591488320449-011701bb6704?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'PC Case', N'NZXT', N'PCCASE-0041', N'NZXT H5 Flow', N'NZXT H5 Flow is a airflow pc case option for airflow users.', 2190000, 10, N'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'PC Case', N'Corsair', N'PCCASE-0042', N'Corsair 4000D Airflow', N'Corsair 4000D Airflow is a clean build pc case option for airflow users.', 2490000, 11, N'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'PC Case', N'Lian Li', N'PCCASE-0043', N'Lian Li O11 Dynamic EVO', N'Lian Li O11 Dynamic EVO is a showcase pc case option for showcase users.', 3990000, 12, N'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'PC Case', N'Cooler Master', N'PCCASE-0044', N'Cooler Master NR200P', N'Cooler Master NR200P is a mini itx pc case option for compact users.', 2390000, 13, N'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'PC Case', N'DeepCool', N'PCCASE-0045', N'DeepCool CH560 Digital', N'DeepCool CH560 Digital is a display panel pc case option for showcase users.', 2890000, 0, N'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Mainboard', N'ASUS', N'MAINBOARD-0046', N'ASUS TUF Gaming B650-Plus', N'ASUS TUF Gaming B650-Plus is a am5 durable mainboard option for gaming users.', 5290000, 15, N'https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Mainboard', N'MSI', N'MAINBOARD-0047', N'MSI MAG B760 Tomahawk WiFi', N'MSI MAG B760 Tomahawk WiFi is a intel value mainboard option for gaming users.', 4890000, 16, N'https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Mainboard', N'Gigabyte', N'MAINBOARD-0048', N'Gigabyte Z790 Aorus Elite AX', N'Gigabyte Z790 Aorus Elite AX is a overclock mainboard option for overclocking users.', 7190000, 5, N'https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Mainboard', N'ASRock', N'MAINBOARD-0049', N'ASRock B550M Steel Legend', N'ASRock B550M Steel Legend is a am4 value mainboard option for budget pc users.', 2890000, 6, N'https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'Mainboard', N'Biostar', N'MAINBOARD-0050', N'Biostar B650MT', N'Biostar B650MT is a budget am5 mainboard option for budget pc users.', 3290000, 7, N'https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'PC Fan', N'Noctua', N'PCFAN-0051', N'Noctua NF-A12x25 PWM', N'Noctua NF-A12x25 PWM is a silent pc fan option for silent users.', 890000, 8, N'https://images.unsplash.com/photo-1624705002806-5d72df19c3ad?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'PC Fan', N'Corsair', N'PCFAN-0052', N'Corsair iCUE AF120 RGB Elite', N'Corsair iCUE AF120 RGB Elite is a rgb pc fan option for rgb users.', 690000, 9, N'https://images.unsplash.com/photo-1624705002806-5d72df19c3ad?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'PC Fan', N'Cooler Master', N'PCFAN-0053', N'Cooler Master SickleFlow 120', N'Cooler Master SickleFlow 120 is a budget rgb pc fan option for rgb users.', 290000, 10, N'https://images.unsplash.com/photo-1624705002806-5d72df19c3ad?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'PC Fan', N'DeepCool', N'PCFAN-0054', N'DeepCool FK120', N'DeepCool FK120 is a radiator pc fan option for radiator users.', 350000, 0, N'https://images.unsplash.com/photo-1624705002806-5d72df19c3ad?auto=format&fit=crop&w=900&q=80', N'active'),
    (N'PC Fan', N'Arctic', N'PCFAN-0055', N'Arctic P14 PWM PST', N'Arctic P14 PWM PST is a 140mm airflow pc fan option for high airflow users.', 390000, 12, N'https://images.unsplash.com/photo-1624705002806-5d72df19c3ad?auto=format&fit=crop&w=900&q=80', N'active');

MERGE dbo.bs_Products AS target
USING (
    SELECT c.category_id, b.brand_id, p.sku, p.product_name, p.description, p.price, p.stock, p.thumbnail,
           CASE LOWER(p.status)
               WHEN 'active' THEN N'Active'
               WHEN 'out of stock' THEN N'Out of Stock'
               WHEN 'hidden' THEN N'Hidden'
               WHEN 'inactive' THEN N'Inactive'
               ELSE p.status
           END AS status
    FROM @Products p
    INNER JOIN dbo.bs_Categories c ON c.category_name = p.category_name
    INNER JOIN dbo.bs_Brands b ON b.brand_name = p.brand_name
) AS source
    ON target.sku = source.sku
WHEN MATCHED THEN
    UPDATE SET category_id = source.category_id, brand_id = source.brand_id, product_name = source.product_name,
               description = source.description, price = source.price, stock = source.stock, thumbnail = source.thumbnail,
               status = source.status, updated_at = SYSDATETIME()
WHEN NOT MATCHED THEN
    INSERT (category_id, brand_id, sku, product_name, description, price, stock, thumbnail, status, created_at, updated_at)
    VALUES (source.category_id, source.brand_id, source.sku, source.product_name, source.description, source.price, source.stock, source.thumbnail, source.status, SYSDATETIME(), SYSDATETIME());

DECLARE @Images TABLE (sku NVARCHAR(80), image_url NVARCHAR(1000), is_primary BIT, sort_order INT);
INSERT INTO @Images (sku, image_url, is_primary, sort_order) VALUES
    (N'LAPTOPS-0001', N'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'LAPTOPS-0002', N'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'LAPTOPS-0003', N'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'LAPTOPS-0004', N'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'LAPTOPS-0005', N'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'MOUSE-0006', N'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'MOUSE-0007', N'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'MOUSE-0008', N'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'MOUSE-0009', N'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'MOUSE-0010', N'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'KEYBOARDS-0011', N'https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'KEYBOARDS-0012', N'https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'KEYBOARDS-0013', N'https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'KEYBOARDS-0014', N'https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'KEYBOARDS-0015', N'https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'MONITORS-0016', N'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'MONITORS-0017', N'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'MONITORS-0018', N'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'MONITORS-0019', N'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'MONITORS-0020', N'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'SSD-0021', N'https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'SSD-0022', N'https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'SSD-0023', N'https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'SSD-0024', N'https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'SSD-0025', N'https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'RAM-0026', N'https://images.unsplash.com/photo-1562976540-1502c2145186?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'RAM-0027', N'https://images.unsplash.com/photo-1562976540-1502c2145186?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'RAM-0028', N'https://images.unsplash.com/photo-1562976540-1502c2145186?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'RAM-0029', N'https://images.unsplash.com/photo-1562976540-1502c2145186?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'RAM-0030', N'https://images.unsplash.com/photo-1562976540-1502c2145186?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'CPU-0031', N'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'CPU-0032', N'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'CPU-0033', N'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'CPU-0034', N'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'CPU-0035', N'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'GPU-0036', N'https://images.unsplash.com/photo-1591488320449-011701bb6704?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'GPU-0037', N'https://images.unsplash.com/photo-1591488320449-011701bb6704?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'GPU-0038', N'https://images.unsplash.com/photo-1591488320449-011701bb6704?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'GPU-0039', N'https://images.unsplash.com/photo-1591488320449-011701bb6704?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'GPU-0040', N'https://images.unsplash.com/photo-1591488320449-011701bb6704?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'PCCASE-0041', N'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'PCCASE-0042', N'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'PCCASE-0043', N'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'PCCASE-0044', N'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'PCCASE-0045', N'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'MAINBOARD-0046', N'https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'MAINBOARD-0047', N'https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'MAINBOARD-0048', N'https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'MAINBOARD-0049', N'https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'MAINBOARD-0050', N'https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'PCFAN-0051', N'https://images.unsplash.com/photo-1624705002806-5d72df19c3ad?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'PCFAN-0052', N'https://images.unsplash.com/photo-1624705002806-5d72df19c3ad?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'PCFAN-0053', N'https://images.unsplash.com/photo-1624705002806-5d72df19c3ad?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'PCFAN-0054', N'https://images.unsplash.com/photo-1624705002806-5d72df19c3ad?auto=format&fit=crop&w=900&q=80', 1, 1),
    (N'PCFAN-0055', N'https://images.unsplash.com/photo-1624705002806-5d72df19c3ad?auto=format&fit=crop&w=900&q=80', 1, 1);

MERGE dbo.bs_ProductImages AS target
USING (
    SELECT p.product_id, i.image_url, i.is_primary, i.sort_order
    FROM @Images i
    INNER JOIN dbo.bs_Products p ON p.sku = i.sku
) AS source
    ON target.product_id = source.product_id AND target.image_url = source.image_url
WHEN MATCHED THEN
    UPDATE SET is_primary = source.is_primary, sort_order = source.sort_order
WHEN NOT MATCHED THEN
    INSERT (product_id, image_url, is_primary, sort_order)
    VALUES (source.product_id, source.image_url, source.is_primary, source.sort_order);

DECLARE @Specs TABLE (sku NVARCHAR(80), spec_key NVARCHAR(80), spec_label NVARCHAR(120), spec_value NVARCHAR(500), sort_order INT);
INSERT INTO @Specs (sku, spec_key, spec_label, spec_value, sort_order) VALUES
    (N'LAPTOPS-0001', N'purpose', N'Purpose', N'Business', 1),
    (N'LAPTOPS-0001', N'cpu', N'CPU', N'Intel Core Ultra 7', 2),
    (N'LAPTOPS-0001', N'gpu', N'GPU', N'Integrated', 3),
    (N'LAPTOPS-0001', N'ram', N'RAM', N'16GB', 4),
    (N'LAPTOPS-0001', N'storage', N'Storage', N'1TB SSD', 5),
    (N'LAPTOPS-0001', N'display', N'Display', N'13.4 inch 3.5K OLED', 6),
    (N'LAPTOPS-0001', N'battery', N'Battery', N'55Wh', 7),
    (N'LAPTOPS-0001', N'badge', N'Badge', N'Top seller', 8),
    (N'LAPTOPS-0001', N'warranty', N'Warranty', N'24 months official warranty', 9),
    (N'LAPTOPS-0002', N'purpose', N'Purpose', N'Gaming', 1),
    (N'LAPTOPS-0002', N'cpu', N'CPU', N'AMD Ryzen 9', 2),
    (N'LAPTOPS-0002', N'gpu', N'GPU', N'RTX 4070', 3),
    (N'LAPTOPS-0002', N'ram', N'RAM', N'32GB', 4),
    (N'LAPTOPS-0002', N'storage', N'Storage', N'1TB SSD', 5),
    (N'LAPTOPS-0002', N'display', N'Display', N'14 inch QHD 165Hz', 6),
    (N'LAPTOPS-0002', N'battery', N'Battery', N'76Wh', 7),
    (N'LAPTOPS-0002', N'badge', N'Badge', N'Gaming pick', 8),
    (N'LAPTOPS-0002', N'warranty', N'Warranty', N'24 months official warranty', 9),
    (N'LAPTOPS-0003', N'purpose', N'Purpose', N'Workstation', 1),
    (N'LAPTOPS-0003', N'cpu', N'CPU', N'Intel Core i7', 2),
    (N'LAPTOPS-0003', N'gpu', N'GPU', N'RTX 4060', 3),
    (N'LAPTOPS-0003', N'ram', N'RAM', N'32GB', 4),
    (N'LAPTOPS-0003', N'storage', N'Storage', N'2TB SSD', 5),
    (N'LAPTOPS-0003', N'display', N'Display', N'16 inch OLED', 6),
    (N'LAPTOPS-0003', N'battery', N'Battery', N'90Wh', 7),
    (N'LAPTOPS-0003', N'badge', N'Badge', N'Workstation', 8),
    (N'LAPTOPS-0003', N'warranty', N'Warranty', N'24 months official warranty', 9),
    (N'LAPTOPS-0004', N'purpose', N'Purpose', N'Student', 1),
    (N'LAPTOPS-0004', N'cpu', N'CPU', N'Intel Core i5', 2),
    (N'LAPTOPS-0004', N'gpu', N'GPU', N'Integrated', 3),
    (N'LAPTOPS-0004', N'ram', N'RAM', N'8GB', 4),
    (N'LAPTOPS-0004', N'storage', N'Storage', N'512GB SSD', 5),
    (N'LAPTOPS-0004', N'display', N'Display', N'15.6 inch FHD', 6),
    (N'LAPTOPS-0004', N'battery', N'Battery', N'41Wh', 7),
    (N'LAPTOPS-0004', N'badge', N'Badge', N'Best value', 8),
    (N'LAPTOPS-0004', N'warranty', N'Warranty', N'24 months official warranty', 9),
    (N'LAPTOPS-0005', N'purpose', N'Purpose', N'Student', 1),
    (N'LAPTOPS-0005', N'cpu', N'CPU', N'Intel Core Ultra 5', 2),
    (N'LAPTOPS-0005', N'gpu', N'GPU', N'Integrated', 3),
    (N'LAPTOPS-0005', N'ram', N'RAM', N'16GB', 4),
    (N'LAPTOPS-0005', N'storage', N'Storage', N'512GB SSD', 5),
    (N'LAPTOPS-0005', N'display', N'Display', N'14 inch 2.8K OLED', 6),
    (N'LAPTOPS-0005', N'battery', N'Battery', N'65Wh', 7),
    (N'LAPTOPS-0005', N'badge', N'Badge', N'OLED value', 8),
    (N'LAPTOPS-0005', N'warranty', N'Warranty', N'24 months official warranty', 9),
    (N'MOUSE-0006', N'purpose', N'Purpose', N'Gaming', 1),
    (N'MOUSE-0006', N'sensor', N'Sensor', N'Hero 25K', 2),
    (N'MOUSE-0006', N'dpi', N'DPI', N'32000 DPI', 3),
    (N'MOUSE-0006', N'connection', N'Connection', N'2.4GHz', 4),
    (N'MOUSE-0006', N'weight', N'Weight', N'60g', 5),
    (N'MOUSE-0006', N'badge', N'Badge', N'Esports', 6),
    (N'MOUSE-0006', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'MOUSE-0007', N'purpose', N'Purpose', N'Gaming', 1),
    (N'MOUSE-0007', N'sensor', N'Sensor', N'Focus Pro', 2),
    (N'MOUSE-0007', N'dpi', N'DPI', N'30000 DPI', 3),
    (N'MOUSE-0007', N'connection', N'Connection', N'2.4GHz', 4),
    (N'MOUSE-0007', N'weight', N'Weight', N'63g', 5),
    (N'MOUSE-0007', N'badge', N'Badge', N'Ergonomic', 6),
    (N'MOUSE-0007', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'MOUSE-0008', N'purpose', N'Purpose', N'Wireless', 1),
    (N'MOUSE-0008', N'sensor', N'Sensor', N'TrueMove Air', 2),
    (N'MOUSE-0008', N'dpi', N'DPI', N'18000 DPI', 3),
    (N'MOUSE-0008', N'connection', N'Connection', N'Bluetooth', 4),
    (N'MOUSE-0008', N'weight', N'Weight', N'74g', 5),
    (N'MOUSE-0008', N'badge', N'Badge', N'Lightweight', 6),
    (N'MOUSE-0008', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'MOUSE-0009', N'purpose', N'Purpose', N'Office', 1),
    (N'MOUSE-0009', N'sensor', N'Sensor', N'PixArt 3395', 2),
    (N'MOUSE-0009', N'dpi', N'DPI', N'26000 DPI', 3),
    (N'MOUSE-0009', N'connection', N'Connection', N'2.4GHz', 4),
    (N'MOUSE-0009', N'weight', N'Weight', N'69g', 5),
    (N'MOUSE-0009', N'badge', N'Badge', N'Compact', 6),
    (N'MOUSE-0009', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'MOUSE-0010', N'purpose', N'Purpose', N'Gaming', 1),
    (N'MOUSE-0010', N'sensor', N'Sensor', N'ROG AimPoint', 2),
    (N'MOUSE-0010', N'dpi', N'DPI', N'36000 DPI', 3),
    (N'MOUSE-0010', N'connection', N'Connection', N'USB-C wired', 4),
    (N'MOUSE-0010', N'weight', N'Weight', N'75g', 5),
    (N'MOUSE-0010', N'badge', N'Badge', N'FPS ready', 6),
    (N'MOUSE-0010', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'KEYBOARDS-0011', N'purpose', N'Purpose', N'Office', 1),
    (N'KEYBOARDS-0011', N'switchType', N'Switch', N'Brown', 2),
    (N'KEYBOARDS-0011', N'layout', N'Layout', N'75%', 3),
    (N'KEYBOARDS-0011', N'connection', N'Connection', N'Bluetooth', 4),
    (N'KEYBOARDS-0011', N'backlight', N'Backlight', N'RGB', 5),
    (N'KEYBOARDS-0011', N'badge', N'Badge', N'Wireless', 6),
    (N'KEYBOARDS-0011', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'KEYBOARDS-0012', N'purpose', N'Purpose', N'Gaming', 1),
    (N'KEYBOARDS-0012', N'switchType', N'Switch', N'Red', 2),
    (N'KEYBOARDS-0012', N'layout', N'Layout', N'TKL', 3),
    (N'KEYBOARDS-0012', N'connection', N'Connection', N'2.4GHz', 4),
    (N'KEYBOARDS-0012', N'backlight', N'Backlight', N'RGB', 5),
    (N'KEYBOARDS-0012', N'badge', N'Badge', N'Tournament', 6),
    (N'KEYBOARDS-0012', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'KEYBOARDS-0013', N'purpose', N'Purpose', N'Gaming', 1),
    (N'KEYBOARDS-0013', N'switchType', N'Switch', N'Optical', 2),
    (N'KEYBOARDS-0013', N'layout', N'Layout', N'60%', 3),
    (N'KEYBOARDS-0013', N'connection', N'Connection', N'USB-C wired', 4),
    (N'KEYBOARDS-0013', N'backlight', N'Backlight', N'RGB', 5),
    (N'KEYBOARDS-0013', N'badge', N'Badge', N'Compact', 6),
    (N'KEYBOARDS-0013', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'KEYBOARDS-0014', N'purpose', N'Purpose', N'Compact', 1),
    (N'KEYBOARDS-0014', N'switchType', N'Switch', N'Blue', 2),
    (N'KEYBOARDS-0014', N'layout', N'Layout', N'75%', 3),
    (N'KEYBOARDS-0014', N'connection', N'Connection', N'Bluetooth', 4),
    (N'KEYBOARDS-0014', N'backlight', N'Backlight', N'RGB', 5),
    (N'KEYBOARDS-0014', N'badge', N'Badge', N'Hot-swap', 6),
    (N'KEYBOARDS-0014', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'KEYBOARDS-0015', N'purpose', N'Purpose', N'Gaming', 1),
    (N'KEYBOARDS-0015', N'switchType', N'Switch', N'Red', 2),
    (N'KEYBOARDS-0015', N'layout', N'Layout', N'Full-size', 3),
    (N'KEYBOARDS-0015', N'connection', N'Connection', N'USB-C wired', 4),
    (N'KEYBOARDS-0015', N'backlight', N'Backlight', N'RGB', 5),
    (N'KEYBOARDS-0015', N'badge', N'Badge', N'Full-size', 6),
    (N'KEYBOARDS-0015', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'MONITORS-0016', N'purpose', N'Purpose', N'Gaming', 1),
    (N'MONITORS-0016', N'size', N'Size', N'27 inch', 2),
    (N'MONITORS-0016', N'resolution', N'Resolution', N'QHD', 3),
    (N'MONITORS-0016', N'refreshRate', N'Refresh Rate', N'165Hz', 4),
    (N'MONITORS-0016', N'panel', N'Panel', N'Nano IPS', 5),
    (N'MONITORS-0016', N'badge', N'Badge', N'QHD gaming', 6),
    (N'MONITORS-0016', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'MONITORS-0017', N'purpose', N'Purpose', N'Gaming', 1),
    (N'MONITORS-0017', N'size', N'Size', N'32 inch', 2),
    (N'MONITORS-0017', N'resolution', N'Resolution', N'QHD', 3),
    (N'MONITORS-0017', N'refreshRate', N'Refresh Rate', N'144Hz', 4),
    (N'MONITORS-0017', N'panel', N'Panel', N'VA', 5),
    (N'MONITORS-0017', N'badge', N'Badge', N'Curved', 6),
    (N'MONITORS-0017', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'MONITORS-0018', N'purpose', N'Purpose', N'Design', 1),
    (N'MONITORS-0018', N'size', N'Size', N'27 inch', 2),
    (N'MONITORS-0018', N'resolution', N'Resolution', N'4K UHD', 3),
    (N'MONITORS-0018', N'refreshRate', N'Refresh Rate', N'60Hz', 4),
    (N'MONITORS-0018', N'panel', N'Panel', N'IPS Black', 5),
    (N'MONITORS-0018', N'badge', N'Badge', N'Creator', 6),
    (N'MONITORS-0018', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'MONITORS-0019', N'purpose', N'Purpose', N'Design', 1),
    (N'MONITORS-0019', N'size', N'Size', N'27 inch', 2),
    (N'MONITORS-0019', N'resolution', N'Resolution', N'QHD', 3),
    (N'MONITORS-0019', N'refreshRate', N'Refresh Rate', N'75Hz', 4),
    (N'MONITORS-0019', N'panel', N'Panel', N'IPS', 5),
    (N'MONITORS-0019', N'badge', N'Badge', N'Color work', 6),
    (N'MONITORS-0019', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'MONITORS-0020', N'purpose', N'Purpose', N'Gaming', 1),
    (N'MONITORS-0020', N'size', N'Size', N'24 inch', 2),
    (N'MONITORS-0020', N'resolution', N'Resolution', N'Full HD', 3),
    (N'MONITORS-0020', N'refreshRate', N'Refresh Rate', N'165Hz', 4),
    (N'MONITORS-0020', N'panel', N'Panel', N'IPS', 5),
    (N'MONITORS-0020', N'badge', N'Badge', N'Budget gaming', 6),
    (N'MONITORS-0020', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'SSD-0021', N'purpose', N'Purpose', N'Gaming', 1),
    (N'SSD-0021', N'capacity', N'Capacity', N'1TB', 2),
    (N'SSD-0021', N'interfaceType', N'Interface', N'PCIe 4.0', 3),
    (N'SSD-0021', N'readSpeed', N'Read Speed', N'7450MB/s', 4),
    (N'SSD-0021', N'formFactor', N'Form Factor', N'M.2 2280', 5),
    (N'SSD-0021', N'badge', N'Badge', N'PCIe 4.0', 6),
    (N'SSD-0021', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'SSD-0022', N'purpose', N'Purpose', N'Creator', 1),
    (N'SSD-0022', N'capacity', N'Capacity', N'2TB', 2),
    (N'SSD-0022', N'interfaceType', N'Interface', N'PCIe 4.0', 3),
    (N'SSD-0022', N'readSpeed', N'Read Speed', N'7300MB/s', 4),
    (N'SSD-0022', N'formFactor', N'Form Factor', N'M.2 2280', 5),
    (N'SSD-0022', N'badge', N'Badge', N'High speed', 6),
    (N'SSD-0022', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'SSD-0023', N'purpose', N'Purpose', N'Boot drive', 1),
    (N'SSD-0023', N'capacity', N'Capacity', N'1TB', 2),
    (N'SSD-0023', N'interfaceType', N'Interface', N'PCIe 3.0', 3),
    (N'SSD-0023', N'readSpeed', N'Read Speed', N'3500MB/s', 4),
    (N'SSD-0023', N'formFactor', N'Form Factor', N'M.2 2280', 5),
    (N'SSD-0023', N'badge', N'Badge', N'Budget NVMe', 6),
    (N'SSD-0023', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'SSD-0024', N'purpose', N'Purpose', N'Boot drive', 1),
    (N'SSD-0024', N'capacity', N'Capacity', N'1TB', 2),
    (N'SSD-0024', N'interfaceType', N'Interface', N'SATA', 3),
    (N'SSD-0024', N'readSpeed', N'Read Speed', N'560MB/s', 4),
    (N'SSD-0024', N'formFactor', N'Form Factor', N'2.5 inch', 5),
    (N'SSD-0024', N'badge', N'Badge', N'SATA reliable', 6),
    (N'SSD-0024', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'SSD-0025', N'purpose', N'Purpose', N'Creator', 1),
    (N'SSD-0025', N'capacity', N'Capacity', N'2TB', 2),
    (N'SSD-0025', N'interfaceType', N'Interface', N'PCIe 5.0', 3),
    (N'SSD-0025', N'readSpeed', N'Read Speed', N'10000MB/s', 4),
    (N'SSD-0025', N'formFactor', N'Form Factor', N'M.2 2280', 5),
    (N'SSD-0025', N'badge', N'Badge', N'PCIe 5.0', 6),
    (N'SSD-0025', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'RAM-0026', N'purpose', N'Purpose', N'Gaming PC', 1),
    (N'RAM-0026', N'capacity', N'Capacity', N'32GB', 2),
    (N'RAM-0026', N'memoryType', N'Memory Type', N'DDR5', 3),
    (N'RAM-0026', N'bus', N'Bus', N'6000MHz', 4),
    (N'RAM-0026', N'formFactor', N'Form Factor', N'DIMM', 5),
    (N'RAM-0026', N'badge', N'Badge', N'DDR5 kit', 6),
    (N'RAM-0026', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'RAM-0027', N'purpose', N'Purpose', N'Gaming PC', 1),
    (N'RAM-0027', N'capacity', N'Capacity', N'16GB', 2),
    (N'RAM-0027', N'memoryType', N'Memory Type', N'DDR4', 3),
    (N'RAM-0027', N'bus', N'Bus', N'3200MHz', 4),
    (N'RAM-0027', N'formFactor', N'Form Factor', N'DIMM', 5),
    (N'RAM-0027', N'badge', N'Badge', N'Gaming value', 6),
    (N'RAM-0027', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'RAM-0028', N'purpose', N'Purpose', N'RGB build', 1),
    (N'RAM-0028', N'capacity', N'Capacity', N'32GB', 2),
    (N'RAM-0028', N'memoryType', N'Memory Type', N'DDR5', 3),
    (N'RAM-0028', N'bus', N'Bus', N'6000MHz', 4),
    (N'RAM-0028', N'formFactor', N'Form Factor', N'DIMM', 5),
    (N'RAM-0028', N'badge', N'Badge', N'RGB build', 6),
    (N'RAM-0028', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'RAM-0029', N'purpose', N'Purpose', N'Laptop upgrade', 1),
    (N'RAM-0029', N'capacity', N'Capacity', N'16GB', 2),
    (N'RAM-0029', N'memoryType', N'Memory Type', N'DDR4', 3),
    (N'RAM-0029', N'bus', N'Bus', N'3200MHz', 4),
    (N'RAM-0029', N'formFactor', N'Form Factor', N'SODIMM', 5),
    (N'RAM-0029', N'badge', N'Badge', N'Laptop upgrade', 6),
    (N'RAM-0029', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'RAM-0030', N'purpose', N'Purpose', N'Laptop upgrade', 1),
    (N'RAM-0030', N'capacity', N'Capacity', N'16GB', 2),
    (N'RAM-0030', N'memoryType', N'Memory Type', N'DDR5', 3),
    (N'RAM-0030', N'bus', N'Bus', N'5200MHz', 4),
    (N'RAM-0030', N'formFactor', N'Form Factor', N'SODIMM', 5),
    (N'RAM-0030', N'badge', N'Badge', N'DDR5 value', 6),
    (N'RAM-0030', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'CPU-0031', N'purpose', N'Purpose', N'Gaming', 1),
    (N'CPU-0031', N'socket', N'Socket', N'LGA1700', 2),
    (N'CPU-0031', N'cores', N'Cores', N'14 cores', 3),
    (N'CPU-0031', N'threads', N'Threads', N'20 threads', 4),
    (N'CPU-0031', N'tdp', N'TDP', N'125W', 5),
    (N'CPU-0031', N'badge', N'Badge', N'Gaming value', 6),
    (N'CPU-0031', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'CPU-0032', N'purpose', N'Purpose', N'Streaming', 1),
    (N'CPU-0032', N'socket', N'Socket', N'LGA1700', 2),
    (N'CPU-0032', N'cores', N'Cores', N'20 cores', 3),
    (N'CPU-0032', N'threads', N'Threads', N'28 threads', 4),
    (N'CPU-0032', N'tdp', N'TDP', N'125W', 5),
    (N'CPU-0032', N'badge', N'Badge', N'Creator pick', 6),
    (N'CPU-0032', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'CPU-0033', N'purpose', N'Purpose', N'Office', 1),
    (N'CPU-0033', N'socket', N'Socket', N'AM5', 2),
    (N'CPU-0033', N'cores', N'Cores', N'6 cores', 3),
    (N'CPU-0033', N'threads', N'Threads', N'12 threads', 4),
    (N'CPU-0033', N'tdp', N'TDP', N'65W', 5),
    (N'CPU-0033', N'badge', N'Badge', N'AM5 value', 6),
    (N'CPU-0033', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'CPU-0034', N'purpose', N'Purpose', N'Gaming', 1),
    (N'CPU-0034', N'socket', N'Socket', N'AM5', 2),
    (N'CPU-0034', N'cores', N'Cores', N'8 cores', 3),
    (N'CPU-0034', N'threads', N'Threads', N'16 threads', 4),
    (N'CPU-0034', N'tdp', N'TDP', N'120W', 5),
    (N'CPU-0034', N'badge', N'Badge', N'Gaming king', 6),
    (N'CPU-0034', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'CPU-0035', N'purpose', N'Purpose', N'Workstation', 1),
    (N'CPU-0035', N'socket', N'Socket', N'AM5', 2),
    (N'CPU-0035', N'cores', N'Cores', N'16 cores', 3),
    (N'CPU-0035', N'threads', N'Threads', N'32 threads', 4),
    (N'CPU-0035', N'tdp', N'TDP', N'170W', 5),
    (N'CPU-0035', N'badge', N'Badge', N'Workstation', 6),
    (N'CPU-0035', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'GPU-0036', N'purpose', N'Purpose', N'1080p gaming', 1),
    (N'GPU-0036', N'chipset', N'Chipset', N'RTX 4060', 2),
    (N'GPU-0036', N'vram', N'VRAM', N'8GB', 3),
    (N'GPU-0036', N'power', N'Power', N'115W', 4),
    (N'GPU-0036', N'ports', N'Ports', N'HDMI, DisplayPort', 5),
    (N'GPU-0036', N'badge', N'Badge', N'1080p', 6),
    (N'GPU-0036', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'GPU-0037', N'purpose', N'Purpose', N'1440p gaming', 1),
    (N'GPU-0037', N'chipset', N'Chipset', N'RTX 4070 Super', 2),
    (N'GPU-0037', N'vram', N'VRAM', N'12GB', 3),
    (N'GPU-0037', N'power', N'Power', N'220W', 4),
    (N'GPU-0037', N'ports', N'Ports', N'HDMI, 3x DisplayPort', 5),
    (N'GPU-0037', N'badge', N'Badge', N'1440p', 6),
    (N'GPU-0037', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'GPU-0038', N'purpose', N'Purpose', N'4K gaming', 1),
    (N'GPU-0038', N'chipset', N'Chipset', N'RTX 4080', 2),
    (N'GPU-0038', N'vram', N'VRAM', N'16GB', 3),
    (N'GPU-0038', N'power', N'Power', N'320W', 4),
    (N'GPU-0038', N'ports', N'Ports', N'HDMI, 3x DisplayPort', 5),
    (N'GPU-0038', N'badge', N'Badge', N'4K', 6),
    (N'GPU-0038', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'GPU-0039', N'purpose', N'Purpose', N'1440p gaming', 1),
    (N'GPU-0039', N'chipset', N'Chipset', N'RX 7800 XT', 2),
    (N'GPU-0039', N'vram', N'VRAM', N'16GB', 3),
    (N'GPU-0039', N'power', N'Power', N'263W', 4),
    (N'GPU-0039', N'ports', N'Ports', N'HDMI, DisplayPort', 5),
    (N'GPU-0039', N'badge', N'Badge', N'Radeon value', 6),
    (N'GPU-0039', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'GPU-0040', N'purpose', N'Purpose', N'AI work', 1),
    (N'GPU-0040', N'chipset', N'Chipset', N'RTX 4090', 2),
    (N'GPU-0040', N'vram', N'VRAM', N'24GB', 3),
    (N'GPU-0040', N'power', N'Power', N'450W', 4),
    (N'GPU-0040', N'ports', N'Ports', N'HDMI, 3x DisplayPort', 5),
    (N'GPU-0040', N'badge', N'Badge', N'AI work', 6),
    (N'GPU-0040', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'PCCASE-0041', N'purpose', N'Purpose', N'Airflow', 1),
    (N'PCCASE-0041', N'caseType', N'Case Type', N'Mid tower', 2),
    (N'PCCASE-0041', N'motherboardSupport', N'Motherboard Support', N'ATX', 3),
    (N'PCCASE-0041', N'color', N'Color', N'Black', 4),
    (N'PCCASE-0041', N'fanSupport', N'Fan Support', N'6 fans', 5),
    (N'PCCASE-0041', N'badge', N'Badge', N'Airflow', 6),
    (N'PCCASE-0041', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'PCCASE-0042', N'purpose', N'Purpose', N'Airflow', 1),
    (N'PCCASE-0042', N'caseType', N'Case Type', N'Mid tower', 2),
    (N'PCCASE-0042', N'motherboardSupport', N'Motherboard Support', N'ATX', 3),
    (N'PCCASE-0042', N'color', N'Color', N'White', 4),
    (N'PCCASE-0042', N'fanSupport', N'Fan Support', N'6 fans', 5),
    (N'PCCASE-0042', N'badge', N'Badge', N'Clean build', 6),
    (N'PCCASE-0042', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'PCCASE-0043', N'purpose', N'Purpose', N'Showcase', 1),
    (N'PCCASE-0043', N'caseType', N'Case Type', N'Mid tower', 2),
    (N'PCCASE-0043', N'motherboardSupport', N'Motherboard Support', N'E-ATX', 3),
    (N'PCCASE-0043', N'color', N'Color', N'White', 4),
    (N'PCCASE-0043', N'fanSupport', N'Fan Support', N'10 fans', 5),
    (N'PCCASE-0043', N'badge', N'Badge', N'Showcase', 6),
    (N'PCCASE-0043', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'PCCASE-0044', N'purpose', N'Purpose', N'Compact', 1),
    (N'PCCASE-0044', N'caseType', N'Case Type', N'Mini tower', 2),
    (N'PCCASE-0044', N'motherboardSupport', N'Motherboard Support', N'Mini-ITX', 3),
    (N'PCCASE-0044', N'color', N'Color', N'Black', 4),
    (N'PCCASE-0044', N'fanSupport', N'Fan Support', N'5 fans', 5),
    (N'PCCASE-0044', N'badge', N'Badge', N'Mini ITX', 6),
    (N'PCCASE-0044', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'PCCASE-0045', N'purpose', N'Purpose', N'Showcase', 1),
    (N'PCCASE-0045', N'caseType', N'Case Type', N'Mid tower', 2),
    (N'PCCASE-0045', N'motherboardSupport', N'Motherboard Support', N'ATX', 3),
    (N'PCCASE-0045', N'color', N'Color', N'Black', 4),
    (N'PCCASE-0045', N'fanSupport', N'Fan Support', N'9 fans', 5),
    (N'PCCASE-0045', N'badge', N'Badge', N'Display panel', 6),
    (N'PCCASE-0045', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'MAINBOARD-0046', N'purpose', N'Purpose', N'Gaming', 1),
    (N'MAINBOARD-0046', N'socket', N'Socket', N'AM5', 2),
    (N'MAINBOARD-0046', N'chipset', N'Chipset', N'B650', 3),
    (N'MAINBOARD-0046', N'formFactor', N'Form Factor', N'ATX', 4),
    (N'MAINBOARD-0046', N'memoryType', N'Memory Type', N'DDR5', 5),
    (N'MAINBOARD-0046', N'badge', N'Badge', N'AM5 durable', 6),
    (N'MAINBOARD-0046', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'MAINBOARD-0047', N'purpose', N'Purpose', N'Gaming', 1),
    (N'MAINBOARD-0047', N'socket', N'Socket', N'LGA1700', 2),
    (N'MAINBOARD-0047', N'chipset', N'Chipset', N'B760', 3),
    (N'MAINBOARD-0047', N'formFactor', N'Form Factor', N'ATX', 4),
    (N'MAINBOARD-0047', N'memoryType', N'Memory Type', N'DDR5', 5),
    (N'MAINBOARD-0047', N'badge', N'Badge', N'Intel value', 6),
    (N'MAINBOARD-0047', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'MAINBOARD-0048', N'purpose', N'Purpose', N'Overclocking', 1),
    (N'MAINBOARD-0048', N'socket', N'Socket', N'LGA1700', 2),
    (N'MAINBOARD-0048', N'chipset', N'Chipset', N'Z790', 3),
    (N'MAINBOARD-0048', N'formFactor', N'Form Factor', N'ATX', 4),
    (N'MAINBOARD-0048', N'memoryType', N'Memory Type', N'DDR5', 5),
    (N'MAINBOARD-0048', N'badge', N'Badge', N'Overclock', 6),
    (N'MAINBOARD-0048', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'MAINBOARD-0049', N'purpose', N'Purpose', N'Budget PC', 1),
    (N'MAINBOARD-0049', N'socket', N'Socket', N'AM4', 2),
    (N'MAINBOARD-0049', N'chipset', N'Chipset', N'B550', 3),
    (N'MAINBOARD-0049', N'formFactor', N'Form Factor', N'Micro-ATX', 4),
    (N'MAINBOARD-0049', N'memoryType', N'Memory Type', N'DDR4', 5),
    (N'MAINBOARD-0049', N'badge', N'Badge', N'AM4 value', 6),
    (N'MAINBOARD-0049', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'MAINBOARD-0050', N'purpose', N'Purpose', N'Budget PC', 1),
    (N'MAINBOARD-0050', N'socket', N'Socket', N'AM5', 2),
    (N'MAINBOARD-0050', N'chipset', N'Chipset', N'B650', 3),
    (N'MAINBOARD-0050', N'formFactor', N'Form Factor', N'Micro-ATX', 4),
    (N'MAINBOARD-0050', N'memoryType', N'Memory Type', N'DDR5', 5),
    (N'MAINBOARD-0050', N'badge', N'Badge', N'Budget AM5', 6),
    (N'MAINBOARD-0050', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'PCFAN-0051', N'purpose', N'Purpose', N'Silent', 1),
    (N'PCFAN-0051', N'size', N'Size', N'120mm', 2),
    (N'PCFAN-0051', N'speed', N'Speed', N'2000 RPM', 3),
    (N'PCFAN-0051', N'airflow', N'Airflow', N'60 CFM', 4),
    (N'PCFAN-0051', N'lighting', N'Lighting', N'None', 5),
    (N'PCFAN-0051', N'badge', N'Badge', N'Silent', 6),
    (N'PCFAN-0051', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'PCFAN-0052', N'purpose', N'Purpose', N'RGB', 1),
    (N'PCFAN-0052', N'size', N'Size', N'120mm', 2),
    (N'PCFAN-0052', N'speed', N'Speed', N'2100 RPM', 3),
    (N'PCFAN-0052', N'airflow', N'Airflow', N'65 CFM', 4),
    (N'PCFAN-0052', N'lighting', N'Lighting', N'RGB', 5),
    (N'PCFAN-0052', N'badge', N'Badge', N'RGB', 6),
    (N'PCFAN-0052', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'PCFAN-0053', N'purpose', N'Purpose', N'RGB', 1),
    (N'PCFAN-0053', N'size', N'Size', N'120mm', 2),
    (N'PCFAN-0053', N'speed', N'Speed', N'1800 RPM', 3),
    (N'PCFAN-0053', N'airflow', N'Airflow', N'62 CFM', 4),
    (N'PCFAN-0053', N'lighting', N'Lighting', N'RGB', 5),
    (N'PCFAN-0053', N'badge', N'Badge', N'Budget RGB', 6),
    (N'PCFAN-0053', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'PCFAN-0054', N'purpose', N'Purpose', N'Radiator', 1),
    (N'PCFAN-0054', N'size', N'Size', N'120mm', 2),
    (N'PCFAN-0054', N'speed', N'Speed', N'1850 RPM', 3),
    (N'PCFAN-0054', N'airflow', N'Airflow', N'68 CFM', 4),
    (N'PCFAN-0054', N'lighting', N'Lighting', N'None', 5),
    (N'PCFAN-0054', N'badge', N'Badge', N'Radiator', 6),
    (N'PCFAN-0054', N'warranty', N'Warranty', N'12 months official warranty', 7),
    (N'PCFAN-0055', N'purpose', N'Purpose', N'High airflow', 1),
    (N'PCFAN-0055', N'size', N'Size', N'140mm', 2),
    (N'PCFAN-0055', N'speed', N'Speed', N'1700 RPM', 3),
    (N'PCFAN-0055', N'airflow', N'Airflow', N'72 CFM', 4),
    (N'PCFAN-0055', N'lighting', N'Lighting', N'None', 5),
    (N'PCFAN-0055', N'badge', N'Badge', N'140mm airflow', 6),
    (N'PCFAN-0055', N'warranty', N'Warranty', N'12 months official warranty', 7);

MERGE dbo.bs_ProductSpecifications AS target
USING (
    SELECT p.product_id, s.spec_key, s.spec_label, s.spec_value, s.sort_order
    FROM @Specs s
    INNER JOIN dbo.bs_Products p ON p.sku = s.sku
) AS source
    ON target.product_id = source.product_id AND target.spec_key = source.spec_key
WHEN MATCHED THEN
    UPDATE SET spec_label = source.spec_label, spec_value = source.spec_value, sort_order = source.sort_order, updated_at = SYSDATETIME()
WHEN NOT MATCHED THEN
    INSERT (product_id, spec_key, spec_label, spec_value, sort_order, created_at, updated_at)
    VALUES (source.product_id, source.spec_key, source.spec_label, source.spec_value, source.sort_order, SYSDATETIME(), SYSDATETIME());


DECLARE @AdministratorUserId INT;
SELECT TOP (1) @AdministratorUserId = user_id
FROM dbo.bs_user
WHERE email = N'administrator@example.com' OR full_name = N'Administrator'
ORDER BY CASE WHEN email = N'administrator@example.com' THEN 1 ELSE 2 END, user_id;

DECLARE @Reviews TABLE (sku NVARCHAR(80), user_id INT, rating INT, comment NVARCHAR(1000));
INSERT INTO @Reviews (sku, user_id, rating, comment) VALUES
    (N'LAPTOPS-0001', @AdministratorUserId, 5, N'Good laptops for the price. The specifications match my current setup.'),
    (N'LAPTOPS-0002', @AdministratorUserId, 4, N'Good laptops for the price. The specifications match my current setup.'),
    (N'LAPTOPS-0003', @AdministratorUserId, 5, N'Good laptops for the price. The specifications match my current setup.'),
    (N'LAPTOPS-0004', @AdministratorUserId, 4, N'Good laptops for the price. The specifications match my current setup.'),
    (N'LAPTOPS-0005', @AdministratorUserId, 5, N'Good laptops for the price. The specifications match my current setup.'),
    (N'MOUSE-0006', @AdministratorUserId, 4, N'Good mouse for the price. The specifications match my current setup.'),
    (N'MOUSE-0007', @AdministratorUserId, 5, N'Good mouse for the price. The specifications match my current setup.'),
    (N'MOUSE-0008', @AdministratorUserId, 4, N'Good mouse for the price. The specifications match my current setup.'),
    (N'MOUSE-0009', @AdministratorUserId, 5, N'Good mouse for the price. The specifications match my current setup.'),
    (N'MOUSE-0010', @AdministratorUserId, 4, N'Good mouse for the price. The specifications match my current setup.'),
    (N'KEYBOARDS-0011', @AdministratorUserId, 5, N'Good keyboards for the price. The specifications match my current setup.'),
    (N'KEYBOARDS-0012', @AdministratorUserId, 4, N'Good keyboards for the price. The specifications match my current setup.'),
    (N'KEYBOARDS-0013', @AdministratorUserId, 5, N'Good keyboards for the price. The specifications match my current setup.'),
    (N'KEYBOARDS-0014', @AdministratorUserId, 4, N'Good keyboards for the price. The specifications match my current setup.'),
    (N'KEYBOARDS-0015', @AdministratorUserId, 5, N'Good keyboards for the price. The specifications match my current setup.'),
    (N'MONITORS-0016', @AdministratorUserId, 4, N'Good monitors for the price. The specifications match my current setup.'),
    (N'MONITORS-0017', @AdministratorUserId, 5, N'Good monitors for the price. The specifications match my current setup.'),
    (N'MONITORS-0018', @AdministratorUserId, 4, N'Good monitors for the price. The specifications match my current setup.'),
    (N'MONITORS-0019', @AdministratorUserId, 5, N'Good monitors for the price. The specifications match my current setup.'),
    (N'MONITORS-0020', @AdministratorUserId, 4, N'Good monitors for the price. The specifications match my current setup.'),
    (N'SSD-0021', @AdministratorUserId, 5, N'Good ssd for the price. The specifications match my current setup.'),
    (N'SSD-0022', @AdministratorUserId, 4, N'Good ssd for the price. The specifications match my current setup.'),
    (N'SSD-0023', @AdministratorUserId, 5, N'Good ssd for the price. The specifications match my current setup.'),
    (N'SSD-0024', @AdministratorUserId, 4, N'Good ssd for the price. The specifications match my current setup.'),
    (N'SSD-0025', @AdministratorUserId, 5, N'Good ssd for the price. The specifications match my current setup.'),
    (N'RAM-0026', @AdministratorUserId, 4, N'Good ram for the price. The specifications match my current setup.'),
    (N'RAM-0027', @AdministratorUserId, 5, N'Good ram for the price. The specifications match my current setup.'),
    (N'RAM-0028', @AdministratorUserId, 4, N'Good ram for the price. The specifications match my current setup.'),
    (N'RAM-0029', @AdministratorUserId, 5, N'Good ram for the price. The specifications match my current setup.'),
    (N'RAM-0030', @AdministratorUserId, 4, N'Good ram for the price. The specifications match my current setup.'),
    (N'CPU-0031', @AdministratorUserId, 5, N'Good cpu for the price. The specifications match my current setup.'),
    (N'CPU-0032', @AdministratorUserId, 4, N'Good cpu for the price. The specifications match my current setup.'),
    (N'CPU-0033', @AdministratorUserId, 5, N'Good cpu for the price. The specifications match my current setup.'),
    (N'CPU-0034', @AdministratorUserId, 4, N'Good cpu for the price. The specifications match my current setup.'),
    (N'CPU-0035', @AdministratorUserId, 5, N'Good cpu for the price. The specifications match my current setup.'),
    (N'GPU-0036', @AdministratorUserId, 4, N'Good gpu for the price. The specifications match my current setup.'),
    (N'GPU-0037', @AdministratorUserId, 5, N'Good gpu for the price. The specifications match my current setup.'),
    (N'GPU-0038', @AdministratorUserId, 4, N'Good gpu for the price. The specifications match my current setup.'),
    (N'GPU-0039', @AdministratorUserId, 5, N'Good gpu for the price. The specifications match my current setup.'),
    (N'GPU-0040', @AdministratorUserId, 4, N'Good gpu for the price. The specifications match my current setup.'),
    (N'PCCASE-0041', @AdministratorUserId, 5, N'Good pc case for the price. The specifications match my current setup.'),
    (N'PCCASE-0042', @AdministratorUserId, 4, N'Good pc case for the price. The specifications match my current setup.'),
    (N'PCCASE-0043', @AdministratorUserId, 5, N'Good pc case for the price. The specifications match my current setup.'),
    (N'PCCASE-0044', @AdministratorUserId, 4, N'Good pc case for the price. The specifications match my current setup.'),
    (N'PCCASE-0045', @AdministratorUserId, 5, N'Good pc case for the price. The specifications match my current setup.'),
    (N'MAINBOARD-0046', @AdministratorUserId, 4, N'Good mainboard for the price. The specifications match my current setup.'),
    (N'MAINBOARD-0047', @AdministratorUserId, 5, N'Good mainboard for the price. The specifications match my current setup.'),
    (N'MAINBOARD-0048', @AdministratorUserId, 4, N'Good mainboard for the price. The specifications match my current setup.'),
    (N'MAINBOARD-0049', @AdministratorUserId, 5, N'Good mainboard for the price. The specifications match my current setup.'),
    (N'MAINBOARD-0050', @AdministratorUserId, 4, N'Good mainboard for the price. The specifications match my current setup.'),
    (N'PCFAN-0051', @AdministratorUserId, 5, N'Good pc fan for the price. The specifications match my current setup.'),
    (N'PCFAN-0052', @AdministratorUserId, 4, N'Good pc fan for the price. The specifications match my current setup.'),
    (N'PCFAN-0053', @AdministratorUserId, 5, N'Good pc fan for the price. The specifications match my current setup.'),
    (N'PCFAN-0054', @AdministratorUserId, 4, N'Good pc fan for the price. The specifications match my current setup.'),
    (N'PCFAN-0055', @AdministratorUserId, 5, N'Good pc fan for the price. The specifications match my current setup.');

MERGE dbo.bs_Reviews AS target
USING (
    SELECT u.user_id, CAST(NULL AS INT) AS order_id, p.product_id, r.rating, r.comment
    FROM @Reviews r
    INNER JOIN dbo.bs_user u ON u.user_id = r.user_id
    INNER JOIN dbo.bs_Products p ON p.sku = r.sku
) AS source
    ON target.user_id = source.user_id
   AND (target.order_id = source.order_id OR (target.order_id IS NULL AND source.order_id IS NULL))
   AND target.product_id = source.product_id
WHEN MATCHED THEN
    UPDATE SET rating = source.rating, updated_at = SYSUTCDATETIME()
WHEN NOT MATCHED THEN
    INSERT (user_id, order_id, product_id, rating, comment, created_at, updated_at)
    VALUES (source.user_id, source.order_id, source.product_id, source.rating, source.comment, SYSUTCDATETIME(), SYSUTCDATETIME());

DELETE cfo
FROM dbo.bs_CategoryFilterOptions cfo
INNER JOIN dbo.bs_CategoryFilters cf ON cf.category_filter_id = cfo.category_filter_id
INNER JOIN dbo.bs_Categories c ON c.category_id = cf.category_id
WHERE c.category_name IN (N'Laptops', N'Mouse', N'Keyboards', N'Monitors', N'SSD', N'RAM', N'CPU', N'GPU', N'PC Case', N'Mainboard', N'PC Fan');

DELETE cf
FROM dbo.bs_CategoryFilters cf
INNER JOIN dbo.bs_Categories c ON c.category_id = cf.category_id
WHERE c.category_name IN (N'Laptops', N'Mouse', N'Keyboards', N'Monitors', N'SSD', N'RAM', N'CPU', N'GPU', N'PC Case', N'Mainboard', N'PC Fan');

DELETE cmo
FROM dbo.bs_CategoryMenuOptions cmo
INNER JOIN dbo.bs_CategoryMenuGroups cmg ON cmg.menu_group_id = cmo.menu_group_id
INNER JOIN dbo.bs_Categories c ON c.category_id = cmg.category_id
WHERE c.category_name IN (N'Laptops', N'Mouse', N'Keyboards', N'Monitors', N'SSD', N'RAM', N'CPU', N'GPU', N'PC Case', N'Mainboard', N'PC Fan');

DELETE cmg
FROM dbo.bs_CategoryMenuGroups cmg
INNER JOIN dbo.bs_Categories c ON c.category_id = cmg.category_id
WHERE c.category_name IN (N'Laptops', N'Mouse', N'Keyboards', N'Monitors', N'SSD', N'RAM', N'CPU', N'GPU', N'PC Case', N'Mainboard', N'PC Fan');

DECLARE @MenuGroups TABLE (category_name NVARCHAR(120), group_title NVARCHAR(120), sort_order INT);
INSERT INTO @MenuGroups (category_name, group_title, sort_order) VALUES
    (N'Laptops', N'Brands', 1),
    (N'Laptops', N'Prices', 2),
    (N'Laptops', N'Purpose', 3),
    (N'Laptops', N'CPU', 4),
    (N'Laptops', N'GPU', 5),
    (N'Laptops', N'Screen', 6),
    (N'Mouse', N'Brands', 1),
    (N'Mouse', N'Prices', 2),
    (N'Mouse', N'Purpose', 3),
    (N'Mouse', N'Sensor', 4),
    (N'Mouse', N'Connection', 5),
    (N'Keyboards', N'Brands', 1),
    (N'Keyboards', N'Prices', 2),
    (N'Keyboards', N'Purpose', 3),
    (N'Keyboards', N'Switch', 4),
    (N'Keyboards', N'Layout', 5),
    (N'Monitors', N'Brands', 1),
    (N'Monitors', N'Prices', 2),
    (N'Monitors', N'Purpose', 3),
    (N'Monitors', N'Resolution', 4),
    (N'Monitors', N'Refresh rate', 5),
    (N'SSD', N'Brands', 1),
    (N'SSD', N'Prices', 2),
    (N'SSD', N'Purpose', 3),
    (N'SSD', N'Capacity', 4),
    (N'SSD', N'Interface', 5),
    (N'RAM', N'Brands', 1),
    (N'RAM', N'Prices', 2),
    (N'RAM', N'Purpose', 3),
    (N'RAM', N'Capacity', 4),
    (N'RAM', N'Type', 5),
    (N'CPU', N'Brands', 1),
    (N'CPU', N'Prices', 2),
    (N'CPU', N'Purpose', 3),
    (N'CPU', N'Socket', 4),
    (N'CPU', N'Cores', 5),
    (N'GPU', N'Brands', 1),
    (N'GPU', N'Prices', 2),
    (N'GPU', N'Purpose', 3),
    (N'GPU', N'Chipset', 4),
    (N'GPU', N'VRAM', 5),
    (N'PC Case', N'Brands', 1),
    (N'PC Case', N'Prices', 2),
    (N'PC Case', N'Purpose', 3),
    (N'PC Case', N'Type', 4),
    (N'PC Case', N'Motherboard Support', 5),
    (N'Mainboard', N'Brands', 1),
    (N'Mainboard', N'Prices', 2),
    (N'Mainboard', N'Purpose', 3),
    (N'Mainboard', N'Socket', 4),
    (N'Mainboard', N'Chipset', 5),
    (N'PC Fan', N'Brands', 1),
    (N'PC Fan', N'Prices', 2),
    (N'PC Fan', N'Purpose', 3),
    (N'PC Fan', N'Size', 4),
    (N'PC Fan', N'Bearing', 5);
INSERT INTO dbo.bs_CategoryMenuGroups (category_id, group_title, sort_order, created_at, updated_at)
SELECT c.category_id, mg.group_title, mg.sort_order, SYSDATETIME(), SYSDATETIME()
FROM @MenuGroups mg
INNER JOIN dbo.bs_Categories c ON c.category_name = mg.category_name;

DECLARE @MenuOptions TABLE (category_name NVARCHAR(120), group_title NVARCHAR(120), option_value NVARCHAR(250), sort_order INT);
INSERT INTO @MenuOptions (category_name, group_title, option_value, sort_order) VALUES
    (N'Laptops', N'Brands', N'Dell', 1),
    (N'Laptops', N'Brands', N'ASUS', 2),
    (N'Laptops', N'Brands', N'Lenovo', 3),
    (N'Laptops', N'Brands', N'HP', 4),
    (N'Laptops', N'Brands', N'Acer', 5),
    (N'Laptops', N'Prices', N'Under 20M', 1),
    (N'Laptops', N'Prices', N'20M - 30M', 2),
    (N'Laptops', N'Prices', N'30M - 40M', 3),
    (N'Laptops', N'Prices', N'Over 40M', 4),
    (N'Laptops', N'Purpose', N'Gaming', 1),
    (N'Laptops', N'Purpose', N'Workstation', 2),
    (N'Laptops', N'Purpose', N'Student', 3),
    (N'Laptops', N'Purpose', N'Business', 4),
    (N'Laptops', N'CPU', N'Intel Core Ultra 7', 1),
    (N'Laptops', N'CPU', N'Intel Core i7', 2),
    (N'Laptops', N'CPU', N'AMD Ryzen 9', 3),
    (N'Laptops', N'GPU', N'RTX 4070', 1),
    (N'Laptops', N'GPU', N'RTX 4060', 2),
    (N'Laptops', N'GPU', N'Integrated', 3),
    (N'Laptops', N'Screen', N'13.6 inch', 1),
    (N'Laptops', N'Screen', N'14 inch QHD', 2),
    (N'Laptops', N'Screen', N'15.6 inch FHD', 3),
    (N'Laptops', N'Screen', N'16 inch OLED', 4),
    (N'Mouse', N'Brands', N'Logitech', 1),
    (N'Mouse', N'Brands', N'Razer', 2),
    (N'Mouse', N'Brands', N'SteelSeries', 3),
    (N'Mouse', N'Brands', N'Corsair', 4),
    (N'Mouse', N'Brands', N'ASUS', 5),
    (N'Mouse', N'Prices', N'Under 500K', 1),
    (N'Mouse', N'Prices', N'500K - 1M', 2),
    (N'Mouse', N'Prices', N'1M - 2M', 3),
    (N'Mouse', N'Prices', N'Over 2M', 4),
    (N'Mouse', N'Purpose', N'Gaming', 1),
    (N'Mouse', N'Purpose', N'Office', 2),
    (N'Mouse', N'Purpose', N'Wireless', 3),
    (N'Mouse', N'Purpose', N'Ergonomic', 4),
    (N'Mouse', N'Sensor', N'Hero 25K', 1),
    (N'Mouse', N'Sensor', N'Focus Pro', 2),
    (N'Mouse', N'Sensor', N'TrueMove Air', 3),
    (N'Mouse', N'Sensor', N'PixArt 3395', 4),
    (N'Mouse', N'Connection', N'Bluetooth', 1),
    (N'Mouse', N'Connection', N'2.4GHz', 2),
    (N'Mouse', N'Connection', N'USB-C wired', 3),
    (N'Keyboards', N'Brands', N'Keychron', 1),
    (N'Keyboards', N'Brands', N'Logitech', 2),
    (N'Keyboards', N'Brands', N'Razer', 3),
    (N'Keyboards', N'Brands', N'Akko', 4),
    (N'Keyboards', N'Brands', N'Corsair', 5),
    (N'Keyboards', N'Prices', N'Under 1M', 1),
    (N'Keyboards', N'Prices', N'1M - 2M', 2),
    (N'Keyboards', N'Prices', N'2M - 3M', 3),
    (N'Keyboards', N'Prices', N'Over 3M', 4),
    (N'Keyboards', N'Purpose', N'Gaming', 1),
    (N'Keyboards', N'Purpose', N'Office', 2),
    (N'Keyboards', N'Purpose', N'Wireless', 3),
    (N'Keyboards', N'Purpose', N'Compact', 4),
    (N'Keyboards', N'Switch', N'Red', 1),
    (N'Keyboards', N'Switch', N'Brown', 2),
    (N'Keyboards', N'Switch', N'Blue', 3),
    (N'Keyboards', N'Switch', N'Optical', 4),
    (N'Keyboards', N'Layout', N'60%', 1),
    (N'Keyboards', N'Layout', N'75%', 2),
    (N'Keyboards', N'Layout', N'TKL', 3),
    (N'Keyboards', N'Layout', N'Full-size', 4),
    (N'Monitors', N'Brands', N'LG', 1),
    (N'Monitors', N'Brands', N'Samsung', 2),
    (N'Monitors', N'Brands', N'Dell', 3),
    (N'Monitors', N'Brands', N'ASUS', 4),
    (N'Monitors', N'Brands', N'AOC', 5),
    (N'Monitors', N'Prices', N'Under 4M', 1),
    (N'Monitors', N'Prices', N'4M - 7M', 2),
    (N'Monitors', N'Prices', N'7M - 12M', 3),
    (N'Monitors', N'Prices', N'Over 12M', 4),
    (N'Monitors', N'Purpose', N'Gaming', 1),
    (N'Monitors', N'Purpose', N'Design', 2),
    (N'Monitors', N'Purpose', N'Office', 3),
    (N'Monitors', N'Purpose', N'Ultrawide', 4),
    (N'Monitors', N'Resolution', N'Full HD', 1),
    (N'Monitors', N'Resolution', N'QHD', 2),
    (N'Monitors', N'Resolution', N'4K UHD', 3),
    (N'Monitors', N'Resolution', N'Ultrawide', 4),
    (N'Monitors', N'Refresh rate', N'75Hz', 1),
    (N'Monitors', N'Refresh rate', N'144Hz', 2),
    (N'Monitors', N'Refresh rate', N'165Hz', 3),
    (N'Monitors', N'Refresh rate', N'240Hz', 4),
    (N'SSD', N'Brands', N'Samsung', 1),
    (N'SSD', N'Brands', N'WD', 2),
    (N'SSD', N'Brands', N'Kingston', 3),
    (N'SSD', N'Brands', N'Crucial', 4),
    (N'SSD', N'Brands', N'Seagate', 5),
    (N'SSD', N'Prices', N'Under 1M', 1),
    (N'SSD', N'Prices', N'1M - 2M', 2),
    (N'SSD', N'Prices', N'2M - 4M', 3),
    (N'SSD', N'Prices', N'Over 4M', 4),
    (N'SSD', N'Purpose', N'Boot drive', 1),
    (N'SSD', N'Purpose', N'Gaming', 2),
    (N'SSD', N'Purpose', N'Creator', 3),
    (N'SSD', N'Purpose', N'NAS', 4),
    (N'SSD', N'Capacity', N'500GB', 1),
    (N'SSD', N'Capacity', N'1TB', 2),
    (N'SSD', N'Capacity', N'2TB', 3),
    (N'SSD', N'Capacity', N'4TB', 4),
    (N'SSD', N'Interface', N'SATA', 1),
    (N'SSD', N'Interface', N'PCIe 3.0', 2),
    (N'SSD', N'Interface', N'PCIe 4.0', 3),
    (N'SSD', N'Interface', N'PCIe 5.0', 4),
    (N'RAM', N'Brands', N'Corsair', 1),
    (N'RAM', N'Brands', N'Kingston', 2),
    (N'RAM', N'Brands', N'G.Skill', 3),
    (N'RAM', N'Brands', N'Crucial', 4),
    (N'RAM', N'Brands', N'TeamGroup', 5),
    (N'RAM', N'Prices', N'Under 1M', 1),
    (N'RAM', N'Prices', N'1M - 2M', 2),
    (N'RAM', N'Prices', N'2M - 3M', 3),
    (N'RAM', N'Prices', N'Over 3M', 4),
    (N'RAM', N'Purpose', N'Laptop upgrade', 1),
    (N'RAM', N'Purpose', N'Gaming PC', 2),
    (N'RAM', N'Purpose', N'Workstation', 3),
    (N'RAM', N'Purpose', N'RGB build', 4),
    (N'RAM', N'Capacity', N'8GB', 1),
    (N'RAM', N'Capacity', N'16GB', 2),
    (N'RAM', N'Capacity', N'32GB', 3),
    (N'RAM', N'Capacity', N'64GB', 4),
    (N'RAM', N'Type', N'DDR3', 1),
    (N'RAM', N'Type', N'DDR4', 2),
    (N'RAM', N'Type', N'DDR5', 3),
    (N'CPU', N'Brands', N'Intel', 1),
    (N'CPU', N'Brands', N'AMD', 2),
    (N'CPU', N'Prices', N'Under 5M', 1),
    (N'CPU', N'Prices', N'5M - 10M', 2),
    (N'CPU', N'Prices', N'10M - 15M', 3),
    (N'CPU', N'Prices', N'Over 15M', 4),
    (N'CPU', N'Purpose', N'Gaming', 1),
    (N'CPU', N'Purpose', N'Office', 2),
    (N'CPU', N'Purpose', N'Streaming', 3),
    (N'CPU', N'Purpose', N'Workstation', 4),
    (N'CPU', N'Socket', N'LGA1200', 1),
    (N'CPU', N'Socket', N'LGA1700', 2),
    (N'CPU', N'Socket', N'AM4', 3),
    (N'CPU', N'Socket', N'AM5', 4),
    (N'CPU', N'Cores', N'6 cores', 1),
    (N'CPU', N'Cores', N'8 cores', 2),
    (N'CPU', N'Cores', N'12 cores', 3),
    (N'CPU', N'Cores', N'16 cores', 4),
    (N'GPU', N'Brands', N'ASUS', 1),
    (N'GPU', N'Brands', N'MSI', 2),
    (N'GPU', N'Brands', N'Gigabyte', 3),
    (N'GPU', N'Brands', N'Sapphire', 4),
    (N'GPU', N'Brands', N'Zotac', 5),
    (N'GPU', N'Prices', N'Under 10M', 1),
    (N'GPU', N'Prices', N'10M - 20M', 2),
    (N'GPU', N'Prices', N'20M - 35M', 3),
    (N'GPU', N'Prices', N'Over 35M', 4),
    (N'GPU', N'Purpose', N'1080p gaming', 1),
    (N'GPU', N'Purpose', N'1440p gaming', 2),
    (N'GPU', N'Purpose', N'4K gaming', 3),
    (N'GPU', N'Purpose', N'AI work', 4),
    (N'GPU', N'Chipset', N'RTX 4060', 1),
    (N'GPU', N'Chipset', N'RTX 4070', 2),
    (N'GPU', N'Chipset', N'RTX 4080', 3),
    (N'GPU', N'Chipset', N'RTX 4090', 4),
    (N'GPU', N'VRAM', N'6GB', 1),
    (N'GPU', N'VRAM', N'8GB', 2),
    (N'GPU', N'VRAM', N'12GB', 3),
    (N'GPU', N'VRAM', N'16GB', 4),
    (N'PC Case', N'Brands', N'NZXT', 1),
    (N'PC Case', N'Brands', N'Corsair', 2),
    (N'PC Case', N'Brands', N'Lian Li', 3),
    (N'PC Case', N'Brands', N'Cooler Master', 4),
    (N'PC Case', N'Brands', N'DeepCool', 5),
    (N'PC Case', N'Prices', N'Under 2M', 1),
    (N'PC Case', N'Prices', N'2M - 3M', 2),
    (N'PC Case', N'Prices', N'3M - 5M', 3),
    (N'PC Case', N'Prices', N'Over 5M', 4),
    (N'PC Case', N'Purpose', N'Compact', 1),
    (N'PC Case', N'Purpose', N'Airflow', 2),
    (N'PC Case', N'Purpose', N'Showcase', 3),
    (N'PC Case', N'Purpose', N'Silent build', 4),
    (N'PC Case', N'Type', N'Mini tower', 1),
    (N'PC Case', N'Type', N'Mid tower', 2),
    (N'PC Case', N'Type', N'Full tower', 3),
    (N'PC Case', N'Motherboard Support', N'Mini-ITX', 1),
    (N'PC Case', N'Motherboard Support', N'Micro-ATX', 2),
    (N'PC Case', N'Motherboard Support', N'ATX', 3),
    (N'PC Case', N'Motherboard Support', N'E-ATX', 4),
    (N'Mainboard', N'Brands', N'ASUS', 1),
    (N'Mainboard', N'Brands', N'MSI', 2),
    (N'Mainboard', N'Brands', N'Gigabyte', 3),
    (N'Mainboard', N'Brands', N'ASRock', 4),
    (N'Mainboard', N'Brands', N'Biostar', 5),
    (N'Mainboard', N'Prices', N'Under 3M', 1),
    (N'Mainboard', N'Prices', N'3M - 5M', 2),
    (N'Mainboard', N'Prices', N'5M - 8M', 3),
    (N'Mainboard', N'Prices', N'Over 8M', 4),
    (N'Mainboard', N'Purpose', N'Budget PC', 1),
    (N'Mainboard', N'Purpose', N'Gaming', 2),
    (N'Mainboard', N'Purpose', N'Creator', 3),
    (N'Mainboard', N'Purpose', N'Overclocking', 4),
    (N'Mainboard', N'Socket', N'LGA1200', 1),
    (N'Mainboard', N'Socket', N'LGA1700', 2),
    (N'Mainboard', N'Socket', N'AM4', 3),
    (N'Mainboard', N'Socket', N'AM5', 4),
    (N'Mainboard', N'Chipset', N'B660', 1),
    (N'Mainboard', N'Chipset', N'B760', 2),
    (N'Mainboard', N'Chipset', N'Z790', 3),
    (N'Mainboard', N'Chipset', N'B550', 4),
    (N'Mainboard', N'Chipset', N'B650', 5),
    (N'PC Fan', N'Brands', N'Noctua', 1),
    (N'PC Fan', N'Brands', N'Corsair', 2),
    (N'PC Fan', N'Brands', N'Cooler Master', 3),
    (N'PC Fan', N'Brands', N'DeepCool', 4),
    (N'PC Fan', N'Brands', N'Arctic', 5),
    (N'PC Fan', N'Prices', N'Under 300K', 1),
    (N'PC Fan', N'Prices', N'300K - 700K', 2),
    (N'PC Fan', N'Prices', N'700K - 1.2M', 3),
    (N'PC Fan', N'Prices', N'Over 1.2M', 4),
    (N'PC Fan', N'Purpose', N'Silent', 1),
    (N'PC Fan', N'Purpose', N'RGB', 2),
    (N'PC Fan', N'Purpose', N'Radiator', 3),
    (N'PC Fan', N'Purpose', N'High airflow', 4),
    (N'PC Fan', N'Size', N'120mm', 1),
    (N'PC Fan', N'Size', N'140mm', 2),
    (N'PC Fan', N'Size', N'200mm', 3),
    (N'PC Fan', N'Bearing', N'Fluid dynamic', 1),
    (N'PC Fan', N'Bearing', N'Magnetic levitation', 2),
    (N'PC Fan', N'Bearing', N'SSO2', 3);
INSERT INTO dbo.bs_CategoryMenuOptions (menu_group_id, option_value, sort_order, created_at, updated_at)
SELECT cmg.menu_group_id, mo.option_value, mo.sort_order, SYSDATETIME(), SYSDATETIME()
FROM @MenuOptions mo
INNER JOIN dbo.bs_Categories c ON c.category_name = mo.category_name
INNER JOIN dbo.bs_CategoryMenuGroups cmg ON cmg.category_id = c.category_id AND cmg.group_title = mo.group_title;

DECLARE @CategoryFilters TABLE (category_name NVARCHAR(120), filter_key NVARCHAR(80), filter_label NVARCHAR(120), sort_order INT);
INSERT INTO @CategoryFilters (category_name, filter_key, filter_label, sort_order) VALUES
    (N'Laptops', N'brand', N'Brand', 1),
    (N'Laptops', N'purpose', N'Purpose', 2),
    (N'Laptops', N'cpu', N'CPU', 3),
    (N'Laptops', N'gpu', N'GPU', 4),
    (N'Laptops', N'ram', N'RAM', 5),
    (N'Laptops', N'storage', N'Storage', 6),
    (N'Laptops', N'display', N'Display', 7),
    (N'Laptops', N'battery', N'Battery', 8),
    (N'Mouse', N'brand', N'Brand', 1),
    (N'Mouse', N'purpose', N'Purpose', 2),
    (N'Mouse', N'sensor', N'Sensor', 3),
    (N'Mouse', N'dpi', N'DPI', 4),
    (N'Mouse', N'connection', N'Connection', 5),
    (N'Mouse', N'weight', N'Weight', 6),
    (N'Keyboards', N'brand', N'Brand', 1),
    (N'Keyboards', N'purpose', N'Purpose', 2),
    (N'Keyboards', N'switchType', N'Switch', 3),
    (N'Keyboards', N'layout', N'Layout', 4),
    (N'Keyboards', N'connection', N'Connection', 5),
    (N'Keyboards', N'backlight', N'Backlight', 6),
    (N'Monitors', N'brand', N'Brand', 1),
    (N'Monitors', N'purpose', N'Purpose', 2),
    (N'Monitors', N'size', N'Size', 3),
    (N'Monitors', N'resolution', N'Resolution', 4),
    (N'Monitors', N'refreshRate', N'Refresh Rate', 5),
    (N'Monitors', N'panel', N'Panel', 6),
    (N'SSD', N'brand', N'Brand', 1),
    (N'SSD', N'purpose', N'Purpose', 2),
    (N'SSD', N'capacity', N'Capacity', 3),
    (N'SSD', N'interfaceType', N'Interface', 4),
    (N'SSD', N'readSpeed', N'Read Speed', 5),
    (N'SSD', N'formFactor', N'Form Factor', 6),
    (N'RAM', N'brand', N'Brand', 1),
    (N'RAM', N'purpose', N'Purpose', 2),
    (N'RAM', N'capacity', N'Capacity', 3),
    (N'RAM', N'memoryType', N'Memory Type', 4),
    (N'RAM', N'bus', N'Bus Speed', 5),
    (N'RAM', N'formFactor', N'Form Factor', 6),
    (N'CPU', N'brand', N'Brand', 1),
    (N'CPU', N'purpose', N'Purpose', 2),
    (N'CPU', N'socket', N'Socket', 3),
    (N'CPU', N'cores', N'Cores', 4),
    (N'CPU', N'threads', N'Threads', 5),
    (N'CPU', N'tdp', N'TDP', 6),
    (N'GPU', N'brand', N'Brand', 1),
    (N'GPU', N'purpose', N'Purpose', 2),
    (N'GPU', N'chipset', N'Chipset', 3),
    (N'GPU', N'vram', N'VRAM', 4),
    (N'GPU', N'power', N'Power', 5),
    (N'GPU', N'ports', N'Ports', 6),
    (N'PC Case', N'brand', N'Brand', 1),
    (N'PC Case', N'purpose', N'Purpose', 2),
    (N'PC Case', N'caseType', N'Case Type', 3),
    (N'PC Case', N'motherboardSupport', N'Motherboard Support', 4),
    (N'PC Case', N'color', N'Color', 5),
    (N'PC Case', N'fanSupport', N'Fan Support', 6),
    (N'Mainboard', N'brand', N'Brand', 1),
    (N'Mainboard', N'purpose', N'Purpose', 2),
    (N'Mainboard', N'socket', N'Socket', 3),
    (N'Mainboard', N'chipset', N'Chipset', 4),
    (N'Mainboard', N'formFactor', N'Form Factor', 5),
    (N'Mainboard', N'memoryType', N'Memory Type', 6),
    (N'PC Fan', N'brand', N'Brand', 1),
    (N'PC Fan', N'purpose', N'Purpose', 2),
    (N'PC Fan', N'size', N'Size', 3),
    (N'PC Fan', N'speed', N'Speed', 4),
    (N'PC Fan', N'airflow', N'Airflow', 5),
    (N'PC Fan', N'lighting', N'Lighting', 6);
INSERT INTO dbo.bs_CategoryFilters (category_id, filter_key, filter_label, sort_order, created_at, updated_at)
SELECT c.category_id, cf.filter_key, cf.filter_label, cf.sort_order, SYSDATETIME(), SYSDATETIME()
FROM @CategoryFilters cf
INNER JOIN dbo.bs_Categories c ON c.category_name = cf.category_name;

DECLARE @FilterOptions TABLE (category_name NVARCHAR(120), filter_key NVARCHAR(80), option_value NVARCHAR(250), sort_order INT);
INSERT INTO @FilterOptions (category_name, filter_key, option_value, sort_order) VALUES
    (N'Laptops', N'brand', N'Apple', 1),
    (N'Laptops', N'brand', N'Dell', 2),
    (N'Laptops', N'brand', N'ASUS', 3),
    (N'Laptops', N'brand', N'Lenovo', 4),
    (N'Laptops', N'brand', N'HP', 5),
    (N'Laptops', N'brand', N'Acer', 6),
    (N'Laptops', N'brand', N'MSI', 7),
    (N'Laptops', N'brand', N'Gigabyte', 8),
    (N'Laptops', N'brand', N'Microsoft Surface', 9),
    (N'Laptops', N'brand', N'LG', 10),
    (N'Laptops', N'purpose', N'Gaming', 1),
    (N'Laptops', N'purpose', N'Workstation', 2),
    (N'Laptops', N'purpose', N'Student', 3),
    (N'Laptops', N'purpose', N'Business', 4),
    (N'Laptops', N'purpose', N'Office', 5),
    (N'Laptops', N'purpose', N'Creator', 6),
    (N'Laptops', N'purpose', N'Thin and light', 7),
    (N'Laptops', N'purpose', N'AI laptop', 8),
    (N'Laptops', N'cpu', N'Intel Core i3', 1),
    (N'Laptops', N'cpu', N'Intel Core i5', 2),
    (N'Laptops', N'cpu', N'Intel Core i7', 3),
    (N'Laptops', N'cpu', N'Intel Core i9', 4),
    (N'Laptops', N'cpu', N'Intel Core Ultra 5', 5),
    (N'Laptops', N'cpu', N'Intel Core Ultra 7', 6),
    (N'Laptops', N'cpu', N'Intel Core Ultra 9', 7),
    (N'Laptops', N'cpu', N'AMD Ryzen 5', 8),
    (N'Laptops', N'cpu', N'AMD Ryzen 7', 9),
    (N'Laptops', N'cpu', N'AMD Ryzen 9', 10),
    (N'Laptops', N'cpu', N'Apple M3', 11),
    (N'Laptops', N'cpu', N'Apple M4', 12),
    (N'Laptops', N'gpu', N'Integrated', 1),
    (N'Laptops', N'gpu', N'Intel Arc Graphics', 2),
    (N'Laptops', N'gpu', N'RTX 3050', 3),
    (N'Laptops', N'gpu', N'RTX 4050', 4),
    (N'Laptops', N'gpu', N'RTX 4060', 5),
    (N'Laptops', N'gpu', N'RTX 4070', 6),
    (N'Laptops', N'gpu', N'RTX 4080', 7),
    (N'Laptops', N'gpu', N'RTX 4090', 8),
    (N'Laptops', N'gpu', N'Radeon 780M', 9),
    (N'Laptops', N'ram', N'8GB', 1),
    (N'Laptops', N'ram', N'16GB', 2),
    (N'Laptops', N'ram', N'24GB', 3),
    (N'Laptops', N'ram', N'32GB', 4),
    (N'Laptops', N'ram', N'64GB', 5),
    (N'Laptops', N'storage', N'256GB SSD', 1),
    (N'Laptops', N'storage', N'512GB SSD', 2),
    (N'Laptops', N'storage', N'1TB SSD', 3),
    (N'Laptops', N'storage', N'2TB SSD', 4),
    (N'Laptops', N'storage', N'4TB SSD', 5),
    (N'Laptops', N'display', N'13.3 inch FHD', 1),
    (N'Laptops', N'display', N'13.6 inch Retina', 2),
    (N'Laptops', N'display', N'14 inch QHD', 3),
    (N'Laptops', N'display', N'14 inch 2.8K OLED', 4),
    (N'Laptops', N'display', N'15.6 inch FHD', 5),
    (N'Laptops', N'display', N'16 inch OLED', 6),
    (N'Laptops', N'display', N'17.3 inch QHD', 7),
    (N'Laptops', N'battery', N'41Wh', 1),
    (N'Laptops', N'battery', N'52Wh', 2),
    (N'Laptops', N'battery', N'55Wh', 3),
    (N'Laptops', N'battery', N'65Wh', 4),
    (N'Laptops', N'battery', N'76Wh', 5),
    (N'Laptops', N'battery', N'90Wh', 6),
    (N'Laptops', N'battery', N'99Wh', 7),
    (N'Mouse', N'brand', N'Logitech', 1),
    (N'Mouse', N'brand', N'Razer', 2),
    (N'Mouse', N'brand', N'SteelSeries', 3),
    (N'Mouse', N'brand', N'Corsair', 4),
    (N'Mouse', N'brand', N'ASUS', 5),
    (N'Mouse', N'brand', N'Glorious', 6),
    (N'Mouse', N'brand', N'Pulsar', 7),
    (N'Mouse', N'brand', N'Zowie', 8),
    (N'Mouse', N'purpose', N'Gaming', 1),
    (N'Mouse', N'purpose', N'Office', 2),
    (N'Mouse', N'purpose', N'Wireless', 3),
    (N'Mouse', N'purpose', N'Ergonomic', 4),
    (N'Mouse', N'purpose', N'FPS', 5),
    (N'Mouse', N'purpose', N'MOBA', 6),
    (N'Mouse', N'purpose', N'Travel', 7),
    (N'Mouse', N'sensor', N'Hero 25K', 1),
    (N'Mouse', N'sensor', N'Focus Pro', 2),
    (N'Mouse', N'sensor', N'TrueMove Air', 3),
    (N'Mouse', N'sensor', N'PixArt 3395', 4),
    (N'Mouse', N'sensor', N'PixArt 3370', 5),
    (N'Mouse', N'sensor', N'ROG AimPoint', 6),
    (N'Mouse', N'dpi', N'12000 DPI', 1),
    (N'Mouse', N'dpi', N'16000 DPI', 2),
    (N'Mouse', N'dpi', N'18000 DPI', 3),
    (N'Mouse', N'dpi', N'26000 DPI', 4),
    (N'Mouse', N'dpi', N'30000 DPI', 5),
    (N'Mouse', N'dpi', N'32000 DPI', 6),
    (N'Mouse', N'dpi', N'36000 DPI', 7),
    (N'Mouse', N'connection', N'Bluetooth', 1),
    (N'Mouse', N'connection', N'2.4GHz', 2),
    (N'Mouse', N'connection', N'USB-C wired', 3),
    (N'Mouse', N'connection', N'Tri-mode', 4),
    (N'Mouse', N'weight', N'Under 50g', 1),
    (N'Mouse', N'weight', N'50g - 60g', 2),
    (N'Mouse', N'weight', N'60g - 70g', 3),
    (N'Mouse', N'weight', N'70g - 90g', 4),
    (N'Mouse', N'weight', N'Over 90g', 5),
    (N'Keyboards', N'brand', N'Keychron', 1),
    (N'Keyboards', N'brand', N'Logitech', 2),
    (N'Keyboards', N'brand', N'Razer', 3),
    (N'Keyboards', N'brand', N'Akko', 4),
    (N'Keyboards', N'brand', N'Corsair', 5),
    (N'Keyboards', N'brand', N'Ducky', 6),
    (N'Keyboards', N'brand', N'Leopold', 7),
    (N'Keyboards', N'brand', N'ASUS', 8),
    (N'Keyboards', N'purpose', N'Gaming', 1),
    (N'Keyboards', N'purpose', N'Office', 2),
    (N'Keyboards', N'purpose', N'Wireless', 3),
    (N'Keyboards', N'purpose', N'Compact', 4),
    (N'Keyboards', N'purpose', N'Creator', 5),
    (N'Keyboards', N'purpose', N'Typing', 6),
    (N'Keyboards', N'switchType', N'Red', 1),
    (N'Keyboards', N'switchType', N'Brown', 2),
    (N'Keyboards', N'switchType', N'Blue', 3),
    (N'Keyboards', N'switchType', N'Silver', 4),
    (N'Keyboards', N'switchType', N'Optical', 5),
    (N'Keyboards', N'switchType', N'Magnetic', 6),
    (N'Keyboards', N'switchType', N'Low profile', 7),
    (N'Keyboards', N'layout', N'60%', 1),
    (N'Keyboards', N'layout', N'65%', 2),
    (N'Keyboards', N'layout', N'75%', 3),
    (N'Keyboards', N'layout', N'TKL', 4),
    (N'Keyboards', N'layout', N'Full-size', 5),
    (N'Keyboards', N'layout', N'Alice', 6),
    (N'Keyboards', N'connection', N'Bluetooth', 1),
    (N'Keyboards', N'connection', N'2.4GHz', 2),
    (N'Keyboards', N'connection', N'USB-C wired', 3),
    (N'Keyboards', N'connection', N'Tri-mode', 4),
    (N'Keyboards', N'backlight', N'None', 1),
    (N'Keyboards', N'backlight', N'White', 2),
    (N'Keyboards', N'backlight', N'RGB', 3),
    (N'Keyboards', N'backlight', N'Per-key RGB', 4),
    (N'Monitors', N'brand', N'LG', 1),
    (N'Monitors', N'brand', N'Samsung', 2),
    (N'Monitors', N'brand', N'Dell', 3),
    (N'Monitors', N'brand', N'ASUS', 4),
    (N'Monitors', N'brand', N'AOC', 5),
    (N'Monitors', N'brand', N'MSI', 6),
    (N'Monitors', N'brand', N'Gigabyte', 7),
    (N'Monitors', N'brand', N'BenQ', 8),
    (N'Monitors', N'purpose', N'Gaming', 1),
    (N'Monitors', N'purpose', N'Design', 2),
    (N'Monitors', N'purpose', N'Office', 3),
    (N'Monitors', N'purpose', N'Ultrawide', 4),
    (N'Monitors', N'purpose', N'Console', 5),
    (N'Monitors', N'purpose', N'Programming', 6),
    (N'Monitors', N'size', N'24 inch', 1),
    (N'Monitors', N'size', N'25 inch', 2),
    (N'Monitors', N'size', N'27 inch', 3),
    (N'Monitors', N'size', N'32 inch', 4),
    (N'Monitors', N'size', N'34 inch', 5),
    (N'Monitors', N'size', N'49 inch', 6),
    (N'Monitors', N'resolution', N'Full HD', 1),
    (N'Monitors', N'resolution', N'QHD', 2),
    (N'Monitors', N'resolution', N'4K UHD', 3),
    (N'Monitors', N'resolution', N'5K', 4),
    (N'Monitors', N'resolution', N'Ultrawide', 5),
    (N'Monitors', N'resolution', N'Super Ultrawide', 6),
    (N'Monitors', N'refreshRate', N'60Hz', 1),
    (N'Monitors', N'refreshRate', N'75Hz', 2),
    (N'Monitors', N'refreshRate', N'100Hz', 3),
    (N'Monitors', N'refreshRate', N'144Hz', 4),
    (N'Monitors', N'refreshRate', N'165Hz', 5),
    (N'Monitors', N'refreshRate', N'180Hz', 6),
    (N'Monitors', N'refreshRate', N'240Hz', 7),
    (N'Monitors', N'refreshRate', N'360Hz', 8),
    (N'Monitors', N'panel', N'IPS', 1),
    (N'Monitors', N'panel', N'Nano IPS', 2),
    (N'Monitors', N'panel', N'IPS Black', 3),
    (N'Monitors', N'panel', N'VA', 4),
    (N'Monitors', N'panel', N'OLED', 5),
    (N'Monitors', N'panel', N'QD-OLED', 6),
    (N'Monitors', N'panel', N'TN', 7),
    (N'SSD', N'brand', N'Samsung', 1),
    (N'SSD', N'brand', N'WD', 2),
    (N'SSD', N'brand', N'Kingston', 3),
    (N'SSD', N'brand', N'Crucial', 4),
    (N'SSD', N'brand', N'Seagate', 5),
    (N'SSD', N'brand', N'Lexar', 6),
    (N'SSD', N'brand', N'ADATA', 7),
    (N'SSD', N'brand', N'Corsair', 8),
    (N'SSD', N'purpose', N'Boot drive', 1),
    (N'SSD', N'purpose', N'Gaming', 2),
    (N'SSD', N'purpose', N'Creator', 3),
    (N'SSD', N'purpose', N'NAS', 4),
    (N'SSD', N'purpose', N'Portable', 5),
    (N'SSD', N'purpose', N'PS5 upgrade', 6),
    (N'SSD', N'capacity', N'250GB', 1),
    (N'SSD', N'capacity', N'500GB', 2),
    (N'SSD', N'capacity', N'1TB', 3),
    (N'SSD', N'capacity', N'2TB', 4),
    (N'SSD', N'capacity', N'4TB', 5),
    (N'SSD', N'capacity', N'8TB', 6),
    (N'SSD', N'interfaceType', N'SATA', 1),
    (N'SSD', N'interfaceType', N'PCIe 3.0', 2),
    (N'SSD', N'interfaceType', N'PCIe 4.0', 3),
    (N'SSD', N'interfaceType', N'PCIe 5.0', 4),
    (N'SSD', N'interfaceType', N'USB 3.2', 5),
    (N'SSD', N'readSpeed', N'560MB/s', 1),
    (N'SSD', N'readSpeed', N'3500MB/s', 2),
    (N'SSD', N'readSpeed', N'5000MB/s', 3),
    (N'SSD', N'readSpeed', N'7000MB/s', 4),
    (N'SSD', N'readSpeed', N'7450MB/s', 5),
    (N'SSD', N'readSpeed', N'10000MB/s', 6),
    (N'SSD', N'readSpeed', N'12000MB/s', 7),
    (N'SSD', N'formFactor', N'2.5 inch', 1),
    (N'SSD', N'formFactor', N'M.2 2230', 2),
    (N'SSD', N'formFactor', N'M.2 2242', 3),
    (N'SSD', N'formFactor', N'M.2 2280', 4),
    (N'SSD', N'formFactor', N'External', 5),
    (N'RAM', N'brand', N'Corsair', 1),
    (N'RAM', N'brand', N'Kingston', 2),
    (N'RAM', N'brand', N'G.Skill', 3),
    (N'RAM', N'brand', N'Crucial', 4),
    (N'RAM', N'brand', N'TeamGroup', 5),
    (N'RAM', N'brand', N'ADATA', 6),
    (N'RAM', N'brand', N'Patriot', 7),
    (N'RAM', N'purpose', N'Laptop upgrade', 1),
    (N'RAM', N'purpose', N'Gaming PC', 2),
    (N'RAM', N'purpose', N'Workstation', 3),
    (N'RAM', N'purpose', N'RGB build', 4),
    (N'RAM', N'purpose', N'Office PC', 5),
    (N'RAM', N'capacity', N'8GB', 1),
    (N'RAM', N'capacity', N'16GB', 2),
    (N'RAM', N'capacity', N'32GB', 3),
    (N'RAM', N'capacity', N'48GB', 4),
    (N'RAM', N'capacity', N'64GB', 5),
    (N'RAM', N'capacity', N'96GB', 6),
    (N'RAM', N'capacity', N'128GB', 7),
    (N'RAM', N'memoryType', N'DDR3', 1),
    (N'RAM', N'memoryType', N'DDR4', 2),
    (N'RAM', N'memoryType', N'DDR5', 3),
    (N'RAM', N'memoryType', N'LPDDR5', 4),
    (N'RAM', N'memoryType', N'SODIMM', 5),
    (N'RAM', N'bus', N'2666MHz', 1),
    (N'RAM', N'bus', N'3200MHz', 2),
    (N'RAM', N'bus', N'3600MHz', 3),
    (N'RAM', N'bus', N'4800MHz', 4),
    (N'RAM', N'bus', N'5200MHz', 5),
    (N'RAM', N'bus', N'5600MHz', 6),
    (N'RAM', N'bus', N'6000MHz', 7),
    (N'RAM', N'bus', N'6400MHz', 8),
    (N'RAM', N'bus', N'7200MHz', 9),
    (N'RAM', N'formFactor', N'DIMM', 1),
    (N'RAM', N'formFactor', N'SODIMM', 2),
    (N'RAM', N'formFactor', N'CAMM2', 3),
    (N'CPU', N'brand', N'Intel', 1),
    (N'CPU', N'brand', N'AMD', 2),
    (N'CPU', N'purpose', N'Gaming', 1),
    (N'CPU', N'purpose', N'Office', 2),
    (N'CPU', N'purpose', N'Streaming', 3),
    (N'CPU', N'purpose', N'Workstation', 4),
    (N'CPU', N'purpose', N'Budget PC', 5),
    (N'CPU', N'purpose', N'Creator', 6),
    (N'CPU', N'socket', N'LGA1200', 1),
    (N'CPU', N'socket', N'LGA1700', 2),
    (N'CPU', N'socket', N'LGA1851', 3),
    (N'CPU', N'socket', N'AM4', 4),
    (N'CPU', N'socket', N'AM5', 5),
    (N'CPU', N'socket', N'sTRX4', 6),
    (N'CPU', N'cores', N'4 cores', 1),
    (N'CPU', N'cores', N'6 cores', 2),
    (N'CPU', N'cores', N'8 cores', 3),
    (N'CPU', N'cores', N'10 cores', 4),
    (N'CPU', N'cores', N'12 cores', 5),
    (N'CPU', N'cores', N'14 cores', 6),
    (N'CPU', N'cores', N'16 cores', 7),
    (N'CPU', N'cores', N'24 cores', 8),
    (N'CPU', N'cores', N'32 cores', 9),
    (N'CPU', N'threads', N'8 threads', 1),
    (N'CPU', N'threads', N'12 threads', 2),
    (N'CPU', N'threads', N'16 threads', 3),
    (N'CPU', N'threads', N'20 threads', 4),
    (N'CPU', N'threads', N'24 threads', 5),
    (N'CPU', N'threads', N'28 threads', 6),
    (N'CPU', N'threads', N'32 threads', 7),
    (N'CPU', N'threads', N'64 threads', 8),
    (N'CPU', N'tdp', N'35W', 1),
    (N'CPU', N'tdp', N'65W', 2),
    (N'CPU', N'tdp', N'95W', 3),
    (N'CPU', N'tdp', N'105W', 4),
    (N'CPU', N'tdp', N'120W', 5),
    (N'CPU', N'tdp', N'125W', 6),
    (N'CPU', N'tdp', N'170W', 7),
    (N'CPU', N'tdp', N'280W', 8),
    (N'GPU', N'brand', N'ASUS', 1),
    (N'GPU', N'brand', N'MSI', 2),
    (N'GPU', N'brand', N'Gigabyte', 3),
    (N'GPU', N'brand', N'Sapphire', 4),
    (N'GPU', N'brand', N'Zotac', 5),
    (N'GPU', N'brand', N'PNY', 6),
    (N'GPU', N'brand', N'PowerColor', 7),
    (N'GPU', N'brand', N'Galax', 8),
    (N'GPU', N'purpose', N'1080p gaming', 1),
    (N'GPU', N'purpose', N'1440p gaming', 2),
    (N'GPU', N'purpose', N'4K gaming', 3),
    (N'GPU', N'purpose', N'AI work', 4),
    (N'GPU', N'purpose', N'Streaming', 5),
    (N'GPU', N'purpose', N'Creator', 6),
    (N'GPU', N'chipset', N'RTX 3050', 1),
    (N'GPU', N'chipset', N'RTX 4060', 2),
    (N'GPU', N'chipset', N'RTX 4060 Ti', 3),
    (N'GPU', N'chipset', N'RTX 4070', 4),
    (N'GPU', N'chipset', N'RTX 4070 Super', 5),
    (N'GPU', N'chipset', N'RTX 4080', 6),
    (N'GPU', N'chipset', N'RTX 4090', 7),
    (N'GPU', N'chipset', N'RX 7600', 8),
    (N'GPU', N'chipset', N'RX 7700 XT', 9),
    (N'GPU', N'chipset', N'RX 7800 XT', 10),
    (N'GPU', N'chipset', N'RX 7900 XTX', 11),
    (N'GPU', N'vram', N'6GB', 1),
    (N'GPU', N'vram', N'8GB', 2),
    (N'GPU', N'vram', N'10GB', 3),
    (N'GPU', N'vram', N'12GB', 4),
    (N'GPU', N'vram', N'16GB', 5),
    (N'GPU', N'vram', N'20GB', 6),
    (N'GPU', N'vram', N'24GB', 7),
    (N'GPU', N'power', N'115W', 1),
    (N'GPU', N'power', N'160W', 2),
    (N'GPU', N'power', N'200W', 3),
    (N'GPU', N'power', N'220W', 4),
    (N'GPU', N'power', N'263W', 5),
    (N'GPU', N'power', N'320W', 6),
    (N'GPU', N'power', N'355W', 7),
    (N'GPU', N'power', N'450W', 8),
    (N'GPU', N'ports', N'HDMI', 1),
    (N'GPU', N'ports', N'DisplayPort', 2),
    (N'GPU', N'ports', N'HDMI', 3),
    (N'GPU', N'ports', N'3x DisplayPort', 4),
    (N'GPU', N'ports', N'2x HDMI', 5),
    (N'GPU', N'ports', N'2x DisplayPort', 6),
    (N'GPU', N'ports', N'USB-C', 7),
    (N'GPU', N'ports', N'HDMI', 8),
    (N'GPU', N'ports', N'DisplayPort', 9),
    (N'PC Case', N'brand', N'NZXT', 1),
    (N'PC Case', N'brand', N'Corsair', 2),
    (N'PC Case', N'brand', N'Lian Li', 3),
    (N'PC Case', N'brand', N'Cooler Master', 4),
    (N'PC Case', N'brand', N'DeepCool', 5),
    (N'PC Case', N'brand', N'Fractal Design', 6),
    (N'PC Case', N'brand', N'Phanteks', 7),
    (N'PC Case', N'purpose', N'Compact', 1),
    (N'PC Case', N'purpose', N'Airflow', 2),
    (N'PC Case', N'purpose', N'Showcase', 3),
    (N'PC Case', N'purpose', N'Silent build', 4),
    (N'PC Case', N'purpose', N'Water cooling', 5),
    (N'PC Case', N'purpose', N'Budget build', 6),
    (N'PC Case', N'caseType', N'Mini tower', 1),
    (N'PC Case', N'caseType', N'Mid tower', 2),
    (N'PC Case', N'caseType', N'Full tower', 3),
    (N'PC Case', N'caseType', N'Open frame', 4),
    (N'PC Case', N'motherboardSupport', N'Mini-ITX', 1),
    (N'PC Case', N'motherboardSupport', N'Micro-ATX', 2),
    (N'PC Case', N'motherboardSupport', N'ATX', 3),
    (N'PC Case', N'motherboardSupport', N'E-ATX', 4),
    (N'PC Case', N'color', N'Black', 1),
    (N'PC Case', N'color', N'White', 2),
    (N'PC Case', N'color', N'Gray', 3),
    (N'PC Case', N'color', N'Silver', 4),
    (N'PC Case', N'fanSupport', N'3 fans', 1),
    (N'PC Case', N'fanSupport', N'5 fans', 2),
    (N'PC Case', N'fanSupport', N'6 fans', 3),
    (N'PC Case', N'fanSupport', N'8 fans', 4),
    (N'PC Case', N'fanSupport', N'9 fans', 5),
    (N'PC Case', N'fanSupport', N'10 fans', 6),
    (N'PC Case', N'fanSupport', N'12 fans', 7),
    (N'Mainboard', N'brand', N'ASUS', 1),
    (N'Mainboard', N'brand', N'MSI', 2),
    (N'Mainboard', N'brand', N'Gigabyte', 3),
    (N'Mainboard', N'brand', N'ASRock', 4),
    (N'Mainboard', N'brand', N'Biostar', 5),
    (N'Mainboard', N'brand', N'NZXT', 6),
    (N'Mainboard', N'purpose', N'Budget PC', 1),
    (N'Mainboard', N'purpose', N'Gaming', 2),
    (N'Mainboard', N'purpose', N'Creator', 3),
    (N'Mainboard', N'purpose', N'Overclocking', 4),
    (N'Mainboard', N'purpose', N'Workstation', 5),
    (N'Mainboard', N'purpose', N'Small form factor', 6),
    (N'Mainboard', N'socket', N'LGA1200', 1),
    (N'Mainboard', N'socket', N'LGA1700', 2),
    (N'Mainboard', N'socket', N'LGA1851', 3),
    (N'Mainboard', N'socket', N'AM4', 4),
    (N'Mainboard', N'socket', N'AM5', 5),
    (N'Mainboard', N'chipset', N'H610', 1),
    (N'Mainboard', N'chipset', N'B660', 2),
    (N'Mainboard', N'chipset', N'B760', 3),
    (N'Mainboard', N'chipset', N'Z790', 4),
    (N'Mainboard', N'chipset', N'A520', 5),
    (N'Mainboard', N'chipset', N'B550', 6),
    (N'Mainboard', N'chipset', N'X570', 7),
    (N'Mainboard', N'chipset', N'A620', 8),
    (N'Mainboard', N'chipset', N'B650', 9),
    (N'Mainboard', N'chipset', N'X670', 10),
    (N'Mainboard', N'formFactor', N'Mini-ITX', 1),
    (N'Mainboard', N'formFactor', N'Micro-ATX', 2),
    (N'Mainboard', N'formFactor', N'ATX', 3),
    (N'Mainboard', N'formFactor', N'E-ATX', 4),
    (N'Mainboard', N'memoryType', N'DDR4', 1),
    (N'Mainboard', N'memoryType', N'DDR5', 2),
    (N'PC Fan', N'brand', N'Noctua', 1),
    (N'PC Fan', N'brand', N'Corsair', 2),
    (N'PC Fan', N'brand', N'Cooler Master', 3),
    (N'PC Fan', N'brand', N'DeepCool', 4),
    (N'PC Fan', N'brand', N'Arctic', 5),
    (N'PC Fan', N'brand', N'Lian Li', 6),
    (N'PC Fan', N'brand', N'Thermaltake', 7),
    (N'PC Fan', N'purpose', N'Silent', 1),
    (N'PC Fan', N'purpose', N'RGB', 2),
    (N'PC Fan', N'purpose', N'Radiator', 3),
    (N'PC Fan', N'purpose', N'High airflow', 4),
    (N'PC Fan', N'purpose', N'Static pressure', 5),
    (N'PC Fan', N'purpose', N'Budget cooling', 6),
    (N'PC Fan', N'size', N'80mm', 1),
    (N'PC Fan', N'size', N'92mm', 2),
    (N'PC Fan', N'size', N'120mm', 3),
    (N'PC Fan', N'size', N'140mm', 4),
    (N'PC Fan', N'size', N'200mm', 5),
    (N'PC Fan', N'speed', N'1200 RPM', 1),
    (N'PC Fan', N'speed', N'1500 RPM', 2),
    (N'PC Fan', N'speed', N'1700 RPM', 3),
    (N'PC Fan', N'speed', N'1800 RPM', 4),
    (N'PC Fan', N'speed', N'1850 RPM', 5),
    (N'PC Fan', N'speed', N'2000 RPM', 6),
    (N'PC Fan', N'speed', N'2100 RPM', 7),
    (N'PC Fan', N'speed', N'3000 RPM', 8),
    (N'PC Fan', N'airflow', N'40 CFM', 1),
    (N'PC Fan', N'airflow', N'50 CFM', 2),
    (N'PC Fan', N'airflow', N'60 CFM', 3),
    (N'PC Fan', N'airflow', N'62 CFM', 4),
    (N'PC Fan', N'airflow', N'65 CFM', 5),
    (N'PC Fan', N'airflow', N'68 CFM', 6),
    (N'PC Fan', N'airflow', N'72 CFM', 7),
    (N'PC Fan', N'airflow', N'90 CFM', 8),
    (N'PC Fan', N'lighting', N'None', 1),
    (N'PC Fan', N'lighting', N'White LED', 2),
    (N'PC Fan', N'lighting', N'RGB', 3),
    (N'PC Fan', N'lighting', N'ARGB', 4);
INSERT INTO dbo.bs_CategoryFilterOptions (category_filter_id, option_value, sort_order, created_at, updated_at)
SELECT cf.category_filter_id, fo.option_value, fo.sort_order, SYSDATETIME(), SYSDATETIME()
FROM @FilterOptions fo
INNER JOIN dbo.bs_Categories c ON c.category_name = fo.category_name
INNER JOIN dbo.bs_CategoryFilters cf ON cf.category_id = c.category_id AND cf.filter_key = fo.filter_key;

COMMIT TRANSACTION;

GO

/* Idempotent demo accounts and orders for the admin dashboard/report screens. */
SET NOCOUNT ON;
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;

DECLARE @CustomerRoleId INT=(SELECT role_id FROM dbo.bs_Roles WHERE role_name=N'User');
DECLARE @StaffRoleId INT=(SELECT role_id FROM dbo.bs_Roles WHERE role_name=N'Staff');
IF @CustomerRoleId IS NULL
    THROW 50001, 'Run admin_management_migration.sql before admin_demo_data.sql.', 1;

DECLARE @PasswordHash NVARCHAR(255)=N'pbkdf2$65536$4u0S7QtsuN6xRdP/ibP+NQ==$AZzekFD608x0d6OS0AZUAIPyhysmAz+xjH8kPgtBkpY=';
IF NOT EXISTS(SELECT 1 FROM dbo.bs_user WHERE email=N'staff.demo@example.com')
    INSERT INTO dbo.bs_user(role_id,full_name,email,phone,password,isverified,status,created_at,updated_at)
    VALUES(@StaffRoleId,N'Demo Staff',N'staff.demo@example.com',N'0902000001',@PasswordHash,1,N'Active',SYSUTCDATETIME(),SYSUTCDATETIME());
ELSE
    UPDATE dbo.bs_user SET role_id=@StaffRoleId,full_name=N'Demo Staff',status=N'Active',isverified=1,updated_at=SYSUTCDATETIME() WHERE email=N'staff.demo@example.com';

DECLARE @Customers TABLE(email NVARCHAR(255),full_name NVARCHAR(150),phone NVARCHAR(20));
INSERT INTO @Customers VALUES
    (N'customer.demo1@example.com',N'Demo Customer One',N'0901000001'),
    (N'customer.demo2@example.com',N'Demo Customer Two',N'0901000002'),
    (N'customer.demo3@example.com',N'Demo Customer Three',N'0901000003');

MERGE dbo.bs_user AS target
USING @Customers AS source ON target.email=source.email
WHEN MATCHED THEN UPDATE SET
    role_id=@CustomerRoleId,full_name=source.full_name,phone=source.phone,
    isverified=1,status=N'Active',updated_at=SYSUTCDATETIME()
WHEN NOT MATCHED THEN
    INSERT(role_id,full_name,email,phone,password,isverified,status,created_at,updated_at)
    VALUES(@CustomerRoleId,source.full_name,source.email,source.phone,@PasswordHash,1,N'Active',SYSUTCDATETIME(),SYSUTCDATETIME());

IF NOT EXISTS(SELECT 1 FROM dbo.bs_StockReceipts WHERE note=N'DEMO-STOCK-001')
BEGIN
    DECLARE @DemoProductId INT=(SELECT product_id FROM dbo.bs_Products WHERE sku=N'LAPTOPS-0001');
    DECLARE @DemoAdminId INT=(SELECT user_id FROM dbo.bs_user WHERE email=N'administrator@example.com');
    DECLARE @PreviousStock INT=(SELECT stock FROM dbo.bs_Products WHERE product_id=@DemoProductId);
    UPDATE dbo.bs_Products SET stock=stock+10,status=CASE WHEN status=N'Out of Stock' THEN N'Active' ELSE status END,updated_at=SYSUTCDATETIME() WHERE product_id=@DemoProductId;
    INSERT INTO dbo.bs_StockReceipts(product_id,quantity,previous_stock,resulting_stock,note,admin_id)
    VALUES(@DemoProductId,10,@PreviousStock,@PreviousStock+10,N'DEMO-STOCK-001',@DemoAdminId);
END

DECLARE @DemoOrders TABLE(
    code NVARCHAR(50),email NVARCHAR(255),sku NVARCHAR(80),quantity INT,
    order_status NVARCHAR(20),payment_method NVARCHAR(30),days_ago INT
);
INSERT INTO @DemoOrders VALUES
    (N'DEMO-ORDER-001',N'customer.demo1@example.com',N'LAPTOPS-0001',1,N'Pending',N'COD',1),
    (N'DEMO-ORDER-002',N'customer.demo2@example.com',N'MOUSE-0006',2,N'Confirmed',N'COD',3),
    (N'DEMO-ORDER-003',N'customer.demo3@example.com',N'KEYBOARDS-0011',1,N'Shipping',N'Bank Transfer',7),
    (N'DEMO-ORDER-004',N'customer.demo1@example.com',N'MONITORS-0016',2,N'Delivered',N'VNPay',14),
    (N'DEMO-ORDER-005',N'customer.demo2@example.com',N'SSD-0021',1,N'Cancelled',N'COD',25),
    (N'DEMO-ORDER-006',N'customer.demo3@example.com',N'RAM-0026',2,N'Payment Failed',N'VNPay',40);

INSERT INTO dbo.bs_Orders(
    user_id,voucher_id,total_amount,shipping_fee,discount_amount,payment_method,
    order_status,phone,note,address_info,created_at,updated_at
)
SELECT u.user_id,NULL,p.price*d.quantity,30000,0,d.payment_method,d.order_status,u.phone,d.code,
       N'123 Demo Street, Ho Chi Minh City',DATEADD(day,-d.days_ago,SYSUTCDATETIME()),SYSUTCDATETIME()
FROM @DemoOrders d
JOIN dbo.bs_user u ON u.email=d.email
JOIN dbo.bs_Products p ON p.sku=d.sku
WHERE NOT EXISTS(SELECT 1 FROM dbo.bs_Orders o WHERE o.note=d.code);

INSERT INTO dbo.bs_OrderDetails(order_id,product_id,quantity,unit_price,created_at)
SELECT o.order_id,p.product_id,d.quantity,p.price,o.created_at
FROM @DemoOrders d
JOIN dbo.bs_Orders o ON o.note=d.code
JOIN dbo.bs_Products p ON p.sku=d.sku
WHERE NOT EXISTS(SELECT 1 FROM dbo.bs_OrderDetails od WHERE od.order_id=o.order_id);

INSERT INTO dbo.bs_Payments(order_id,payment_method,payment_status,transaction_no,paid_at,created_at,updated_at)
SELECT o.order_id,d.payment_method,
       CASE d.order_status WHEN N'Delivered' THEN N'Paid' WHEN N'Cancelled' THEN N'Refunded'
            WHEN N'Payment Failed' THEN N'Failed' ELSE N'Pending' END,
       CASE WHEN d.payment_method=N'COD' THEN NULL ELSE N'DEMO-'+RIGHT(d.code,3) END,
       CASE WHEN d.order_status=N'Delivered' THEN o.created_at ELSE NULL END,
       o.created_at,SYSUTCDATETIME()
FROM @DemoOrders d
JOIN dbo.bs_Orders o ON o.note=d.code
WHERE NOT EXISTS(SELECT 1 FROM dbo.bs_Payments py WHERE py.order_id=o.order_id);

PRINT 'Admin demo data is ready.';
GO


/* Admin management migration. Safe to run more than once. */
IF EXISTS (SELECT 1 FROM dbo.bs_Roles WHERE role_name=N'Customer')
   AND NOT EXISTS (SELECT 1 FROM dbo.bs_Roles WHERE role_name=N'User')
    UPDATE dbo.bs_Roles SET role_name=N'User' WHERE role_name=N'Customer';
GO
IF EXISTS (SELECT 1 FROM dbo.bs_Roles WHERE role_name=N'Customer')
   AND EXISTS (SELECT 1 FROM dbo.bs_Roles WHERE role_name=N'User')
BEGIN
    DECLARE @UserRoleId INT=(SELECT role_id FROM dbo.bs_Roles WHERE role_name=N'User');
    UPDATE dbo.bs_user SET role_id=@UserRoleId WHERE role_id=(SELECT role_id FROM dbo.bs_Roles WHERE role_name=N'Customer');
    DELETE FROM dbo.bs_Roles WHERE role_name=N'Customer';
END
GO
IF NOT EXISTS (SELECT 1 FROM dbo.bs_Roles WHERE role_name=N'Staff')
    INSERT INTO dbo.bs_Roles(role_name) VALUES(N'Staff');
GO

IF COL_LENGTH('dbo.bs_Orders', 'phone') IS NULL
    ALTER TABLE dbo.bs_Orders ADD phone NVARCHAR(20) NULL;
GO
IF COL_LENGTH('dbo.bs_Reviews', 'status') IS NULL
    ALTER TABLE dbo.bs_Reviews ADD status NVARCHAR(20) NOT NULL CONSTRAINT DF_bs_Reviews_status DEFAULT ('Visible');
GO
IF COL_LENGTH('dbo.bs_Reviews', 'moderated_by') IS NULL
    ALTER TABLE dbo.bs_Reviews ADD moderated_by INT NULL;
GO
IF COL_LENGTH('dbo.bs_Reviews', 'moderated_at') IS NULL
    ALTER TABLE dbo.bs_Reviews ADD moderated_at DATETIME2(0) NULL;
GO
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name=N'CK_bs_Reviews_status')
    ALTER TABLE dbo.bs_Reviews WITH CHECK ADD CONSTRAINT CK_bs_Reviews_status CHECK (status IN ('Visible','Hidden'));
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'FK_bs_Reviews_moderated_by')
    ALTER TABLE dbo.bs_Reviews ADD CONSTRAINT FK_bs_Reviews_moderated_by FOREIGN KEY (moderated_by) REFERENCES dbo.bs_user(user_id);
GO

IF OBJECT_ID(N'dbo.bs_AdminAuditLogs', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_AdminAuditLogs (
        audit_id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_AdminAuditLogs PRIMARY KEY,
        admin_id INT NOT NULL,
        action NVARCHAR(80) NOT NULL,
        entity_type NVARCHAR(80) NOT NULL,
        entity_id NVARCHAR(80) NULL,
        details NVARCHAR(1000) NULL,
        created_at DATETIME2(0) NOT NULL CONSTRAINT DF_bs_AdminAuditLogs_created_at DEFAULT SYSUTCDATETIME(),
        CONSTRAINT FK_bs_AdminAuditLogs_user FOREIGN KEY (admin_id) REFERENCES dbo.bs_user(user_id)
    );
END
GO
IF OBJECT_ID(N'dbo.bs_StockReceipts', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_StockReceipts (
        receipt_id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_StockReceipts PRIMARY KEY,
        product_id INT NOT NULL,
        quantity INT NOT NULL,
        previous_stock INT NOT NULL,
        resulting_stock INT NOT NULL,
        note NVARCHAR(500) NULL,
        admin_id INT NOT NULL,
        created_at DATETIME2(0) NOT NULL CONSTRAINT DF_bs_StockReceipts_created_at DEFAULT SYSUTCDATETIME(),
        CONSTRAINT CK_bs_StockReceipts_quantity CHECK (quantity > 0),
        CONSTRAINT FK_bs_StockReceipts_product FOREIGN KEY (product_id) REFERENCES dbo.bs_Products(product_id),
        CONSTRAINT FK_bs_StockReceipts_admin FOREIGN KEY (admin_id) REFERENCES dbo.bs_user(user_id)
    );
END
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name=N'IX_bs_AdminAuditLogs_created_at' AND object_id=OBJECT_ID(N'dbo.bs_AdminAuditLogs'))
    CREATE INDEX IX_bs_AdminAuditLogs_created_at ON dbo.bs_AdminAuditLogs(created_at DESC);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name=N'IX_bs_Reviews_status_created' AND object_id=OBJECT_ID(N'dbo.bs_Reviews'))
    CREATE INDEX IX_bs_Reviews_status_created ON dbo.bs_Reviews(status,created_at DESC);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name=N'IX_bs_Orders_status_created' AND object_id=OBJECT_ID(N'dbo.bs_Orders'))
    CREATE INDEX IX_bs_Orders_status_created ON dbo.bs_Orders(order_status,created_at DESC);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name=N'IX_bs_StockReceipts_created' AND object_id=OBJECT_ID(N'dbo.bs_StockReceipts'))
    CREATE INDEX IX_bs_StockReceipts_created ON dbo.bs_StockReceipts(created_at DESC);
GO

/* Normalize legacy status casing so text and CSS are consistent. */
UPDATE dbo.bs_Products
SET status = CASE LOWER(status)
    WHEN 'active' THEN N'Active'
    WHEN 'out of stock' THEN N'Out of Stock'
    WHEN 'hidden' THEN N'Hidden'
    WHEN 'inactive' THEN N'Inactive'
    ELSE status
END
WHERE status COLLATE Latin1_General_100_BIN2 NOT IN (N'Active',N'Out of Stock',N'Hidden',N'Inactive');
GO
PRINT 'Admin management migration completed.';
GO
