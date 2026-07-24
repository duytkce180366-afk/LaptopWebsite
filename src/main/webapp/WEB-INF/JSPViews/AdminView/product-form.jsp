<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${product.productId == 0 ? 'Add Product' : 'Edit Product'}" />
<%@ include file="_start.jsp" %>


<form method="post" action="${pageContext.request.contextPath}/admin/products/save">
    <input type="hidden" name="csrfToken" value="${sessionScope.adminCsrfToken}">
    <input type="hidden" name="id" value="${product.productId}">

    <div class="admin-card">
        <h2 class="h5 mb-3">Product information</h2>

        <div class="row g-3">
            <div class="col-md-4">
                <label class="form-label">SKU *</label>
                <input class="form-control" required name="sku" value="<c:out value='${product.sku}' />">
            </div>

            <div class="col-md-8">
                <label class="form-label">Product name *</label>
                <input class="form-control" required name="productName" value="<c:out value='${product.productName}' />">
            </div>

            <div class="col-md-6">
                <label class="form-label">Category *</label>
                <select class="form-select" required name="categoryId">
                    <option value="">Select</option>

                    <c:forEach var="x" items="${categories}">
                        <option value="${x.id}" ${product.categoryId == x.id ? 'selected' : ''}>
                            <c:out value="${x.name}" />
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-md-6">
                <label class="form-label">Brand *</label>
                <select class="form-select" required name="brandId">
                    <option value="">Select</option>

                    <c:forEach var="x" items="${brands}">
                        <option value="${x.id}" ${product.brandId == x.id ? 'selected' : ''}>
                            <c:out value="${x.name}" />
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-md-4">
                <label class="form-label">Price *</label>
                <input
                    class="form-control"
                    type="number"
                    min="1"
                    step="0.01"
                    required
                    name="price"
                    value="${product.price}">
            </div>

            <div class="col-md-4">
                <label class="form-label">Current stock</label>
                <input class="form-control" value="${product.stock}" readonly>
                <div class="form-text">Stock is updated from the Inventory menu.</div>
            </div>

            <div class="col-md-4">
                <label class="form-label">Status *</label>
                <select class="form-select" name="status">
                    <c:forEach var="s" items="${['Active','Out of Stock','Hidden','Inactive']}">
                        <option ${product.status == s ? 'selected' : ''}>
                            <c:out value="${s}" />
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-12">
                <label class="form-label">Thumbnail URL</label>
                <input class="form-control" type="url" name="thumbnail" value="<c:out value='${product.thumbnail}' />">
            </div>

            <div class="col-12">
                <label class="form-label">Description</label>
                <textarea class="form-control" rows="4" name="description">
                    <c:out value="${product.description}" />
                </textarea>
            </div>
        </div>
    </div>

    <div class="admin-card">
        <div class="d-flex justify-content-between align-items-center mb-1">
            <h2 class="h5 mb-0">Specifications</h2>
            <button class="btn btn-sm btn-outline-primary" type="button" onclick="addSpec()">
                Add row
            </button>
        </div>
        <div class="form-text mb-3 text-muted">
            <small><i class="bi bi-info-circle"></i> <strong>Note for Laptops:</strong> The exact keys <code class="text-primary">cpu, ram, storage, gpu, display, battery, os</code> are strictly required.</small>
        </div>

        <div id="specs">
            <c:if test="${empty product.specifications}">
                <c:forEach var="key" items="${['cpu','ram','storage','gpu','display','battery','os']}">
                    <div class="spec-row">
                        <input class="form-control" name="specKey" value="${key}">
                        <input class="form-control" name="specValue" placeholder="Value">
                        <button class="btn btn-outline-danger" type="button" onclick="removeSpec(this)">
                            &times;
                        </button>
                    </div>
                </c:forEach>
            </c:if>

            <c:forEach var="spec" items="${product.specifications}">
                <div class="spec-row">
                    <input class="form-control" name="specKey" value="<c:out value='${spec.key}' />">
                    <input class="form-control" name="specValue" value="<c:out value='${spec.value}' />">
                    <button class="btn btn-outline-danger" type="button" onclick="removeSpec(this)">
                        &times;
                    </button>
                </div>
            </c:forEach>
        </div>
    </div>

    <div class="admin-actions">
        <button class="btn btn-primary">Save product</button>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/products">
            Cancel
        </a>
    </div>
</form>

<script>
    function addSpec() {
        const d = document.createElement('div');
        d.className = 'spec-row';
        d.innerHTML =
            '<input class="form-control" name="specKey" placeholder="Key">' +
            '<input class="form-control" name="specValue" placeholder="Value">' +
            '<button class="btn btn-outline-danger" type="button" onclick="removeSpec(this)">&times;</button>';
        document.getElementById('specs').appendChild(d);
    }

    function isLaptopCategory() {
        const categorySelect = document.querySelector('select[name="categoryId"]');
        if (!categorySelect || categorySelect.selectedIndex < 0) return false;
        const text = (categorySelect.options[categorySelect.selectedIndex]?.text || '').toLowerCase().trim();
        return text.includes('laptop');
    }

    function removeSpec(btn) {
        const row = btn.parentElement;
        const keyInput = row.querySelector('input[name="specKey"]');
        const requiredKeys = ['cpu', 'ram', 'storage', 'gpu', 'display', 'battery', 'os'];
        
        if (keyInput) {
            const keyVal = keyInput.value.trim().toLowerCase();
            if (isLaptopCategory() && requiredKeys.includes(keyVal)) {
                alert("Thông số '" + keyVal + "' là BẮT BUỘC cho Laptop và KHÔNG THỂ XÓA!");
                return false;
            }
        }
        
        row.remove();
        return false;
    }

    function updateSpecRows() {
        const isLaptop = isLaptopCategory();
        const requiredKeys = ['cpu', 'ram', 'storage', 'gpu', 'display', 'battery', 'os'];

        document.querySelectorAll('.spec-row').forEach(row => {
            const keyInput = row.querySelector('input[name="specKey"]');
            const btn = row.querySelector('button');
            if (!keyInput) return;

            const keyVal = keyInput.value.trim().toLowerCase();
            const isReq = isLaptop && requiredKeys.includes(keyVal);

            if (isReq) {
                keyInput.readOnly = true;
                keyInput.setAttribute('readonly', 'readonly');
                keyInput.style.backgroundColor = '#e9ecef';
                keyInput.style.cursor = 'not-allowed';
                if (btn) {
                    btn.style.setProperty('display', 'none', 'important');
                    btn.disabled = true;
                }
            } else {
                keyInput.readOnly = false;
                keyInput.removeAttribute('readonly');
                keyInput.style.backgroundColor = '';
                keyInput.style.cursor = '';
                if (btn) {
                    btn.style.display = '';
                    btn.disabled = false;
                }
            }
        });
    }

    // Run immediately on initial load & polling safeguard
    updateSpecRows();
    document.addEventListener('DOMContentLoaded', updateSpecRows);
    window.addEventListener('load', updateSpecRows);
    setInterval(updateSpecRows, 300);

    // Auto-add missing keys when Laptops is selected
    const catSel = document.querySelector('select[name="categoryId"]');
    if (catSel) {
        catSel.addEventListener('change', function() {
            if (isLaptopCategory()) {
                const requiredKeys = ['cpu', 'ram', 'storage', 'gpu', 'display', 'battery', 'os'];
                const currentKeys = Array.from(document.querySelectorAll('input[name="specKey"]')).map(i => i.value.trim().toLowerCase());
                
                requiredKeys.forEach(req => {
                    if (!currentKeys.includes(req)) {
                        const d = document.createElement('div');
                        d.className = 'spec-row';
                        d.innerHTML =
                            '<input class="form-control" name="specKey" value="' + req + '">' +
                            '<input class="form-control" name="specValue" placeholder="Value">' +
                            '<button class="btn btn-outline-danger" type="button" onclick="removeSpec(this)">&times;</button>';
                        document.getElementById('specs').appendChild(d);
                    }
                });
            }
            updateSpecRows();
        });
    }

    // Intercept form submission to prevent saving invalid laptops
    const mainForm = document.querySelector('form');
    if (mainForm) {
        mainForm.addEventListener('submit', function(e) {
            if (isLaptopCategory()) {
                const requiredKeys = ['cpu', 'ram', 'storage', 'gpu', 'display', 'battery', 'os'];
                const currentKeys = Array.from(document.querySelectorAll('input[name="specKey"]')).map(i => i.value.trim().toLowerCase());
                
                for (const req of requiredKeys) {
                    if (!currentKeys.includes(req)) {
                        e.preventDefault();
                        alert("Thiếu thông số bắt buộc: '" + req + "'. Sản phẩm Laptop phải có thuộc tính này.");
                        const d = document.createElement('div');
                        d.className = 'spec-row';
                        d.innerHTML =
                            '<input class="form-control" name="specKey" value="' + req + '">' +
                            '<input class="form-control" name="specValue" placeholder="Value">' +
                            '<button class="btn btn-outline-danger" type="button" onclick="removeSpec(this)">&times;</button>';
                        document.getElementById('specs').appendChild(d);
                        updateSpecRows();
                        return;
                    }
                }
            }
        });
    }
</script>

<%@ include file="_end.jsp" %>