<%@page import="com.mycompany.techstore.Models.Objects.Address"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Address - TechStore</title>
        <%@include file="/WEB-INF/JSPViews/global/header.jsp" %>
    </head>
    <body id="top">
        <%@include file="/WEB-INF/JSPViews/global/nav.jsp" %>
        <main class="auth-shell">
            <% String ctx = request.getContextPath();%>
            <div class="container d-flex align-items-center justify-content-center min-vh-75 py-5">
                <div class="card auth-panel shadow-sm w-100" style="max-width:680px;">
                    <div class="card-body p-4">
                        <h2 class="card-title mb-3">Address Form</h2>

                        <% Address addr = (Address) request.getAttribute("address");%>
                        <form id="address-form" method="post" action="<%= ctx%>/profile?action=<%= request.getParameter("action")%>" style="display:none;">
                            <input type="hidden" name="address_id" value="<%= addr == null ? -1 : addr.getAddressId()%>" />

                            <input type="hidden" id="initial_province_val" value="<%= addr == null ? "" : addr.getProvince()%>" />
                            <input type="hidden" id="initial_ward_val" value="<%= addr == null ? "" : addr.getWard()%>" />

                            <div id="address-loading" class="mb-3">
                                Loading address data...
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Home Address</label>
                                <input type="text" name="home_address" class="form-control" value="<%= addr == null ? "" : addr.getHomeAddress()%>" required />
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Phone</label>
                                <input type="text" name="phone" class="form-control" value="<%= addr == null ? "" : addr.getPhone()%>" required />
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Province</label>
                                <select id="province-select" name="province" class="form-select" required>
                                    <option value="">Loading provinces...</option>
                                </select>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Postal Code</label>
                                <input type="text" name="postal_code" class="form-control" value="<%= addr == null ? "" : addr.getPostalCode()%>" required />
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Ward</label>
                                <select id="ward-select" name="ward" class="form-select" required disabled>
                                    <option value="">Select a province first</option>
                                </select>
                            </div>

                            <div class="form-check mb-3">
                                <input class="form-check-input" type="checkbox" name="is_default" id="is_default" <%= (addr != null && addr.isIsDefault()) ? "checked" : ""%> />
                                <label class="form-check-label" for="is_default">Set as default</label>
                            </div>

                            <div class="d-flex gap-2 align-items-center">
                                <button class="btn btn-primary primary-action" type="submit">Save</button>
                                <a class="btn btn-outline-secondary secondary-action" href="<%= ctx%>/profile">Cancel</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>

        <script type="text/javascript">
            (function () {
                var ctx = '<%= ctx%>';
                var apiUrl = ctx + '/lib/communes.json';
                var form = document.getElementById('address-form');
                var loading = document.getElementById('address-loading');
                var provinceSelect = document.getElementById('province-select');
                var wardSelect = document.getElementById('ward-select');
                var initialProvince = (document.getElementById('initial_province_val') || {value: ''}).value || '';
                var initialWard = (document.getElementById('initial_ward_val') || {value: ''}).value || '';

                fetch(apiUrl).then(function (res) {
                    if (!res.ok)
                        throw new Error('Network response was not ok');
                    return res.json();
                }).then(function (data) {
                    var communes = data && data.communes ? data.communes : [];
                    // build unique provinces map
                    var provinceMap = new Map();
                    communes.forEach(function (c) {
                        if (c.provinceCode && c.provinceName && !provinceMap.has(c.provinceCode)) {
                            provinceMap.set(c.provinceCode, c.provinceName);
                        }
                    });

                    var provinces = Array.from(provinceMap.entries()).map(function (e) {
                        return {code: e[0], name: e[1]};
                    });
                    provinces.sort(function (a, b) {
                        return a.name.localeCompare(b.name, 'vi');
                    });

                    // populate province select
                    provinceSelect.innerHTML = '<option value="">Select province</option>';
                    provinces.forEach(function (p) {
                        var opt = document.createElement('option');
                        opt.value = p.name;
                        opt.text = p.name;
                        opt.dataset.code = p.code;
                        provinceSelect.appendChild(opt);
                    });

                    function populateWards(provinceName, setSelected) {
                        var filtered = communes.filter(function (c) {
                            return c.provinceName === provinceName;
                        });
                        wardSelect.innerHTML = '';
                        if (filtered.length === 0) {
                            wardSelect.innerHTML = '<option value="">No wards found</option>';
                            wardSelect.disabled = true;
                            return;
                        }
                        filtered.sort(function (a, b) {
                            return a.name.localeCompare(b.name, 'vi');
                        });
                        wardSelect.disabled = false;
                        wardSelect.innerHTML = '<option value="">Select ward</option>';
                        filtered.forEach(function (c) {
                            var opt = document.createElement('option');
                            opt.value = c.name;
                            opt.text = c.name;
                            opt.dataset.code = c.code;
                            wardSelect.appendChild(opt);
                        });
                        if (setSelected && initialWard) {
                            for (var i = 0; i < wardSelect.options.length; i++) {
                                if (wardSelect.options[i].value === initialWard) {
                                    wardSelect.options[i].selected = true;
                                    break;
                                }
                            }
                        }
                    }

                    // preselect province if available (match by name or code)
                    if (initialProvince) {
                        // try match by name
                        var optionFound = null;
                        for (var i = 0; i < provinceSelect.options.length; i++) {
                            var o = provinceSelect.options[i];
                            if (o.value === initialProvince || o.dataset.code === initialProvince) {
                                optionFound = o;
                                break;
                            }
                        }
                        if (optionFound) {
                            optionFound.selected = true;
                            populateWards(optionFound.value, true);
                        }
                    }

                    provinceSelect.addEventListener('change', function () {
                        var val = this.value;
                        if (!val) {
                            wardSelect.innerHTML = '<option value="">Select a province first</option>';
                            wardSelect.disabled = true;
                            return;
                        }
                        populateWards(val, false);
                    });

                    // done loading
                    loading.style.display = 'none';
                    form.style.display = '';
                }).catch(function (err) {
                    console.error('Failed to load communes.json', err);
                    loading.textContent = 'Could not load address data; please enter Province and Ward manually.';
                    // show form so user can proceed
                    form.style.display = '';
                });
            })();
        </script>

        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
    </body>
</html>
