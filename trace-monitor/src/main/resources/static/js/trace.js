$(function () {

    const loadingHtml =
        "<div class='col-xs-12 text-center'>"
        + "<img src='/static/images/loading.gif'/> 数据加载中..."
        + "</div>";

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


    /**
     * 查询调用链信息(提交调用链查询条件区域表单)
     *
     */
    function searchCallChains() {
        $("#trace-search-alert").hide();

        const applicationName = $("#applicationName").val();
        const traceId = $("#traceId").val();

        if (applicationName === '' && traceId === '') {
            $("#trace-error-container").text("不使用精确查找Trace Id的情况下，应用名称必填");
            $("#trace-search-alert").show();
            return false;
        }

        const startTime = $("#startTime").val();
        const endTime = $("#endTime").val();

        if (startTime !== '' && endTime === '') {
            $("#trace-error-container").text("开始时间和结束时间需都填");
            $("#trace-search-alert").show();
            return false;
        }

        if (startTime === '' && endTime !== '') {
            $("#trace-error-container").text("开始时间和结束时间需都填");
            $("#trace-search-alert").show();
            return false;
        }

        if (startTime !== '' && endTime !== '') {
            const start = Date.parse(startTime);
            const now = new Date().getTime();

            console.log(now);
            console.log(start);

            if (now - start > 30 * 24 * 3600 * 1000) {


                $("#trace-error-container").text("开始时间只能在1个月之内");
                $("#trace-search-alert").show();
                return false;
            }
        }

        $("#call-chain-results").html(loadingHtml);
        $.ajax(
            "/trace/search.html",
            {
                type: 'GET',
                async: false,
                data: $("#callChainSearchForm").serialize(),
                success: data => {
                    $("#call-chain-results").html(data);
                }
            }
        );
    }

    // 查询调用链
    $(".js-call-chain-search").on("click", function () {
        searchCallChains();
    });

    // 查询调用链错误信息隐藏
    $(".js-close-trace-alert").on("click", function () {
        $("#trace-search-alert").hide();
    });
});