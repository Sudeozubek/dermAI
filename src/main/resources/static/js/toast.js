  document.addEventListener('DOMContentLoaded', function() {
    const toasts = document.querySelectorAll('.toast');
    toasts.forEach(function(toast) {
      // 5 saniye sonra otomatik kapat
      setTimeout(function() {
        toast.remove();
      }, 5000);
    });
  });

  function closeToast(button) {
    const toast = button.closest('.toast');
    toast.remove();
  }
