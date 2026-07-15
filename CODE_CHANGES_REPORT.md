# TechStore Admin - Code Changes Report

Ngày lập báo cáo: 2026-07-14

## Tổng quan

- File tạo mới: **43**
- File có sẵn được chỉnh sửa: **12**
- Tổng số file liên quan: **55**
- Trạng thái Git: các thay đổi hiện vẫn chưa được commit.

Số dòng bên dưới được tính theo source hiện tại. Nếu IDE tự động format code, vị trí dòng có thể thay đổi.

## 1. File tạo mới

Toàn bộ nội dung trong các file dưới đây được thêm mới.

### Controllers

| File | Dòng |
|---|---:|
| `src/main/java/com/mycompany/techstore/Controllers/AdminDashboardController.java` | 1-17 |
| `src/main/java/com/mycompany/techstore/Controllers/AdminOrderController.java` | 1-42 |
| `src/main/java/com/mycompany/techstore/Controllers/AdminProductController.java` | 1-65 |
| `src/main/java/com/mycompany/techstore/Controllers/AdminReportController.java` | 1-33 |
| `src/main/java/com/mycompany/techstore/Controllers/AdminReviewController.java` | 1-30 |
| `src/main/java/com/mycompany/techstore/Controllers/AdminUserController.java` | 1-34 |

### Filters

| File | Dòng |
|---|---:|
| `src/main/java/com/mycompany/techstore/Filters/AccountStatusFilter.java` | 1-69 |
| `src/main/java/com/mycompany/techstore/Filters/AdminAuthorizationFilter.java` | 1-27 |
| `src/main/java/com/mycompany/techstore/Filters/PurchaseAuthorizationFilter.java` | 1-22 |

### Models

| File | Dòng |
|---|---:|
| `src/main/java/com/mycompany/techstore/Models/Objects/AdminOrder.java` | 1-31 |
| `src/main/java/com/mycompany/techstore/Models/Objects/AdminProduct.java` | 1-41 |
| `src/main/java/com/mycompany/techstore/Models/Objects/AdminReview.java` | 1-20 |
| `src/main/java/com/mycompany/techstore/Models/Objects/AdminUser.java` | 1-20 |
| `src/main/java/com/mycompany/techstore/Models/Objects/DashboardStats.java` | 1-21 |
| `src/main/java/com/mycompany/techstore/Models/Objects/LookupOption.java` | 1-9 |
| `src/main/java/com/mycompany/techstore/Models/Objects/PageResult.java` | 1-19 |

### Repositories

| File | Dòng |
|---|---:|
| `src/main/java/com/mycompany/techstore/Repositories/AdminAuditRepository.java` | 1-20 |
| `src/main/java/com/mycompany/techstore/Repositories/AdminOrderRepository.java` | 1-80 |
| `src/main/java/com/mycompany/techstore/Repositories/AdminProductRepository.java` | 1-94 |
| `src/main/java/com/mycompany/techstore/Repositories/AdminReviewRepository.java` | 1-37 |
| `src/main/java/com/mycompany/techstore/Repositories/AdminUserRepository.java` | 1-52 |
| `src/main/java/com/mycompany/techstore/Repositories/DashboardRepository.java` | 1-38 |

### Services

| File | Dòng |
|---|---:|
| `src/main/java/com/mycompany/techstore/services/AdminOrderService.java` | 1-20 |
| `src/main/java/com/mycompany/techstore/services/AdminProductService.java` | 1-48 |
| `src/main/java/com/mycompany/techstore/services/AdminReviewService.java` | 1-15 |
| `src/main/java/com/mycompany/techstore/services/AdminUserService.java` | 1-25 |
| `src/main/java/com/mycompany/techstore/services/DashboardService.java` | 1-13 |
| `src/main/java/com/mycompany/techstore/services/OrderStatusPolicy.java` | 1-15 |

### Admin JSP và CSS

| File | Dòng |
|---|---:|
| `src/main/webapp/WEB-INF/JSPViews/AdminView/_start.jsp` | 1-20 |
| `src/main/webapp/WEB-INF/JSPViews/AdminView/_end.jsp` | 1-3 |
| `src/main/webapp/WEB-INF/JSPViews/AdminView/dashboard.jsp` | 1-9 |
| `src/main/webapp/WEB-INF/JSPViews/AdminView/order-detail.jsp` | 1-6 |
| `src/main/webapp/WEB-INF/JSPViews/AdminView/orders.jsp` | 1-13 |
| `src/main/webapp/WEB-INF/JSPViews/AdminView/product-form.jsp` | 1-21 |
| `src/main/webapp/WEB-INF/JSPViews/AdminView/products.jsp` | 1-14 |
| `src/main/webapp/WEB-INF/JSPViews/AdminView/reports.jsp` | 1-6 |
| `src/main/webapp/WEB-INF/JSPViews/AdminView/reviews.jsp` | 1-6 |
| `src/main/webapp/WEB-INF/JSPViews/AdminView/users.jsp` | 1-6 |
| `src/main/webapp/css/admin.css` | 1-9 |

### Database migration

| File | Dòng |
|---|---:|
| `.sql/admin_management_migration.sql` | 1-60 |

### Unit tests

| File | Dòng |
|---|---:|
| `src/test/java/com/mycompany/techstore/Filters/AccountStatusFilterTest.java` | 1-22 |
| `src/test/java/com/mycompany/techstore/Models/Objects/PageResultTest.java` | 1-15 |
| `src/test/java/com/mycompany/techstore/services/OrderStatusPolicyTest.java` | 1-24 |

## 2. File có sẵn được chỉnh sửa

| File | Các dòng thay đổi hiện tại |
|---|---|
| `.sql/setup.sql` | 257; 316-337; 449-460; 477-488; 624; 627; 752-759 |
| `src/main/java/com/mycompany/techstore/Controllers/AuthController.java` | 271; 298; 302-309 |
| `src/main/java/com/mycompany/techstore/Controllers/CancelOrderController.java` | 3-4; 34-39 |
| `src/main/java/com/mycompany/techstore/Repositories/OrderRepository.java` | 73-79; 121; 156; 166; 169; 191; 423-427 |
| `src/main/java/com/mycompany/techstore/Repositories/ProductRepository.java` | 164 |
| `src/main/java/com/mycompany/techstore/Repositories/UserRepository.java` | 3-9; 12-46 |
| `src/main/java/com/mycompany/techstore/services/AuthService.java` | 104-106; 177-179 |
| `src/main/webapp/WEB-INF/JSPViews/AuthView/Login.jsp` | 16-29 |
| `src/main/webapp/WEB-INF/JSPViews/GuestView/Checkout.jsp` | 98; 127; 215; 217 |
| `src/main/webapp/WEB-INF/JSPViews/GuestView/order-detail.jsp` | 154; 159; 168; 177 |
| `src/main/webapp/WEB-INF/JSPViews/GuestView/vnpay-result.jsp` | 5; 55-58; 61-63; 66-68 |
| `src/main/webapp/WEB-INF/web.xml` | 8 |

## 3. File không tính là code được thêm

Các file sau là cấu hình hoặc tài nguyên đã có thay đổi từ phía người dùng trước khi triển khai module Admin:

- `src/main/java/com/mycompany/techstore/resources/DbClass.java`
- `src/main/webapp/META-INF/context.xml`
- `Business Rules.docx`
- `LaptopWebsite_RDS.docx`
- `Use Case.drawio`
- `nhiệm_vụ.png`

## 4. Chức năng tương ứng

- Manage Products
- Manage Orders
- Manage Users
- Manage Reviews
- Dashboard and Reports
- Admin authorization and CSRF protection
- Admin audit logs
- Immediate account blocking and session invalidation
- Product status normalization
- English UI normalization
