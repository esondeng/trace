$(function () {

    /**
     * 初始化时间类型搜索
     */
    $(".js-log-group").on("click", function (e) {
        $(".js-log-self").removeClass("active");
        if ($("#timeQueryForm").is(":visible")) {
            $("#timeQueryForm").hide();
        }

        e.preventDefault();
        $(".js-log-group").removeClass("active");
        $(e.target).addClass("active");
    });

    $(".js-log-self").on("click", function (e) {
        e.preventDefault();
        $("#timeQueryForm").show();

        $(".js-log-group").removeClass("active");
        $(e.target).addClass("active");
    });

    /**
     * 组件时间初始化
     */
    function initDateComponent() {

        // 初始化日期控件
        $("#startTime").datetimepicker({
            language: "zh-CN",
            autoclose: true,
            todayBtn: true,
            format: "yyyy-mm-dd hh:ii:ss",
            startView: 2,
            minView: 0
        }).on('changeDate', function (selected) {
            const minDate = new Date(selected.date.valueOf());
            $('#endTime').datetimepicker('setStartDate', minDate);
        });

        $("#endTime").datetimepicker({
            language: "zh-CN",
            autoclose: true,
            todayBtn: true,
            format: "yyyy-mm-dd hh:ii:ss",
            startView: 2,
            minView: 0,
            initialDate: new Date()
        }).on('changeDate', function (selected) {
            const maxDate = new Date(selected.date.valueOf());
            $('#startTime').datetimepicker('setEndDate', maxDate);
        });
    }

    // 时间控件
    initDateComponent();

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

        let count = 0;
        for (let i = 0; i < condition.length; i++) {
            if (condition[i] === "\"")
                count ++;
        }

        if(count %2 !== 0){
            $("#log-error-container").text("引号个数不对");
            $("#log-search-alert").show();
            return false;
        }

        $("#log-results").html(loadingHtml);

        let dateType = undefined;
        const jsLogGroup = $(".js-log-group.active");
        if(jsLogGroup){
            dateType = jsLogGroup.get(0).getAttribute("data-type");
        }

        const startTime = $("#startTime").val();
        const endTime = $("#endTime").val();

        const inputData = {
            "dateType": dateType,
            "startTime": startTime,
            "endTime": endTime,
            "condition": condition
        }

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

    // 查询日志
    $(".js-log-search").on("click", function () {
        searchLogs();
    });

    // 错误信息隐藏
    $(".js-close-log-alert").on("click", function () {
        $("#log-search-alert").hide();
    });
});