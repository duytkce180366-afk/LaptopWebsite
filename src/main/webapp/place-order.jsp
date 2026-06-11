<%-- 
    Document   : placeorder
    Created on : Jun 9, 2026, 9:40:45 AM
    Author     : Nguyen Lam Khang
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Checkout – TechStore</title>
        <link rel="stylesheet" href="lib/bootstrap/css/bootstrap.min.css">
        <style>

            body {
                background: #f0f2f5;
                font-family: 'Segoe UI', sans-serif;
            }

            /* ── Wrapper ── */
            .checkout-wrapper {
                max-width: 560px;
                margin: 60px auto;
                padding: 0 16px;
            }

            /* ── Page header ── */
            .page-header {
                display: flex;
                align-items: center;
                gap: 10px;
                margin-bottom: 24px;
            }

            .page-header h2 {
                font-size: 22px;
                font-weight: 600;
                margin: 0;
                color: #1a1f2e;
            }

            .store-badge {
                font-size: 11px;
                font-weight: 600;
                background: #dbeafe;
                color: #1e40af;
                padding: 3px 10px;
                border-radius: 20px;
                letter-spacing: 0.03em;
            }

            /* ── Card ── */
            .checkout-card {
                background: #ffffff;
                border-radius: 14px;
                box-shadow: 0 1px 4px rgba(0,0,0,.06), 0 4px 16px rgba(0,0,0,.05);
                padding: 28px 32px;
            }

            /* ── Section label ── */
            .section-label {
                font-size: 11px;
                font-weight: 700;
                letter-spacing: 0.08em;
                text-transform: uppercase;
                color: #9ca3af;
                margin-bottom: 16px;
            }

            .divider {
                border: none;
                border-top: 1px solid #f1f3f6;
                margin: 24px 0;
            }

            /* ── Form controls ── */
            .form-label {
                font-size: 13px;
                font-weight: 600;
                color: #374151;
                margin-bottom: 6px;
            }

            .form-control,
            .form-select {
                font-size: 13.5px;
                padding: 10px 12px;
                border: 1px solid #e2e8f0;
                border-radius: 10px;
                background: #f8f9fc;
                color: #1a1f2e;
                transition: border-color 0.15s, background 0.15s;
            }

            .form-control:focus,
            .form-select:focus {
                border-color: #3b82f6;
                background: #fff;
                box-shadow: 0 0 0 3px rgba(59,130,246,.12);
                outline: none;
            }

            .form-control::placeholder {
                color: #b0b7c3;
            }

            /* ── Payment options ── */
            .payment-options {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 10px;
            }

            .payment-option {
                position: relative;
            }

            .payment-option input[type="radio"] {
                position: absolute;
                opacity: 0;
                width: 0;
            }

            .payment-option label {
                display: flex;
                align-items: center;
                gap: 10px;
                padding: 12px 14px;
                border: 1.5px solid #e2e8f0;
                border-radius: 10px;
                background: #f8f9fc;
                cursor: pointer;
                font-size: 13px;
                font-weight: 500;
                color: #374151;
                transition: border-color 0.15s, background 0.15s;
                user-select: none;
            }

            .payment-option input[type="radio"]:checked + label {
                border-color: #3b82f6;
                background: #eff6ff;
                color: #1e40af;
            }

            .payment-option label svg {
                flex-shrink: 0;
            }

            /* ── Buttons ── */
            .btn-place {
                width: 100%;
                background: #1e40af;
                color: #fff;
                border: none;
                border-radius: 10px;
                padding: 13px;
                font-size: 14px;
                font-weight: 600;
                cursor: pointer;
                transition: background 0.15s, transform 0.1s;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 8px;
            }

            .btn-place:hover  { background: #1d3fa5; }
            .btn-place:active { transform: scale(0.985); }

            .btn-history {
                width: 100%;
                background: transparent;
                color: #6b7280;
                border: 1px solid #e2e8f0;
                border-radius: 10px;
                padding: 11px;
                font-size: 13px;
                font-weight: 500;
                cursor: pointer;
                text-align: center;
                text-decoration: none;
                display: block;
                margin-top: 10px;
                transition: background 0.15s, color 0.15s;
            }

            .btn-history:hover {
                background: #f1f3f6;
                color: #374151;
            }

        </style>
    </head>
    <body>

        <div class="checkout-wrapper">

            <!-- Page header -->
            <div class="page-header">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
                     viewBox="0 0 24 24" fill="none" stroke="#1e40af"
                     stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="2" y="3" width="20" height="14" rx="2"/>
                    <path d="M8 21h8M12 17v4"/>
                </svg>
                <h2>Đặt hàng</h2>
                <span class="store-badge">TechStore</span>
            </div>

            <!-- Checkout card -->
            <div class="checkout-card">

                <form action="place-order" method="post">

                    <!-- Delivery info -->
                    <p class="section-label">Thông tin giao hàng</p>

                    <div class="mb-3">
                        <label class="form-label">Địa chỉ giao hàng</label>
                        <input type="text"
                               class="form-control"
                               name="address"
                               placeholder="Số nhà, đường, phường, quận..."
                               required>
                    </div>

                    <div class="mb-0">
                        <label class="form-label">Số điện thoại</label>
                        <input type="text"
                               class="form-control"
                               name="phone"
                               placeholder="0xxx xxx xxx"
                               required>
                    </div>

                    <hr class="divider">

                    <!-- Payment -->
                    <p class="section-label">Phương thức thanh toán</p>

                    <div class="payment-options">

                        <div class="payment-option">
                            <input type="radio"
                                   id="cod"
                                   name="paymentMethod"
                                   value="COD"
                                   checked>
                            <label for="cod">
                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18"
                                     viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                     stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <rect x="2" y="6" width="20" height="12" rx="2"/>
                                    <circle cx="12" cy="12" r="2"/>
                                    <path d="M6 12h.01M18 12h.01"/>
                                </svg>
                                Tiền mặt (COD)
                            </label>
                        </div>

                        <div class="payment-option">
                            <input type="radio"
                                   id="bank"
                                   name="paymentMethod"
                                   value="Bank Transfer">
                            <label for="bank">
                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18"
                                     viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                     stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <rect x="2" y="5" width="20" height="14" rx="2"/>
                                    <path d="M2 10h20"/>
                                </svg>
                                Chuyển khoản
                            </label>
                        </div>

                    </div>

                    <hr class="divider">

                    <!-- Actions -->
                    <button type="submit" class="btn-place">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                             viewBox="0 0 24 24" fill="none" stroke="currentColor"
                             stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M5 12l5 5L20 7"/>
                        </svg>
                        Xác nhận đặt hàng
                    </button>

                    <a href="order-history" class="btn-history">
                        Xem lịch sử đơn hàng
                    </a>

                </form>

            </div>

        </div>

        <script src="lib/bootstrap/js/bootstrap.min.js"></script>
        <script src="lib/@popperjs/core/dist/umd/popper.min.js"></script>
        <script src="js/theme.js"></script>
        <script src="js/megaMenu.js"></script>
        <script src="js/priceSlider.js"></script>

    </body>
</html>
