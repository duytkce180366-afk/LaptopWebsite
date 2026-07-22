</main></div>
<script src="${pageContext.request.contextPath}/lib/bootstrap/dist/js/bootstrap.bundle.min.js"></script>

<!-- Global Confirmation Modal -->
<div class="modal fade" id="confirmModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-sm">
    <div class="modal-content" style="border-radius: 16px; border: none; box-shadow: 0 10px 30px rgba(0,0,0,0.1);">
      <div class="modal-body text-center p-4">
        <div class="mb-3" style="font-size: 3.5rem; color: #ef4444;">
          <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" class="bi bi-exclamation-circle" viewBox="0 0 16 16">
            <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
            <path d="M7.002 11a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 4.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 4.995z"/>
          </svg>
        </div>
        <h5 class="mb-2" id="confirmModalText" style="font-weight: 700;">Are you sure?</h5>
        <p class="text-muted mb-4" style="font-size: 0.9rem;">This action cannot be undone.</p>
        <div class="d-flex justify-content-center gap-2">
            <button type="button" class="btn btn-light" data-bs-dismiss="modal" style="border-radius: 8px; font-weight: 500; padding: 8px 16px;">Cancel</button>
            <button type="button" class="btn btn-danger" id="confirmModalBtn" style="border-radius: 8px; font-weight: 500; padding: 8px 16px; background-color: #ef4444; border-color: #ef4444;">Yes, confirm</button>
        </div>
      </div>
    </div>
  </div>
</div>

<script>
window.showConfirm = function(message, onConfirm) {
    document.getElementById('confirmModalText').innerText = message;
    var myModal = new bootstrap.Modal(document.getElementById('confirmModal'));
    
    var confirmBtn = document.getElementById('confirmModalBtn');
    var newBtn = confirmBtn.cloneNode(true); // Remove previous event listeners
    confirmBtn.parentNode.replaceChild(newBtn, confirmBtn);
    
    newBtn.addEventListener('click', function() {
        myModal.hide();
        if (typeof onConfirm === 'function') onConfirm();
    });
    
    myModal.show();
};
</script>
</body></html>
