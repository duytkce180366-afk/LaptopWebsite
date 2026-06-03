/*
    01_fix_product_specifications_schema.sql
    Purpose: replace the laptop-only bs_ProductSpecifications shape with a generic
    product specification table that supports every store category.

    Safety notes:
    - This script does not drop the old laptop-specific table.
    - If the old shape exists, it renames that table to a timestamped backup name.
    - It also renames old constraints first so new constraint names do not collide.
    - The script runs inside a transaction. If an error happens, SQL Server rolls back.

    Run this once before 02_seed_repository_data.sql.
*/

SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    IF OBJECT_ID(N'dbo.bs_ProductSpecifications', N'U') IS NULL
    BEGIN
        CREATE TABLE dbo.bs_ProductSpecifications (
            spec_id INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_ProductSpecifications PRIMARY KEY,
            product_id INT NOT NULL,
            spec_key NVARCHAR(80) NOT NULL,
            spec_label NVARCHAR(120) NOT NULL,
            spec_value NVARCHAR(500) NOT NULL,
            sort_order INT NOT NULL CONSTRAINT DF_bs_ProductSpecifications_sort_order DEFAULT (0),
            created_at DATETIME2(0) NOT NULL CONSTRAINT DF_bs_ProductSpecifications_created_at DEFAULT (SYSDATETIME()),
            updated_at DATETIME2(0) NULL
        );
    END
    ELSE IF COL_LENGTH(N'dbo.bs_ProductSpecifications', N'spec_key') IS NULL
    BEGIN
        DECLARE @suffix NVARCHAR(32) = CONVERT(CHAR(8), GETDATE(), 112) + REPLACE(CONVERT(CHAR(8), GETDATE(), 108), N':', N'');
        DECLARE @backupName SYSNAME = N'bs_ProductSpecifications_LaptopBackup_' + @suffix;
        DECLARE @counter INT = 1;

        WHILE OBJECT_ID(N'dbo.' + QUOTENAME(@backupName), N'U') IS NOT NULL
        BEGIN
            SET @counter += 1;
            SET @backupName = N'bs_ProductSpecifications_LaptopBackup_' + @suffix + N'_' + CONVERT(NVARCHAR(10), @counter);
        END;

        DECLARE @constraintName SYSNAME;
        DECLARE @constraintRename SYSNAME;
        DECLARE constraint_cursor CURSOR LOCAL FAST_FORWARD FOR
            SELECT name
            FROM sys.objects
            WHERE parent_object_id = OBJECT_ID(N'dbo.bs_ProductSpecifications')
              AND type IN (N'PK', N'UQ', N'F', N'D', N'C');

        OPEN constraint_cursor;
        FETCH NEXT FROM constraint_cursor INTO @constraintName;

        WHILE @@FETCH_STATUS = 0
        BEGIN
            SET @constraintRename = LEFT(@constraintName + N'_LaptopBackup_' + @suffix, 128);
            EXEC sys.sp_rename @objname = @constraintName, @newname = @constraintRename, @objtype = N'OBJECT';
            FETCH NEXT FROM constraint_cursor INTO @constraintName;
        END;

        CLOSE constraint_cursor;
        DEALLOCATE constraint_cursor;

        EXEC sys.sp_rename N'dbo.bs_ProductSpecifications', @backupName;

        CREATE TABLE dbo.bs_ProductSpecifications (
            spec_id INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bs_ProductSpecifications PRIMARY KEY,
            product_id INT NOT NULL,
            spec_key NVARCHAR(80) NOT NULL,
            spec_label NVARCHAR(120) NOT NULL,
            spec_value NVARCHAR(500) NOT NULL,
            sort_order INT NOT NULL CONSTRAINT DF_bs_ProductSpecifications_sort_order DEFAULT (0),
            created_at DATETIME2(0) NOT NULL CONSTRAINT DF_bs_ProductSpecifications_created_at DEFAULT (SYSDATETIME()),
            updated_at DATETIME2(0) NULL
        );
    END;

    IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'UX_bs_ProductSpecifications_Product_Key' AND object_id = OBJECT_ID(N'dbo.bs_ProductSpecifications'))
    BEGIN
        CREATE UNIQUE INDEX UX_bs_ProductSpecifications_Product_Key
            ON dbo.bs_ProductSpecifications(product_id, spec_key);
    END;

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

    IF OBJECT_ID(N'dbo.bs_Products', N'U') IS NOT NULL
       AND NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bs_ProductSpecifications_bs_Products')
    BEGIN
        ALTER TABLE dbo.bs_ProductSpecifications WITH CHECK ADD CONSTRAINT FK_bs_ProductSpecifications_bs_Products
            FOREIGN KEY(product_id) REFERENCES dbo.bs_Products(product_id) ON DELETE CASCADE;
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
