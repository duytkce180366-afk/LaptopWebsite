<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<link rel="icon" href="<%= request.getContextPath()%>/Logo.ico" type="image/x-icon" />
<script>
    (function () {
        var theme = localStorage.getItem('techstore-theme');
        if (theme === 'dark') {
            document.documentElement.dataset.theme = 'dark';
        }
    })();
</script>
<meta name="description" content="TechHub computer store demo for browsing products, filters, details, and reviews." />
<!-- Bootstrap 5 -->
<link href="<%= request.getContextPath()%>/lib/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet" integrity="" crossorigin="anonymous">
<link rel="stylesheet" href="<%= request.getContextPath()%>/css/styles.css?v=storefront" />
<script defer src="<%= request.getContextPath()%>/js/theme.js"></script>
<script defer src="<%= request.getContextPath()%>/js/megaMenu.js"></script>
<script defer src="<%= request.getContextPath()%>/js/priceSlider.js?v=price-slider"></script>
<script defer src="<%= request.getContextPath()%>/lib/bootstrap/dist/js/bootstrap.bundle.min.js" integrity="" crossorigin="anonymous"></script>
