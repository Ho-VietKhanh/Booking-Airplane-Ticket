// Admin JS: sidebar toggle, helpers
document.addEventListener('DOMContentLoaded', function () {
    const sidebar = document.querySelector('.admin-sidebar');
    const toggle = document.getElementById('sidebarToggle');

    if (toggle && sidebar) {
        toggle.addEventListener('click', function () {
            sidebar.classList.toggle('open');
        });
    }

    // Close sidebar when clicking outside on mobile
    document.addEventListener('click', function (e) {
        if (!sidebar) return;
        if (window.innerWidth <= 991 && sidebar.classList.contains('open')) {
            const inside = e.target.closest('.admin-sidebar') || e.target.closest('#sidebarToggle');
            if (!inside) sidebar.classList.remove('open');
        }
    });

    // Simple helper: flash message auto-dismiss
    document.querySelectorAll('.alert[data-auto-dismiss]').forEach(function (el) {
        const t = parseInt(el.getAttribute('data-auto-dismiss'), 10) || 3000;
        setTimeout(() => { if (el && el.parentNode) el.parentNode.removeChild(el); }, t);
    });
});

