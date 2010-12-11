/**
 * Analytics Event Tracking. Original version from
 * http://www.ravelrumba.com/blog/tracking-events-in-analytics/
 */
$(function() {
  $(document).click(function(event) {
    event = event || window.event;
    var target = event.target || event.srcElement;

    $(target).parent("a").each(function() {
      var href = $(this).attr("href");
      var urlHost = document.domain.replace(/^www\./i,"");
      var urlPattern = "^(?:https?:)?\\/\\/(?:(?:www)\\.)?" + urlHost + "\\/?";
      eventCheck(href, urlPattern);
    });
  });
});

function eventCheck(href, urlPattern) {
  var downPattern = /^.*\.(css|eps|exe|flv|gif|ico|jar|jpeg|jpg|mp3|pdf|png|qt|tif|txt|wav|wmv|xml|zip)$/i;

  if ((href.match(/^https?\:/i)) && (!href.match(urlPattern))){
    if (href.match(downPattern)) {
      _gaq.push(['_trackEvent', 'download', 'click', href]);
    } else {
      _gaq.push(['_trackEvent', 'link', 'click', href]);
    }
  } else if (href.match(/^mailto\:/i)) {
    _gaq.push(['_trackEvent', 'email', 'click', href.substr(7)]);
  } else if (href.match(downPattern)) {
    _gaq.push(['_trackEvent', 'download', 'click', href]);
  }
}
