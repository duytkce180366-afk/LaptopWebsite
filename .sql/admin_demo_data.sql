USE LaptopWebsiteDB;
GO

/* Idempotent demo accounts and orders for the admin dashboard/report screens. */
SET NOCOUNT ON;
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;

DECLARE @CustomerRoleId INT=(SELECT role_id FROM dbo.bs_Roles WHERE role_name=N'Customer');
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
