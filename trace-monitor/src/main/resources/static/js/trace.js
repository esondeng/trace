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

        $("#call-chain-results").html(loadingHtml);
        $.post(
            "/trace/search.html",
            $("#callChainSearchForm").serialize(),
            function (result) {
                $("#call-chain-results").html(result);
            }
        );
    }

    // 查询调用链
    $(".js-call-chain-search").on("click", function () {
        searchCallChains();
    });
});