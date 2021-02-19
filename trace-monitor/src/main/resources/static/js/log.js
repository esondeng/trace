$(function () {
    const loadingHtml =
        "<div class='col-xs-12 text-center'>"
        + "<img src='/static/images/loading.gif'/> 数据加载中..."
        + "</div>";

    function searchLogs() {
        $("#log-search-alert").hide();

        const condition = $("#condition").val();
        if (condition === '') {
            $("#log-error-container").text("日志查询条件必填");
            $("#log-search-alert").show();
            return false;
        }

        $("#log-results").html(loadingHtml);
        $.ajax(
            "/log/search.html",
            {
                type: 'GET',
                async: false,
                data: $("#logSearchForm").serialize(),
                success: data => {
                    $("#log-results").html(data);
                }
            }
        );
    }

    // 查询日志
    $(".js-log-search").on("click", function () {
        searchLogs();
    });

    // 错误信息隐藏
    $(".js-close-log-alert").on("click", function () {
        $("#log-search-alert").hide();
    });
});