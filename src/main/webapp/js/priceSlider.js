(function () {
    function formatVnd(value) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND',
            maximumFractionDigits: 0
        }).format(value);
    }

    function clamp(value, min, max) {
        return Math.min(Math.max(value, min), max);
    }

    document.querySelectorAll('[data-price-slider]').forEach(function (slider) {
        var toggle = slider.querySelector('[data-price-toggle]');
        var closeButton = slider.querySelector('[data-price-close]');
        var buttonLabel = slider.querySelector('[data-price-button-label]');
        var panel = slider.querySelector('[data-price-panel]');
        var minRange = slider.querySelector('[data-price-min-range]');
        var maxRange = slider.querySelector('[data-price-max-range]');
        var minInput = slider.querySelector('[data-price-min-input]');
        var maxInput = slider.querySelector('[data-price-max-input]');
        var output = slider.querySelector('[data-price-output]');
        var track = slider.querySelector('.price-slider-track');
        var minThumb = slider.querySelector('[data-price-min-thumb]');
        var maxThumb = slider.querySelector('[data-price-max-thumb]');
        var max = Number(maxRange.max);
        var step = Number(maxRange.step) || 1;

        function sync(source) {
            var minValue = clamp(Number(minInput.value || minRange.value), 0, max);
            var maxValue = clamp(Number(maxInput.value || maxRange.value), 0, max);

            if (minValue > maxValue) {
                if (source === 'min') {
                    maxValue = minValue;
                } else {
                    minValue = maxValue;
                }
            }

            minValue = Math.round(minValue / step) * step;
            maxValue = Math.round(maxValue / step) * step;

            minInput.value = minValue;
            maxInput.value = maxValue;
            minRange.value = minValue;
            maxRange.value = maxValue;
            var label = formatVnd(minValue) + ' - ' + formatVnd(maxValue);
            output.value = label;
            buttonLabel.textContent = 'Price: ' + label;

            var minPercent = (minValue / max) * 100 + '%';
            var maxPercent = (maxValue / max) * 100 + '%';
            slider.style.setProperty('--price-min', minPercent);
            slider.style.setProperty('--price-max', maxPercent);
            if (track) {
                track.style.setProperty('--price-min', minPercent);
                track.style.setProperty('--price-max', maxPercent);
            }
            if (minThumb) {
                minThumb.style.left = minPercent;
            }
            if (maxThumb) {
                maxThumb.style.left = maxPercent;
            }
        }

        function openPanel() {
            panel.hidden = false;
            slider.classList.add('open');
            toggle.setAttribute('aria-expanded', 'true');
        }

        function closePanel() {
            panel.hidden = true;
            slider.classList.remove('open');
            toggle.setAttribute('aria-expanded', 'false');
        }

        minRange.addEventListener('input', function () {
            minInput.value = minRange.value;
            sync('min');
        });
        maxRange.addEventListener('input', function () {
            maxInput.value = maxRange.value;
            sync('max');
        });
        minInput.addEventListener('input', function () {
            sync('min');
        });
        maxInput.addEventListener('input', function () {
            sync('max');
        });
        toggle.addEventListener('click', function () {
            if (slider.classList.contains('open')) {
                closePanel();
            } else {
                openPanel();
            }
        });
        closeButton.addEventListener('click', function () {
            closePanel();
            toggle.focus();
        });
        document.addEventListener('click', function (event) {
            if (!slider.contains(event.target)) {
                closePanel();
            }
        });
        document.addEventListener('keydown', function (event) {
            if (event.key === 'Escape') {
                closePanel();
            }
        });

        sync();
    });
})();
