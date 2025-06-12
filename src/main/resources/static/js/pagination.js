document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('.page-link').forEach(function(link) {
    link.addEventListener('click', function(e) {
      e.preventDefault();
      var page = this.getAttribute('data-page');
      console.log('Navigating to page:', page);
      var newUrl = buildPageUrl(page);
      console.log('New URL:', newUrl);
      window.location.href = newUrl;
    });
  });
});

function buildPageUrl(page) {
  console.log('buildPageUrl chiamata con page:', page);
  var url = new URL(window.location.href);
  var params = new URLSearchParams(url.search);
  params.set('page', page);
  url.search = params.toString();
  var newUrl = url.toString();
  console.log('Nuovo URL:', newUrl);
  return newUrl;
}
