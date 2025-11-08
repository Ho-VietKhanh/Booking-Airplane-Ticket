// Small admin JS: sidebar toggle for mobile
document.addEventListener('DOMContentLoaded', function() {
  var toggle = document.getElementById('sidebarToggle');
  var sidebar = document.querySelector('.admin-sidebar');
  if(toggle && sidebar) {
    toggle.addEventListener('click', function(e){
      e.preventDefault();
      sidebar.classList.toggle('open');
    });
  }
});

