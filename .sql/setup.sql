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
        avatar         NVARCHAR(500) NULL,
        status         NVARCHAR(20) NOT NULL CONSTRAINT DF_bs_user_status DEFAULT ('Active'),
        created_at     DATETIME2(0) NOT NULL CONSTRAINT DF_bs_user_created_at DEFAULT SYSUTCDATETIME(),
        updated_at     DATETIME2(0) NULL,
        CONSTRAINT UQ_bs_user_email UNIQUE (email),
        CONSTRAINT UQ_bs_user_phone UNIQUE (phone),
        CONSTRAINT CK_bs_user_status CHECK (status IN ('Active', 'Blocked', 'Inactive', 'Pending'))
    );
END
GO

IF OBJECT_ID(N'dbo.bs_Addresses', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bs_Addresses (
        address_id       INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_Addresses PRIMARY KEY,
        user_id          INT NOT NULL,
        receiver_name    NVARCHAR(150) NOT NULL,
        phone            NVARCHAR(20) NOT NULL,
        province         NVARCHAR(100) NOT NULL,
        district         NVARCHAR(100) NOT NULL,
        ward             NVARCHAR(100) NOT NULL,
        detail_address   NVARCHAR(255) NOT NULL,
        is_default       BIT NOT NULL CONSTRAINT DF_bs_Addresses_is_default DEFAULT (0),
        created_at       DATETIME2(0) NOT NULL CONSTRAINT DF_bs_Addresses_created_at DEFAULT SYSUTCDATETIME()
    );
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
        spec_id             INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_ProductSpecifications PRIMARY KEY,
        product_id          INT NOT NULL,
        cpu                 NVARCHAR(100) NULL,
        gpu                 NVARCHAR(100) NULL,
        screen              NVARCHAR(100) NULL,
        battery             NVARCHAR(100) NULL,
        ram                 NVARCHAR(100) NULL,
        memory              NVARCHAR(100) NULL,
        storage             NVARCHAR(100) NULL,
        operating_system    NVARCHAR(100) NULL,
        created_at          DATETIME2(0) NOT NULL CONSTRAINT DF_bs_ProductSpecifications_created_at DEFAULT SYSUTCDATETIME(),
        CONSTRAINT UQ_bs_ProductSpecifications_product UNIQUE (product_id)
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
        product_id     INT NOT NULL,
        rating         INT NOT NULL,
        comment        NVARCHAR(1000) NULL,
        created_at     DATETIME2(0) NOT NULL CONSTRAINT DF_bs_Reviews_created_at DEFAULT SYSUTCDATETIME(),
        updated_at     DATETIME2(0) NULL,
        CONSTRAINT UQ_bs_Reviews_user_product UNIQUE (user_id, product_id),
        CONSTRAINT CK_bs_Reviews_rating CHECK (rating BETWEEN 1 AND 5)
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

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_Reviews_bs_Products')
    ALTER TABLE dbo.bs_Reviews
    ADD CONSTRAINT FK_bs_Reviews_bs_Products
    FOREIGN KEY (product_id) REFERENCES dbo.bs_Products(product_id) ON DELETE CASCADE;
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

PRINT 'LaptopWebsiteDB schema created successfully.';
GO
