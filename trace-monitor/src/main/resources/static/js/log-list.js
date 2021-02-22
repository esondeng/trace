$(function () {
    // 查询日志
    $("#logPrev").on("click", function (e) {
        e.preventDefault();
        if($(e.target).parent("li").hasClass("disabled")){
            return false;
        }
        const pageNum = e.target.getAttribute("data-page");
        searchLogs(pageNum);
    });

    // 查询日志
    $("#logNext").on("click", function (e) {
        e.preventDefault();
        if($(e.target).parent("li").hasClass("disabled")){
            return false;
        }
        const pageNum = e.target.getAttribute("data-page");
        searchLogs(pageNum);
    });

    const loadingHtml =
        "<div class='col-xs-12 text-center'>"
        + "<img src='/static/images/loading.gif'/> 数据加载中..."
        + "</div>";

    function searchLogs(pageNum) {
        $("#log-search-alert").hide();

        const condition = $("#condition").val();

        let dateType = undefined;
        const jsLogGroup = $(".js-log-group.active");
        if (jsLogGroup.size() > 0) {
            dateType = jsLogGroup.attr("data-type");
        }

        const startTime = $("#startTime").val();
        const endTime = $("#endTime").val();

        const inputData = {
            "dateType": dateType,
            "pageNum": pageNum,
            "startTime": startTime,
            "endTime": endTime,
            "condition": condition
        }

        $("#log-results").html(loadingHtml);
        $.ajax(
            "/log/search.html",
            {
                type: 'GET',
                async: false,
                data: inputData,
                success: data => {
                    $("#log-results").html(data);
                }
            }
        );
    }
});