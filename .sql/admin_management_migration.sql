USE LaptopWebsiteDB;
GO

/* Admin management migration. Safe to run more than once. */
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
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name=N'IX_bs_AdminAuditLogs_created_at' AND object_id=OBJECT_ID(N'dbo.bs_AdminAuditLogs'))
    CREATE INDEX IX_bs_AdminAuditLogs_created_at ON dbo.bs_AdminAuditLogs(created_at DESC);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name=N'IX_bs_Reviews_status_created' AND object_id=OBJECT_ID(N'dbo.bs_Reviews'))
    CREATE INDEX IX_bs_Reviews_status_created ON dbo.bs_Reviews(status,created_at DESC);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name=N'IX_bs_Orders_status_created' AND object_id=OBJECT_ID(N'dbo.bs_Orders'))
    CREATE INDEX IX_bs_Orders_status_created ON dbo.bs_Orders(order_status,created_at DESC);
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
