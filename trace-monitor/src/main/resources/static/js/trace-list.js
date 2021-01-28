$(function () {
    $("#trace-table .trace-list-a").on("click", function (e) {
        e.preventDefault();
        var traceId = e.target.getAttribute("data-trace-id");
        window.open("/trace/detail.html?traceId=" + traceId);
    });
});